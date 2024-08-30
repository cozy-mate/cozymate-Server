package com.cozymate.cozymate_server.domain.notificationlog.enums;

import static com.cozymate.cozymate_server.domain.memberstat.util.MemberUtil.getNicknameShowFormat;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentResultDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {

    // GroupWithOutMeTarget
    // TODO :
    COMPLETE_ALL_TODAY_TODO(NotificationCategory.COZY_HOME) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            String notificationContent =
                fcmPushContentDto.getMember().getNickname() + "님이 오늘 해야 할 일을 전부 완료했어요!";
            String logContent = getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님이 오늘 해야 할 일을 전부 완료했어요!";

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();

        }
    },

    // OneTargetReverse
    COZY_MATE_REQUEST_FROM(NotificationCategory.COZY_MATE_ARRIVE) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            String notificationContent =
                fcmPushContentDto.getMember().getNickname() + "님에게서 코지메이트 신청이 도착했어요!";
            String logContent = getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님에게서 코지메이트 신청이 도착했어요!";

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();
        }
    },

    // OneTargetReverse
    COZY_MATE_REQUEST_TO(NotificationCategory.COZY_MATE_SEND) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            String notificationContent =
                fcmPushContentDto.getMember().getNickname() + "님에게 코지메이트 신청을 보냈어요!";
            String logContent = getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님에게 코지메이트 신청을 보냈어요!";

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();
        }
    },

    // OneTargetReverse
    COZY_MATE_REQUEST_ACCEPT(NotificationCategory.COZY_MATE_ACCEPT) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            String notificationContent =
                fcmPushContentDto.getMember().getNickname() + "님이 코지메이트 신청을 수락했어요!";
            String logContent = getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님이 코지메이트 신청을 수락했어요!";

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();
        }
    },

    // GroupTargetDto
    ROOM_CREATED(NotificationCategory.COZY_HOME) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            String content = "방이 열렸어요, 얼른 가서 코지메이트를 만나봐요!";
            return FcmPushContentResultDto.builder()
                .notificationContent(content)
                .logContent(content)
                .build();
        }
    },

    // OneTarget -> 투표가 완료되면 알림 -> 해당 투표 요청이 마지막 투표자인지 체크하고 사용
    BEST_COZY_MATE(NotificationCategory.COZY_HOME) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            String notificationContent = String.format(
                fcmPushContentDto.getMember().getNickname()
                    + "님, %s Best 코지메이트로 선정되셨어요!",
                month);

            String logContent = String.format(
                getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                    + "님, %s Best 코지메이트로 선정되셨어요!",
                month);

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();
        }
    },

    // OneTarget, 스케줄러
    SELECT_COZY_MATE(NotificationCategory.COZY_HOME) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));

            String notificationContent = String.format(
                fcmPushContentDto.getMember().getNickname()
                    + "님, %s Best, Worst 코지메이트를 선정해주세요!",
                month);

            String logContent = String.format(
                getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                    + "님, %s Best, Worst 코지메이트를 선정해주세요!",
                month);

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();
        }
    },

    // OneTarget, 스케줄러
    REMINDER_ROLE(NotificationCategory.COZY_ROLE) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            String notificationContent = fcmPushContentDto.getMember().getNickname()
                + "님, 오늘 "
                + fcmPushContentDto.getRoleContent()
                + " 잊지 않으셨죠?";

            String logContent = getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님, 오늘 "
                + fcmPushContentDto.getRoleContent()
                + " 잊지 않으셨죠?";

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();
        }
    },

    // OneTarget, 스케줄러
    TODO_LIST(NotificationCategory.COZY_ROLE) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            StringBuilder builder = new StringBuilder();

            List<String> todoContents = fcmPushContentDto.getTodoContents();
            todoContents.forEach(
                todoContent -> builder.append("\n• ").append(todoContent)
            );

            String notificationContent = fcmPushContentDto.getMember().getNickname()
                + "님, 오늘 해야할 일이에요!"
                + builder.toString();

            String logContent = getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님, 오늘 해야할 일이에요!"
                + builder.toString();

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();
        }
    },

    JOIN_ROOM(NotificationCategory.COZY_HOME) {
        @Override
        public FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto) {
            String logContent = getNicknameShowFormat(fcmPushContentDto.getRoom().getName()) + "에 "
                + getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님이 뛰어들어왔어요!";

            String notificationContent = fcmPushContentDto.getRoom().getName() + "에 "
                + fcmPushContentDto.getMember().getNickname()
                + "님이 뛰어들어왔어요!";

            return FcmPushContentResultDto.builder()
                .notificationContent(notificationContent)
                .logContent(logContent)
                .build();
        }
    };

    private NotificationCategory category;

    public abstract FcmPushContentResultDto generateContent(FcmPushContentDto fcmPushContentDto);

    public enum NotificationCategory {
        COZY_HOME, COZY_MATE_ARRIVE, COZY_MATE_SEND, COZY_MATE_ACCEPT, COZY_ROLE
    }
}