# testrail-automation-integration

This project uses TestRail's API to automatically add a test run and update your automated TestNG test results in your TestRail instance

It also supports printing test steps to the comments of your TestRail result

#### Usage

```
public class TestRailIntegrationTest extends TestNGTestRailBaseTest
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
```

The above TestNG test case will create the following test result in TestRail

<img src="https://i.imgur.com/ADrFTFa.png" width="500" >

Your test class must extend TestNGTestRailBaseTest for the integration to work

You can tag your TestNG test case with the @TestRail annotation and include the TestRail test case id

Run your tests manually or via maven and the results will be posted to TestRail. Keep reading for full instructions

#### Building tests locally
The tests use Lombok so ensure you have annotation processing enabled in your IDE and install the Lombok plugin

IntelliJ

[https://www.jetbrains.com/help/idea/configuring-annotation-processing.html](https://www.jetbrains.com/help/idea/configuring-annotation-processing.html)

[https://projectlombok.org/setup/intellij](https://projectlombok.org/setup/intellij)

Eclipse

[https://stackoverflow.com/questions/43404891/how-to-configure-java-annotation-processors-in-eclipse](https://stackoverflow.com/questions/43404891/how-to-configure-java-annotation-processors-in-eclipse)

[https://projectlombok.org/setup/eclipse](https://projectlombok.org/setup/eclipse)

## Setup
To use this library in your automation project - Add this project as a dependency
```
<dependency>
    <groupId>justinebateman.github.io</groupId>
    <artifactId>testrailintegration</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Add the package to your component scan, and ensure that the configuration classes are excluded eg.

```
@ComponentScan(basePackages = {"your.package.here", "justinebateman.github.io.testrailintegration"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"justinebateman.github.io.testrailintegration.*.config*.*"}))
```

TODO - This library has been deployed to GitHub Packages so you will need to configure the repo on your machine.

Add these two repositories to your project's pom file:

```
<repository>
    <id>TODO</id>
    <name>TODO</name>
    <url>TODO</url>
</repository>
<repository>
    <id>TODO</id>
    <name>TODO</name>
    <url>TODOs</url>
</repository>
```

## TestRail Configuration
Add a Config.properties file in /src/main/resources/Config.properties in your project, with the following structure

```
updateTestRail=false
testRunId=0
defects=
```

- Set updateTestRail to true to update TestRail with the test results
- If testRunId is set to 0 then a new test run will be created in TestRail, or you can set it to an existing run id to add results to it
- defects is a comma separated list of JIRA issues linked to the current test run, this can be left blank

Add the following your application.yml file

```
testrail:
  apiendpoint: https://yourtestrailinstance.testrail.io
  authorizationheader: Basic yourbasicauthorisationheader
  projectid: 10
  suiteid: 0
```

- Set projectid as the TestRail project id. You can find this id in the "Overview" tab for your project in TestRail
- Set suiteid as the TestRail suite id if your project uses suites

To add detailed logging for TestRail add the following to your application.yml file

```
logging.level.justinebateman.github.io.testrailintegration.testrail.service: DEBUG
```

## Running tests

To enable running the tests and setting the TestRail configuration via command line, first add the following to your test project pom file build plugins

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven-surefire-plugin.version}</version>
    <executions>
        <execution>
            <id>surefire-it</id>
            <phase>integration-test</phase>
            <goals>
                <goal>test</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <systemProperties>
            <updateTestRail>${updateTestRail}</updateTestRail>
            <testRunId>${testRunId}</testRunId>
            <defects>${defects}</defects>
        </systemProperties>
        <suiteXmlFiles>
            <suiteXmlFile>./src/test/resources/Tests.xml</suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

Add a file called Tests.xml in src/test/resources with the following structure. Be sure to set your package name


```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Test Suite" verbose="1">
    <parameter name="updateTestRail" value="${updateTestRail}"/>
    <parameter name="testRunId" value="${testRunId}"/>
    <parameter name="defects" value="${defects}"/>
    <test name="Test Suite">
        <packages>
            <package name="YOUR.PROJECT.PACKAGE.*"/>
        </packages>
    </test>
</suite>
```


To run your tests via command line run this command and set your parameters accordingly

```
mvn clean test -U -DupdateTestRail=true -DtestRunId=0 -Ddefects=OPS-123 -Dgroups=smoke
```

The defects parameter can be left empty if you don't need to link your test results to a JIRA ticket

The groups parameter can be removed if you just want to run all tests in the project

You can run all tests except for an excluded group with:

```
mvn clean test -U -DupdateTestRail=true -DtestRunId=0 -Ddefects= -Dgroups= -DexcludedGroups=known-issues
```