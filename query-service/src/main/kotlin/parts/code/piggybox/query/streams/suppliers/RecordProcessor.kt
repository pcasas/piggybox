package parts.code.piggybox.query.streams.suppliers

import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import parts.code.money.Currency
import parts.code.money.Money
import parts.code.piggybox.query.config.KafkaConfig
import parts.code.piggybox.schemas.BalanceState
import parts.code.piggybox.schemas.FundsAdded
import parts.code.piggybox.schemas.GameBought
import parts.code.piggybox.schemas.toMoney
import parts.code.piggybox.schemas.toMoneyIDL

class RecordProcessor @Inject constructor(
    private val config: KafkaConfig
) : Processor<String, SpecificRecord> {

    private val logger = LoggerFactory.getLogger(RecordProcessor::class.java)
    private lateinit var state: KeyValueStore<String, BalanceState>

    override fun init(context: ProcessorContext) {
        @Suppress("UNCHECKED_CAST")
        state = context.getStateStore(config.stateStores.balanceReadModel) as KeyValueStore<String, BalanceState>
    }

    override fun process(key: String, record: SpecificRecord) {
        when (record) {
            is FundsAdded -> fundsAdded(record)
            is GameBought -> gameBought(record)
        }

        logger.info("Processed ${record.schema.name}\n\trecord: $record")
    }

    private fun fundsAdded(record: FundsAdded) {
        val money = record.moneyIDL.toMoney()
        val newBalance = currentBalance(record.customerId, money.currency) + money
        saveBalance(record.customerId, newBalance)
    }

    private fun gameBought(record: GameBought) {
        val money = record.moneyIDL.toMoney()
        val newBalance = currentBalance(record.customerId, money.currency) - money
        saveBalance(record.customerId, newBalance)
    }

    override fun close() {}

    private fun currentBalance(customerId: String, currency: Currency): Money {
        val balanceState = state.get(customerId)

        return if (balanceState != null) {
            balanceState.moneyIDL.toMoney()
        } else {
            Money.zero(currency)
        }
    }

    private fun saveBalance(customerId: String, balance: Money) {
        state.put(customerId, BalanceState(customerId, balance.toMoneyIDL()))
    }
}
