//package com.cozymate.cozymate_server.domain.notificationlog;
//
//import com.cozymate.cozymate_server.domain.chat.ChatTestBuilder;
//import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
//import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
//import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentDto;
//
//public class NotificationLogTestBuilder {
//
//    public static NotificationLog testNotificationLogBuild() {
//        return NotificationLog.builder()
//            .member(ChatTestBuilder.testSenderBuild())
//            .category(NotificationCategory.COZY_MATE_SEND)
//            .content(NotificationType.COZY_MATE_REQUEST_TO.generateContent(
//                FcmPushContentDto.create(ChatTestBuilder.testRecipientBuild())))
//            .build();
//    }
//
//    public static NotificationLog testNotificationLog2Build() {
//        return NotificationLog.builder()
//            .member(ChatTestBuilder.testSenderBuild())
//            .category(NotificationCategory.COZY_MATE_ACCEPT)
//            .content(NotificationType.COZY_MATE_REQUEST_ACCEPT.generateContent(
//                FcmPushContentDto.create(ChatTestBuilder.testRecipientBuild())))
//            .build();
//    }
//}