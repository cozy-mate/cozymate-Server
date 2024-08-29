package com.cozymate.cozymate_server.domain.notificationlog.enums;

import static com.cozymate.cozymate_server.domain.memberstat.util.MemberUtil.getNicknameShowFormat;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentDto;
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
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님이 오늘 해야 할 일을 전부 완료했어요!";
        }
    },

    // OneTargetReverse
    COZY_MATE_REQUEST_FROM(NotificationCategory.COZY_MATE_ARRIVE) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님에게서 코지메이트 신청이 도착했어요!";
        }
    },

    // OneTargetReverse
    COZY_MATE_REQUEST_TO(NotificationCategory.COZY_MATE_SEND) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님에게 코지메이트 신청을 보냈어요!";
        }
    },

    // OneTargetReverse
    COZY_MATE_REQUEST_ACCEPT(NotificationCategory.COZY_MATE_ACCEPT) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님이 코지메이트 신청을 수락했어요!";
        }
    },

    // GroupTargetDto
    ROOM_CREATED(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return "방이 열렸어요, 얼른 가서 코지메이트를 만나봐요!";
        }
    },

    // OneTarget -> 투표가 완료되면 알림 -> 해당 투표 요청이 마지막 투표자인지 체크하고 사용
    BEST_COZY_MATE(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            return String.format(
                getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                    + "님, %s Best 코지메이트로 선정되셨어요!",
                month);
        }
    },

    // OneTarget, 스케줄러
    SELECT_COZY_MATE(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            return String.format(
                getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                    + "님, %s Best, Worst 코지메이트를 선정해주세요!",
                month);
        }
    },

    // OneTarget, 스케줄러
    REMINDER_ROLE(NotificationCategory.COZY_ROLE) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님, 오늘 "
                + fcmPushContentDto.getRoleContent()
                + " 잊지 않으셨죠?";
        }
    },

    // OneTarget, 스케줄러
    TODO_LIST(NotificationCategory.COZY_ROLE) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            StringBuilder builder = new StringBuilder();

            List<String> todoContents = fcmPushContentDto.getTodoContents();
            todoContents.forEach(
                todoContent -> builder.append("\n• ").append(todoContent)
            );

            return getNicknameShowFormat(fcmPushContentDto.getMember().getNickname())
                + "님, 오늘 해야할 일이에요!"
                + builder.toString();
        }
    },
    ;

    private NotificationCategory category;

    public abstract String generateContent(FcmPushContentDto fcmPushContentDto);

    public enum NotificationCategory {
        COZY_HOME, COZY_MATE_ARRIVE, COZY_MATE_SEND, COZY_MATE_ACCEPT, COZY_ROLE
    }
}