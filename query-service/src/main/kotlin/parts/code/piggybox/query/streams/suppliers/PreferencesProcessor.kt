package parts.code.piggybox.query.streams.suppliers

import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import parts.code.piggybox.query.config.KafkaConfig
import parts.code.piggybox.schemas.CountryChanged
import parts.code.piggybox.schemas.PreferencesCreated
import parts.code.piggybox.schemas.PreferencesState

class PreferencesProcessor @Inject constructor(
    private val config: KafkaConfig
) : Processor<String, SpecificRecord> {

    private val logger = LoggerFactory.getLogger(PreferencesProcessor::class.java)
    private lateinit var stateStore: KeyValueStore<String, PreferencesState>

    @Suppress("UNCHECKED_CAST")
    override fun init(context: ProcessorContext) {
        stateStore =
            context.getStateStore(config.stateStores.preferencesReadModel) as KeyValueStore<String, PreferencesState>
    }

    override fun process(key: String, record: SpecificRecord) {
        when (record) {
            is PreferencesCreated -> preferencesCreated(record)
            is CountryChanged -> countryChanged(record)
        }

        logger.info("Processed ${record.schema.name}\n\trecord: $record")
    }

    private fun preferencesCreated(record: PreferencesCreated) {
        stateStore.put(
            record.customerId,
            PreferencesState(record.customerId, record.currency, record.country)
        )
    }

    private fun countryChanged(record: CountryChanged) {
        val preferencesState = stateStore.get(record.customerId)

        stateStore.put(
            record.customerId,
            PreferencesState(record.customerId, preferencesState.currency, record.country)
        )
    }

    override fun close() {}
}
