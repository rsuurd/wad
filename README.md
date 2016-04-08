## Wad

Wad enables you to use data that use DOOM's WAD file format.

## Usage

### Reading

This code snipped shows you how to open a WAD file and how to obtain an `InputStream` for each entry:

```java
    try (WadFile wadFile = new WadFile("path/to/DOOM.wad")) {
        for (WadEntry entry: wadFile.entries()) {
            InputStream in = wadFile.getInputStream(entry);

             // do something with the stream
        }
    }
```

### Writing

To create your own WAD files:

```java
    WadOutputStream wadOutputStream = new WadOutputStream(new FileOutputStream("/path/to/file.wad");

    wadOutputStream.putNextEntry("name");
    wadOutputStream.write(data);
    wadOutputStream.closeEntry();
    wadOutputStream.close();
```

