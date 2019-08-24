package justinebateman.github.io.testrailintegration.testrail.configuration;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import justinebateman.github.io.testrailintegration.testconfig.FeignRetryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.springframework.util.ReflectionUtils.findField;

public class TestRailConfiguration
{
    @Value("${testrail.authorizationheader}")
    String authorization;

    @Bean
    Logger.Level feignLoggerLevel()
    {
        return Logger.Level.FULL;
    }

    // Hacky workaround for feign not being able to deal with index.php?/ in the api endpoint
    @Bean
    public RequestInterceptor testRailApiInterceptor()
    {
        return requestTemplate ->
        {
            requestTemplate.header("Authorization", authorization);
            String url = requestTemplate.url();
            if (url.contains("index.php/api/v2"))
            {
                url = url.replace("index.php/", "index.php?/");
                Optional<Field> field = ofNullable(findField(RequestTemplate.class, "url"));
                if (field.isPresent())
                {
                    ReflectionUtils.makeAccessible(field.get());
                    ReflectionUtils.setField(field.get(), requestTemplate, new StringBuilder(url));
                }
            }
        };
    }

    @Bean
    public Retryer retryer()
    {
        return new FeignRetryer();
    }
}
