package com.smartsociety.platform.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.util.UUID;

@Service
@ConditionalOnProperty(name="app.media.provider", havingValue="s3")
public class S3MediaStorage implements MediaStorage {
    private final S3Client client; private final String bucket; private final String prefix;
    public S3MediaStorage(S3Client client,@Value("${app.media.s3.bucket}") String bucket,@Value("${app.media.s3.prefix:complaint-photos/}") String prefix){this.client=client;this.bucket=bucket;this.prefix=prefix;}
    @Override public String store(InputStream input,String filename,String mime,long size)throws IOException{
        String key=prefix+UUID.randomUUID()+"-"+filename.replaceAll("[^a-zA-Z0-9._-]","_");
        client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(mime).contentLength(size).build(),RequestBody.fromInputStream(input,size)); return key;
    }
    @Override public StoredMedia read(String key)throws IOException{
        try{ResponseInputStream<GetObjectResponse> in=client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());return new StoredMedia(in,in.response().contentType(),in.response().contentLength());}
        catch(S3Exception e){if(e.statusCode()==404)throw new FileNotFoundException(key);throw e;}
    }
}
