package nl.rolfsuurd.wad;

import java.util.Objects;

/**
 * A single {@code WAD-entry} which can be used to read data from a {@link WadFile WAD-File} or write to a
 * {@link WadOutputStream}.
 *
 * @see WadFile
 * @see WadOutputStream
 * @author Rolf
 */
public class WadEntry {
    private String name;
    private int location;
    private int size;

    /**
     * Constructs a named {@code WAD-entry}.
     *
     * @param name the name of this entry
     */
    public WadEntry(String name) {
        this(name, -1, -1);
    }

    /**
     * Constructs a named {@code WAD-entry} with location and size
     *
     * @param name the name of this entry
     * @param location at what location within the WAD file this entry can be found
     * @param size the size of this entry
     */
    public WadEntry(String name, int location, int size) {
        Objects.requireNonNull(name, "name");
        if (name.length() > 8) {
            throw new IllegalArgumentException("entry name too long");
        }

        this.name = name;
        this.location = location;
        this.size = size;
    }

    /**
     *
     * @return the name of this entry
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return at what location within the WAD file this entry can be found
     */
    public int getLocation() {
        return location;
    }

    /**
     *
     * @return the size of this entry
     */
    public int getSize() {
        return size;
    }
}
