package racingcar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import racingcar.dto.response.RaceHistoryResponse;
import racingcar.entity.Car;
import racingcar.entity.RaceResult;
import racingcar.repository.RaceHistoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RaceHistoryService {

    private final CarService carService;
    private final RaceHistoryRepository raceHistoryRepository;

    public RaceHistoryResponse getRaceHistory(String carName) {
        Car car = carService.findByName(carName);

        int winCount = raceHistoryRepository.countByCarAndResult(car, RaceResult.WIN);
        int loseCount = raceHistoryRepository.countByCarAndResult(car, RaceResult.LOSE);

        return new RaceHistoryResponse(winCount, loseCount);
    }
}
