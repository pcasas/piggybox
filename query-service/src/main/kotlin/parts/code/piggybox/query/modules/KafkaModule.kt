package parts.code.piggybox.query.modules

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
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.Stores
import parts.code.piggybox.query.QueryServiceApplication
import parts.code.piggybox.query.config.KafkaConfig
import parts.code.piggybox.query.streams.suppliers.BalanceProcessor
import parts.code.piggybox.query.streams.suppliers.PreferencesProcessor
import parts.code.piggybox.schemas.BalanceState
import parts.code.piggybox.schemas.PreferencesState

class KafkaModule : AbstractModule() {

    override fun configure() {
        bind(PreferencesProcessor::class.java)
        bind(BalanceProcessor::class.java)
    }

    @Provides
    @Singleton
    fun provideKafkaStreams(
        config: KafkaConfig
    ): KafkaStreams {
        val builder = StreamsBuilder()

        builder
            .addPreferencesStateStore(config)
            .stream<String, SpecificRecord>(config.topics.preferences)
            .process(ProcessorSupplier { PreferencesProcessor(config) }, config.stateStores.preferencesReadModel)

        builder
            .addBalanceStateStore(config)
            .stream<String, SpecificRecord>(config.topics.balance)
            .process(ProcessorSupplier { BalanceProcessor(config) }, config.stateStores.balanceReadModel)

        return KafkaStreams(builder.build(), properties(config))
    }

    private fun properties(config: KafkaConfig) =
        Properties().apply {
            put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServersConfig)
            put(StreamsConfig.APPLICATION_ID_CONFIG, QueryServiceApplication::class.java.simpleName)
            put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java)
            put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde::class.java)
            put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, config.schemaRegistryUrlConfig)
            put("value.subject.name.strategy", TopicRecordNameStrategy::class.java)
        }
}

fun StreamsBuilder.addPreferencesStateStore(config: KafkaConfig): StreamsBuilder =
    addStateStore(
        Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(config.stateStores.preferencesReadModel),
            Serdes.String(),
            SpecificAvroSerde<PreferencesState>().apply {
                configure(mapOf(SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrlConfig), false)
            }
        )
    )

fun StreamsBuilder.addBalanceStateStore(config: KafkaConfig): StreamsBuilder =
    addStateStore(
        Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(config.stateStores.balanceReadModel),
            Serdes.String(),
            SpecificAvroSerde<BalanceState>().apply {
                configure(mapOf(SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrlConfig), false)
            }
        )
    )
