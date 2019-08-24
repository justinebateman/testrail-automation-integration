package justinebateman.github.io.testrailintegration.testconfig;

import feign.RetryableException;
import feign.Retryer;

public class FeignRetryer implements Retryer
{
    private final int maxAttempts;
    private final long backoff;
    int attempt;

    public FeignRetryer()
    {
        this(2000, 3);
    }

    public FeignRetryer(long backoff, int maxAttempts)
    {
        this.backoff = backoff;
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
    }

    public void continueOrPropagate(RetryableException e)
    {
        if (attempt++ >= maxAttempts)
        {
            throw e;
        }

        try
        {
            Thread.sleep(backoff);
        }
        catch (InterruptedException ignored)
        {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Retryer clone()
    {
        return new FeignRetryer(backoff, maxAttempts);
    }
}