package smb.philosophers;

import java.util.Locale;
import java.util.ResourceBundle;

import static java.text.MessageFormat.format;

public class Messages {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static String msg(String key) {
        return BUNDLE.getString(key);
    }

    public static String msg(String key, Object... args) {
        return format(msg(key), args);
    }

    public static ResourceBundle getBundle() {
        return BUNDLE;
    }
}
