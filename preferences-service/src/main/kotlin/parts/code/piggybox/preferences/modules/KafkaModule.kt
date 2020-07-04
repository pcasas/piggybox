package parts.code.piggybox.preferences.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
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
import org.apache.kafka.streams.kstream.Predicate
import org.apache.kafka.streams.kstream.TransformerSupplier
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.Stores
import org.slf4j.LoggerFactory
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.preferences.config.KafkaConfig
import parts.code.piggybox.preferences.streams.suppliers.RecordProcessor
import parts.code.piggybox.preferences.streams.suppliers.RecordTransformer
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.AddFundsDenied
import parts.code.piggybox.schemas.ChangeCountryDenied
import parts.code.piggybox.schemas.CountryChanged
import parts.code.piggybox.schemas.CreatePreferencesDenied
import parts.code.piggybox.schemas.PreferencesCreated
import parts.code.piggybox.schemas.PreferencesState
import parts.code.piggybox.schemas.WithdrawFundsCommand
import parts.code.piggybox.schemas.WithdrawFundsDenied

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
        val builder = StreamsBuilder().addPreferencesStateStore(config)
        addPreferencesAuthorizationStream(builder, config, transformer)
        addPreferencesStream(builder, config, processor)

        return KafkaStreams(builder.build(), properties(config))
    }

    private fun addPreferencesAuthorizationStream(
        builder: StreamsBuilder,
        config: KafkaConfig,
        transformer: RecordTransformer
    ) {
        val (preferences, preferencesAuthorization, balanceAuthorization) = builder
            .stream<String, SpecificRecord>(config.topics.preferencesAuthorization)
            .transform(TransformerSupplier { transformer }, config.stateStores.preferences)
            .branch(
                Predicate { _, v -> v is PreferencesCreated || v is CountryChanged },
                Predicate { _, v -> v is CreatePreferencesDenied || v is AddFundsDenied || v is WithdrawFundsDenied || v is ChangeCountryDenied },
                Predicate { _, v -> v is AddFundsCommand || v is WithdrawFundsCommand }
            )

        preferences
            .peek { _, record -> log(record, config.topics.preferences) }
            .to(config.topics.preferences)

        preferencesAuthorization
            .peek { _, record -> log(record, config.topics.preferencesAuthorization) }
            .to(config.topics.preferencesAuthorization)

        balanceAuthorization
            .peek { _, record -> log(record, config.topics.balanceAuthorization) }
            .to(config.topics.balanceAuthorization)
    }

    private fun addPreferencesStream(builder: StreamsBuilder, config: KafkaConfig, processor: RecordProcessor) {
        builder
            .stream<String, SpecificRecord>(config.topics.preferences)
            .process(ProcessorSupplier { processor }, config.stateStores.preferences)
    }

    private fun properties(config: KafkaConfig) =
        Properties().apply {
            put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServersConfig)
            put(StreamsConfig.APPLICATION_ID_CONFIG, PreferencesServiceApplication::class.java.simpleName)
            put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java)
            put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde::class.java)
            put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, config.schemaRegistryUrlConfig)
            put("value.subject.name.strategy", TopicRecordNameStrategy::class.java)
        }

    private fun log(record: SpecificRecord, topic: String) {
        logger.info("Sent ${record.schema.name} to topic: $topic\n\trecord: $record")
    }
}

fun StreamsBuilder.addPreferencesStateStore(config: KafkaConfig): StreamsBuilder =
    addStateStore(
        Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(config.stateStores.preferences),
            Serdes.String(),
            SpecificAvroSerde<PreferencesState>().apply {
                configure(mapOf(SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrlConfig), false)
            }
        )
    )
