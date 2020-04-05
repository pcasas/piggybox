package parts.code.piggybox.integration.tests.features

import org.junit.jupiter.api.Test
import parts.code.piggybox.integration.tests.IntegrationTest
import parts.code.piggybox.integration.tests.Topics

private class AddFundsFeature : IntegrationTest() {

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
