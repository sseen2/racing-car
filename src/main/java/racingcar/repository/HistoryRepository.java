package racingcar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import racingcar.entity.Car;
import racingcar.entity.History;
import racingcar.entity.RaceResult;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    int countByCarAndResult(Car car, RaceResult raceResult);
}
