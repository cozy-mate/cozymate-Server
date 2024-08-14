package com.cozymate.cozymate_server.domain.notificationlog.enums;

import com.cozymate.cozymate_server.domain.fcm.NotificationContentDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {

    // OneTargetReverse
    COZY_MATE_REQUEST_FROM(NotificationCategory.COZY_MATE_ARRIVE) {
        @Override
        public String generateContent(NotificationContentDto notificationContentDto) {
            return notificationContentDto.getMember().getNickname() + "님에게서 코지메이트 신청이 도착했어요!";
        }
    },

    // OneTargetReverse
    COZY_MATE_REQUEST_TO(NotificationCategory.COZY_MATE_SEND) {
        @Override
        public String generateContent(NotificationContentDto notificationContentDto) {
            return notificationContentDto.getMember().getNickname() + "님에게 코지메이트 신청을 보냈어요!";
        }
    },

    // OneTargetReverse
    COZY_MATE_REQUEST_ACCEPT(NotificationCategory.COZY_MATE_ACCEPT) {
        @Override
        public String generateContent(NotificationContentDto notificationContentDto) {
            return notificationContentDto.getMember().getNickname() + "님이 코지메이트 신청을 수락했어요!";
        }
    },

    // GroupTargetDto
    ROOM_CREATED(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentDto notificationContentDto) {
            return "방이 열렸어요, 얼른 가서 코지메이트를 만나봐요!";
        }
    },

    // OneTarget -> 투표가 완료되면 알림 -> 해당 투표 요청이 마지막 투표자인지 체크하고 사용
    BEST_COZY_MATE(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentDto notificationContentDto) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            return String.format(
                notificationContentDto.getMember().getNickname() + "님, %s Best 코지메이트로 선정되셨어요!",
                month);
        }
    },

    // OneTarget, 스케줄러
    SELECT_COZY_MATE(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentDto notificationContentDto) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            return String.format(
                notificationContentDto.getMember().getNickname()
                    + "님, %s Best, Worst 코지메이트를 선정해주세요!",
                month);
        }
    },

    // OneTarget, 스케줄러
    REMINDER_ROLE(NotificationCategory.COZY_ROLE) {
        @Override
        public String generateContent(NotificationContentDto notificationContentDto) {
            return notificationContentDto.getMember().getNickname() + "님, 오늘 "
                + notificationContentDto.getRoleContent()
                + " 잊지 않으셨죠?";
        }
    },

    // OneTarget, 스케줄러
    TODO_LIST(NotificationCategory.COZY_ROLE) {
        @Override
        public String generateContent(NotificationContentDto notificationContentDto) {
            StringBuilder builder = new StringBuilder();

            List<String> todoContents = notificationContentDto.getTodoContents();
            todoContents.forEach(
                todoContent -> builder.append("\n• ").append(todoContent)
            );

            return notificationContentDto.getMember().getNickname() + "님, 오늘 해야할 일이에요!"
                + builder.toString();
        }
    },
    ;

    private NotificationCategory category;

    public abstract String generateContent(NotificationContentDto notificationContentDto);

    public enum NotificationCategory {
        COZY_HOME, COZY_MATE_ARRIVE, COZY_MATE_SEND, COZY_MATE_ACCEPT, COZY_ROLE
    }
}