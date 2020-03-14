package parts.code.piggybox.integration.tests.features

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.time.Duration
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parts.code.piggybox.command.CommandServiceApplication
import parts.code.piggybox.integration.tests.TestKafkaConsumer
import parts.code.piggybox.integration.tests.TestKafkaProducer
import parts.code.piggybox.integration.tests.Topics
import parts.code.piggybox.integration.tests.lastRecord
import parts.code.piggybox.kafka.init.KafkaInitServiceApplication
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.schemas.events.ChangeCountryDenied
import parts.code.piggybox.schemas.events.CountryChanged
import parts.code.piggybox.schemas.events.PreferencesCreated
import parts.code.piggybox.schemas.test.UnknownRecord
import ratpack.test.MainClassApplicationUnderTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class ChangeCountryFeature {

    val kafkaInitService = object : MainClassApplicationUnderTest(KafkaInitServiceApplication::class.java) {}
    val commandService = object : MainClassApplicationUnderTest(CommandServiceApplication::class.java) {}
    val preferencesService = object : MainClassApplicationUnderTest(PreferencesServiceApplication::class.java) {}

    @BeforeAll
    fun setUp() {
        runBlocking {
            withTimeout(Duration.ofSeconds(30).toMillis()) {
                while (
                    kafkaInitService.address == null ||
                    commandService.address == null ||
                    preferencesService.address == null
                ) {
                    delay(50)
                }
            }
        }
    }

    @AfterAll
    fun tearDown() {
        kafkaInitService.close()
        commandService.close()
        preferencesService.close()
    }

    @Test
    fun `should change the country`() {
        val consumerPreferences = TestKafkaConsumer.of(Topics.preferences)
        val producerPreferences = TestKafkaProducer.create()

        val customerId = UUID.randomUUID().toString()

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","currency":"EUR","country":"ES"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        consumerPreferences.lastRecord(customerId, PreferencesCreated::class.java).value() as PreferencesCreated

        val record = ProducerRecord(Topics.preferences, customerId, UnknownRecord() as SpecificRecord)
        producerPreferences.send(record).get()

        consumerPreferences.lastRecord(customerId, UnknownRecord::class.java).value() as UnknownRecord

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","country":"UK"}""")
        }.post("/api/preferences.changeCountry").status.code shouldBe 202

        val event = consumerPreferences.lastRecord(customerId, CountryChanged::class.java).value() as CountryChanged

        UUID.fromString(event.id)
        event.occurredOn shouldNotBe null
        event.customerId shouldBe customerId
        event.country shouldBe "UK"
    }

    @Test
    fun `should deny change the country if preferences don't exist`() {
        val consumerPreferencesAuthorization = TestKafkaConsumer.of(Topics.preferencesAuthorization)

        val customerId = UUID.randomUUID().toString()

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","country":"ES"}""")
        }.post("/api/preferences.changeCountry").status.code shouldBe 202

        val event = consumerPreferencesAuthorization.lastRecord(customerId, ChangeCountryDenied::class.java).value() as ChangeCountryDenied

        UUID.fromString(event.id)
        event.occurredOn shouldNotBe null
        event.customerId shouldBe customerId
        event.country shouldBe "ES"
    }
}
