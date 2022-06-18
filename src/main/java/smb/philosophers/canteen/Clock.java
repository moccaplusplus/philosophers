package smb.philosophers.canteen;

import smb.philosophers.Settings;
import smb.philosophers.canteen.event.ClockEvent;
import smb.philosophers.canteen.event.EventBus;
import smb.philosophers.canteen.event.MealEvent;
import smb.philosophers.canteen.event.MealEventComparator;

import java.util.PriorityQueue;

import static smb.philosophers.canteen.event.ClockEvent.Type.CLOCK_END;
import static smb.philosophers.canteen.event.ClockEvent.Type.CLOCK_START;
import static smb.philosophers.canteen.event.MealEvent.Type.FINISHED;
import static smb.philosophers.canteen.event.MealEvent.Type.REQUESTED;
import static smb.philosophers.canteen.event.MealEvent.Type.STARTED;

public class Clock {

    private final Settings settings;
    private final EventBus eventBus;
    private final PriorityQueue<MealEvent> queue = new PriorityQueue<>(new MealEventComparator());

    public Clock(Settings settings, EventBus eventBus) {
        this.settings = settings;
        this.eventBus = eventBus;
        for (var i = 0; i < settings.getCount(); i++) {
            final var lambda = settings.getLambda().get(i);
            final var distribution = settings.getDistribution();
            for (var t = distribution.getNext(lambda); t <= settings.getTime(); t += distribution.getNext(lambda)) {
                queue.add(new MealEvent(REQUESTED, t, i));
            }
        }
        eventBus.addListener(MealEvent.class, this::onMealStarted, STARTED::equals);
    }

    public void start() {
        eventBus.publishEvent(new ClockEvent(CLOCK_START, 0));
        while (!queue.isEmpty()) {
            final var tick = queue.poll();
            eventBus.publishEvent(tick);
        }
        eventBus.publishEvent(new ClockEvent(CLOCK_END, settings.getTime()));
    }

    public void onMealStarted(MealEvent event) {
        final var max = queue.stream()
                .filter(e -> e.getTarget() == event.getTarget() && e.getType() == REQUESTED)
                .mapToDouble(MealEvent::getTimestamp).min();
        var endTimestamp = event.getTimestamp() + settings.getDistribution().getNext(settings.getMi());
        if (max.isPresent()) endTimestamp = Math.min(endTimestamp, max.getAsDouble());
        if (endTimestamp <= settings.getTime()) {
            queue.add(new MealEvent(FINISHED, endTimestamp, event.getTarget()));
        }
    }
}
