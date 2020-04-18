package parts.code.piggybox.preferences.streams.suppliers

import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import parts.code.piggybox.preferences.config.KafkaConfig
import parts.code.piggybox.preferences.services.BalanceService
import parts.code.piggybox.preferences.services.PreferencesService
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.BuyGameCommand
import parts.code.piggybox.schemas.ChangeCountryCommand
import parts.code.piggybox.schemas.CreatePreferencesCommand
import parts.code.piggybox.schemas.PreferencesState
import parts.code.piggybox.schemas.UnknownRecord

class RecordTransformer @Inject constructor(
    private val config: KafkaConfig,
    private val preferencesService: PreferencesService,
    private val balanceService: BalanceService
) : Transformer<String, SpecificRecord, KeyValue<String, SpecificRecord>?> {

    private val logger = LoggerFactory.getLogger(RecordTransformer::class.java)
    private lateinit var state: KeyValueStore<String, PreferencesState>

    override fun init(context: ProcessorContext) {
        @Suppress("UNCHECKED_CAST")
        state = context.getStateStore(config.stateStores.preferences) as KeyValueStore<String, PreferencesState>
    }

    override fun transform(key: String, record: SpecificRecord): KeyValue<String, SpecificRecord>? {
        val result = when (record) {
            is CreatePreferencesCommand -> createPreferences(record)
            is ChangeCountryCommand -> changeCountry(record)
            is AddFundsCommand -> addFunds(record)
            is BuyGameCommand -> buyGame(record)
            else -> unknown()
        }

        logger.info(
            "Transformed ${record.schema.name} to ${result.value.schema.name}" +
                    "\n\trecord to transform: $record" +
                    "\n\ttransformed to: $result"
        )

        return result
    }

    private fun createPreferences(record: CreatePreferencesCommand): KeyValue<String, SpecificRecord> {
        val preferencesState = state.get(record.customerId)

        return if (preferencesState == null) {
            preferencesService.createPreferences(record)
        } else {
            preferencesService.denyCreatePreferences(record)
        }
    }

    private fun changeCountry(record: ChangeCountryCommand): KeyValue<String, SpecificRecord> {
        val preferencesState = state.get(record.customerId)

        return if (preferencesState != null) {
            preferencesService.changeCountry(record)
        } else {
            preferencesService.denyChangeCountry(record)
        }
    }

    private fun addFunds(record: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val preferencesState = state.get(record.customerId)

        return if (preferencesState == null || preferencesState.currency != record.currency) {
            balanceService.denyAddFunds(record)
        } else {
            KeyValue(record.customerId, record as SpecificRecord)
        }
    }

    private fun buyGame(record: BuyGameCommand): KeyValue<String, SpecificRecord> {
        val preferencesState = state.get(record.customerId)

        return if (preferencesState == null || preferencesState.currency != record.currency) {
            balanceService.denyBuyGame(record)
        } else {
            KeyValue(record.customerId, record as SpecificRecord)
        }
    }

    private fun unknown(): KeyValue<String, SpecificRecord> = KeyValue("", UnknownRecord())

    override fun close() {}
}
