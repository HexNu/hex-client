package nu.hex.client;

/**
 * Created 2017-okt-06
 *
 * @author hl
 */
public enum Provider {
    DROPBOX("Dropbox"),
    GOOGLE_DRIVE("Google Drive");
    private final String name;

    private Provider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return name().toLowerCase().replaceAll("_", "-");
    }
}
