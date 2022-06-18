package smb.philosophers.canteen.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@Builder
@ToString
public class MealEvent implements Event<MealEvent.Type> {

    public enum Type {
        REQUESTED,
        STARTED,
        REJECTED,
        FINISHED;
    }

    private final Type type;
    private final double timestamp;
    private final int target;
}
