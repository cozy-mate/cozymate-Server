//package com.cozymate.cozymate_server.domain.fcm.service;
//
//import com.cozymate.cozymate_server.domain.fcm.Fcm;
//import com.cozymate.cozymate_server.domain.fcm.FcmTestBuilder;
//import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentDto;
//import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
//import com.cozymate.cozymate_server.domain.member.Member;
//import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
//import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
//import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepository;
//import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupTargetDto;
//import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
//import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetReverseDto;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.DisplayNameGeneration;
//import org.junit.jupiter.api.DisplayNameGenerator;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.*;
//
//@DisplayName("FcmPushService 클래스의")
//@ExtendWith(MockitoExtension.class)
//public class FcmPushServiceTest {
//
//    @Mock
//    FirebaseMessaging firebaseMessaging;
//    @Mock
//    NotificationLogRepository notificationLogRepository;
//    @Mock
//    FcmRepository fcmRepository;
//    @InjectMocks
//    FcmPushService fcmPushService;
//
//    Member ve;
//    Member ro;
//    NotificationType notificationType;
//    Fcm iPhoneFcm;
//    Fcm iPhoneFcm2;
//    Fcm iPadFcm;
//    Fcm iPadFcm2;
//
//    @Nested
//    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//    class sendNotification_메서드는 {
//
//        @Nested
//        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//        class Parameter_OneTargetDto_경우 {
//
//            @Nested
//            @DisplayName("한 유저가 기기 두개로 로그인한 경우")
//            class Context_with_two_login {
//
//                @BeforeEach
//                void setUp() {
//                    ve = FcmTestBuilder.testMemberBuild();
//                    iPhoneFcm = FcmTestBuilder.testIPhoneFcmBuild();
//                    iPadFcm = FcmTestBuilder.testIPadFcmBuild();
//                    notificationType = NotificationType.REMINDER_ROLE;
//                }
//
//                @Test
//                @DisplayName("두 기기에 모두 알림을 보내고, 알림 로그는 1개만 저장된다.")
//                void testSendNotification() throws FirebaseMessagingException {
//                    //given
//                    OneTargetDto oneTargetDto = OneTargetDto.create(ve, notificationType, "빨래하기");
//
//                    FcmPushContentDto fcmPushContentDto = FcmPushContentDto.create(
//                        oneTargetDto.getMember(), oneTargetDto.getRoleContent());
//                    String content = notificationType.generateContent(fcmPushContentDto);
//
//                    given(fcmRepository.findByMember(ve)).willReturn(List.of(iPhoneFcm, iPadFcm));
//
//                    //when
//                    fcmPushService.sendNotification(oneTargetDto);
//
//                    // then
//                    then(firebaseMessaging).should(times(2)).send(any(Message.class));
//                    then(notificationLogRepository).should(times(1))
//                        .save(any(NotificationLog.class));
//                    assertThat(content).isEqualTo("베님, 오늘 빨래하기 잊지 않으셨죠?");
//                }
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//        class Parameter_OneTargetReverseDto_경우 {
//
//            @Nested
//            @DisplayName("한명은 로그인 기기가 2개, 다른 한명은 1개인 경우")
//            class Context_with_two_and_One_login {
//
//                @BeforeEach
//                void setUp() {
//                    ve = FcmTestBuilder.testMemberBuild();
//                    ro = FcmTestBuilder.testMember2Build();
//                    iPhoneFcm = FcmTestBuilder.testIPhoneFcmBuild();
//                    iPadFcm = FcmTestBuilder.testIPadFcmBuild();
//                    iPhoneFcm2 = FcmTestBuilder.testIPhoneFcm2Build();
//                    notificationType = NotificationType.COZY_MATE_REQUEST_TO;
//                }
//
//                @Test
//                @DisplayName("총 3개의 기기에 알림을 보내고, 알림 로그는 2개가 저장된다.")
//                void testSendNotification() throws FirebaseMessagingException {
//                    // given
//                    OneTargetReverseDto oneTargetReverseDto = OneTargetReverseDto.create(ve, ro,
//                        notificationType);
//                    FcmPushContentDto fcmPushContentDto = FcmPushContentDto.create(
//                        ve);
//                    String content = notificationType.generateContent(fcmPushContentDto);
//
//                    OneTargetReverseDto oneTargetReverseDto2 = OneTargetReverseDto.create(ro, ve,
//                        notificationType);
//                    FcmPushContentDto fcmPushContentDto2 = FcmPushContentDto.create(
//                        ro);
//                    NotificationType notificationType2 = NotificationType.COZY_MATE_REQUEST_FROM;
//                    String content2 = notificationType2.generateContent(fcmPushContentDto2);
//
//                    given(fcmRepository.findByMember(ve)).willReturn(List.of(iPhoneFcm, iPadFcm));
//                    given(fcmRepository.findByMember(ro)).willReturn(List.of(iPhoneFcm2));
//
//                    // when
//                    fcmPushService.sendNotification(oneTargetReverseDto);
//                    fcmPushService.sendNotification(oneTargetReverseDto2);
//
//                    // then
//                    then(firebaseMessaging).should(times(3)).send(any(Message.class));
//                    then(notificationLogRepository).should(times(2))
//                        .save(any(NotificationLog.class));
//                    assertThat(content).isEqualTo("베님에게 코지메이트 신청을 보냈어요!");
//                    assertThat(content2).isEqualTo("로님에게서 코지메이트 신청이 도착했어요!");
//                }
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//        class Parameter_GroupTargetDto_경우 {
//
//            @Nested
//            @DisplayName("둘 다 로그인 기기가 2개인 경우")
//            class Context_with_two_and_two_login {
//
//                @BeforeEach
//                void setUp() {
//                    ve = FcmTestBuilder.testMemberBuild();
//                    ro = FcmTestBuilder.testMember2Build();
//                    iPhoneFcm = FcmTestBuilder.testIPhoneFcmBuild();
//                    iPadFcm = FcmTestBuilder.testIPadFcmBuild();
//                    iPhoneFcm2 = FcmTestBuilder.testIPhoneFcm2Build();
//                    iPadFcm2 = FcmTestBuilder.testIPadFcm2Build();
//                    notificationType = NotificationType.ROOM_CREATED;
//                }
//
//                @Test
//                @DisplayName("총 4개의 기기에 알림을 보내고, 알림 로그는 2개가 저장된다.")
//                void testSendNotification() throws FirebaseMessagingException {
//                    // given
//                    List<Member> memberList = List.of(ve, ro);
//                    GroupTargetDto groupTargetDto = GroupTargetDto.create(memberList,
//                        notificationType);
//
//                    FcmPushContentDto fcmPushContentDto = FcmPushContentDto.create(
//                        ve);
//                    String content = notificationType.generateContent(fcmPushContentDto);
//
//                    FcmPushContentDto fcmPushContentDto2 = FcmPushContentDto.create(
//                        ro);
//                    String content2 = notificationType.generateContent(fcmPushContentDto2);
//
//                    given(fcmRepository.findByMember(ve)).willReturn(List.of(iPhoneFcm, iPadFcm));
//                    given(fcmRepository.findByMember(ro)).willReturn(List.of(iPhoneFcm2, iPadFcm2));
//
//                    // when
//                    fcmPushService.sendNotification(groupTargetDto);
//
//                    // then
//                    then(firebaseMessaging).should(times(4)).send(any(Message.class));
//                    then(notificationLogRepository).should(times(memberList.size()))
//                        .save(any(NotificationLog.class));
//                    assertThat(memberList.size()).isEqualTo(2);
//                    assertThat(content).isEqualTo(content2)
//                        .isEqualTo("방이 열렸어요, 얼른 가서 코지메이트를 만나봐요!");
//                }
//            }
//        }
//    }
//}