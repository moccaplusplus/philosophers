package smb.philosophers.canteen;

import smb.philosophers.Settings;
import smb.philosophers.canteen.event.EventBus;
import smb.philosophers.canteen.report.Logger;
import smb.philosophers.canteen.report.Reporter;

public class Canteen {
    public static void run(Settings settings, Logger logger) {
        final var eventBus = new EventBus();
        final var clock = new Clock(settings, eventBus);
        new Table(settings, eventBus);
        new Reporter(settings, eventBus, logger);
        clock.start();
    }
}
