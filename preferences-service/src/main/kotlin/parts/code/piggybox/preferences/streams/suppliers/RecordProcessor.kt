package parts.code.piggybox.preferences.streams.suppliers

import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import parts.code.piggybox.preferences.config.KafkaConfig
import parts.code.piggybox.schemas.events.CountryChanged
import parts.code.piggybox.schemas.events.PreferencesCreated
import parts.code.piggybox.schemas.state.PreferencesState

class RecordProcessor @Inject constructor(
    private val config: KafkaConfig
) : Processor<String, SpecificRecord> {

    private val logger = LoggerFactory.getLogger(RecordProcessor::class.java)
    private lateinit var state: KeyValueStore<String, PreferencesState>

    override fun init(context: ProcessorContext) {
        @Suppress("UNCHECKED_CAST")
        state = context.getStateStore(config.stateStores.preferences) as KeyValueStore<String, PreferencesState>
    }

    override fun process(key: String, record: SpecificRecord) {
        when (record) {
            is PreferencesCreated -> preferencesCreated(record)
            is CountryChanged -> countryChanged(record)
            else -> Unit
        }

        logger.info("Processed ${record.schema.name}\n\trecord: $record")
    }

    private fun preferencesCreated(record: PreferencesCreated) {
        state.put(
            record.customerId,
            PreferencesState(record.customerId, record.currency, record.country)
        )
    }

    private fun countryChanged(record: CountryChanged) {
        val preferencesState = state.get(record.customerId)

        state.put(
            record.customerId,
            PreferencesState(record.customerId, preferencesState.currency, record.country)
        )
    }

    override fun close() {}
}
