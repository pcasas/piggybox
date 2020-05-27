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
import parts.code.piggybox.schemas.CountryChanged
import parts.code.piggybox.schemas.FundsAdded
import parts.code.piggybox.schemas.GameBought
import parts.code.piggybox.schemas.PreferencesCreated
import parts.code.piggybox.schemas.PreferencesState
import parts.code.piggybox.schemas.toMoney
import parts.code.piggybox.schemas.toMoneyIDL

class RecordProcessor @Inject constructor(
    private val config: KafkaConfig
) : Processor<String, SpecificRecord> {

    private val logger = LoggerFactory.getLogger(RecordProcessor::class.java)
    private lateinit var preferencesStateStore: KeyValueStore<String, PreferencesState>
    private lateinit var balanceStateStore: KeyValueStore<String, BalanceState>

    @Suppress("UNCHECKED_CAST")
    override fun init(context: ProcessorContext) {
        preferencesStateStore =
            context.getStateStore(config.stateStores.preferencesReadModel) as KeyValueStore<String, PreferencesState>
        balanceStateStore = context.getStateStore(config.stateStores.balanceReadModel) as KeyValueStore<String, BalanceState>
    }

    override fun process(key: String, record: SpecificRecord) {
        when (record) {
            is PreferencesCreated -> preferencesCreated(record)
            is CountryChanged -> countryChanged(record)
            is FundsAdded -> fundsAdded(record)
            is GameBought -> gameBought(record)
        }

        logger.info("Processed ${record.schema.name}\n\trecord: $record")
    }

    private fun preferencesCreated(record: PreferencesCreated) {
        preferencesStateStore.put(
            record.customerId,
            PreferencesState(record.customerId, record.currency, record.country)
        )
    }

    private fun countryChanged(record: CountryChanged) {
        val preferencesState = preferencesStateStore.get(record.customerId)

        preferencesStateStore.put(
            record.customerId,
            PreferencesState(record.customerId, preferencesState.currency, record.country)
        )
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

    private fun currentBalance(customerId: String, currency: Currency): Money {
        val balanceState = balanceStateStore.get(customerId)
        return if (balanceState != null) balanceState.moneyIDL.toMoney() else Money.zero(currency)
    }

    private fun saveBalance(customerId: String, balance: Money) {
        balanceStateStore.put(customerId, BalanceState(customerId, balance.toMoneyIDL()))
    }

    override fun close() {}
}
