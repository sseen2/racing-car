package racingcar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import racingcar.dto.request.CarRegisterRequest;
import racingcar.dto.response.CarErrorResponse;
import racingcar.entity.Car;
import racingcar.global.exception.BusinessException;
import racingcar.repository.CarRepository;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public void registerCar(CarRegisterRequest request) {
        String carName = request.carName();
        String password = request.password();

        Car car = carRepository.findByName(carName)
                .orElseGet(() -> saveCar(carName, password));

        validatePassword(car, password);

        updateHostStatus(car);
    }

    private Car saveCar(String name, String password) {
        Car car = Car.builder()
                .name(name)
                .password(password)
                .build();
        return carRepository.save(car);
    }

    private void validatePassword(Car car, String password) {
        if (!car.isMatchedPassword(password)) {
            throw new BusinessException(CarErrorResponse.PASSWORD_NOT_MATCHED);
        }
    }

    private void updateHostStatus(Car car) {
        if (!carRepository.hasHost()) {
            car.registerHost();
        }
    }
}
