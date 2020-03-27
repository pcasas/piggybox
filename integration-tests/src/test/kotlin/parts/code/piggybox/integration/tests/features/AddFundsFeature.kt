package parts.code.piggybox.integration.tests.features

import com.tngtech.jgiven.junit5.ScenarioTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import parts.code.piggybox.balance.BalanceServiceApplication
import parts.code.piggybox.command.CommandServiceApplication
import parts.code.piggybox.integration.tests.ApplicationsUnderTest
import parts.code.piggybox.integration.tests.Topics
import parts.code.piggybox.integration.tests.features.stage.Given
import parts.code.piggybox.integration.tests.features.stage.Then
import parts.code.piggybox.integration.tests.features.stage.When
import parts.code.piggybox.kafka.init.KafkaInitServiceApplication
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.query.QueryServiceApplication
import parts.code.skeptical.AssertConditions
import ratpack.test.MainClassApplicationUnderTest

private class AddFundsFeature : ScenarioTest<Given, When, Then>() {

    companion object {
        val applicationsUnderTest = ApplicationsUnderTest(
            MainClassApplicationUnderTest(KafkaInitServiceApplication::class.java),
            MainClassApplicationUnderTest(CommandServiceApplication::class.java),
            MainClassApplicationUnderTest(PreferencesServiceApplication::class.java),
            MainClassApplicationUnderTest(BalanceServiceApplication::class.java),
            MainClassApplicationUnderTest(QueryServiceApplication::class.java)
        )

        @BeforeAll
        @JvmStatic
        fun setUp() {
            AssertConditions(timeout = 30).until {
                assertTrue(applicationsUnderTest.started())
            }
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            applicationsUnderTest.close()
        }
    }

    @Test
    fun `should add funds to a customer`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("EUR", "ES")
        `when`()
            .adding_funds(1.0, "EUR")
        then()
            .the_funds_are_added(1.0, "EUR")
            .and().the_customer_balance_is(1.0, "EUR")
    }

    @Test
    fun `should not add funds to a customer if no preferences exist`() {
        given().applicationsUnderTest(applicationsUnderTest)
        `when`().adding_funds(1.0, "EUR")
        then().the_funds_are_denied_by(1.0, "EUR", Topics.preferencesAuthorization)
    }

    @Test
    fun `should not add funds to a customer if the currency is different than the currency of the preferences`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("GBP", "UK")
        `when`().adding_funds(1.0, "EUR")
        then().the_funds_are_denied_by(1.0, "EUR", Topics.preferencesAuthorization)
    }

    @Test
    fun `should not add funds if new balance is greater than 2000`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("EUR", "ES")
            .and().funds(2000.0, "EUR")
        `when`().adding_funds(1.0, "EUR")
        then().the_funds_are_denied_by(1.0, "EUR", Topics.balanceAuthorization)
    }

    @Test
    fun `should ignore an unknown record`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("EUR", "ES")
            .and().an_unknown_record(Topics.balance)
        `when`()
            .adding_funds(1.0, "EUR")
        then()
            .the_funds_are_added(1.0, "EUR")
            .and().the_customer_balance_is(1.0, "EUR")
    }
}
