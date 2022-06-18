package smb.philosophers.canteen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Bowl {

    private final int n;
    private final Stick leftStick;
    private final Stick rightStick;

    public boolean isTaken() {
        return leftStick.isTaken() || rightStick.isTaken();
    }

    public void setTaken(boolean taken) {
        leftStick.setTaken(taken);
        rightStick.setTaken(taken);
    }
}
