package racingcar.service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import racingcar.dto.request.RaceEnterRequest;
import racingcar.dto.request.RaceLeaveRequest;
import racingcar.dto.request.RaceStartRequest;
import racingcar.dto.response.CarInfoResponse;
import racingcar.dto.response.RaceResultResponse;
import racingcar.dto.response.error.CarError;
import racingcar.entity.Car;
import racingcar.entity.History;
import racingcar.entity.RaceResult;
import racingcar.global.exception.BusinessException;
import racingcar.repository.CarRepository;
import racingcar.repository.HistoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RaceService {

    private static final String ENTER_MESSAGE = "%s님이 입장했습니다.";
    private static final String START_MESSAGE = "\uD83C\uDFC1 자동차 경주를 시작합니다!";
    private static final String TRY_COUNT_MESSAGE = "시도횟수는 %d회 입니다.";
    private static final String WINNER_MESSAGE = "\uD83C\uDFC6 최종 우승자는 %s입니다.";
    private static final String LEAVE_MESSAGE = "%s님이 퇴장했습니다.";

    private static final long INTERVAL_MILLIS = 1000L;
    private static final int MIN_PARTICIPANT_COUNT = 1;
    private static final int MAX_PARTICIPANT_COUNT = 5;
    private static final int MAX_RANDOM_NUMBER = 9;

    private final CarService carService;
    private final WebSocketService webSocketService;
    private final CarRepository carRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    public CarInfoResponse enterRace(RaceEnterRequest request) {
        validateMaxParticipant();

        Car car = carService.findByName(request.carName());

        updateStatus(car);
        sendEnter(request.carName());

        return new CarInfoResponse(car.getName(), car.getIsHost());
    }

    private void validateMaxParticipant() {
        int participantCount = carRepository.countByIsParticipated(true);

        if (participantCount >= MAX_PARTICIPANT_COUNT) {
            throw new BusinessException(CarError.TOO_MANY_PARTICIPANT);
        }
    }

    private void updateStatus(Car car) {
        updateHostStatus(car);
        car.updateParticipatedStatus(true);
    }

    private void updateHostStatus(Car car) {
        if (!carRepository.hasHost()) {
            car.updateHostStatus(true);
            return;
        }

        car.updateHostStatus(false);
    }

    private void sendParticipants() {
        List<CarInfoResponse> response = carService.updateParticipants();
        webSocketService.sendParticipants(response);
    }

    private void sendEnter(String name) {
        sendParticipants();
        webSocketService.sendLog(String.format(ENTER_MESSAGE, name));
    }

    @Transactional
    public void startRace(RaceStartRequest request) {
        String carName = request.carName();
        int tryCount = request.tryCount();

        validateStart(carName);

        webSocketService.sendLog(START_MESSAGE);
        webSocketService.sendLog(String.format(TRY_COUNT_MESSAGE, tryCount));

        startRace(tryCount);
    }

    private void validateStart(String carName) {
        validateHost(carName);
        validateParticipantCount();
    }

    private void validateHost(String carName) {
        boolean isHost = carService.findByName(carName)
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

        saveResult(maxPosition, participantCars);
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

    private void saveResult(int maxPosition, List<Car> cars) {
        List<Car> winners = getWinner(maxPosition, cars);
        saveRaceResult(winners, cars);
        sendWinner(winners);
    }

    private List<Car> getWinner(int maxPosition, List<Car> cars) {
        return cars.stream()
                .filter(car -> car.getPosition() == maxPosition)
                .toList();
    }

    private void saveRaceResult(List<Car> winners, List<Car> cars) {
        winners.forEach(car -> {
            createHistory(car, RaceResult.WIN);
        });

        for (Car car : cars) {
            if (winners.contains(car)) {
                continue;
            }

            createHistory(car, RaceResult.LOSE);
        }
    }

    private void createHistory(Car car, RaceResult raceResult) {
        History history = History.builder()
                .car(car)
                .result(raceResult)
                .build();

        historyRepository.save(history);
    }

    private void sendWinner(List<Car> winners) {
        String names = winners.stream()
                .map(Car::getName)
                .collect(Collectors.joining(", "));

        webSocketService.sendLog(String.format(WINNER_MESSAGE, names));
    }

    @Transactional
    public void leaveRace(RaceLeaveRequest request) {
        String carName = request.carName();

        Car car = carService.findByName(carName);

        cleanupBeforeLeave(car);

        sendParticipants();
        webSocketService.sendLog(String.format(LEAVE_MESSAGE, carName));
    }

    private void cleanupBeforeLeave(Car car) {
        car.updateParticipatedStatus(false);
        handOverHost(car);
        car.resetPosition();
    }

    private void handOverHost(Car car) {
        if (car.getIsHost()) {
            car.updateHostStatus(false);

            Car newHostCar = carRepository.findTopByIsParticipatedIsTrue();
            newHostCar.updateHostStatus(true);
        }
    }
}
