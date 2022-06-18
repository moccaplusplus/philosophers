package smb.philosophers.canteen.report;

@FunctionalInterface
public interface Logger {

    void log(String msg);

    default void log() { log(""); }
}
