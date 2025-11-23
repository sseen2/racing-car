package racingcar.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import racingcar.entity.Car;
import racingcar.entity.Race;
import racingcar.entity.RaceResult;
import racingcar.repository.RaceRepository;

@Service
@RequiredArgsConstructor
public class RaceService {

    private static final String WINNER_MESSAGE = "\uD83C\uDFC6 최종 우승자는 %s입니다.";

    private final RaceRepository raceRepository;
    private final WebSocketService webSocketService;

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
            createRace(car, RaceResult.WIN);
        });

        for (Car car : cars) {
            if (winners.contains(car)) {
                continue;
            }

            createRace(car, RaceResult.LOSE);
        }
    }

    private void createRace(Car car, RaceResult raceResult) {
        Race race = Race.builder()
                .car(car)
                .result(raceResult)
                .build();

        raceRepository.save(race);
    }

    private void sendWinner(List<Car> winners) {
        String names = winners.stream()
                .map(Car::getName)
                .collect(Collectors.joining(", "));

        webSocketService.sendLog(String.format(WINNER_MESSAGE, names));
    }
}
