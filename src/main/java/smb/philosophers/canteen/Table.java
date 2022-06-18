package smb.philosophers.canteen;

import smb.philosophers.Settings;
import smb.philosophers.canteen.event.EventBus;
import smb.philosophers.canteen.event.MealEvent;

import java.util.Map;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static smb.philosophers.canteen.event.MealEvent.Type.FINISHED;
import static smb.philosophers.canteen.event.MealEvent.Type.REJECTED;
import static smb.philosophers.canteen.event.MealEvent.Type.REQUESTED;
import static smb.philosophers.canteen.event.MealEvent.Type.STARTED;

public class Table {

    private final EventBus eventBus;
    private final Map<Integer, Bowl> bowls;

    public Table(Settings settings, EventBus eventBus) {
        this.eventBus = eventBus;
        final var sticks = range(0, settings.getCount()).mapToObj(Stick::new).collect(toList());
        bowls = IntStream.range(0, settings.getCount())
                .mapToObj(i -> new Bowl(i, sticks.get(i), sticks.get((i + 1) % settings.getCount())))
                .collect(toMap(Bowl::getN, identity()));
        eventBus.addListener(MealEvent.class, this::onMealRequest, REQUESTED::equals);
        eventBus.addListener(MealEvent.class, this::onMealFinished, FINISHED::equals);
    }

    private void onMealRequest(MealEvent event) {
        var bowl = bowls.get(event.getTarget());
        var eventBuilder = MealEvent.builder()
                .target(event.getTarget()).timestamp(event.getTimestamp());
        if (bowl.isTaken()) {
            eventBuilder.type(REJECTED);
        } else {
            bowl.setTaken(true);
            eventBuilder.type(STARTED);
        }
        eventBus.publishEvent(eventBuilder.build());
    }

    private void onMealFinished(MealEvent event) {
        bowls.get(event.getTarget()).setTaken(false);
    }
}
