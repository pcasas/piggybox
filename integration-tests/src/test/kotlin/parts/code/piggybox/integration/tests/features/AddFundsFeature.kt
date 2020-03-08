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
import parts.code.piggybox.balance.BalanceServiceApplication
import parts.code.piggybox.command.CommandServiceApplication
import parts.code.piggybox.integration.tests.TestKafkaConsumer
import parts.code.piggybox.integration.tests.Topics
import parts.code.piggybox.integration.tests.lastRecord
import parts.code.piggybox.kafka.init.KafkaInitServiceApplication
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.schemas.events.AddFundsDenied
import parts.code.piggybox.schemas.events.FundsAdded
import parts.code.piggybox.schemas.events.PreferencesCreated
import ratpack.test.MainClassApplicationUnderTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class AddFundsFeature {

    val kafkaInitService = object : MainClassApplicationUnderTest(KafkaInitServiceApplication::class.java) {}
    val commandService = object : MainClassApplicationUnderTest(CommandServiceApplication::class.java) {}
    val preferencesService = object : MainClassApplicationUnderTest(PreferencesServiceApplication::class.java) {}
    val balanceService = object : MainClassApplicationUnderTest(BalanceServiceApplication::class.java) {}

    @BeforeAll
    fun setUp() {
        runBlocking {
            withTimeout(Duration.ofSeconds(30).toMillis()) {
                while (
                    kafkaInitService.address == null ||
                    commandService.address == null ||
                    preferencesService.address == null ||
                    balanceService.address == null
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
        balanceService.close()
    }

    @Test
    fun `should add funds to a customer`() {
        val consumerPreferences = TestKafkaConsumer.of(Topics.preferences)
        val consumerBalance = TestKafkaConsumer.of(Topics.balance)

        val customerId = UUID.randomUUID().toString()

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "currency": "EUR"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        consumerPreferences.lastRecord(customerId).value() as PreferencesCreated

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "amount": 1.00, "currency": "EUR"}""")
        }.post("/api/balance.addFunds").status.code shouldBe 202

        val event = consumerBalance.lastRecord(customerId).value() as FundsAdded

        UUID.fromString(event.id)
        event.occurredOn shouldNotBe null
        event.customerId shouldBe customerId
        event.amount shouldBe BigDecimal.ONE.setScale(2)
        event.currency shouldBe "EUR"
    }

    @Test
    fun `should deny add funds if no preferences exist`() {
        val consumerPreferencesAuthorization = TestKafkaConsumer.of(Topics.preferencesAuthorization)

        val customerId = UUID.randomUUID().toString()

        val response = commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "amount": 1.00, "currency": "EUR"}""")
        }.post("/api/balance.addFunds")
        response.status.code shouldBe 202

        val event = consumerPreferencesAuthorization.lastRecord(customerId).value() as AddFundsDenied

        UUID.fromString(event.id)
        event.occurredOn shouldNotBe null
        event.customerId shouldBe customerId
        event.amount shouldBe BigDecimal.ONE.setScale(2)
        event.currency shouldBe "EUR"
    }

    @Test
    fun `should deny add funds if the currency of the funds is different than the currency of the preferences`() {
        val consumerPreferencesAuthorization = TestKafkaConsumer.of(Topics.preferencesAuthorization)
        val consumerPreferences = TestKafkaConsumer.of(Topics.preferences)

        val customerId = UUID.randomUUID().toString()

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "currency": "GBP"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        consumerPreferences.lastRecord(customerId).value() as PreferencesCreated

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "amount": 1.00, "currency": "EUR"}""")
        }.post("/api/balance.addFunds").status.code shouldBe 202

        val event = consumerPreferencesAuthorization.lastRecord(customerId).value() as AddFundsDenied

        UUID.fromString(event.id)
        event.occurredOn shouldNotBe null
        event.customerId shouldBe customerId
        event.amount shouldBe BigDecimal.ONE.setScale(2)
        event.currency shouldBe "EUR"
    }

    @Test
    fun `should deny add funds if new balance is greater than 2000`() {
        val consumerPreferences = TestKafkaConsumer.of(Topics.preferences)
        val consumerBalance = TestKafkaConsumer.of(Topics.balance)
        val consumerBalanceAuthorization = TestKafkaConsumer.of(Topics.balanceAuthorization)

        val customerId = UUID.randomUUID().toString()

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "currency": "EUR"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        consumerPreferences.lastRecord(customerId).value() as PreferencesCreated

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "amount": 2000.00, "currency": "EUR"}""")
        }.post("/api/balance.addFunds").status.code shouldBe 202

        consumerBalance.lastRecord(customerId).value() as FundsAdded

        commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId": "$customerId", "amount": 1.00, "currency": "EUR"}""")
        }.post("/api/balance.addFunds").status.code shouldBe 202

        val command = consumerBalanceAuthorization.lastRecord(customerId).value() as AddFundsDenied

        UUID.fromString(command.id)
        command.occurredOn shouldNotBe null
        command.customerId shouldBe customerId
        command.amount shouldBe BigDecimal.ONE.setScale(2)
        command.currency shouldBe "EUR"
    }
}
