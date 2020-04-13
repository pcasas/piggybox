package parts.code.piggybox.integration.tests

import com.tngtech.jgiven.spock.ScenarioSpec
import org.junit.AfterClass
import org.junit.BeforeClass
import parts.code.piggybox.integration.tests.stages.Given
import parts.code.piggybox.integration.tests.stages.Then
import parts.code.piggybox.integration.tests.stages.When
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

abstract class IntegrationTest extends ScenarioSpec<Given, When, Then> {

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
}
