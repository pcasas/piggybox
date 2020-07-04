package parts.code.piggybox.integration.tests.stages

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import parts.code.money.Currency
import parts.code.piggybox.integration.tests.ApplicationsUnderTest

import static groovy.json.JsonOutput.toJson
import static ratpack.http.MediaType.APPLICATION_JSON
import static ratpack.http.internal.HttpHeaderConstants.CONTENT_TYPE

class When extends Stage<When> {

    @ExpectedScenarioState String customerId
    @ExpectedScenarioState ApplicationsUnderTest aut

    When adding_$_$_worth_of_funds(BigDecimal amount, Currency currency) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, money: [amount: amount, currency: currency.name()]]))
        }

        assert httpClient.post("/api/balance.addFunds").status.code == 202
        self()
    }

    When withdrawing_$_$_worth_of_funds(BigDecimal amount, Currency currency) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, money: [amount: amount, currency: currency.name()]]))
        }

        assert httpClient.post("/api/balance.withdrawFunds").status.code == 202
        self()
    }

    When changing_the_country_to_$(String country) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, country: country]))
        }

        assert httpClient.post("/api/preferences.changeCountry").status.code == 202
        self()
    }

    When creating_preferences_with_currency_$_and_country_$(Currency currency, String country) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, currency: currency.name(), country: country]))
        }

        assert httpClient.post("/api/preferences.create").status.code == 202
        self()
    }
}
