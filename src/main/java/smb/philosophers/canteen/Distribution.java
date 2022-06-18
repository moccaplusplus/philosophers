package smb.philosophers.canteen;

import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public enum Distribution {

    FIXED {
        @Override
        public double getNext(double l) {
            return 1 / l;
        }
    },

    RANDOM {

        private final Random random = new Random();

        @Override
        public double getNext(double l) {
            final var d = FIXED.getNext(l);
            return d * (1 + 0.75 * min(1, max(-1, random.nextGaussian())));
        }
    };

    public abstract double getNext(double l);
}
