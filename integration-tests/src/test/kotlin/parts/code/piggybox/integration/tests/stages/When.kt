package parts.code.piggybox.integration.tests.stages

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import io.kotlintest.shouldBe
import java.util.UUID
import parts.code.piggybox.integration.tests.ApplicationsUnderTest

open class When : Stage<When>() {

    @ExpectedScenarioState
    lateinit var customerId: String

    @ExpectedScenarioState
    lateinit var applicationsUnderTest: ApplicationsUnderTest

    @ProvidedScenarioState
    val gameId = UUID.randomUUID().toString()

    @As("adding $ $ worth of funds")
    open fun adding_funds(amount: Double, currency: String): When {
        applicationsUnderTest.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }
                .body.text("""{"customerId":"$customerId","amount":${amount.toBigDecimal().setScale(2)},"currency":"$currency"}""")
        }.post("/api/balance.addFunds").status.code shouldBe 202

        return self()
    }

    @As("buying a game worth $ $")
    open fun buying_a_game(amount: Double, currency: String): When {
        applicationsUnderTest.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }
                .body.text("""{"customerId":"$customerId","gameId":"$gameId","amount":${amount.toBigDecimal().setScale(2)},"currency":"$currency"}""")
        }.post("/api/balance.buyGame").status.code shouldBe 202

        return self()
    }

    @As("changing the country to $")
    open fun changing_the_country(country: String): When {
        applicationsUnderTest.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","country":"$country"}""")
        }.post("/api/preferences.changeCountry").status.code shouldBe 202

        return self()
    }

    @As("creating preferences with currency $ and country $")
    open fun creating_preferences(currency: String, country: String): When {
        applicationsUnderTest.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","currency":"$currency","country":"$country"}""")
        }.post("/api/preferences.create").status.code shouldBe 202

        return self()
    }
}
