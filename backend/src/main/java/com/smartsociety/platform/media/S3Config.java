package com.smartsociety.platform.media;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import java.net.URI;

@org.springframework.context.annotation.Configuration
@ConditionalOnProperty(name="app.media.provider", havingValue="s3")
public class S3Config {
    @Bean S3Client s3Client(@Value("${app.media.s3.region:ap-south-1}") String region,@Value("${app.media.s3.endpoint:}") String endpoint,@Value("${app.media.s3.access-key:}") String accessKey,@Value("${app.media.s3.secret-key:}") String secretKey){
        var b=S3Client.builder().region(Region.of(region));
        if(!accessKey.isBlank()&&!secretKey.isBlank())b.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey,secretKey)));
        if(!endpoint.isBlank())b.endpointOverride(URI.create(endpoint));
        return b.build();
    }
}
