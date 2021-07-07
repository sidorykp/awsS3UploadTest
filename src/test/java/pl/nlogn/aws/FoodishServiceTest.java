package pl.nlogn.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.nlogn.aws.domain.Base64Encoder;
import pl.nlogn.aws.domain.BurgerResponse;
import pl.nlogn.aws.integration.FoodishDownloader;
import pl.nlogn.aws.integration.S3Uploader;
import pl.nlogn.aws.service.FoodishService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FoodishServiceTest {
    private FoodishService sut;
    private FoodishDownloader foodishDownloader;
    private Base64Encoder base64Encoder;
    private S3Uploader s3Uploader;

    private final TestUtil testUtil = new TestUtil();

    @BeforeEach
    public void setUp() {
        foodishDownloader = mock(FoodishDownloader.class);
        base64Encoder = mock(Base64Encoder.class);
        s3Uploader = mock(S3Uploader.class);
        sut = new FoodishService(foodishDownloader, base64Encoder, s3Uploader);
    }

    @Test
    public void downloadThenUploadTest() throws Exception {
        Mono<Tuple2<ByteBuffer, String>> downloadDetailsMono = Mono.just(Tuples.of(testUtil.byteBuffer(), "sampleUrl"));
        when(foodishDownloader.download(anyString())).thenReturn(downloadDetailsMono);

        Tuple3<ByteBuffer, Integer, String> encodedBufferDetails = Tuples.of(testUtil.byteBuffer(), testUtil.encodedLength(), testUtil.encodedString());
        when(base64Encoder.encode(any(ByteBuffer.class))).thenReturn(encodedBufferDetails);

        SdkHttpResponse sdkHttpResponse = mock(SdkHttpResponse.class);
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        PutObjectResponse putObjectResponse = mock(PutObjectResponse.class);
        when(putObjectResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);
        CompletableFuture<PutObjectResponse> putObjectResponseFuture = CompletableFuture.completedFuture(putObjectResponse);
        when(s3Uploader.uploadAsync(anyString(), any(Flux.class), anyInt())).thenReturn(putObjectResponseFuture);

        Mono<BurgerResponse> burgerResponseMono =  sut.downloadThenUpload("63");
        BurgerResponse burgerResponse = burgerResponseMono.block();

        assertThat(burgerResponse.getOriginalUrl().equals("sampleUrl"));
        assertThat(burgerResponse.getImage().equals(testUtil.encodedString()));
    }
}
