package com.cozymate.cozymate_server.domain.notificationlog.enums;

import com.cozymate.cozymate_server.global.fcm.NotificationContentVO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {

    // TwoTargetVO
    ROOM_MATE_FROM(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVo) {
            return notificationContentVo.getMember().getNickname() + "님에게서 룸메이트 신청이 도착했어요!";
        }
    },

    // TwoTargetVO
    ROOM_MATE_TO(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVo) {
            return notificationContentVo.getMember().getNickname() + "님에게 룸메이트 신청을 보냈어요!";
        }
    },

    // OneTargetReverseVO
    ROOM_MATE_ACCEPT(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVo) {
            return notificationContentVo.getMember().getNickname() + "님이 룸메이트 신청을 수락했어요!";
        }
    },

    // OneTargetReverseVO
    ROOM_MATE_REJECT(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님이 룸메이트 신청을 거절했어요!";
        }
    },

    // OneTargetVO
    ROOM_ENTRY(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getRoomName() + " 방에 입장했어요!";
        }
    },

    // OneTargetReverseVO
    ROOM_REJECT(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님이 방 초대를 거절했어요.";
        }
    },

    // GroupTargetVO
    ROOM_OPEN(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님, cozymate가 모두 모여, 방이 열렸어요!";
        }
    },

    // OneTargetReverseVO
    ARRIVE_CHAT(NotificationCategory.CHAT) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님에게서 쪽지가 도착했어요!";
        }
    },

    // OneTargetVO
    LAUNDRY_REMINDER(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님, 오늘 빨래 당번 잊지 않으셨죠?";
        }
    },

    // OneTargetVO
    CLEANING_REMINDER(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님, 화장실 청소 까먹으신거 아니죠?";
        }
    },

    // OneTargetVO
    DISH_REMINDER(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님, 오늘은 설거지 하는 날이에요!";
        }
    },

    // OneTargetVO
    BEST_ROOMMATE(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            return String.format(
                notificationContentVO.getMember().getNickname() + "님, %s Best 룸메이트로 선정되셨어요!",
                month);
        }
    },
    ;

    private NotificationCategory category;

    public abstract String generateContent(NotificationContentVO notificationContentVO);

    public enum NotificationCategory {
        COZY_HOME, CHAT, ROOM_MATE
    }
}