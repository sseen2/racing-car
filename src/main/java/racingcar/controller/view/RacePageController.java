package racingcar.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RacePageController {

    @PostMapping("/room")
    public String racePage() {
        return "race/room";
    }
}
