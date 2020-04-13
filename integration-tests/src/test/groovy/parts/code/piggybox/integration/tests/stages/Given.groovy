package parts.code.piggybox.integration.tests.stages

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.Hidden
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.ProducerRecord
import parts.code.piggybox.integration.tests.ApplicationsUnderTest
import parts.code.piggybox.integration.tests.KafkaTestUtils
import parts.code.piggybox.integration.tests.Topics
import parts.code.piggybox.schemas.events.FundsAdded
import parts.code.piggybox.schemas.events.PreferencesCreated
import parts.code.piggybox.schemas.test.UnknownRecord
import spock.util.concurrent.PollingConditions

import java.time.Duration

import static groovy.json.JsonOutput.toJson
import static ratpack.http.MediaType.APPLICATION_JSON
import static ratpack.http.internal.HttpHeaderConstants.CONTENT_TYPE

class Given extends Stage<Given> {

    @ProvidedScenarioState String customerId = UUID.randomUUID().toString()
    @ProvidedScenarioState ApplicationsUnderTest aut

    @Hidden
    Given applicationsUnderTest(ApplicationsUnderTest aut) {
        this.aut = aut
        self()
    }

    Given customer_preferences_with_currency_$_and_country_$(String currency, String country) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, currency: currency, country: country]))
        }

        assert httpClient.post("/api/preferences.create").status.code == 202

        def consumer = KafkaTestUtils.consumer(Topics.preferences)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()
            assert events.last() instanceof PreferencesCreated
        }

        self()
    }

    Given $_$_worth_of_funds(BigDecimal amount, String currency) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, amount: amount, currency: currency]))
        }

        assert httpClient.post("/api/balance.addFunds").status.code == 202

        def consumer = KafkaTestUtils.consumer(Topics.balance)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }

            assert !events.isEmpty()
            assert events.last() instanceof FundsAdded
        }

        self()
    }

    Given an_unknown_record_in_the_topic_$(String topic) {
        def record = new ProducerRecord(topic, customerId, new UnknownRecord() as SpecificRecord)

        KafkaTestUtils.producer().send(record).get()

        def consumer = KafkaTestUtils.consumer(topic)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }

            assert !events.isEmpty()
            assert events.last() instanceof UnknownRecord
        }

        self()
    }
}
