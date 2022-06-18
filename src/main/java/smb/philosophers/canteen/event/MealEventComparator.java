package smb.philosophers.canteen.event;

import java.util.Comparator;

import static smb.philosophers.canteen.event.MealEvent.Type.FINISHED;
import static smb.philosophers.canteen.event.MealEvent.Type.REQUESTED;

public class MealEventComparator implements Comparator<MealEvent> {

    public static final double PRECISION = 0.000001d;

    @Override
    public int compare(MealEvent o1, MealEvent o2) {
        final var timeDiff = o1.getTimestamp() - o2.getTimestamp();
        if (Math.abs(timeDiff) < PRECISION) {
            if (o1.getType() != o2.getType()) {
                if (o1.getType() == FINISHED) return -1;
                if (o2.getType() == FINISHED) return 1;
                if (o1.getType() == REQUESTED) return -1;
                if (o2.getType() == REQUESTED) return 1;
            }
            return 0;
        }
        return timeDiff < 0 ? -1 : 1;
    }
}
