package parts.code.piggybox.preferences.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import java.util.Properties
import javax.inject.Singleton
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.TransformerSupplier
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.preferences.config.KafkaConfig
import parts.code.piggybox.preferences.streams.transformers.RecordTransformer

class KafkaModule : AbstractModule() {

    override fun configure() {
        bind(RecordTransformer::class.java)
    }

    @Provides
    @Singleton
    fun provideKafkaStreams(config: KafkaConfig, transformer: RecordTransformer): KafkaStreams {
        val supplier = TransformerSupplier { transformer }

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
}
