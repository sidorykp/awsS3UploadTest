package pl.nlogn.aws.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.nlogn.aws.domain.Base64Encoder;
import pl.nlogn.aws.domain.BurgerResponse;
import pl.nlogn.aws.integration.FoodishDownloader;
import pl.nlogn.aws.integration.S3Uploader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@Component
public class FoodishService {
    private static final Logger LOG = LoggerFactory.getLogger(FoodishDownloader.class);

    @Autowired
    private FoodishDownloader foodishDownloader;

    @Autowired
    private Base64Encoder base64Encoder;

    @Autowired
    private S3Uploader s3Uploader;

    public static final String FOODISH_DOWNLOAD_FAILURE = "Download from Foodish failed";

    public static final String S3_UPLOAD_FAILURE = "Upload to S3 failed";

    public FoodishService(FoodishDownloader foodishDownloader, Base64Encoder base64Encoder, S3Uploader s3Uploader) {
        this.foodishDownloader = foodishDownloader;
        this.base64Encoder = base64Encoder;
        this.s3Uploader = s3Uploader;
    }

    public Mono<BurgerResponse> downloadThenUpload(String id) {
        String burgerFileName = "burger" + id + ".jpg";

        Mono<Tuple2<ByteBuffer, String>> downloadDetailsMono;
        try {
            downloadDetailsMono = foodishDownloader.download(burgerFileName);
        } catch (Exception e) {
            LOG.error("Exception while downloading from Foodish", e);
            return Mono.error(new RuntimeException(FOODISH_DOWNLOAD_FAILURE, e));
        }

        return downloadDetailsMono.flatMap(downloadDetails -> {
            try {
                ByteBuffer byteBuffer = downloadDetails.getT1();
                String url = downloadDetails.getT2();

                Tuple3<ByteBuffer, Integer, String> encodedBufferDetails = base64Encoder.encode(byteBuffer);
                ByteBuffer encodedBuffer = encodedBufferDetails.getT1();
                int encodedContentLength = encodedBufferDetails.getT2();
                String encodedBufferString = encodedBufferDetails.getT3();

                CompletableFuture<PutObjectResponse> uploadFuture = s3Uploader.uploadAsync(burgerFileName, Flux.just(encodedBuffer), encodedContentLength);

                return Mono.fromFuture(uploadFuture).flatMap(uploadResult -> {
                    if (uploadResult.sdkHttpResponse().isSuccessful()) {
                        BurgerResponse burgerResponse = new BurgerResponse(url, encodedBufferString);
                        return Mono.just(burgerResponse);
                    } else {
                        LOG.warn("Unexpected response status from S3: {}", uploadResult.sdkHttpResponse().statusCode());
                        return Mono.error(new RuntimeException(S3_UPLOAD_FAILURE));
                    }
                }).onErrorResume(throwable -> {
                    LOG.error("Throwable while uploading to S3", throwable);
                    return Mono.error(new RuntimeException(S3_UPLOAD_FAILURE, throwable));
                });
            } catch (Exception e) {
                LOG.error("Exception while uploading to S3", e);
                return Mono.error(new RuntimeException(S3_UPLOAD_FAILURE, e));
            }
        });
    }
}
