package nl.rolfsuurd.wad;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WadOutputStreamTest {
    private @Mock OutputStream out;

    @Test
    public void shouldCreateWadFile() throws IOException {
        WadOutputStream wadOutputStream = new WadOutputStream(out);

        wadOutputStream.putNextEntry("test");
        wadOutputStream.write("bytes".getBytes());
        wadOutputStream.closeEntry();
        wadOutputStream.close();

        verify(out).write("IWAD".getBytes(), 0 , 4);
    }

    @Test
    public void shouldCloseEntryOnNew() throws IOException {
        WadOutputStream wadOutputStream = spy(new WadOutputStream(out));

        wadOutputStream.putNextEntry("one");
        wadOutputStream.putNextEntry("two");

        verify(wadOutputStream).closeEntry();
    }

    @Test
    public void shouldCloseEntryOnClose() throws IOException {
        WadOutputStream wadOutputStream = spy(new WadOutputStream(out));

        wadOutputStream.putNextEntry("one");
        wadOutputStream.close();

        verify(wadOutputStream).closeEntry();
    }

    @Test(expected = WadException.class)
    public void shouldRequireEntryForWriting() throws IOException {
        WadOutputStream wadOutputStream = spy(new WadOutputStream(out));

        wadOutputStream.write("bytes".getBytes());
    }

    @Test(expected = WadException.class)
    public void shouldRequireEntryForClosing() throws IOException {
        WadOutputStream wadOutputStream = spy(new WadOutputStream(out));

        wadOutputStream.closeEntry();
    }

    @Test(expected = WadException.class)
    public void shouldNotAllowOperationsOnClosedStream() throws IOException {
        WadOutputStream wadOutputStream = new WadOutputStream(out);

        wadOutputStream.putNextEntry("test");
        wadOutputStream.close();
        wadOutputStream.putNextEntry("closed");
    }
}
