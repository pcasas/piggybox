package parts.code.piggybox.balance.streams.suppliers

import java.math.BigDecimal.ZERO
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import parts.code.piggybox.balance.config.KafkaConfig
import parts.code.piggybox.schemas.BalanceState
import parts.code.piggybox.schemas.FundsAdded
import parts.code.piggybox.schemas.FundsWithdrawn

class RecordProcessor @Inject constructor(
    private val config: KafkaConfig
) : Processor<String, SpecificRecord> {

    private val logger = LoggerFactory.getLogger(RecordProcessor::class.java)
    private lateinit var state: KeyValueStore<String, BalanceState>

    override fun init(context: ProcessorContext) {
        @Suppress("UNCHECKED_CAST")
        state = context.getStateStore(config.stateStores.balance) as KeyValueStore<String, BalanceState>
    }

    override fun process(key: String, record: SpecificRecord) {
        when (record) {
            is FundsAdded -> fundsAdded(record)
            is FundsWithdrawn -> fundsWithdrawn(record)
            else -> Unit
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
        state.put(record.customerId, newBalanceState)
    }

    private fun fundsWithdrawn(record: FundsWithdrawn) {
        val balanceState = currentBalance(record.customerId)
        val newBalanceState = BalanceState(
            balanceState.customerId,
            balanceState.amount - record.amount,
            balanceState.version + 1
        )
        state.put(record.customerId, newBalanceState)
    }

    private fun currentBalance(customerId: String): BalanceState {
        val balanceState = state.get(customerId)
        return if (balanceState != null) balanceState else BalanceState(customerId, ZERO, 0)
    }

    override fun close() {}
}
