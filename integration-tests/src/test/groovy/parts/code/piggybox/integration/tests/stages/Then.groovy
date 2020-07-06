package parts.code.piggybox.integration.tests.stages

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.Hidden
import parts.code.money.Currency
import parts.code.piggybox.integration.tests.ApplicationsUnderTest
import parts.code.piggybox.integration.tests.KafkaTestUtils
import parts.code.piggybox.integration.tests.Topics
import parts.code.piggybox.schemas.*
import ratpack.http.MediaType
import ratpack.http.internal.HttpHeaderConstants
import spock.util.concurrent.PollingConditions

import java.time.Duration

import static groovy.json.JsonOutput.toJson

class Then extends Stage<Then> {

    @ExpectedScenarioState String customerId
    @ExpectedScenarioState ApplicationsUnderTest aut

    Then $_worth_of_funds_are_added(BigDecimal amount) {
        def consumer = KafkaTestUtils.instance.consumer(Topics.balance)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()

            def event = events.last()
            assert event instanceof FundsAdded
            assert UUID.fromString(event.id)
            assert event.occurredOn != null
            assert event.customerId == customerId
            assert event.amount == amount
        }

        self()
    }

    Then adding_$_worth_of_funds_is_denied(BigDecimal amount, @Hidden String topic) {
        def consumer = KafkaTestUtils.instance.consumer(topic)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()

            def event = events.last()
            assert event instanceof AddFundsDenied
            assert UUID.fromString(event.id)
            assert event.occurredOn != null
            assert event.customerId == customerId
            assert event.amount == amount
        }

        self()
    }

    Then the_customer_balance_is_$(BigDecimal amount) {
        def httpClient = aut.queryService.httpClient.requestSpec { request ->
            request.headers {
                it.set(HttpHeaderConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            }
        }.params {
            it.put("customerId", customerId)
        }

        def response = httpClient.get("/api/customers.getBalance")
        assert response.status.code == 200
        assert response.body.text == toJson([amount: amount, min: 0, max: 500])
        self()
    }

    Then the_customer_balance_is_not_found() {
        def httpClient = aut.queryService.httpClient.requestSpec { request ->
            request.headers {
                it.set(HttpHeaderConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            }
        }.params {
            it.put("customerId", customerId)
        }

        def response = httpClient.get("/api/customers.getBalance")
        assert response.status.code == 404
        self()
    }

    Then $_worth_of_funds_are_withdrawn(BigDecimal amount) {
        def consumer = KafkaTestUtils.instance.consumer(Topics.balance)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()

            def event = events.last()
            assert event instanceof FundsWithdrawn
            assert UUID.fromString(event.id)
            assert event.occurredOn != null
            assert event.customerId == customerId
            assert event.amount == amount
        }

        self()
    }

    Then withdrawing_$_worth_of_funds_is_denied(BigDecimal amount, @Hidden String topic) {
        def consumer = KafkaTestUtils.instance.consumer(topic)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()

            def event = events.last()
            assert event instanceof WithdrawFundsDenied
            assert UUID.fromString(event.id)
            assert event.occurredOn != null
            assert event.customerId == customerId
            assert event.amount == amount
        }

        self()
    }

    Then the_country_is_changed_to_$(String country) {
        def consumer = KafkaTestUtils.instance.consumer(Topics.preferences)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()

            def event = events.last()
            assert event instanceof CountryChanged
            assert UUID.fromString(event.id)
            assert event.occurredOn != null
            assert event.customerId == customerId
            assert event.country == country
        }

        self()
    }

    Then changing_the_country_to_$_is_denied(String country) {
        def consumer = KafkaTestUtils.instance.consumer(Topics.preferencesAuthorization)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()

            def event = events.last()
            assert event instanceof ChangeCountryDenied
            assert UUID.fromString(event.id)
            assert event.occurredOn != null
            assert event.customerId == customerId
            assert event.country == country
        }

        self()
    }

    Then the_preferences_are_created_with_currency_$_and_country_$(Currency currency, String country) {
        def consumer = KafkaTestUtils.instance.consumer(Topics.preferences)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()

            def event = events.last()
            assert event instanceof PreferencesCreated
            assert UUID.fromString(event.id)
            assert event.occurredOn != null
            assert event.customerId == customerId
            assert event.currency == currency.name()
            assert event.country == country
        }

        self()
    }

    Then the_customer_preferences_are_currency_$_and_country_$(Currency currency, String country) {
        def httpClient = aut.queryService.httpClient.requestSpec { request ->
            request.headers {
                it.set(HttpHeaderConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            }
        }.params {
            it.put("customerId", customerId)
        }

        def response = httpClient.get("/api/customers.getPreferences")
        assert response.status.code == 200
        assert response.body.text == toJson([currency: currency.name(), country: country])
        self()
    }

    Then the_customer_preferences_are_not_found() {
        def httpClient = aut.queryService.httpClient.requestSpec { request ->
            request.headers {
                it.set(HttpHeaderConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            }
        }.params {
            it.put("customerId", customerId)
        }

        def response = httpClient.get("/api/customers.getPreferences")
        assert response.status.code == 404
        self()
    }

    Then create_preferences_with_currency_$_and_country_$_is_denied(Currency currency, String country) {
        def consumer = KafkaTestUtils.instance.consumer(Topics.preferencesAuthorization)

        new PollingConditions(timeout: 30).eventually {
            def events = consumer.poll(Duration.ZERO).findResults { it.key() == customerId ? it.value() : null }
            assert !events.isEmpty()

            def event = events.last()
            assert event instanceof CreatePreferencesDenied
            assert UUID.fromString(event.id)
            assert event.occurredOn != null
            assert event.customerId == customerId
            assert event.currency == currency.name()
            assert event.country == country
        }

        self()
    }
}
