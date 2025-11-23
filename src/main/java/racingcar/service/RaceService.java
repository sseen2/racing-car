package racingcar.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import racingcar.entity.Car;
import racingcar.entity.History;
import racingcar.entity.RaceResult;
import racingcar.repository.HistoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RaceService {

    private static final String WINNER_MESSAGE = "\uD83C\uDFC6 최종 우승자는 %s입니다.";

    private final WebSocketService webSocketService;
    private final HistoryRepository historyRepository;

    @Transactional
    public void saveResult(int maxPosition, List<Car> cars) {
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
}
