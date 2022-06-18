package smb.philosophers.canteen.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@Builder
@ToString
public class ClockEvent implements Event<ClockEvent.Type> {

    public enum Type {
        CLOCK_START,
        CLOCK_END
    }

    private final Type type;
    private final double timestamp;
}
