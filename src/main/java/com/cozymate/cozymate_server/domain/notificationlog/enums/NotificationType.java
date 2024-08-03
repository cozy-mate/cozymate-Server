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
    ROOM_MATE_REQUEST_FROM(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVo) {
            return notificationContentVo.getMember().getNickname() + "님에게서 룸메이트 신청이 도착했어요!";
        }
    },

    // TwoTargetVO
    ROOM_MATE_REQUEST_TO(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVo) {
            return notificationContentVo.getMember().getNickname() + "님에게 룸메이트 신청을 보냈어요!";
        }
    },

    // OneTargetReverseVO
    ROOM_MATE_ACCEPTED(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVo) {
            return notificationContentVo.getMember().getNickname() + "님이 룸메이트 신청을 수락했어요!";
        }
    },

    // OneTargetReverseVO
    ROOM_MATE_REJECTED(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님이 룸메이트 신청을 거절했어요!";
        }
    },

    // OneTargetVO
    ROOM_JOINED(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getRoomName() + " 방에 입장했어요!";
        }
    },

    // OneTargetReverseVO
    ROOM_INVITE_REJECTED(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님이 방 초대를 거절했어요.";
        }
    },

    // GroupTargetVO
    ROOM_CREATED(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님, cozymate가 모두 모여, 방이 열렸어요!";
        }
    },

    // OneTargetReverseVO
    CHAT_RECEIVED(NotificationCategory.CHAT) {
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
    BEST_ROOM_MATE(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            return String.format(
                notificationContentVO.getMember().getNickname() + "님, %s Best 룸메이트로 선정되셨어요!",
                month);
        }
    },

    ROOM_CLOSED(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getRoomName() + " 방이 오늘부로 종로됐어요...";
        }
    },

    ROOM_LEFT(NotificationCategory.ROOM_MATE) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            String nickname = notificationContentVO.getMember().getNickname();
            String roomName = notificationContentVO.getRoomName();
            return roomName + " 방에서 " + nickname + "님이 떠났어요..";
        }
    },

    COZY_MATE_RENEWAL(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            return notificationContentVO.getMember().getNickname() + "님, cozymate가 새롭게 리뉴얼 됐어요";
        }
    },

    BEST_SELECTED(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            return String.format(
                notificationContentVO.getMember().getNickname()
                    + "님, %s의 Best, Worst 코지메이트가 선정됐어요!\n지금 바로 확인해볼까요?",
                month);
        }
    },

    SELECT_COZYMATE(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(NotificationContentVO notificationContentVO) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));
            return String.format(
                notificationContentVO.getMember().getNickname()
                    + "님, %s의 Best, Worst 코지메이트를 선정해주세요!",
                month);
        }
    };;

    private NotificationCategory category;

    public abstract String generateContent(NotificationContentVO notificationContentVO);

    public enum NotificationCategory {
        COZY_HOME, CHAT, ROOM_MATE
    }
}