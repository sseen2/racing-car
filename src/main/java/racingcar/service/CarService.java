package racingcar.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import racingcar.dto.request.CarRegisterRequest;
import racingcar.dto.request.CarResetPositionRequest;
import racingcar.dto.response.CarInfoResponse;
import racingcar.dto.response.error.CarError;
import racingcar.entity.Car;
import racingcar.global.exception.BusinessException;
import racingcar.repository.CarRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;

    @Transactional
    public CarInfoResponse registerCar(CarRegisterRequest request) {
        String carName = request.carName();
        String password = request.password();

        Car car = carRepository.findByName(carName)
                .orElseGet(() -> saveCar(carName, password));

        validatePassword(car, password);

        return new CarInfoResponse(car.getName(), car.getIsHost());
    }

    private void validatePassword(Car car, String password) {
        if (!car.isMatchedPassword(password)) {
            throw new BusinessException(CarError.PASSWORD_NOT_MATCHED);
        }
    }

    private Car saveCar(String name, String password) {
        Car car = Car.builder()
                .name(name)
                .password(password)
                .build();
        return carRepository.save(car);
    }

    public List<CarInfoResponse> updateParticipants() {
        return carRepository.findAllByIsParticipated(true)
                .stream()
                .map(car -> new CarInfoResponse(car.getName(), car.getIsHost()))
                .toList();
    }

    @Transactional
    public void resetPosition(CarResetPositionRequest request) {
        Car car = findByName(request.carName());
        car.resetPosition();
    }

    public Car findByName(String carName) {
        return carRepository.findByName(carName)
                .orElseThrow(() -> new BusinessException(CarError.CAR_NOT_FOUND));
    }
}
