package parts.code.piggybox.balance.streams.suppliers

import java.math.BigDecimal
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import parts.code.piggybox.balance.config.KafkaConfig
import parts.code.piggybox.balance.services.BalanceService
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.BalanceState
import parts.code.piggybox.schemas.UnknownRecord
import parts.code.piggybox.schemas.WithdrawFundsCommand

class RecordTransformer @Inject constructor(
    private val config: KafkaConfig,
    private val balanceService: BalanceService
) : Transformer<String, SpecificRecord, KeyValue<String, SpecificRecord>?> {

    private val logger = LoggerFactory.getLogger(RecordTransformer::class.java)
    private lateinit var state: KeyValueStore<String, BalanceState>

    override fun init(context: ProcessorContext) {
        @Suppress("UNCHECKED_CAST")
        state = context.getStateStore(config.stateStores.balance) as KeyValueStore<String, BalanceState>
    }

    override fun transform(key: String, record: SpecificRecord): KeyValue<String, SpecificRecord>? {
        val result: KeyValue<String, SpecificRecord> = when (record) {
            is AddFundsCommand -> addFunds(record)
            is WithdrawFundsCommand -> withdrawFunds(record)
            else -> unknown()
        }

        logger.info(
            "Transformed ${record.schema.name} to ${result.value.schema.name}" +
                    "\n\trecord to transform: $record" +
                    "\n\ttransformed to: $result"
        )

        return result
    }

    private fun addFunds(record: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val newBalance = currentBalance(record.customerId) + record.amount

        return if (newBalance > BigDecimal.valueOf(2000)) {
            balanceService.denyAddFunds(record)
        } else {
            balanceService.addFunds(record)
        }
    }

    private fun withdrawFunds(record: WithdrawFundsCommand): KeyValue<String, SpecificRecord> {
        val newBalance = currentBalance(record.customerId) - record.amount

        return if (newBalance < BigDecimal.ZERO) {
            balanceService.denyWithdrawFunds(record)
        } else {
            balanceService.withdrawFunds(record)
        }
    }

    private fun unknown(): KeyValue<String, SpecificRecord> = KeyValue("", UnknownRecord())

    override fun close() {}

    private fun currentBalance(customerId: String): BigDecimal {
        val balanceState = state.get(customerId)
        return if (balanceState != null) balanceState.amount else BigDecimal.ZERO
    }
}
