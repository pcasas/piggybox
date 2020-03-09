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
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Predicate
import org.apache.kafka.streams.kstream.TransformerSupplier
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.Stores
import org.slf4j.LoggerFactory
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.preferences.config.KafkaConfig
import parts.code.piggybox.preferences.streams.suppliers.RecordProcessor
import parts.code.piggybox.preferences.streams.suppliers.RecordTransformer
import parts.code.piggybox.schemas.commands.AddFundsCommand
import parts.code.piggybox.schemas.commands.BuyGameCommand
import parts.code.piggybox.schemas.commands.BuyGameDenied
import parts.code.piggybox.schemas.events.AddFundsDenied
import parts.code.piggybox.schemas.events.PreferencesCreated
import parts.code.piggybox.schemas.events.PreferencesDenied

class KafkaModule : AbstractModule() {

    private val logger = LoggerFactory.getLogger(KafkaModule::class.java)

    override fun configure() {
        bind(RecordTransformer::class.java)
        bind(RecordProcessor::class.java)
    }

    @Provides
    @Singleton
    fun provideKafkaStreams(
        config: KafkaConfig,
        transformer: RecordTransformer,
        processor: RecordProcessor
    ): KafkaStreams {
        val builder = StreamsBuilder()

        val keyValueStoreBuilder: StoreBuilder<out KeyValueStore<String, out Any>> =
            Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(config.stateStores.preferences),
                Serdes.String(),
                null as? Serde<*>
            )

        builder.addStateStore(keyValueStoreBuilder)

        val (preferences, preferencesAuthorization, balanceAuthorization) = builder
            .stream<String, SpecificRecord>(config.topics.preferencesAuthorization)
            .transform(TransformerSupplier { transformer }, config.stateStores.preferences)
            .branch(
                Predicate { _, v -> v is PreferencesCreated },
                Predicate { _, v -> v is PreferencesDenied || v is AddFundsDenied || v is BuyGameDenied },
                Predicate { _, v -> v is AddFundsCommand || v is BuyGameCommand }
            )

        preferences
            .peek { _, record ->
                logger.info("Sent ${record.schema.name} to topic: ${config.topics.preferences}\n\trecord: $record")
            }
            .to(config.topics.preferences)

        preferencesAuthorization
            .peek { _, record ->
                logger.info("Sent ${record.schema.name} to topic: ${config.topics.preferencesAuthorization}\n\trecord: $record")
            }
            .to(config.topics.preferencesAuthorization)

        balanceAuthorization
            .peek { _, record ->
                logger.info("Sent ${record.schema.name} to topic: ${config.topics.balanceAuthorization}\n\trecord: $record")
            }
            .to(config.topics.balanceAuthorization)

        builder
            .stream<String, SpecificRecord>(config.topics.preferences)
            .process(ProcessorSupplier { processor }, config.stateStores.preferences)

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
