package racingcar.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CarPageController {

    @RequestMapping("/")
    public String home() {
        return "home";
    }
}
