package justinebateman.github.io.testrailintegration.testconfig;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigProperties
{
    String propertyFilePath = "./src/main/resources/Config.properties";

    public int projectId = 0;
    public Boolean markTestCaseAsAutomated = false;
    public Boolean updateTestRail = false;
    public int testRunId = 0;
    public String defects = "";

    public String keyProjectId = "projectId";
    public String keyMarkTestCaseAsAutomated = "markTestCaseAsAutomated";
    public String keyUpdateTestRail = "updateTestRail";
    public String keyTestRunId = "testRunId";
    public String keyDefects = "defects";

    public ConfigProperties()
    {
        String projectId = readValue(keyProjectId);
        String markTestCaseAsAutomated = readValue(keyMarkTestCaseAsAutomated);
        String updateTestRail = readValue(keyUpdateTestRail);
        String testRunId = readValue(keyTestRunId);
        String defects = readValue(keyDefects);

        if (StringUtils.isNotEmpty(projectId) && StringUtils.isNumeric(projectId))
            this.projectId = Integer.parseInt(projectId);
        if (StringUtils.isNotEmpty(markTestCaseAsAutomated) && BooleanUtils.toBooleanObject(markTestCaseAsAutomated))
            this.markTestCaseAsAutomated = Boolean.parseBoolean(markTestCaseAsAutomated);
        if (StringUtils.isNotEmpty(updateTestRail) && BooleanUtils.toBooleanObject(updateTestRail))
            this.updateTestRail = Boolean.parseBoolean(updateTestRail);
        if (StringUtils.isNotEmpty(testRunId) && StringUtils.isNumeric(testRunId))
            this.testRunId = Integer.parseInt(testRunId);
        if (StringUtils.isNotEmpty(defects))
            this.defects = defects;
    }

    public Properties loadProperties()
    {
        Properties prop = new Properties();
        try
        {
            FileInputStream fileInput = new FileInputStream(propertyFilePath);
            prop.load(fileInput);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return prop;
    }

    public String readValue(String key)
    {
        String value;
        try
        {
            value = loadProperties().getProperty(key);
        }
        catch (Exception ex)
        {
            value = "";
        }
        return value;
    }

    public String writeValue(String key, String value)
    {
        Properties prop = loadProperties();
        prop.setProperty(key, value);
        try
        {
            prop.store(new FileOutputStream(propertyFilePath), null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return readValue(key);
    }

}
