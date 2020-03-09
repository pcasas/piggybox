package parts.code.piggybox.integration.tests.features

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.time.Duration
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parts.code.piggybox.command.CommandServiceApplication
import parts.code.piggybox.integration.tests.TestKafkaConsumer
import parts.code.piggybox.integration.tests.Topics
import parts.code.piggybox.integration.tests.lastRecord
import parts.code.piggybox.kafka.init.KafkaInitServiceApplication
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.schemas.events.PreferencesCreated
import parts.code.piggybox.schemas.events.PreferencesDenied
import ratpack.test.MainClassApplicationUnderTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class CreatePreferencesFeature {

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
    fun `should create preferences`() {
        val consumerPreferences = TestKafkaConsumer.of(Topics.preferences)

        val customerId = UUID.randomUUID().toString()

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","currency":"EUR"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        val event = consumerPreferences.lastRecord(customerId).value() as PreferencesCreated

        UUID.fromString(event.id)
        event.occurredOn shouldNotBe null
        event.customerId shouldBe customerId
        event.currency shouldBe "EUR"
    }

    @Test
    fun `should deny create preferences if preferences already exist`() {
        val consumerPreferencesAuthorization = TestKafkaConsumer.of(Topics.preferencesAuthorization)
        val consumerPreferences = TestKafkaConsumer.of(Topics.preferences)

        val customerId = UUID.randomUUID().toString()

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","currency":"EUR"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        val preferencesCreated = consumerPreferences.lastRecord(customerId).value() as PreferencesCreated

        UUID.fromString(preferencesCreated.id)
        preferencesCreated.occurredOn shouldNotBe null
        preferencesCreated.customerId shouldBe customerId
        preferencesCreated.currency shouldBe "EUR"

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","currency":"USD"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        val preferencesDenied = consumerPreferencesAuthorization.lastRecord(customerId).value() as PreferencesDenied

        UUID.fromString(preferencesDenied.id)
        preferencesDenied.occurredOn shouldNotBe null
        preferencesDenied.customerId shouldBe customerId
        preferencesDenied.currency shouldBe "USD"
    }
}
