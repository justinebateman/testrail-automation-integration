package justinebateman.github.io.testrailintegration;

import justinebateman.github.io.testrailintegration.testrail.configuration.TestRailConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan(basePackages = {"justinebateman.github.io.testrailintegration"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestRailConfiguration.class}))
public class TestRailAutomationIntegrationApplication
{
    public static void main(String[] args) { SpringApplication.run(TestRailAutomationIntegrationApplication.class, args);}
}