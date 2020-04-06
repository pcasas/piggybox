package parts.code.piggybox.integration.tests.features

import org.junit.jupiter.api.Test
import parts.code.piggybox.integration.tests.IntegrationTest
import parts.code.piggybox.integration.tests.Topics

private class ChangeCountryFeature : IntegrationTest() {

    @Test
    fun `should change the country`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("EUR", "ES")
        `when`()
            .changing_the_country("UK")
        then()
            .the_country_is_changed("UK")
    }

    @Test
    fun `should deny change the country if preferences don't exist`() {
        given().applicationsUnderTest(applicationsUnderTest)
        `when`().changing_the_country("UK")
        then().changing_the_country_is_denied("UK")
    }

    @Test
    fun `should ignore an unknown record`() {
        given()
            .applicationsUnderTest(applicationsUnderTest)
            .customer_preferences("EUR", "ES")
            .and().an_unknown_record(Topics.preferences)
        `when`()
            .changing_the_country("UK")
        then()
            .the_country_is_changed("UK")
    }
}
