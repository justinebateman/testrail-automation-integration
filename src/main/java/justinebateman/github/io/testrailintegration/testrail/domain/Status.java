package justinebateman.github.io.testrailintegration.testrail.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status
{
    PASSED(1, "Passed"),
    BLOCKED(2, "Blocked"),
    UNTESTED(3, "Untested"),
    RETEST(4, "Retest"),
    FAILED(5, "Failed");

    private final int id;
    private final String label;

    public int value()
    {
        return this.id;
    }
}
