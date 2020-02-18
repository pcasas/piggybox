package parts.code.piggybox.command.acceptance

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.kotlintest.shouldBe
import java.math.BigDecimal
import java.time.Duration
import java.util.Properties
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parts.code.piggybox.command.application.CommandServiceApplication
import parts.code.piggybox.schemas.AddFundsRequested
import ratpack.test.MainClassApplicationUnderTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddFundsFeature {

    private val aut = object : MainClassApplicationUnderTest(CommandServiceApplication::class.java) {}

    private val consumer: KafkaConsumer<String, SpecificRecord> = KafkaConsumer(
        Properties().apply {
            put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
            put(CommonClientConfigs.GROUP_ID_CONFIG, "${AddFundsFeature::class.simpleName}-${UUID.randomUUID()}")
            put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer::class.java)
            put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        })

    @BeforeAll
    fun setUp() {
        consumer.subscribe(listOf("funds-authorization-v1"))
        consumer.poll(Duration.ofMillis(1)).count()
    }

    @AfterAll
    fun tearDown() {
        consumer.close()
        aut.close()
    }

    @Test
    fun `should add funds to a customer`() {
        val customerId = UUID.randomUUID().toString()

        val response = aut.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "amount": 1.00}""")
        }.post("/add-funds")
        response.status.code shouldBe 200

        val event = consumer.lastRecord(customerId).value() as AddFundsRequested
        event.customerId shouldBe customerId
        event.amount shouldBe BigDecimal.ONE.setScale(2)
    }
}
