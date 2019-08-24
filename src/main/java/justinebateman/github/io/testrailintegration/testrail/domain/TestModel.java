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
public class TestModel
{
    Integer assignedto_id;
    Integer case_id;
    String estimate;
    String estimate_forecast;
    Integer id;
    Integer milestone_id;
    Integer priority_id;
    String refs;
    Integer run_id;
    Integer status_id;
    String title;
    Integer type_id;
}
