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
public class TestRunResponseModel
{
    int id;
    int suite_id;
    String name;
    String description;
    int milestone_id;
    int assignedto_id;
    Boolean include_all;
    List<Integer> case_ids;
}
