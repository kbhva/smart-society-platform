package com.smartsociety.platform.media;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface MediaStorage {
    String store(InputStream input, String filename, String mime, long size) throws IOException;
    StoredMedia read(String key) throws IOException;

    record StoredMedia(InputStream input, String mimeType, long contentLength) implements AutoCloseable {
        @Override public void close() throws IOException { input.close(); }
    }
}
