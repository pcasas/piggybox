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
import parts.code.piggybox.query.streams.suppliers.RecordProcessor
import parts.code.piggybox.schemas.BalanceState

class KafkaModule : AbstractModule() {

    override fun configure() {
        bind(RecordProcessor::class.java)
    }

    @Provides
    @Singleton
    fun provideKafkaStreams(
        config: KafkaConfig,
        processor: RecordProcessor
    ): KafkaStreams {
        val builder = StreamsBuilder().addBalanceStateStore(config)
        addBalanceStream(builder, config, processor)

        return KafkaStreams(builder.build(), properties(config))
    }

    private fun addBalanceStream(builder: StreamsBuilder, config: KafkaConfig, processor: RecordProcessor) {
        builder
            .stream<String, SpecificRecord>(config.topics.balance)
            .process(ProcessorSupplier { processor }, config.stateStores.balanceReadModel)
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
