package racingcar.service;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import racingcar.dto.request.CarRegisterRequest;
import racingcar.dto.request.RaceStartRequest;
import racingcar.dto.response.CarErrorResponse;
import racingcar.dto.response.CarInfoResponse;
import racingcar.dto.response.RaceResultResponse;
import racingcar.entity.Car;
import racingcar.global.exception.BusinessException;
import racingcar.repository.CarRepository;

@Service
@RequiredArgsConstructor
public class CarService {

    private final RaceService raceService;
    private final CarRepository carRepository;
    private final WebSocketService webSocketService;

    public CarInfoResponse registerCar(CarRegisterRequest request) {
        String carName = request.carName();
        String password = request.password();

        Car car = carRepository.findByName(carName)
                .orElseGet(() -> saveCar(carName, password));

        validatePassword(car, password);

        updateHostStatus(car);
        car.updateParticipatedStatus(true);
        carRepository.save(car);

        return new CarInfoResponse(car.getName(), car.getIsHost());
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
            car.updateHostStatus(true);
            return;
        }

        car.updateHostStatus(false);
    }

    public void sendParticipants() {
        List<CarInfoResponse> response = updateParticipants();
        webSocketService.sendParticipants(response);
    }

    public List<CarInfoResponse> updateParticipants() {
        return carRepository.findAllByIsParticipated(true)
                .stream()
                .map(car -> new CarInfoResponse(car.getName(), car.getIsHost()))
                .toList();
    }

    public void startRace(RaceStartRequest request) {
        String carName = request.carName();
        int tryCount = request.tryCount();

        validateStart(carName);

        webSocketService.sendLog("\uD83C\uDFC1 자동차 경주를 시작합니다!");
        webSocketService.sendLog("시도횟수는 " + tryCount + "회 입니다.");

        for (int i = 0; i < tryCount; i++) {
            moveCars();
        }

        int maxPosition = carRepository.findTopByOrderByPositionDesc().getPosition();
        List<Car> participantCars = carRepository.findAllByIsParticipated(true);

        raceService.saveResult(maxPosition, participantCars);

        List<RaceResultResponse> response = getRaceResults();
        webSocketService.sendResult(response);
    }

    private void validateStart(String carName) {
        validateHost(carName);
        validateParticipantCount();
    }

    private void validateHost(String carName) {
        boolean isHost = carRepository.findByName(carName)
                .orElseThrow(() -> new BusinessException(CarErrorResponse.CAR_NOT_FOUND))
                .getIsHost();

        if (!isHost) {
            throw new BusinessException(CarErrorResponse.ONLY_HOST_ALLOWED);
        }
    }

    private void validateParticipantCount() {
        int participantCount = carRepository.countByIsParticipated(true);

        if (participantCount <= 1) {
            throw new BusinessException(CarErrorResponse.PARTICIPANT_TOO_FEW);
        }
    }

    private void moveCars() {
        List<Car> cars = carRepository.findAllByIsParticipated(true);

        cars.forEach(car -> {
            Random random = new Random();
            int randomValue = random.nextInt(9);
            car.move(randomValue);
        });
        carRepository.saveAll(cars);
    }

    private List<RaceResultResponse> getRaceResults() {
        List<Car> participatedCars = carRepository.findAllByIsParticipated(true);
        return participatedCars.stream()
                .map(car -> new RaceResultResponse(car.getName(), car.getPosition()))
                .toList();
    }
}
