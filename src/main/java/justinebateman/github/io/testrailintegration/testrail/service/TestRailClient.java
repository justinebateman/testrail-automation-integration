package justinebateman.github.io.testrailintegration.testrail.service;

import justinebateman.github.io.testrailintegration.testrail.configuration.TestRailConfiguration;
import justinebateman.github.io.testrailintegration.testrail.domain.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@ActiveProfiles("TestRail")
@FeignClient(name = "testrail", configuration = TestRailConfiguration.class, url = "${testrail.apiendpoint}")
public interface TestRailClient
{
    String api = "/index.php/api/v2";

    @GetMapping(value = api + "/get_results/{test_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<List<ResultResponse>> getTestResults(@PathVariable("test_id") String testId);

    @GetMapping(value = api + "/get_user/{user_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<User> getUser(@PathVariable("user_id") String userId);

    @PostMapping(value = api + "/add_run/{project_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<TestRunResponseModel> addTestRun(@PathVariable("project_id") int projectId, @RequestBody TestRunModel testRunModel);

    @PostMapping(value = api + "/add_result_for_case/{run_id}/{case_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<ResultResponse> addResultForCase(@PathVariable("run_id") int runId, @PathVariable("case_id") int caseId, @RequestBody ResultModel resultModel);

    @PostMapping(value = api + "/update_case/{case_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<TestCaseModel> updateCase(@PathVariable("case_id") int caseId, @RequestBody TestCaseModel testCaseModel);

    @GetMapping(value = api + "/get_case/{case_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<TestCaseModel> getCase(@PathVariable("case_id") int caseId);

    @GetMapping(value = api + "/get_suite/{suite_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<TestSuiteModel> getSuite(@PathVariable("suite_id") int suiteId);

    @GetMapping(value = api + "/get_run/{run_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<TestRunResponseModel> getRun(@PathVariable("run_id") int runId);

    @PostMapping(value = api + "/update_run/{run_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<TestRunResponseModel> updateRun(@PathVariable("run_id") int runId, @RequestBody TestRunModel testRunModel);

    @GetMapping(value = api + "/get_tests/{run_id}", consumes = {APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity<List<TestModel>> getTests(@PathVariable("run_id") int runId);

}
