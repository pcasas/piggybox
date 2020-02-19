package parts.code.piggybox.command.acceptance

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.time.Duration
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parts.code.piggybox.command.TestKafkaConsumer
import parts.code.piggybox.command.application.CommandServiceApplication
import parts.code.piggybox.command.lastRecord
import parts.code.piggybox.schemas.CreatePreferencesCommand
import ratpack.test.MainClassApplicationUnderTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreatePreferencesFeature {

    private val aut = object : MainClassApplicationUnderTest(CommandServiceApplication::class.java) {}
    private val consumer = TestKafkaConsumer.of(CreatePreferencesFeature::class.simpleName)

    @BeforeAll
    fun setUp() {
        consumer.subscribe(listOf("preferences-authorization"))
        consumer.poll(Duration.ofMillis(1)).count()
    }

    @AfterAll
    fun tearDown() {
        consumer.close()
        aut.close()
    }

    @Test
    fun `should create preferences`() {
        val customerId = UUID.randomUUID().toString()

        val response = aut.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "currency": "EUR"}""")
        }.post("/api/preferences.create")
        response.status.code shouldBe 202

        val event = consumer.lastRecord(customerId).value() as CreatePreferencesCommand

        UUID.fromString(event.id)
        event.occurredOn shouldNotBe null
        event.customerId shouldBe customerId
        event.currency shouldBe "EUR"
    }
}
