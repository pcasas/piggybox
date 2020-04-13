package parts.code.piggybox.integration.tests.features

import parts.code.piggybox.integration.tests.IntegrationTest
import parts.code.piggybox.integration.tests.Topics

class ChangeCountryShould extends IntegrationTest {

    def "change the country"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("EUR", "ES")
        when().changing_the_country_to_$("UK")
        then().the_country_is_changed_to_$("UK")
    }

    def "deny change the country if preferences don't exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().changing_the_country_to_$("UK")
        then().changing_the_country_to_$_is_denied("UK")
    }

    def "ignore an unknown record"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$("EUR", "ES")
               .and().an_unknown_record_in_the_topic_$(Topics.preferences)
        when().changing_the_country_to_$("UK")
        then().the_country_is_changed_to_$("UK")
    }
}
