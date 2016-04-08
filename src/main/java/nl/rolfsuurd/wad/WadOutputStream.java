package nl.rolfsuurd.wad;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This class is used to write {@link WadEntry entries} to an output stream.
 *
 * @author Rolf
 */
public class WadOutputStream extends OutputStream {
    private WadType wadType;
    private DataOutputStream out;

    private ByteArrayOutputStream buffer;
    private Collection<WadEntry> entries;

    private String name;
    private Integer size;

    private boolean closed;

    /**
     * Creates a new {@code WAD} output stream. The type of {@code WAD} that will be created
     * is an {@link WadType#Internal IWAD}.
     *
     * @param out the underlying stream to write to.
     */
    public WadOutputStream(OutputStream out) {
        this(WadType.Internal, out);
    }

    /**
     * Creates a new {@code WAD} output stream of the specified {@link WadType}.
     *
     * @param wadType what type of {@code WAD} will be created.
     * @param out the underlying stream to write to.
     */
    public WadOutputStream(WadType wadType, OutputStream out) {
        this.wadType = wadType;
        this.out = new DataOutputStream(out);

        entries = new LinkedList<>();
        buffer = new ByteArrayOutputStream(2048);
    }

    /**
     * Begin writing the next {@link WadEntry WAD-entry}. Closes the current entry if needed.
     *
     * @param name the name of the entry.
     * @throws WadException if the next entry could not be started.
     */
    public void putNextEntry(String name) throws WadException {
        checkOpen("add");
        closeActiveEntry();

        this.name = name;
        this.size = 0;
    }

    /**
     * Writes a byte for the current {@link WadEntry WAD-entry}
     *
     * @param b the {@code byte}.
     * @throws  IOException  if an I/O error occurs.
     */
    public void write(int b) throws IOException {
        checkOpen("write");

        if (!hasActiveEntry()) {
            throw new WadException("No active entry");
        }

        buffer.write(b);
        size ++;
    }

    /**
     * Closes the current {@link WadEntry WAD-entry}.
     *
     * @throws WadException if no {@link WadEntry WAD-entry} is active.
     */
    public void closeEntry() throws WadException {
        if (name == null) {
            throw new WadException("No WAD-entry active");
        }

        entries.add(new WadEntry(name, buffer.size() - size, size));

        name = null;
        size = null;
    }

    @Override
    public void close() throws IOException {
        checkOpen("write");
        closeActiveEntry();

        out.write(wadType.getCode().getBytes());
        out.writeInt(Integer.reverseBytes(entries.size()));
        out.writeInt(Integer.reverseBytes(HEADER_SIZE));

        int directorySize = entries.size() * ENTRY_SIZE;

        for (WadEntry entry: entries) {
            out.writeInt(Integer.reverseBytes(HEADER_SIZE + directorySize + entry.getLocation()));
            out.writeInt(Integer.reverseBytes(entry.getSize()));
            out.write(String.format("%1$-8s", entry.getName()).replace(' ', '\0').getBytes());
        }

        out.write(buffer.toByteArray());

        out.close();
        buffer.close();
        entries.clear();

        closed = true;
    }

    private void checkOpen(String operation) throws WadException {
        if (closed) {
            throw new WadException(String.format("Can not %s WAD-entries to closed stream", operation));
        }
    }

    private boolean hasActiveEntry() {
        return name != null;
    }

    private void closeActiveEntry() throws WadException {
        if (hasActiveEntry()) {
            closeEntry();
        }
    }

    private static final int HEADER_SIZE = 12;
    private static final int ENTRY_SIZE = 16;

    public static void main(String... args) throws IOException {
        try (WadOutputStream wos = new WadOutputStream(new FileOutputStream("src/test/resources/test.wad"))) {
            wos.putNextEntry("test");
            wos.write("bytes".getBytes());
            wos.closeEntry();
        }
    }
}
