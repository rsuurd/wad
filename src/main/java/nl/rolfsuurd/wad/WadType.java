package nl.rolfsuurd.wad;

/**
 * Describes the types of {@code WADs} we support.
 *
 * @author Rolf
 */
public enum WadType {
    Internal("IWAD"),
    Patch("PWAD");

    private String code;

    private WadType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
