package parts.code.piggybox.command.application.config

import com.google.inject.AbstractModule
import com.google.inject.Provides
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
import java.util.Properties
import javax.inject.Singleton
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import parts.code.piggybox.command.application.CommandServiceApplication
import parts.code.piggybox.command.application.handlers.AddFundsHandler
import parts.code.piggybox.command.application.handlers.CreatePreferencesHandler

class ApplicationModule : AbstractModule() {

    override fun configure() {
        bind(AddFundsHandler::class.java)
        bind(CreatePreferencesHandler::class.java)
    }

    @Provides
    @Singleton
    fun provideKafkaProducer(kafkaConfig: KafkaConfig): KafkaProducer<String, SpecificRecord> {
        val properties = Properties().apply {
            put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.bootstrapServersConfig)
            put(CommonClientConfigs.CLIENT_ID_CONFIG, CommandServiceApplication::class.java.simpleName)
            put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.schemaRegistryUrlConfig)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java)
            put("value.subject.name.strategy", TopicRecordNameStrategy::class.java)
        }

        return KafkaProducer(properties)
    }
}
