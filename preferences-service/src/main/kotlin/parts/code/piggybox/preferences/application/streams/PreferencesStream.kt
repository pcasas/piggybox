package parts.code.piggybox.preferences.application.streams

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import java.util.Properties
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.TransformerSupplier
import parts.code.piggybox.preferences.application.PreferencesServiceApplication
import parts.code.piggybox.preferences.application.config.KafkaConfig
import ratpack.service.Service
import ratpack.service.StartEvent
import ratpack.service.StopEvent

class PreferencesStream @Inject constructor(
    private val config: KafkaConfig
) : Service {

    private val streams = init()

    private fun init(): KafkaStreams {
        val supplier = TransformerSupplier { RecordTransformer() }

        val builder = StreamsBuilder()

        builder
            .stream<String, SpecificRecord>(config.topics.preferencesAuthorization)
            .transform(supplier)
            .to(config.topics.preferences)

        val properties = Properties().apply {
            put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServersConfig)
            put(StreamsConfig.APPLICATION_ID_CONFIG, PreferencesServiceApplication::class.java.simpleName)
            put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java)
            put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde::class.java)
            put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, config.schemaRegistryUrlConfig)
            put("value.subject.name.strategy", TopicRecordNameStrategy::class.java)
        }

        return KafkaStreams(builder.build(), properties)
    }

    override fun onStart(event: StartEvent?) {
        streams.start()
    }

    override fun onStop(event: StopEvent?) {
        streams.close()
    }
}
