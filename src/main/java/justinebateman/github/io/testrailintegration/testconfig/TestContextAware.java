package justinebateman.github.io.testrailintegration.testconfig;

import org.springframework.test.context.TestContext;

public interface TestContextAware
{
    TestContext setTestContext(TestContext testContext);
}
