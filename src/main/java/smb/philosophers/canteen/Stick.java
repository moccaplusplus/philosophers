package smb.philosophers.canteen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Stick {
    private final int n;
    private boolean taken;
}
