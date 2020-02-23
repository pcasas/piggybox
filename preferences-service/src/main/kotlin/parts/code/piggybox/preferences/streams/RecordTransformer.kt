package parts.code.piggybox.preferences.streams

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.slf4j.LoggerFactory
import parts.code.piggybox.schemas.CreatePreferencesCommand
import parts.code.piggybox.schemas.PreferencesCreated

class RecordTransformer : Transformer<String, SpecificRecord, KeyValue<String, SpecificRecord>?> {

    private val logger = LoggerFactory.getLogger(RecordTransformer::class.java)

    override fun init(context: ProcessorContext?) {
    }

    override fun transform(key: String?, record: SpecificRecord?): KeyValue<String, SpecificRecord>? {
        logger.info("Transforming record...")

        return when (record) {
            is CreatePreferencesCommand -> transform(record)
            else -> null
        }
    }

    private fun transform(command: CreatePreferencesCommand): KeyValue<String, SpecificRecord>? {
        val event = PreferencesCreated(
            command.id,
            command.occurredOn,
            command.customerId,
            command.currency
        )

        logger.info("Record mapped: $event")

        return KeyValue(command.customerId, event)
    }

    override fun close() {
    }
}
