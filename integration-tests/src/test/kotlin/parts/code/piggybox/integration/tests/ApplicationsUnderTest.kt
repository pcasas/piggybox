package parts.code.piggybox.integration.tests

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
import java.util.Properties
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import parts.code.piggybox.balance.BalanceServiceApplication
import parts.code.piggybox.command.CommandServiceApplication
import parts.code.piggybox.kafka.init.KafkaInitServiceApplication
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.query.QueryServiceApplication
import ratpack.test.MainClassApplicationUnderTest

class ApplicationsUnderTest {
    val kafkaInitService = MainClassApplicationUnderTest(KafkaInitServiceApplication::class.java)
    val commandService = MainClassApplicationUnderTest(CommandServiceApplication::class.java)
    val preferencesService = MainClassApplicationUnderTest(PreferencesServiceApplication::class.java)
    val balanceService = MainClassApplicationUnderTest(BalanceServiceApplication::class.java)
    val queryService = MainClassApplicationUnderTest(QueryServiceApplication::class.java)
    val producer = producer()

    fun started() = kafkaInitService.address != null &&
            commandService.address != null &&
            preferencesService.address != null &&
            balanceService.address != null &&
            queryService.address != null

    fun close() {
        kafkaInitService.close()
        commandService.close()
        preferencesService.close()
        balanceService.close()
        queryService.close()
    }

    private fun producer(): KafkaProducer<String, SpecificRecord> {
        return KafkaProducer(
            Properties().apply {
                put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java)
                put("value.subject.name.strategy", TopicRecordNameStrategy::class.java)
            })
    }

    fun consumer(topic: String): KafkaConsumer<String, SpecificRecord> {
        val consumer: KafkaConsumer<String, SpecificRecord> = KafkaConsumer(
            Properties().apply {
                put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                put(CommonClientConfigs.GROUP_ID_CONFIG, "${UUID.randomUUID()}")
                put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
                put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
                put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer::class.java)
                put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
                put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1)
            })
        consumer.subscribe(listOf(topic))

        return consumer
    }
}
