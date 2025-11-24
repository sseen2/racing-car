package racingcar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import racingcar.entity.Car;
import racingcar.entity.RaceHistory;
import racingcar.entity.RaceResult;

@Repository
public interface RaceHistoryRepository extends JpaRepository<RaceHistory, Long> {

    int countByCarAndResult(Car car, RaceResult raceResult);
}
