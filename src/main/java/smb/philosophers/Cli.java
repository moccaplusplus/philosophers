package smb.philosophers;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.UnixStyleUsageFormatter;
import javafx.application.Application;
import smb.philosophers.canteen.Canteen;
import smb.philosophers.gui.Gui;

import java.io.PrintStream;
import java.util.Optional;

import static java.lang.System.exit;
import static smb.philosophers.Messages.msg;

public class Cli {

    public static void main(String... args) {
        new Cli().read(args);
    }

    private final Settings settings = new Settings();
    private final JCommander jCommander = JCommander.newBuilder()
            .programName(msg("philosophers.program.cmdLine"))
            .addObject(settings)
            .build();

    public void read(String... args) {
        try {
            jCommander.parse(args);
            settings.validate();
        } catch (Exception e) {
            printError(e);
            exit(1);
            return;
        }

        if (settings.isHelp()) {
            printHelp();
            return;
        }

        if (settings.isNoGui()) {
            try {
                Canteen.run(settings, System.out::println);
            } catch (Exception e) {
                final var error = Optional.of(e).map(Exception::getMessage).orElseGet(() -> e.getClass().getName());
                System.err.println(error);
                exit(1);
            }
        } else {
            Gui.settings = settings;
            Application.launch(Gui.class);
        }
    }

    private void printHelp() {
        printHelp(System.out);
    }

    private void printHelp(PrintStream out) {
        final var formatter = new UnixStyleUsageFormatter(jCommander);
        final var helpContents = new StringBuilder();
        formatter.usage(helpContents);
        out.println(helpContents);
    }

    private void printError(Exception error) {
        printError(System.err, error);
    }

    private void printError(PrintStream out, Exception error) {
        printHelp(out);
        Optional.ofNullable(error).map(Exception::getMessage).ifPresent(out::println);
    }
}
