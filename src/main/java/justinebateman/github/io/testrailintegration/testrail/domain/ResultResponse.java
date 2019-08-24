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
public class ResultResponse
{
    int assignedto_id;
    String comment;
    int created_by;
    String created_on;
    String defects;
    String elapsed;
    int id;
    int status_id;
    int test_id;
    String version;
}
