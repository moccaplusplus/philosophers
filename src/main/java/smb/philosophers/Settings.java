package smb.philosophers;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import smb.philosophers.canteen.Distribution;

import java.util.ArrayList;
import java.util.List;

import static smb.philosophers.Messages.msg;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Parameters(separators = "=", resourceBundle = "messages")
public class Settings {

    @Parameter(names = {"-t", "--time"}, descriptionKey = "philosophers.settings.t.shortInfo", order = 1)
    private int time = 10;

    @Parameter(names = {"-m", "--mi"}, descriptionKey = "philosophers.settings.mi.shortInfo", order = 1)
    private double mi = 1.0;

    @Parameter(names = {"-d", "--distribution"}, descriptionKey = "philosophers.settings.distribution.shortInfo", order = 1)
    private Distribution distribution = Distribution.FIXED;

    @Parameter(names = {"-l", "--lambda"}, descriptionKey = "philosophers.settings.lambda.shortInfo", order = 1)
    private List<Double> lambda = List.of(0.7, 0.8, 0.9, 0.8, 0.7);

    @Parameter(names = {"-n", "--no-gui"}, descriptionKey = "philosophers.settings.noGui.shortInfo", order = 2)
    private boolean noGui;

    @Parameter(names = {"-h", "--help"}, descriptionKey = "philosophers.settings.help.shortInfo", order = 2)
    private boolean help;

    public int getCount() {
        return lambda.size();
    }

    public void validate() throws IllegalStateException {
        if (help) return;
        final var errors = new ArrayList<String>();
        if (time <= 0) errors.add(msg("philosophers.validation.negative", "--time"));
        if (mi <= 0) errors.add(msg("philosophers.validation.negative", "--mi"));
        if (lambda.size() % 2 != 1) errors.add(msg("philosophers.validation.lambdaEven"));
        if (lambda.stream().anyMatch(d -> d > mi)) errors.add(msg("philosophers.validation.lambdaBiggerThanMi"));
        if (errors.size() > 0) {
            errors.add(0, msg("philosophers.validation.errors"));
            throw new IllegalStateException(String.join("\n", errors));
        }
    }
}
