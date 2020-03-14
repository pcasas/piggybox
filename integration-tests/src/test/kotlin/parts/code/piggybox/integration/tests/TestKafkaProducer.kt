package parts.code.piggybox.integration.tests

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
import java.util.Properties
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer

class TestKafkaProducer private constructor(properties: Properties) :
    KafkaProducer<String, SpecificRecord>(properties) {

    companion object {
        fun create(): KafkaProducer<String, SpecificRecord> {
            return TestKafkaProducer(
                Properties().apply {
                    put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                    put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
                    put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
                    put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java)
                    put("value.subject.name.strategy", TopicRecordNameStrategy::class.java)
                })
        }
    }
}
