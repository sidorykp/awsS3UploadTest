package pl.nlogn.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.nlogn.aws.integration.FoodishDownloader;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class FoodishDownloaderTest {
    private FoodishDownloader sut;

    @BeforeEach
    public void setUp() {
        sut = new FoodishDownloader();
    }

    @Test
    public void testDownload() {
        Mono<Tuple2<ByteBuffer, String>> responseDetailsMono =  sut.download("burger63.jpg");
        Tuple2<ByteBuffer, String> responseDetails = responseDetailsMono.block();

        assertThat(responseDetails.getT1().remaining() == 885671);
        assertThat(responseDetails.getT2().equals("https://foodish-api.herokuapp.com/images/burger/burger63.jpg"));
    }
}
