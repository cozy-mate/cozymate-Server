package com.cozymate.cozymate_server.domain.notificationlog.enums;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentDto;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.converter.NotificationLogConverter;
import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogCreateDTO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {

    // OneTarget, 스케줄러
    SELECT_COZY_MATE(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            LocalDateTime now = LocalDateTime.now();
            String month = now.format(DateTimeFormatter.ofPattern("M월"));

            return String.format(
                fcmPushContentDto.getMember().getNickname()
                    + "님, %s Best, Worst 코지메이트를 선정해주세요!",
                month);
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), null);
        }
    },

    /**
     * 여기부터 이미 사용중인 알림들
     */

    // OneTarget, 스케줄러
    REMINDER_ROLE(NotificationCategory.COZY_ROLE) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname()
                + "님, 오늘 "
                + fcmPushContentDto.getRoleContent()
                + " 잊지 않으셨죠?";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), null);
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

            return fcmPushContentDto.getMember().getNickname()
                + "님, 오늘 해야할 일이에요!"
                + builder.toString();
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), null);
        }
    },

    // GroupWithOutMeTarget
    COMPLETE_ALL_TODAY_TODO(NotificationCategory.COZY_HOME) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname() + "님이 오늘 해야 할 일을 전부 완료했어요!";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getContentMember().getId());
        }
    },

    /**
     * GroupWithOutMeTarget
     * ex) xx님이 xx에 뛰어들어왔어요!
     * 해당 방의 모든 메이트들에게 알림
     * 방 나가기 API에 추가
     */
    ROOM_IN(NotificationCategory.ROOM) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname() + "님이 "
                + fcmPushContentDto.getRoom().getName() + "에 뛰어들어왔어요!";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getRoom().getId());
        }
    },

    /**
     * GroupWithOutMeTarget
     * ex) xx님이 xx을/를 뛰쳐나갔어요!
     * 해당 방의 모든 메이트들에게 알림
     * 방 나가기 API에 추가
     */
    ROOM_OUT(NotificationCategory.ROOM) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname() + "님이 "
                + fcmPushContentDto.getRoom().getName() + "을/를 뛰쳐나갔어요!";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getRoom().getId());
        }
    },

    /**
     * ex) xx님이 초대 요청을 거절했어요
     * 방장이 특정 사용자에게 방 초대를 했지만 그 사용자가 요청을 거절한 경우 방의 방장에게만 가는 알림
     * 방 초대 수락/거절 API에 추가
     * OneTargetReverse
     */
    REJECT_ROOM_INVITE(NotificationCategory.ROOM_INVITE_REQUEST) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname()
                + "님이 초대 요청을 거절했어요";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getContentMember().getId());
        }
    },

    /**
     * ex) xx님이 방 초대 요청을 수락했어요
     * 방장이 특정 사용자에게 방 초대에 그 사용자가 요청을 수락한 경우 방의 방장에게만 가는 알림
     * 방 초대 수락/거절 API에 추가
     * OneTargetReverse
     */
    ACCEPT_ROOM_INVITE(NotificationCategory.ROOM_INVITE_REQUEST) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname()
                + "님이 방 초대 요청을 수락했어요";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getContentMember().getId());
        }
    },

    /**
     * ex) xx님에게 xx으로 초대 요청을 보냈어요
     * 방장이 특정 사용자에게 방 초대 요청을 보냈을 때 자기 자신에게 오는 알림
     * 선택한 코지메이트 방에 초대요청 보내기 API에 추가
     * OneTargetReverse
     */
    SEND_ROOM_INVITE(NotificationCategory.ROOM_INVITE_REQUEST) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname() + "님에게 "
                + fcmPushContentDto.getRoom().getName() + "으로 초대 요청을 보냈어요";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getRoom().getId());
        }
    },

    /**
     * ex) xx님이 xx으로 나를 초대했어요
     * 방장으로부터 방 초대를 받은 사용자에게 보내는 알림
     * OneTargetReverseWithRoomName
     */
    ARRIVE_ROOM_INVITE(NotificationCategory.ROOM_INVITE_REQUEST) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname() + "님이 "
                + fcmPushContentDto.getRoom().getName() + "으로 나를 초대했어요";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getRoom().getId());
        }
    },

    /**
     * ex) xx님이 방 참여 요청을 거절했어요
     * 내가 방 참여 요청을 보낸 방의 방장이 나의 요청을 거절한 경우 알림
     * OneTargetReverse
     */
    REJECT_ROOM_JOIN(NotificationCategory.ROOM_JOIN_REQUEST) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname()
                + "님이 방 참여 요청을 거절했어요";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getContentMember().getId());
        }
    },

    /**
     * ex) xx님이 방 참여 요청을 수락했어요
     * 내가 방 참여 요청을 보낸 방의 방장이 나의 요청을 수락한 경우 알림
     * OneTargetReverse
     */
    ACCEPT_ROOM_JOIN(NotificationCategory.ROOM_JOIN_REQUEST) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname()
                + "님이 방 참여 요청을 수락했어요";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getContentMember().getId());
        }
    },

    /**
     * ex) xx님이 방 참여 요청을 보냈어요
     * xx가 방장에게 방 참여 요청을 보낸 경우 방장이 받는 알림
     * OneTargetReverse
     */
    ARRIVE_ROOM_JOIN_REQUEST(NotificationCategory.ROOM_JOIN_REQUEST) {
        @Override
        public String generateContent(FcmPushContentDto fcmPushContentDto) {
            return fcmPushContentDto.getMember().getNickname()
                + "님이 방 참여 요청을 보냈어요";
        }

        @Override
        public NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO) {
            return NotificationLogConverter.toEntity(createDTO.getRecipientMember(), getCategory(),
                createDTO.getContent(), createDTO.getContentMember().getId());
        }
    }
    ;

    private NotificationCategory category;

    public abstract String generateContent(FcmPushContentDto fcmPushContentDto);
    public abstract NotificationLog generateNotificationLog(NotificationLogCreateDTO createDTO);

    @AllArgsConstructor
    @Getter
    public enum NotificationCategory {
        NOTICE("공지사항"), ROOM("방"), ROOM_INVITE_REQUEST("초대요청"), ROOM_JOIN_REQUEST("방 참여요청"),

        // 카테고리 미정
        COZY_HOME("A"), COZY_ROLE("E"),
        ;

        private String name;
    }
}