package pl.nlogn.aws.integration;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

@Component
public class S3Uploader {
    private static final String BUCKET_NAME = "upload1";

    private static final Region REGION = Region.US_EAST_1;

    private static final String ACCESS_KEY = "dummyAccessKey";
    private static final String SECRET_KEY = "dummySecretKey";

    public AwsCredentialsProvider awsCredentialsProvider() {
        return () -> AwsBasicCredentials.create(
                ACCESS_KEY, SECRET_KEY);
    }

    private S3AsyncClient s3AsyncClient() throws URISyntaxException {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(3)
                .build();

        URI endpointOverride = new URI("http://localhost:4566");

        return S3AsyncClient.builder()
                .httpClient(httpClient)
                .region(REGION)
                .credentialsProvider(awsCredentialsProvider())
                .endpointOverride(endpointOverride)
                .build();
    }

    public CompletableFuture<PutObjectResponse> uploadAsync(String key, Flux<ByteBuffer> byteBufferFlux, int contentLength) throws Exception {
        S3AsyncClient s3 = s3AsyncClient();

        return s3.putObject(PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .contentLength((long) contentLength)
                        .key(key)
                        .contentType("application/octet-stream")
                        .build(),
                AsyncRequestBody.fromPublisher(byteBufferFlux));
    }
}
