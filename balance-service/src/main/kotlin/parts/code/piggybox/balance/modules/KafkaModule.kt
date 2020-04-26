package parts.code.piggybox.balance.modules

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
import parts.code.piggybox.balance.BalanceServiceApplication
import parts.code.piggybox.balance.config.KafkaConfig
import parts.code.piggybox.balance.streams.suppliers.RecordProcessor
import parts.code.piggybox.balance.streams.suppliers.RecordTransformer
import parts.code.piggybox.schemas.AddFundsDenied
import parts.code.piggybox.schemas.BalanceState
import parts.code.piggybox.schemas.BuyGameDenied
import parts.code.piggybox.schemas.FundsAdded
import parts.code.piggybox.schemas.GameBought

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
        val builder = StreamsBuilder().addBalanceStateStore(config)
        addBalanceAuthorizationStream(builder, config, transformer)
        addBalanceStream(builder, config, processor)

        return KafkaStreams(builder.build(), properties(config))
    }

    private fun addBalanceAuthorizationStream(
        builder: StreamsBuilder,
        config: KafkaConfig,
        transformer: RecordTransformer
    ) {
        val (balance, balanceAuthorization) = builder
            .stream<String, SpecificRecord>(config.topics.balanceAuthorization)
            .transform(TransformerSupplier { transformer }, config.stateStores.balance)
            .branch(
                Predicate { _, v -> v is FundsAdded || v is GameBought },
                Predicate { _, v -> v is AddFundsDenied || v is BuyGameDenied }
            )

        balance
            .peek { _, record -> log(record, config.topics.balance) }
            .to(config.topics.balance)

        balanceAuthorization
            .peek { _, record -> log(record, config.topics.balanceAuthorization) }
            .to(config.topics.balanceAuthorization)
    }

    private fun addBalanceStream(builder: StreamsBuilder, config: KafkaConfig, processor: RecordProcessor) {
        builder
            .stream<String, SpecificRecord>(config.topics.balance)
            .process(ProcessorSupplier { processor }, config.stateStores.balance)
    }

    private fun properties(config: KafkaConfig) =
        Properties().apply {
            put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServersConfig)
            put(StreamsConfig.APPLICATION_ID_CONFIG, BalanceServiceApplication::class.java.simpleName)
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

fun StreamsBuilder.addBalanceStateStore(config: KafkaConfig): StreamsBuilder =
    addStateStore(
        Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(config.stateStores.balance),
            Serdes.String(),
            SpecificAvroSerde<BalanceState>().apply {
                configure(mapOf(SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrlConfig), false)
            }
        )
    )
