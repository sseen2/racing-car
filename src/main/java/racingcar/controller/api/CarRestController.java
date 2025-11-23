package racingcar.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import racingcar.dto.request.CarRegisterRequest;
import racingcar.dto.response.CarInfoResponse;
import racingcar.dto.response.success.CarSuccess;
import racingcar.global.dto.ApiResponse;
import racingcar.service.CarService;
import racingcar.service.WebSocketService;

@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarRestController {

    private final CarService carService;
    private final WebSocketService webSocketService;

    @PostMapping("/register")
    @Operation(summary = "자동차 등록", description = "자동차가 없는 경우 자동차를 등록하고, 이미 등록된 경우 비밀번호를 확인 후 게임에 입장합니다.")
    public ApiResponse<CarInfoResponse> registerCar(@RequestBody @Valid CarRegisterRequest request) {
        CarInfoResponse response = carService.registerCar(request);
        carService.sendParticipants();
        webSocketService.sendLog(request.carName() + "님이 입장했습니다.");
        return ApiResponse.success(CarSuccess.REGISTER_CAR, response);
    }

    @GetMapping("/participants")
    @Operation(summary = "자동차 경주 참가자 목록 조회", description = "자동차 경주에 참가한 자동차 이름 목록을 조회합니다.")
    public ApiResponse<List<CarInfoResponse>> getParticipants() {
        List<CarInfoResponse> response = carService.updateParticipants();
        return ApiResponse.success(CarSuccess.GET_PARTICIPANTS, response);
    }
}
