package racingcar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@Table(name = "car")
public class Car {

    private static final int MOVE_CONDITION = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = true)
    private Boolean isHost;

    @Column(nullable = false)
    @Builder.Default
    private boolean isParticipated = false;

    @Column(nullable = false)
    @Builder.Default
    private int position = 0;

    public boolean isMatchedPassword(String password) {
        return password.equals(this.password);
    }

    public void updateHostStatus(boolean isHost) {
        this.isHost = isHost;
    }

    public void updateParticipatedStatus(boolean isParticipated) {
        this.isParticipated = isParticipated;
    }

    public void move(int randomValue) {
        if (randomValue >= MOVE_CONDITION) {
            position++;
        }
    }
}
