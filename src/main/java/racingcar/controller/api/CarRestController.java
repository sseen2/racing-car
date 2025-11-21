package racingcar.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import racingcar.dto.request.CarRegisterRequest;
import racingcar.dto.response.CarRegisterResponse;
import racingcar.dto.response.CarSuccessResponse;
import racingcar.global.dto.ApiResponse;
import racingcar.service.CarService;

@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarRestController {

    private final CarService carService;

    @PostMapping("/register")
    @Operation(summary = "자동차 등록", description = "자동차가 없는 경우 자동차를 등록하고, 이미 등록된 경우 비밀번호를 확인 후 게임에 입장합니다.")
    public ApiResponse<CarRegisterResponse> registerCar(@RequestBody @Valid CarRegisterRequest request) {
        CarRegisterResponse response = carService.registerCar(request);
        return ApiResponse.success(CarSuccessResponse.REGISTER_CAR, response);
    }
}
