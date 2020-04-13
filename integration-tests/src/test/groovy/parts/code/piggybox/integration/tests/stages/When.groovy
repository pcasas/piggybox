package parts.code.piggybox.integration.tests.stages

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import parts.code.piggybox.integration.tests.ApplicationsUnderTest

import static groovy.json.JsonOutput.toJson
import static ratpack.http.MediaType.APPLICATION_JSON
import static ratpack.http.internal.HttpHeaderConstants.CONTENT_TYPE

class When extends Stage<When> {

    @ExpectedScenarioState String customerId
    @ExpectedScenarioState ApplicationsUnderTest aut
    @ProvidedScenarioState String gameId = UUID.randomUUID().toString()

    When adding_$_$_worth_of_funds(BigDecimal amount, String currency) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, amount: amount, currency: currency]))
        }

        assert httpClient.post("/api/balance.addFunds").status.code == 202
        self()
    }

    When buying_a_game_worth_$_$(BigDecimal amount, String currency) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, gameId: gameId, amount: amount, currency: currency]))
        }

        assert httpClient.post("/api/balance.buyGame").status.code == 202
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

    When creating_preferences_with_currency_$_and_country_$(String currency, String country) {
        def httpClient = aut.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set(CONTENT_TYPE, APPLICATION_JSON)
            }.body.text(toJson([customerId: customerId, currency: currency, country: country]))
        }

        assert httpClient.post("/api/preferences.create").status.code == 202
        self()
    }
}
