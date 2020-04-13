package parts.code.piggybox.integration.tests.features

import parts.code.piggybox.integration.tests.IntegrationTest
import parts.code.piggybox.integration.tests.Topics

class BuyGameShould extends IntegrationTest {

    def "buy a game for a customer"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("EUR", "ES")
               .and().$_$_worth_of_funds(100.00, "EUR")
        when().buying_a_game_worth_$_$(60.00, "EUR")
        then().a_game_worth_$_$_is_bought(60.00, "EUR")
              .and().the_customer_balance_is_$_$(40.00, "EUR")
    }

    def "deny buy a game if no preferences exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().buying_a_game_worth_$_$(60.00, "EUR")
        then().buying_a_game_worth_$_$_is_denied(60.00, "EUR", Topics.preferencesAuthorization)
    }

    def "deny buy a game if the currency of the command is different than the currency of the preferences"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("GBP", "UK")
        when().buying_a_game_worth_$_$(60.00, "EUR")
        then().buying_a_game_worth_$_$_is_denied(60.00, "EUR", Topics.preferencesAuthorization)
    }

    def "deny buy a game if new balance is lower than 0"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("EUR", "ES")
        when().buying_a_game_worth_$_$(1.00, "EUR")
        then().buying_a_game_worth_$_$_is_denied(1.00, "EUR", Topics.balanceAuthorization)
    }
}
