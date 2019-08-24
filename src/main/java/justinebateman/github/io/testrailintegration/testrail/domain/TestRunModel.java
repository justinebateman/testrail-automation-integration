package justinebateman.github.io.testrailintegration.testrail.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import java.util.List;

@Data
@Builder(toBuilder = true)
@Wither
@NoArgsConstructor
@AllArgsConstructor
public class TestRunModel
{
    int suite_id;
    String name;
    String description;
    @Builder.Default
    Integer milestone_id = null;
    Boolean include_all;
    List<Integer> case_ids;
}
