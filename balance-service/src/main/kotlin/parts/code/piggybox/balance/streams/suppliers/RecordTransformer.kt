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
import parts.code.piggybox.schemas.commands.AddFundsCommand
import parts.code.piggybox.schemas.commands.BuyGameCommand
import parts.code.piggybox.schemas.state.BalanceState
import parts.code.piggybox.schemas.test.UnknownRecord

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
            is AddFundsCommand -> transform(record)
            is BuyGameCommand -> transform(record)
            else -> unknown()
        }

        logger.info(
            "Transformed ${record.schema.name} to ${result.value.schema.name}" +
                    "\n\trecord to transform: $record" +
                    "\n\ttransformed to: $result"
        )

        return result
    }

    private fun transform(record: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val newBalance = getCurrentBalance(record.customerId) + record.amount

        return if (newBalance > BigDecimal.valueOf(2000)) {
            balanceService.denyAddFunds(record)
        } else {
            balanceService.addFunds(record)
        }
    }

    private fun transform(record: BuyGameCommand): KeyValue<String, SpecificRecord> {
        val newBalance = getCurrentBalance(record.customerId) - record.amount

        return if (newBalance < BigDecimal.ZERO) {
            balanceService.denyBuyGame(record)
        } else {
            balanceService.buyGame(record)
        }
    }

    private fun unknown(): KeyValue<String, SpecificRecord> = KeyValue("", UnknownRecord())

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
