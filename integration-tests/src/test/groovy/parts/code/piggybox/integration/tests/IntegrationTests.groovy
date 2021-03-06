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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
              .and().the_customer_preferences_are_currency_$_and_country_$(EUR, "ES", 1)
    }

    def "return get preferences not found if no preferences exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        then().the_customer_preferences_are_not_found()
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
        when().adding_$_worth_of_funds(1.00)
        then().$_worth_of_funds_are_added(1.00)
              .and().the_customer_balance_is_$(1.00, 1)
    }

    def "return get balance 0.00 if no balance exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        then().the_customer_balance_is_$(0.00, 0)
    }

    def "deny add funds if no preferences exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().adding_$_worth_of_funds(1.00)
        then().adding_$_worth_of_funds_is_denied(1.00, Topics.preferencesAuthorization)
    }

    @Unroll
    def "deny add #fundsToAdd funds if new balance is greater than 500"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
               .and().$_worth_of_funds(originalFunds)
        when().adding_$_worth_of_funds(fundsToAdd)
        then().adding_$_worth_of_funds_is_denied(fundsToAdd, Topics.balanceAuthorization)

        where:
        originalFunds | fundsToAdd
        0.00          | 500.01
        250.00        | 250.01
        500.00        | 0.01
    }

    def "ignore an unknown record adding funds"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
               .and().an_unknown_record_in_the_topic_$(Topics.balance)
        when().adding_$_worth_of_funds(1.00)
        then().$_worth_of_funds_are_added(1.00)
              .and().the_customer_balance_is_$(1.00, 1)
    }

    def "withdraw funds"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
               .and().$_worth_of_funds(100.00)
        when().withdrawing_$_worth_of_funds(60.00)
        then().$_worth_of_funds_are_withdrawn(60.00)
              .and().the_customer_balance_is_$(40.00, 2)
    }

    def "deny withdraw funds if no preferences exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        when().withdrawing_$_worth_of_funds(60.00)
        then().withdrawing_$_worth_of_funds_is_denied(60.00, Topics.preferencesAuthorization)
    }

    def "deny withdraw funds if new balance is lower than 0"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
        when().withdrawing_$_worth_of_funds(1.00)
        then().withdrawing_$_worth_of_funds_is_denied(1.00, Topics.balanceAuthorization)
    }

    def "change the country"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(EUR, "ES")
        when().changing_the_country_to_$("UK")
        then().the_country_is_changed_to_$("UK")
              .and().the_customer_preferences_are_currency_$_and_country_$(EUR, "UK", 2)
    }

    def "deny change the country if no preferences exist"() {
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

    def "get history"() {
        given:
        def date = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yyyy"))
        def expectedTransactions = [
                [description: "Funds Added", date: "$date", type: "FUNDS_ADDED", amount: "1.00"],
                [description: "Funds Withdrawn", date: "$date", type: "FUNDS_WITHDRAWN", amount: "-1.00"]
        ]

        expect:
        given().applicationsUnderTest(applicationsUnderTest)
               .customer_preferences_with_currency_$_and_country_$(GBP, "UK")
               .and().$_worth_of_funds(1.00)
        when().withdrawing_$_worth_of_funds(1.00)
        then().the_customer_history_is_$(expectedTransactions)
    }

    def "return get history not found if no history exist"() {
        expect:
        given().applicationsUnderTest(applicationsUnderTest)
        then().the_customer_history_is_not_found()
    }
}
