package parts.code.piggybox.integration.tests.features

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.math.BigDecimal
import java.time.Duration
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parts.code.piggybox.command.application.CommandServiceApplication
import parts.code.piggybox.integration.tests.TestKafkaConsumer
import parts.code.piggybox.integration.tests.lastRecord
import parts.code.piggybox.schemas.AddFundsCommand
import ratpack.test.MainClassApplicationUnderTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddFundsFeature {

    private val aut = object : MainClassApplicationUnderTest(CommandServiceApplication::class.java) {}
    private val consumer = TestKafkaConsumer.of(AddFundsFeature::class.simpleName)

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
    fun `should add funds to a customer`() {
        val customerId = UUID.randomUUID().toString()

        val response = aut.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "amount": 1.00}""")
        }.post("/api/balance.addFunds")
        response.status.code shouldBe 202

        val event = consumer.lastRecord(customerId).value() as AddFundsCommand

        UUID.fromString(event.id)
        event.occurredOn shouldNotBe null
        event.customerId shouldBe customerId
        event.amount shouldBe BigDecimal.ONE.setScale(2)
    }
}
