package smb.philosophers.canteen.event;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public class EventBus {

    private static final int DEFAULT_PRIORITY = 100;

    @SuppressWarnings("rawtypes")
    private final Map<Class, List> consumers = new HashMap<>();

    public void publishEvent(Object event) {
        getForEvent(event).stream().map(Pair::getValue).forEach(c -> c.accept(event));
    }

    @SafeVarargs
    public final <E extends Event<T>, T> void addListener(Class<E> type, Consumer<E> consumer, Predicate<T>... predicates) {
        addListener(DEFAULT_PRIORITY, type, consumer, predicates);
    }

    @SafeVarargs
    public final <E extends Event<T>, T> void addListener(int priority, Class<E> type, Consumer<E> consumer, Predicate<T>... predicates) {
        final var list = computeForType(type);
        final var pair = Pair.of(priority, withPredicates(consumer, predicates));
        final var index = Collections.binarySearch(list, pair, Comparator.comparingInt(Pair::getKey));
        if (index < 0) {
            list.add(-index - 1, pair);
        } else {
            list.add(index, pair);
        }
    }

    private <E extends Event<T>, T> Consumer<E> withPredicates(Consumer<E> consumer, Predicate<T>[] predicates) {
        return predicates.length == 0 ? consumer : e -> {
            if (Stream.of(predicates).allMatch(p -> p.test(e.getType()))) consumer.accept(e);
        };
    }

    @SuppressWarnings("unchecked")
    private Collection<Pair<Integer, Consumer<Object>>> getForEvent(Object event) {
        return consumers.getOrDefault(event.getClass(), emptyList());
    }

    @SuppressWarnings("unchecked")
    private <E> List<Pair<Integer, Consumer<E>>> computeForType(Class<E> type) {
        return consumers.computeIfAbsent(type, k -> new ArrayList<>());
    }
}
