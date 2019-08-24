package justinebateman.github.io.testrailintegration.testrail.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@Builder(toBuilder = true)
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class TestSuiteModel
{
    String completed_on;
    String description;
    Integer id;
    Boolean is_baseline;
    Boolean is_completed;
    Boolean is_master;
    String name;
    Integer project_id;
    String url;
}
