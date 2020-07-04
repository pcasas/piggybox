package parts.code.piggybox.integration.tests

import com.tngtech.jgiven.spock.ScenarioSpec
import org.junit.AfterClass
import org.junit.BeforeClass
import parts.code.piggybox.integration.tests.stages.Given
import parts.code.piggybox.integration.tests.stages.Then
import parts.code.piggybox.integration.tests.stages.When
import spock.lang.Shared
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

import static parts.code.money.Currency.EUR
import static parts.code.money.Currency.GBP

class IntegrationTests extends ScenarioSpec<Given, When, Then> {

    @Shared ApplicationsUnderTest applicationsUnderTest = new ApplicationsUnderTest()

    @BeforeClass
    void start() {
        new PollingConditions(timeout: 30).eventually {
            assert applicationsUnderTest.started()
        }
    }

    @AfterClass
    void tearDown() {
        applicationsUnderTest.close()
    }

    def "create preferences"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().creating_preferences_with_currency_$_and_country_$(EUR, "ES")
        then().the_preferences_are_created_with_currency_$_and_country_$(EUR, "ES")
              .and().the_customer_preferences_are_currency_$_and_country_$(EUR, "ES")
    }

    def "deny create preferences if preferences already exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
        when().creating_preferences_with_currency_$_and_country_$(EUR, "ES")
        then().create_preferences_with_currency_$_and_country_$_is_denied(EUR, "ES")
    }

    def "add funds"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
        when().adding_$_$_worth_of_funds(1.00, EUR)
        then().$_$_worth_of_funds_are_added(1.00, EUR)
              .and().the_customer_balance_is_$_$(1.00, EUR)
    }

    def "not add funds if no preferences exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().adding_$_$_worth_of_funds(1.00, EUR)
        then().adding_$_$_worth_of_funds_is_denied(1.00, EUR, Topics.preferencesAuthorization)
    }

    def "not add funds if the currency is different than the currency of the preferences"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(GBP, "UK")
        when().adding_$_$_worth_of_funds(1.00, EUR)
        then().adding_$_$_worth_of_funds_is_denied(1.00, EUR, Topics.preferencesAuthorization)
    }

    @Unroll
    def "not add #fundsToAdd funds if new balance is greater than 2000"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
               .and().$_$_worth_of_funds(originalFunds, EUR)
        when().adding_$_$_worth_of_funds(fundsToAdd, EUR)
        then().adding_$_$_worth_of_funds_is_denied(fundsToAdd, EUR, Topics.balanceAuthorization)

        where:
        originalFunds | fundsToAdd
        0.00          | 2000.01
        1000.00       | 1000.01
        2000.00       | 0.01
    }

    def "ignore an unknown record adding funds"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
               .and().an_unknown_record_in_the_topic_$(Topics.balance)
        when().adding_$_$_worth_of_funds(1.00, EUR)
        then().$_$_worth_of_funds_are_added(1.00, EUR)
              .and().the_customer_balance_is_$_$(1.00, EUR)
    }

    def "withdraw funds"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
               .and().$_$_worth_of_funds(100.00, EUR)
        when().withdrawing_$_$_worth_of_funds(60.00, EUR)
        then().$_$_worth_of_funds_are_withdrawn(60.00, EUR)
              .and().the_customer_balance_is_$_$(40.00, EUR)
    }

    def "deny withdraw funds if no preferences exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().withdrawing_$_$_worth_of_funds(60.00, EUR)
        then().withdrawing_$_$_worth_of_funds_is_denied(60.00, EUR, Topics.preferencesAuthorization)
    }

    def "deny withdraw funds if the currency of the command is different than the currency of the preferences"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(GBP, "UK")
        when().withdrawing_$_$_worth_of_funds(60.00, EUR)
        then().withdrawing_$_$_worth_of_funds_is_denied(60.00, EUR, Topics.preferencesAuthorization)
    }

    def "deny withdraw funds if new balance is lower than 0"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
        when().withdrawing_$_$_worth_of_funds(1.00, EUR)
        then().withdrawing_$_$_worth_of_funds_is_denied(1.00, EUR, Topics.balanceAuthorization)
    }

    def "change the country"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
        when().changing_the_country_to_$("UK")
        then().the_country_is_changed_to_$("UK")
    }

    def "deny change the country if preferences don't exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().changing_the_country_to_$("UK")
        then().changing_the_country_to_$_is_denied("UK")
    }

    def "ignore an unknown record changing the country"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
               .and().an_unknown_record_in_the_topic_$(Topics.preferences)
        when().changing_the_country_to_$("UK")
        then().the_country_is_changed_to_$("UK")
    }
}
