package parts.code.piggybox.query.streams.suppliers

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import parts.code.piggybox.query.config.KafkaConfig
import parts.code.piggybox.schemas.BalanceState
import parts.code.piggybox.schemas.FundsAdded
import parts.code.piggybox.schemas.FundsWithdrawn
import parts.code.piggybox.schemas.HistoryState
import parts.code.piggybox.schemas.Transaction
import parts.code.piggybox.schemas.TransactionType

class BalanceProcessor @Inject constructor(
    private val config: KafkaConfig
) : Processor<String, SpecificRecord> {

    private val logger = LoggerFactory.getLogger(BalanceProcessor::class.java)
    private lateinit var balanceStateStore: KeyValueStore<String, BalanceState>
    private lateinit var historyStateStore: KeyValueStore<String, HistoryState>

    @Suppress("UNCHECKED_CAST")
    override fun init(context: ProcessorContext) {
        balanceStateStore = context.getStateStore(config.stateStores.balanceReadModel) as KeyValueStore<String, BalanceState>
        historyStateStore = context.getStateStore(config.stateStores.historyReadModel) as KeyValueStore<String, HistoryState>
    }

    override fun process(key: String, record: SpecificRecord) {
        when (record) {
            is FundsAdded -> fundsAdded(record)
            is FundsWithdrawn -> fundsWithdrawn(record)
        }

        logger.info("Processed ${record.schema.name}\n\trecord: $record")
    }

    private fun fundsAdded(record: FundsAdded) {
        val balanceState = currentBalance(record.customerId)
        val newBalanceState = BalanceState(
            balanceState.customerId,
            balanceState.amount + record.amount,
            balanceState.version + 1
        )
        balanceStateStore.put(record.customerId, newBalanceState)

        val transaction = Transaction(
            "Funds Added",
            LocalDateTime.ofInstant(record.occurredOn, ZoneOffset.UTC).toLocalDate(),
            TransactionType.FUNDS_ADDED,
            record.amount
        )
        saveHistory(record.customerId, transaction)
    }

    private fun fundsWithdrawn(record: FundsWithdrawn) {
        val balanceState = currentBalance(record.customerId)
        val newBalanceState = BalanceState(
            balanceState.customerId,
            balanceState.amount - record.amount,
            balanceState.version + 1
        )
        balanceStateStore.put(record.customerId, newBalanceState)

        val transaction = Transaction(
            "Funds Withdrawn",
            LocalDateTime.ofInstant(record.occurredOn, ZoneOffset.UTC).toLocalDate(),
            TransactionType.FUNDS_WITHDRAWN,
            record.amount
        )
        saveHistory(record.customerId, transaction)
    }

    private fun currentBalance(customerId: String): BalanceState {
        val balanceState = balanceStateStore.get(customerId)
        return if (balanceState != null) balanceState else BalanceState(customerId, BigDecimal.ZERO, 0)
    }

    private fun currentHistory(customerId: String): HistoryState {
        val historyState = historyStateStore.get(customerId)
        return if (historyState != null) historyState else HistoryState(customerId, mutableListOf())
    }

    private fun saveHistory(customerId: String, transaction: Transaction) {
        val newHistory = HistoryState(
            customerId,
            currentHistory(customerId).transactions + transaction
        )
        historyStateStore.put(customerId, newHistory)
    }

    override fun close() {}
}
