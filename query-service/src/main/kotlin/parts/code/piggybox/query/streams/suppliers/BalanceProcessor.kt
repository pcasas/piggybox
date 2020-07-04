package parts.code.piggybox.query.streams.suppliers

import java.math.BigDecimal
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

class BalanceProcessor @Inject constructor(
    private val config: KafkaConfig
) : Processor<String, SpecificRecord> {

    private val logger = LoggerFactory.getLogger(BalanceProcessor::class.java)
    private lateinit var stateStore: KeyValueStore<String, BalanceState>

    @Suppress("UNCHECKED_CAST")
    override fun init(context: ProcessorContext) {
        stateStore = context.getStateStore(config.stateStores.balanceReadModel) as KeyValueStore<String, BalanceState>
    }

    override fun process(key: String, record: SpecificRecord) {
        when (record) {
            is FundsAdded -> fundsAdded(record)
            is FundsWithdrawn -> fundsWithdrawn(record)
        }

        logger.info("Processed ${record.schema.name}\n\trecord: $record")
    }

    private fun fundsAdded(record: FundsAdded) {
        val newBalance = currentBalance(record.customerId) + record.amount
        saveBalance(record.customerId, newBalance)
    }

    private fun fundsWithdrawn(record: FundsWithdrawn) {
        val newBalance = currentBalance(record.customerId) - record.amount
        saveBalance(record.customerId, newBalance)
    }

    private fun currentBalance(customerId: String): BigDecimal {
        val balanceState = stateStore.get(customerId)
        return if (balanceState != null) balanceState.amount else BigDecimal.ZERO
    }

    private fun saveBalance(customerId: String, balance: BigDecimal) {
        stateStore.put(customerId, BalanceState(customerId, balance))
    }

    override fun close() {}
}
