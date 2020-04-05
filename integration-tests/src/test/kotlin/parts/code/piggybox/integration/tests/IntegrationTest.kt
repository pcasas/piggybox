package parts.code.piggybox.integration.tests

import com.tngtech.jgiven.junit5.ScenarioTest
import io.kotlintest.shouldBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import parts.code.piggybox.integration.tests.features.stage.Given
import parts.code.piggybox.integration.tests.features.stage.Then
import parts.code.piggybox.integration.tests.features.stage.When
import parts.code.skeptical.AssertConditions

open class IntegrationTest : ScenarioTest<Given, When, Then>() {

    companion object {
        val applicationsUnderTest = ApplicationsUnderTest()

        @BeforeAll
        @JvmStatic
        fun setUp() {
            AssertConditions(timeout = 30).until {
                applicationsUnderTest.started() shouldBe true
            }
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            applicationsUnderTest.close()
        }
    }
}
