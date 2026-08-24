package com.smartsociety.platform.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="app.media.provider", havingValue="local", matchIfMissing=true)
public class LocalMediaStorage implements MediaStorage {
    private final Path root;
    public LocalMediaStorage(@Value("${app.media.root}") String r) { root = Paths.get(r).toAbsolutePath().normalize(); }

    @Override public String store(InputStream input, String filename, String mime, long size) throws IOException {
        Files.createDirectories(root);
        String safe = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        String key = UUID.randomUUID() + "-" + safe;
        Files.copy(input, root.resolve(key).normalize(), StandardCopyOption.REPLACE_EXISTING);
        return key;
    }

    @Override public StoredMedia read(String key) throws IOException {
        Path file = root.resolve(key).normalize();
        if (!file.startsWith(root) || !Files.isRegularFile(file)) throw new NoSuchFileException(key);
        String mime = Files.probeContentType(file);
        return new StoredMedia(Files.newInputStream(file), mime == null ? "application/octet-stream" : mime, Files.size(file));
    }
}
