package parts.code.piggybox.integration.tests.features

import parts.code.piggybox.integration.tests.IntegrationTest

import static parts.code.money.Currency.EUR

class CreatePreferencesShould extends IntegrationTest {

    def "create preferences"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().creating_preferences_with_currency_$_and_country_$(EUR, "ES")
        then().the_preferences_are_created_with_currency_$_and_country_$(EUR, "ES")
    }

    def "deny create preferences if preferences already exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
        when().creating_preferences_with_currency_$_and_country_$(EUR, "ES")
        then().create_preferences_with_currency_$_and_country_$_is_denied(EUR, "ES")
    }
}
