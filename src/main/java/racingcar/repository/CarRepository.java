package racingcar.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import racingcar.entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByName(String name);

    @Query("""
        SELECT COUNT(c) > 0
          FROM Car c
         WHERE c.isHost = TRUE
    """)
    boolean hasHost();

    List<Car> findAllByIsParticipated(boolean isParticipated);

    int countByIsParticipated(boolean isParticipated);

    Car findTopByOrderByPositionDesc();

    Car findTopByIsParticipatedIsTrue();
}
