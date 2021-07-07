package pl.nlogn.aws.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.nlogn.aws.domain.BurgerResponse;
import pl.nlogn.aws.service.FoodishService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/burgers")
public class Controller {
    @Autowired
    private FoodishService foodishService;

    @GetMapping("/{id}")
    private Mono<BurgerResponse> getBurgerById(@PathVariable String id) {
        return foodishService.downloadThenUpload(id);
    }
}
