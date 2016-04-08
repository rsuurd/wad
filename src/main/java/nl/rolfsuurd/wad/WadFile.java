package nl.rolfsuurd.wad;

import java.io.*;
import java.util.*;

/**
 * This class is used to read {@link WadEntry entries} from a {@code WAD-file}.
 *
 * @author Rolf
 */
public class WadFile implements Closeable {
    private final RandomAccessFile file;

    private Collection<WadEntry> entries;
    private Collection<WadFileInputStream> streams;

    /**
     * Opens a {@code WAD-file} for reading.
     *
     * @param name the name of the {@code WAD-file}
     * @throws WadException if the {@code WAD-file} could not be read
     */
    public WadFile(String name) throws WadException {
        this(new File(name));
    }

    /**
     * Opens a {@code WAD-file} to read from the specified <code>File</code> object.
     *
     * @param file the {@code WAD-file} to be opened for reading
     * @throws WadException if the {@code WAD-file} could not be read
     */
    public WadFile(File file) throws WadException {
        try {
            this.file = new RandomAccessFile(file, "r");

            byte[] header = new byte[4];
            this.file.read(header);

            if (!VALID_HEADERS.contains(new String(header))) {
                throw new WadException("Not a WAD-file.");
            }

            int numberOfEntries = Integer.reverseBytes(this.file.readInt());
            this.file.seek(Integer.reverseBytes(this.file.readInt()));

            entries = new ArrayList<>(numberOfEntries);
            for (int i = 0; i < numberOfEntries; i ++) {
                int entryLocation = Integer.reverseBytes(this.file.readInt());
                int entrySize = Integer.reverseBytes(this.file.readInt());
                byte[] nameBytes = new byte[8];
                this.file.read(nameBytes);

                String name = new String(nameBytes).replace(Character.toString('\0'), "");

                entries.add(new WadEntry(name, entryLocation, entrySize));
            }

            streams = new LinkedList<>();
        } catch (IOException exception) {
            throw new WadException(String.format("Could not open %s for reading.", file.getName()), exception);
        }
    }

    /**
     * @return an {@link Iterable} of {@link WadEntry WAD-File entries}
     */
    public Iterable<WadEntry> entries() {
        return entries;
    }

    /**
     * Tries to find the {@link WadEntry WAD-Entry} within the {@code WAD-File}.
     *
     * @param name the name of the {@link WadEntry WAD-Entry}
     * @return the {@link WadEntry WAD-Entry} with the specified name, or {@code null} if it was not found
     */
    public WadEntry getEntry(String name) {
        return entries.stream().filter(wadEntry -> wadEntry.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Opens an {@link InputStream} for reading a single {@link WadEntry WAD-Entry}
     *
     * @param entry the {@link WadEntry WAD-Entry} to open the {@link InputStream} for
     * @return an {@link InputStream} with the specified {@link WadEntry WAD-Entry}'s data
     *
     * @throws WadException if the {@link WadEntry WAD-Entry} could not be read
     */
    public InputStream getInputStream(WadEntry entry) throws WadException {
        WadFileInputStream inputStream = new WadFileInputStream(getEntry(entry.getName()));

        streams.add(inputStream);

        return inputStream;
    }

    /**
     * Closes this {@code WAD-File} and all associated {@link #getInputStream(WadEntry) streams}.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        entries.clear();

        for (InputStream inputStream: streams) {
            inputStream.close();
        }

        file.close();
    }

    private byte[] readWadEntry(WadEntry entry) throws WadException {
        try {
            file.seek(entry.getLocation());

            byte[] bytes = new byte[entry.getSize()];

            if (file.read(bytes) == -1) {
                throw new IOException("Unexpected end of file");
            }

            return bytes;
        } catch (IOException exception) {
            throw new WadException(String.format("Wad-entry %s could not be read", entry.getName()), exception);
        }
    }

    private static List<String> VALID_HEADERS = Arrays.asList(WadType.Internal.getCode(), WadType.Patch.getCode());

    private class WadFileInputStream extends InputStream {
        private InputStream delegate;

        public WadFileInputStream(WadEntry entry) throws WadException {
            delegate = new ByteArrayInputStream(readWadEntry(entry));
        }

        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }
}
