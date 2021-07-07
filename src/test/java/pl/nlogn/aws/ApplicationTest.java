package pl.nlogn.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.nlogn.aws.domain.Base64Encoder;
import pl.nlogn.aws.integration.FoodishDownloader;
import pl.nlogn.aws.integration.S3Uploader;
import pl.nlogn.aws.rest.Controller;
import pl.nlogn.aws.service.FoodishService;

import java.time.Duration;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = Controller.class)
@Import({FoodishService.class, FoodishDownloader.class, S3Uploader.class, Base64Encoder.class})
public class ApplicationTest {

    @Autowired
    private FoodishDownloader foodishDownloader;

    @Autowired
    private WebTestClient webClient;

    @BeforeEach
    public void setUp() {
        webClient = webClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .codecs(foodishDownloader.codecs())
                .build();
    }

    @Test
    public void getBurgerByIdTest() {
        WebTestClient.ResponseSpec responseSpec = webClient.get().uri("/burgers/{id}", "63")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange();

        responseSpec.expectStatus().isOk();
        responseSpec.expectBody().jsonPath("originalUrl", "https://foodish-api.herokuapp.com/images/burger/burger63.jpg");
    }
}
