package racingcar.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import racingcar.dto.response.CarParticipantResponse;
import racingcar.dto.response.RaceResultResponse;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private static final String SUB_PARTICIPANTS_PATH = "/sub/race/participants";
    private static final String SUB_LOG_PATH = "/sub/race/log";
    private static final String SUB_RESULT_PATH = "/sub/race/result";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendParticipants(List<CarParticipantResponse> response) {
        simpMessagingTemplate.convertAndSend(SUB_PARTICIPANTS_PATH, response);
    }

    public void sendLog(String message) {
        simpMessagingTemplate.convertAndSend(SUB_LOG_PATH, message);
    }

    public void sendResult(List<RaceResultResponse> response) {
        simpMessagingTemplate.convertAndSend(SUB_RESULT_PATH, response);
    }
}
