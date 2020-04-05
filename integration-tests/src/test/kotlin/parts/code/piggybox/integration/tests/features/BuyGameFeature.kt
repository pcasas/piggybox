package parts.code.piggybox.integration.tests.features

import org.junit.jupiter.api.Test
import parts.code.piggybox.integration.tests.IntegrationTest
import parts.code.piggybox.integration.tests.Topics

private class BuyGameFeature : IntegrationTest() {

    @Test
    fun `should buy a game for a customer`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("EUR", "ES")
            .and().funds(100.0, "EUR")
        `when`()
            .buying_a_game(60.0, "EUR")
        then()
            .the_game_is_bought(60.0, "EUR")
            .and().the_customer_balance_is(40.0, "EUR")
    }

    @Test
    fun `should deny buy a game if no preferences exist`() {
        given().applicationsUnderTest(applicationsUnderTest)
        `when`().buying_a_game(60.0, "EUR")
        then().buying_a_game_is_denied_by(60.0, "EUR", Topics.preferencesAuthorization)
    }

    @Test
    fun `should deny buy a game if the currency of the command is different than the currency of the preferences`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("GBP", "UK")
        `when`().buying_a_game(60.0, "EUR")
        then().buying_a_game_is_denied_by(60.0, "EUR", Topics.preferencesAuthorization)
    }

    @Test
    fun `should deny buy a game if new balance is lower than 0`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("EUR", "ES")
        `when`().buying_a_game(1.0, "EUR")
        then().buying_a_game_is_denied_by(1.0, "EUR", Topics.balanceAuthorization)
    }
}
