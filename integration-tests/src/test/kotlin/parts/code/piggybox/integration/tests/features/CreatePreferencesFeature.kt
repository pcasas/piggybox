package parts.code.piggybox.integration.tests.features

import org.junit.jupiter.api.Test
import parts.code.piggybox.integration.tests.IntegrationTest

private class CreatePreferencesFeature : IntegrationTest() {

    @Test
    fun `should create preferences`() {
        given().applicationsUnderTest(applicationsUnderTest)
        `when`().creating_preferences("EUR", "ES")
        then().the_preferences_are_created("EUR", "ES")
    }

    @Test
    fun `should deny create preferences if preferences already exist`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("EUR", "ES")
        `when`().creating_preferences("EUR", "ES")
        then().create_preferences_is_denied("EUR", "ES")
    }
}
