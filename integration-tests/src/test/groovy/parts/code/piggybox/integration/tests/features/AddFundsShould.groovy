package parts.code.piggybox.integration.tests.features

import parts.code.piggybox.integration.tests.IntegrationTest
import parts.code.piggybox.integration.tests.Topics

class AddFundsShould extends IntegrationTest {

    def "add funds to a customer"() {
        expect:

        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("EUR", "ES")
        when().adding_$_$_worth_of_funds(1.00, "EUR")
        then().$_$_worth_of_funds_are_added(1.00, "EUR")
              .and().the_customer_balance_is_$_$(1.00, "EUR")
    }

    def "not add funds to a customer if no preferences exist"() {
        expect:

        given().applicationsUnderTest(applicationsUnderTest)
        when().adding_$_$_worth_of_funds(1.00, "EUR")
        then().$_$_worth_of_funds_are_denied(1.00, "EUR", Topics.preferencesAuthorization)
    }

    def "not add funds to a customer if the currency is different than the currency of the preferences"() {
        expect:

        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("GBP", "UK")
        when().adding_$_$_worth_of_funds(1.00, "EUR")
        then().$_$_worth_of_funds_are_denied(1.00, "EUR", Topics.preferencesAuthorization)
    }


    def "not add funds if new balance is greater than 2000"() {
        expect:

        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("EUR", "ES")
               .and().$_$_worth_of_funds(2000.00, "EUR")
        when().adding_$_$_worth_of_funds(1.00, "EUR")
        then().$_$_worth_of_funds_are_denied(1.00, "EUR", Topics.balanceAuthorization)
    }

    def "ignore an unknown record"() {
        expect:

        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("EUR", "ES")
               .and().an_unknown_record_in_the_topic_$(Topics.balance)
        when().adding_$_$_worth_of_funds(1.00, "EUR")
        then().$_$_worth_of_funds_are_added(1.00, "EUR")
              .and().the_customer_balance_is_$_$(1.00, "EUR")
    }
}
