package parts.code.piggybox.integration.tests.features.stage

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import org.junit.jupiter.api.Assertions
import parts.code.piggybox.integration.tests.ApplicationsUnderTest

open class When : Stage<When>() {

    @ExpectedScenarioState
    lateinit var customerId: String

    @ExpectedScenarioState
    lateinit var applicationsUnderTest: ApplicationsUnderTest

    @As("adding $ $ worth of funds")
    open fun adding_funds(amount: Double, currency: String): When {
        val code = applicationsUnderTest.commandService.httpClient.requestSpec { request ->
            request.headers {
                it.set("Content-Type", "application/json")
            }.body.text("""{"customerId":"$customerId","amount": ${amount.toBigDecimal().setScale(2)},"currency":"$currency"}""")
        }.post("/api/balance.addFunds").status.code

        Assertions.assertEquals(202, code)

        return self()
    }
}
