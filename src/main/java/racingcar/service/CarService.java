package racingcar.service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import racingcar.dto.request.CarRegisterRequest;
import racingcar.dto.request.RaceStartRequest;
import racingcar.dto.response.error.CarError;
import racingcar.dto.response.CarInfoResponse;
import racingcar.dto.response.RaceResultResponse;
import racingcar.entity.Car;
import racingcar.global.exception.BusinessException;
import racingcar.repository.CarRepository;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final long INTERVAL_MILLIS = 1000L;
    private static final int MIN_PARTICIPANT_COUNT = 1;
    private static final int MAX_PARTICIPANT_COUNT = 5;
    private static final int MAX_RANDOM_NUMBER = 9;

    private final RaceService raceService;
    private final WebSocketService webSocketService;
    private final CarRepository carRepository;

    public CarInfoResponse registerCar(CarRegisterRequest request) {
        String carName = request.carName();
        String password = request.password();

        Car car = carRepository.findByName(carName)
                .orElseGet(() -> saveCar(carName, password));

        validateEnter(car, password);

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

    private void validateEnter(Car car, String password) {
        validateMaxParticipant();
        validatePassword(car, password);
    }

    private void validateMaxParticipant() {
        int participantCount = carRepository.countByIsParticipated(true);

        if (participantCount >= MAX_PARTICIPANT_COUNT) {
            throw new BusinessException(CarError.TOO_MANY_PARTICIPANT);
        }
    }

    private void validatePassword(Car car, String password) {
        if (!car.isMatchedPassword(password)) {
            throw new BusinessException(CarError.PASSWORD_NOT_MATCHED);
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

        startRace(tryCount);
    }

    private void validateStart(String carName) {
        validateHost(carName);
        validateParticipantCount();
    }

    private void validateHost(String carName) {
        boolean isHost = carRepository.findByName(carName)
                .orElseThrow(() -> new BusinessException(CarError.CAR_NOT_FOUND))
                .getIsHost();

        if (!isHost) {
            throw new BusinessException(CarError.ONLY_HOST_ALLOWED);
        }
    }

    private void validateParticipantCount() {
        int participantCount = carRepository.countByIsParticipated(true);

        if (participantCount <= MIN_PARTICIPANT_COUNT) {
            throw new BusinessException(CarError.TOO_FEW_PARTICIPANT);
        }
    }

    private void startRace(int tryCount) {
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            for (int round = 0; round < tryCount; round++) {
                long delay = round * INTERVAL_MILLIS;

                schedule(scheduler, delay, this::raceRound);
            }

            long finalDelay = tryCount * INTERVAL_MILLIS;
            schedule(scheduler, finalDelay, this::result);
        }
    }

    private void raceRound() {
        moveCars();
        List<RaceResultResponse> response = getRaceResults();
        webSocketService.sendResult(response);
    }

    private void result() {
        int maxPosition = carRepository.findTopByOrderByPositionDesc().getPosition();
        List<Car> participantCars = carRepository.findAllByIsParticipated(true);

        raceService.saveResult(maxPosition, participantCars);
    }

    private void schedule(ScheduledExecutorService scheduler, long delay, Runnable task) {
        scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    private void moveCars() {
        List<Car> cars = carRepository.findAllByIsParticipated(true);

        cars.forEach(car -> {
            Random random = new Random();
            int randomValue = random.nextInt(MAX_RANDOM_NUMBER);
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
