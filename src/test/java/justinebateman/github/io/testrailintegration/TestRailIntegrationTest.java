package justinebateman.github.io.testrailintegration;

import justinebateman.github.io.testrailintegration.testrail.TestRail;
import justinebateman.github.io.testrailintegration.util.Log;
import justinebateman.github.io.testrailintegration.util.TestNGTestRailBaseTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestRailIntegrationTest  extends TestNGTestRailBaseTest
{
    @Test(groups = {"smoke"})
    @TestRail(testCaseIds = {103})
    public void quickTest()
    {
        Log.info("This is a test step");
        assertThat(true)
                .as("Check true is true")
                .isTrue();
        Log.assertion("True is true!");

        Log.info("This is another test step");
        assertThat("STRING")
                .as("Check string is string")
                .isEqualToIgnoringCase("string");
        Log.assertion("String is string!");
    }
}
