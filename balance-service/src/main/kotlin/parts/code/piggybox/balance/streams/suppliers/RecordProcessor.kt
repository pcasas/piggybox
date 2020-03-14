package parts.code.piggybox.balance.streams.suppliers

import java.math.BigDecimal
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import parts.code.piggybox.balance.config.KafkaConfig
import parts.code.piggybox.schemas.commands.GameBought
import parts.code.piggybox.schemas.events.FundsAdded
import parts.code.piggybox.schemas.state.BalanceState

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
            is FundsAdded -> {
                val newBalance = getCurrentBalance(record.customerId) + record.amount
                state.put(record.customerId, BalanceState(record.customerId, newBalance, record.currency))
            }
            is GameBought -> {
                val newBalance = getCurrentBalance(record.customerId) - record.amount
                state.put(record.customerId, BalanceState(record.customerId, newBalance, record.currency))
            }
            else -> Unit
        }

        logger.info("Processed ${record.schema.name}\n\trecord: $record")
    }

    override fun close() {}

    private fun getCurrentBalance(customerId: String): BigDecimal {
        val balanceState = state.get(customerId)

        return if (balanceState != null) {
            balanceState.amount
        } else {
            BigDecimal.ZERO
        }
    }
}
