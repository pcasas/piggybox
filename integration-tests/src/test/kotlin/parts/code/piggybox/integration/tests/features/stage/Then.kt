package parts.code.piggybox.integration.tests.features.stage

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.Hidden
import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.time.Duration
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import parts.code.piggybox.integration.tests.ApplicationsUnderTest
import parts.code.piggybox.integration.tests.TestKafkaConsumer
import parts.code.piggybox.integration.tests.Topics
import parts.code.piggybox.schemas.events.AddFundsDenied
import parts.code.piggybox.schemas.events.FundsAdded
import parts.code.skeptical.AssertConditions

open class Then : Stage<Then>() {

    @ExpectedScenarioState
    lateinit var customerId: String

    @ExpectedScenarioState
    lateinit var applicationsUnderTest: ApplicationsUnderTest

    @As("$ $ worth of funds are added")
    open fun the_funds_are_added(amount: Double, currency: String): Then {
        val consumerBalance: KafkaConsumer<String, SpecificRecord> = TestKafkaConsumer.of(Topics.balance)

        AssertConditions(timeout = 30).until {
            val events = consumerBalance.poll(Duration.ZERO).filter { it.key() == customerId }.toList()
            events.shouldNotBeEmpty()
            (events.last().value() is FundsAdded) shouldBe true

            val event = events.last().value() as FundsAdded
            UUID.fromString(event.id)
            event.occurredOn shouldNotBe null
            event.customerId shouldBe customerId
            event.amount shouldBe amount.toBigDecimal().setScale(2)
            event.currency shouldBe currency
        }

        return self()
    }

    @As("$ $ worth of funds are denied")
    open fun the_funds_are_denied_by(amount: Double, currency: String, @Hidden topic: String): Then {
        val consumer = TestKafkaConsumer.of(topic)

        AssertConditions(timeout = 30).until {
            val events = consumer.poll(Duration.ZERO).filter { it.key() == customerId }.toList()
            events.shouldNotBeEmpty()
            (events.last().value() is AddFundsDenied) shouldBe true

            val event = events.last().value() as AddFundsDenied
            UUID.fromString(event.id)
            event.occurredOn shouldNotBe null
            event.customerId shouldBe customerId
            event.amount shouldBe amount.toBigDecimal().setScale(2)
            event.currency shouldBe currency
        }

        return self()
    }

    @As("the customer balance is $ $")
    open fun the_customer_balance_is(amount: Double, currency: String): Then {
        val response = applicationsUnderTest.queryService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId"}""")
        }.get("/api/customers.getBalance")

        response.status.code shouldBe 200
        response.body.text shouldBe """{"amount":${amount.toBigDecimal().setScale(2)},"currency":"$currency"}"""

        return self()
    }
}
