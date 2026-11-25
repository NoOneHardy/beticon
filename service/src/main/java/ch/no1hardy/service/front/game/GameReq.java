package ch.no1hardy.service.front.game;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameReq {
    private LocalDateTime timestamp;
    private String group;
    private String teamHome;
    private String teamGuest;
}
