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
public class TestCaseModel
{
    String title;
    @Builder.Default
    Integer template_id = 1;
    Integer type_id;
    Integer priority_id;
    String estimate;
    Integer milestone_id;
    String refs;
}
