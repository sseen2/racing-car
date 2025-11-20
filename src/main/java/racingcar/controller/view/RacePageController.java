package racingcar.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RacePageController {

    @GetMapping("/room")
    public String racePage() {
        return "race/room";
    }
}
