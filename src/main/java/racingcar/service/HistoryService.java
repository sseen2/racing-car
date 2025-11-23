package racingcar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import racingcar.dto.response.HistoryResponse;
import racingcar.entity.Car;
import racingcar.entity.RaceResult;
import racingcar.repository.HistoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final CarService carService;
    private final HistoryRepository historyRepository;

    public HistoryResponse getHistory(String carName) {
        Car car = carService.findByName(carName);

        int winCount = historyRepository.countByCarAndResult(car, RaceResult.WIN);
        int loseCount = historyRepository.countByCarAndResult(car, RaceResult.LOSE);

        return new HistoryResponse(winCount, loseCount);
    }
}
