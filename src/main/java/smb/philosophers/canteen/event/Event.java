package smb.philosophers.canteen.event;

public interface Event<T> {
    T getType();
}
