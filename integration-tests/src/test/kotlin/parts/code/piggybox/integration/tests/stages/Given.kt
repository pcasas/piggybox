package parts.code.piggybox.integration.tests.stages

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.Hidden
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.shouldBe
import java.time.Duration
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerRecord
import parts.code.piggybox.integration.tests.ApplicationsUnderTest
import parts.code.piggybox.integration.tests.Topics
import parts.code.piggybox.schemas.events.FundsAdded
import parts.code.piggybox.schemas.events.PreferencesCreated
import parts.code.piggybox.schemas.test.UnknownRecord
import parts.code.skeptical.AssertConditions

open class Given : Stage<Given>() {

    @ProvidedScenarioState
    val customerId = UUID.randomUUID().toString()

    @ProvidedScenarioState
    lateinit var applicationsUnderTest: ApplicationsUnderTest

    @Hidden
    open fun applicationsUnderTest(applicationsUnderTest: ApplicationsUnderTest): Given {
        this.applicationsUnderTest = applicationsUnderTest

        return self()
    }

    @As("customer preferences with currency $ and country $")
    open fun customer_preferences(currency: String, country: String): Given {
        applicationsUnderTest.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","currency":"$currency","country":"$country"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        val consumerPreferences = applicationsUnderTest.consumer(Topics.preferences)

        AssertConditions(timeout = 30).until {
            val events = consumerPreferences.poll(Duration.ZERO).filter { it.key() == customerId }.toList()
            events.shouldNotBeEmpty()
            (events.last().value() is PreferencesCreated) shouldBe true
        }

        return self()
    }

    @As("$ $ worth of funds")
    open fun funds(amount: Double, currency: String): Given {
        applicationsUnderTest.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }
                .body.text("""{"customerId":"$customerId","amount": ${amount.toBigDecimal().setScale(2)},"currency":"$currency"}""")
        }.post("/api/balance.addFunds").status.code shouldBe 202

        val consumerBalance: KafkaConsumer<String, SpecificRecord> = applicationsUnderTest.consumer(Topics.balance)

        AssertConditions(timeout = 30).until {
            val events = consumerBalance.poll(Duration.ZERO).filter { it.key() == customerId }.toList()
            events.shouldNotBeEmpty()
            (events.last().value() is FundsAdded) shouldBe true
        }

        return self()
    }

    @As("an unknown record in the topic $")
    open fun an_unknown_record(topic: String): Given {
        val record = ProducerRecord(topic, customerId, UnknownRecord() as SpecificRecord)

        applicationsUnderTest.producer.send(record).get()

        val consumer = applicationsUnderTest.consumer(topic)

        AssertConditions(timeout = 30).until {
            val events = consumer.poll(Duration.ZERO).filter { it.key() == customerId }.toList()
            events.shouldNotBeEmpty()
            (events.last().value() is UnknownRecord) shouldBe true
        }

        return self()
    }
}
