package parts.code.piggybox.integration.tests

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import java.util.Properties
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

class TestKafkaConsumer private constructor(properties: Properties) :
    KafkaConsumer<String, SpecificRecord>(properties) {

    companion object {
        fun of(topic: String): KafkaConsumer<String, SpecificRecord> {
            val consumer = TestKafkaConsumer(
                Properties().apply {
                    put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                    put(CommonClientConfigs.GROUP_ID_CONFIG, "${UUID.randomUUID()}")
                    put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
                    put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
                    put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer::class.java)
                    put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
                    put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                })
            consumer.subscribe(listOf(topic))

            return consumer
        }
    }
}
