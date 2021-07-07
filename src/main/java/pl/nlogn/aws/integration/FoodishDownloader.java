package pl.nlogn.aws.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

@Component
public class FoodishDownloader {
    private static final Logger LOG = LoggerFactory.getLogger(FoodishDownloader.class);

    private static final String BASE_URL = "https://foodish-api.herokuapp.com";

    private static final int MAX_IN_MEMORY_SIZE_BYTES = 4 * 1024 * 1024;

    public Consumer<ClientCodecConfigurer> codecs() {
        return configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(MAX_IN_MEMORY_SIZE_BYTES);
    }

    private WebClient client() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(codecs())
                                .build())
                .build();
    }

    private WebClient.RequestBodySpec requestBodySpec(String imageName) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client().method(HttpMethod.GET);

        return uriSpec.uri(
                (uriBuilder -> uriBuilder
                        .pathSegment("images")
                        .pathSegment("burger")
                        .pathSegment(imageName).build()));
    }

    public Mono<Tuple2<ByteBuffer, String>> download(String imageName) {

        return requestBodySpec(imageName).exchangeToMono(response -> {
            if (response.statusCode()
                    .equals(HttpStatus.OK)) {
                return response
                        .bodyToMono(ByteBuffer.class)
                        .map(byteBuffer -> Tuples.of(byteBuffer, BASE_URL + "/images/burger/" + imageName));
            } else {
                LOG.warn("Unexpected response status from Foodish: {}", response.statusCode());
                return response.createException()
                        .flatMap(Mono::error);
            }
        });
    }
}
