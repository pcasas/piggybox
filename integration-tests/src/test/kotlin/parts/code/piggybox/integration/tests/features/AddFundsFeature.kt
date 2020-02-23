package parts.code.piggybox.integration.tests.features

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.math.BigDecimal
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
import parts.code.piggybox.integration.tests.lastRecord
import parts.code.piggybox.kafka.init.KafkaInitServiceApplication
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.schemas.commands.AddFundsCommand
import ratpack.test.MainClassApplicationUnderTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class AddFundsFeature {

    val kafkaInitService = object : MainClassApplicationUnderTest(KafkaInitServiceApplication::class.java) {}
    val commandService = object : MainClassApplicationUnderTest(CommandServiceApplication::class.java) {}
    val preferencesService = object : MainClassApplicationUnderTest(PreferencesServiceApplication::class.java) {}
    val consumerPreferencesAuthorization = TestKafkaConsumer.of(AddFundsFeature::class.simpleName)

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

        consumerPreferencesAuthorization.subscribe(listOf("preferences-authorization"))
        consumerPreferencesAuthorization.poll(Duration.ofMillis(1)).count()
    }

    @AfterAll
    fun tearDown() {
        consumerPreferencesAuthorization.close()
        commandService.close()
    }

    @Test
    fun `should add funds to a customer`() {
        val customerId = UUID.randomUUID().toString()

        val response = commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "amount": 1.00}""")
        }.post("/api/balance.addFunds")
        response.status.code shouldBe 202

        val command = consumerPreferencesAuthorization.lastRecord(customerId).value() as AddFundsCommand

        UUID.fromString(command.id)
        command.occurredOn shouldNotBe null
        command.customerId shouldBe customerId
        command.amount shouldBe BigDecimal.ONE.setScale(2)
    }
}
