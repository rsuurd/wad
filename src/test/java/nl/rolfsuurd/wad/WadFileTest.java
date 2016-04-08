package nl.rolfsuurd.wad;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class WadFileTest {
    @Test
    public void shouldReadWadFileEntries() throws IOException {
        try (WadFile wadFile = new WadFile(new File(getClass().getResource("/test.wad").getFile()))) {
            WadEntry entry = wadFile.getEntry("test");

            assertEquals("test", entry.getName());

            InputStream in  = wadFile.getInputStream(entry);

            byte[] bytes = new byte[entry.getSize()];
            int bytesRead = in.read(bytes);

            assertEquals(bytesRead, entry.getSize());
            assertArrayEquals("bytes".getBytes(), bytes);
        }
    }
}
