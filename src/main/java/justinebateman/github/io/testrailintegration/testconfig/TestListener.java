package justinebateman.github.io.testrailintegration.testconfig;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

public class TestListener extends TransactionalTestExecutionListener
{
    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception
    {
        Object testInstance = testContext.getTestInstance();
        if (TestContextAware.class.isInstance(testInstance))
        {
            ((TestContextAware) testInstance).setTestContext(testContext);
        }
        super.beforeTestMethod(testContext);
    }

}
