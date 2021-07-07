package pl.nlogn.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.nlogn.aws.integration.S3Uploader;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class S3UploaderTest {
    private S3Uploader sut;

    private ByteBuffer getRandomByteBuffer(int size) {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }

    @BeforeEach
    public void setUp() {
        sut = new S3Uploader();
    }

    @Test
    public void testUploadAsync() throws Exception {
        int size = 100;

        CompletableFuture<PutObjectResponse> uploadFuture = sut.uploadAsync("test1", Flux.just(getRandomByteBuffer(size)), size);

        PutObjectResponse response = uploadFuture.get();
        SdkHttpResponse sdkHttpResponse = response.sdkHttpResponse();
        assertTrue(sdkHttpResponse.isSuccessful());
        assertThat(sdkHttpResponse.headers().get("content-length").get(0).equals("100"));
    }
}
