package justinebateman.github.io.testrailintegration.util;

import com.google.common.base.MoreObjects;
import feign.FeignException;
import justinebateman.github.io.testrailintegration.testconfig.ConfigProperties;
import justinebateman.github.io.testrailintegration.testconfig.TestContextAware;
import justinebateman.github.io.testrailintegration.testconfig.TestListener;
import justinebateman.github.io.testrailintegration.testrail.TestRail;
import justinebateman.github.io.testrailintegration.testrail.domain.*;
import justinebateman.github.io.testrailintegration.testrail.service.TestRailClient;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.xml.XmlTest;

import java.lang.reflect.Method;
import java.util.*;

@SpringBootTest
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {TestListener.class}
)
public class TestNGTestRailBaseTest extends AbstractTestNGSpringContextTests implements TestContextAware
{
    @Value("${testrail.projectid:0}")
    Integer projectId;
    @Value("${testrail.suiteid:0}")
    Integer suiteId;
    @Autowired
    TestRailClient testRailClient;
    static Integer testRunId = 0;
    static Boolean updateTestRail = false;
    static String defects = "";

    public TestContext testContext;

    @Value("${spring.profiles.active:Default}")
    private String activeProfile;

    @Override
    public TestContext setTestContext(TestContext testContext)
    {
        this.testContext = testContext;
        return testContext;
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod()
    {
        Reporter.clear();
        ITestNGMethod currentTestNGMethod = getCurrentTestNGMethod();
        Log.debug("Test starting: " + currentTestNGMethod.getTestClass().getRealClass().getSimpleName() + "." + currentTestNGMethod.getMethodName() + " on environment " + activeProfile);

        if(activeProfile.contains("Prod"))
            Log.warning("WARNING: This is a production environment. Please be careful!");

        if (currentTestNGMethod != null)
        {
            List<String> groups = Arrays.asList(currentTestNGMethod.getGroups());
            if (groups.contains("known-issue"))
                Log.warning("**Known Issue** - This test case has a known issue linked");
        }
    }


    @BeforeSuite(alwaysRun = true)
    public void addTestRailRun()
    {
        try
        {
            // without this step we couldn't autowire anything
            super.springTestContextPrepareTestInstance();
        }
        catch (Exception e)
        {
            System.out.println(e.getStackTrace());
        }

        // if we're using a testNG xml file then take the values from it first
        ConfigProperties configProperties = new ConfigProperties();
        updateTestRail = MoreObjects.firstNonNull(BooleanUtils.toBooleanObject(getXmlTest().getParameter("updateTestRail")), configProperties.updateTestRail);

        if (updateTestRail)
        {
            // read the run id from either the testng xml or the config file
            Integer testRunId = MoreObjects.firstNonNull(NumberUtils.createInteger(getXmlTest().getParameter("testRunId")), configProperties.testRunId);
            if (testRunId == 0) // if run id is zero then add a new one
            {
                addNewTestRun();
            }
            else
            {
                configProperties.writeValue("testRunId", Integer.toString(testRunId));
            }

            configProperties = new ConfigProperties(); // refresh the values

            defects = StringUtils.defaultIfEmpty(getXmlTest().getParameter("defects"), configProperties.defects);
            testRunId = configProperties.testRunId;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void postTestRailResult()
    {
        if (updateTestRail)
        {
            // If we have TestRail testCaseIds specified then we can deal with them
            if (testContext.getTestMethod().getAnnotation(TestRail.class) != null)
            {
                if (testContext.getTestMethod().getAnnotation(TestRail.class).testCaseIds().length > 0)
                {
                    addResultsForCases();
                }
            }
        }
    }

    public String getTestComments()
    {
        StringBuilder fullComment = new StringBuilder();
        fullComment.append("Automation Test Method = " + testContext.getTestClass().getName() + "." + testContext.getTestMethod().getName());

        String additionalComments = String.join("\n", Reporter.getOutput());
        if (!additionalComments.isEmpty())
        {
            fullComment.append("\n\n" + additionalComments);
        }

        // if the test threw an exception then we need to add the exception
        ITestResult testNGResult = getTestNGResult(testContext.getTestMethod());
        if (testNGResult.getThrowable() != null)
        {
            fullComment.append("\n\nException Message: " + testNGResult.getThrowable().getMessage());
        }

        return fullComment.toString();
    }

    public void addNewTestRun()
    {
        ConfigProperties configProperties = new ConfigProperties();

        /* get the test case ids for each test method and add them to a list
                    so we can add these to the TestRail run later */
        ITestNGMethod[] allTestMethods = getITestContext().getAllTestMethods();
        List<Integer> testCaseIds = new ArrayList<>();
        for (ITestNGMethod testMethod : allTestMethods)
        {
            Method method = testMethod.getConstructorOrMethod().getMethod();
            if (method.getAnnotation(TestRail.class) != null)
            {
                if (method.getAnnotation(TestRail.class).testCaseIds().length > 0)
                {
                    int[] testCaseIdsForThisTest = method.getAnnotation(TestRail.class).testCaseIds();
                    for (int id : testCaseIdsForThisTest)
                    {
                        testCaseIds.add(id);
                    }
                }
            }
        }

        TestRunModel testRunModel = new TestRunModel();
        String testRunName = Util.getPrettyCurrentDateTime() + " Automation Test";

        // if we are using the test case suite structure then get the suite id
        if (suiteId != null && suiteId != 0)
        {
            testRunModel.setSuite_id(suiteId);
            String suiteName = testRailClient.getSuite(suiteId).getBody().getName();
            String currentUser = System.getProperty("user.name");
            testRunName = testRunName + " - " + suiteName + " - " + currentUser;
        }
        testRunModel.setName(testRunName)
                .setDescription(getRunDescription())
                .setInclude_all(false)
                .setCase_ids(testCaseIds);

        // add a new test run
        ResponseEntity<TestRunResponseModel> responseEntity = testRailClient.addTestRun(projectId, testRunModel);
        testRunId = responseEntity.getBody().getId();
        configProperties.writeValue("testRunId", Integer.toString(testRunId));
    }

    public String getRunDescription()
    {
        // Print the included and excluded test groups to the test run description
        String[] includedGroups = getITestContext().getIncludedGroups();
        String[] excludedGroups = getITestContext().getExcludedGroups();

        StringBuilder fullDescription = new StringBuilder();
        if (includedGroups.length > 0)
        {
            fullDescription.append("Included Groups: " + String.join(",", includedGroups) + "\n");
        }
        if (excludedGroups.length > 0)
        {
            fullDescription.append("Excluded Groups: " + String.join(",", excludedGroups));
        }
        return fullDescription.toString();
    }

    public void addResultsForCases()
    {
        ITestResult testNGResult = getTestNGResult(testContext.getTestMethod());

        // Create TestRail result model
        ResultModel resultModel = new ResultModel();
        resultModel.setStatus_id(getTestStatusFromTestNGResult(testNGResult));
        resultModel.setComment(getTestComments());
        resultModel.setDefects(defects);

        int[] testCaseIdsForCurrentTest = testContext.getTestMethod().getAnnotation(TestRail.class).testCaseIds();
        for (int testCaseIdForCurrentTest : testCaseIdsForCurrentTest)
        {
            // Add result to testrail for each test case
            try
            {
                testRailClient.addResultForCase(testRunId, testCaseIdForCurrentTest, resultModel);
            }
            // if the current test is not in this test run
            catch (FeignException e)
            {
                if (e.getMessage().contains("No (active) test found for the run\\/case combination."))
                {
                    List<Integer> testCasesWeWantInRun = new ArrayList<>();
                    // get the test run and store all the test cases currently added
                    List<TestModel> tests = testRailClient.getTests(testRunId).getBody();
                    for (TestModel test : tests)
                    {
                        testCasesWeWantInRun.add(test.getCase_id());
                    }

                    // add our test case id to this list
                    testCasesWeWantInRun.add(testCaseIdForCurrentTest);

                    // get the test run so we can keep the same details when we update it
                    TestRunResponseModel testRunResponseModel = testRailClient.getRun(testRunId).getBody();
                    // update the test run
                    TestRunModel updateTestRunModel = new TestRunModel()
                            .setName(testRunResponseModel.getName())
                            .setInclude_all(false)
                            .setCase_ids(testCasesWeWantInRun);
                    testRailClient.updateRun(testRunId, updateTestRunModel);

                    // try to add the result again
                    testRailClient.addResultForCase(testRunId, testCaseIdForCurrentTest, resultModel);
                }
                else
                {
                    throw e;
                }
            }
        }
    }

    protected ITestResult getCurrentITestResult()
    {
        return Reporter.getCurrentTestResult();
    }

    protected ITestContext getITestContext()
    {
        return getCurrentITestResult().getTestContext();
    }

    public XmlTest getXmlTest()
    {
        return getITestContext().getCurrentXmlTest();
    }

    protected ITestResult getTestNGResult(Method method)
    {
        ITestContext context = getITestContext();
        Set<ITestResult> allResults = new HashSet<>();
        allResults.addAll(context.getPassedTests().getAllResults());
        allResults.addAll(context.getFailedTests().getAllResults());
        allResults.addAll(context.getSkippedTests().getAllResults());
        return allResults
                .stream()
                .filter(result -> result.getMethod().getConstructorOrMethod().getMethod().equals(method))
                .findAny()
                .orElse(null);
    }

    protected ITestNGMethod getCurrentTestNGMethod()
    {
        ITestContext iTestContext = getITestContext();
        return Arrays.stream(iTestContext.getAllTestMethods()).filter(t -> t.getMethodName().equalsIgnoreCase(testContext.getTestMethod().getName())).findFirst().orElse(null);
    }

    public int getTestStatusFromTestNGResult(ITestResult testNGTestResult)
    {
        int testRailStatus = 3; // UNTESTED
        switch (testNGTestResult.getStatus())
        {
            case 1:
                testRailStatus = Status.PASSED.value(); // SUCCESS
                break;
            case 2:
                testRailStatus = Status.FAILED.value(); //FAILURE
                break;
            case 3:
                testRailStatus = Status.RETEST.value(); //SKIP
                break;
            default:
                testRailStatus = Status.UNTESTED.value();
                break;
        }
        return testRailStatus;
    }
}
