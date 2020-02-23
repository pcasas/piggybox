package parts.code.piggybox.preferences.streams.transformers

import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.slf4j.LoggerFactory
import parts.code.piggybox.preferences.services.PreferencesService
import parts.code.piggybox.schemas.commands.CreatePreferencesCommand

class RecordTransformer @Inject constructor(
    private val preferencesService: PreferencesService
) : Transformer<String, SpecificRecord, KeyValue<String, SpecificRecord>?> {

    private val logger = LoggerFactory.getLogger(RecordTransformer::class.java)

    override fun init(context: ProcessorContext?) {}

    override fun transform(key: String?, record: SpecificRecord?): KeyValue<String, SpecificRecord>? {
        logger.info("Transforming record...")

        val result = when (record) {
            is CreatePreferencesCommand -> preferencesService.createPreferences(record)
            else -> null
        }

        logger.info("Record $record transformed to $result")

        return result
    }

    override fun close() {}
}
