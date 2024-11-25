package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.auth.repository.TokenRepository;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.mail.repository.MailRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.repository.MemberStatEqualityRepository;
import com.cozymate.cozymate_server.domain.memberstatpreference.repository.MemberStatPreferenceRepository;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepository;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.repository.PostRepository;
import com.cozymate.cozymate_server.domain.postcomment.PostCommentRepository;
import com.cozymate.cozymate_server.domain.postimage.PostImageRepository;
import com.cozymate.cozymate_server.domain.report.repository.ReportRepository;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.roomlog.repository.RoomLogRepository;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberWithdrawService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final MailRepository mailRepository;
    private final MemberStatRepository memberStatRepository;
    private final MemberStatPreferenceRepository memberStatPreferenceRepository;
    private final MemberStatEqualityRepository memberStatEqualityRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ReportRepository reportRepository;
    private final FcmRepository fcmRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final MateRepository mateRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostCommentRepository postCommentRepository;
    private final RoleRepository roleRepository;
    private final TodoRepository todoRepository;
    private final RoomLogRepository roomLogRepository;


    /**
     * 회원 탈퇴 로직을 처리하는 메서드. 관련된 모든 데이터를 삭제한 뒤, 최종적으로 회원 정보를 삭제한다.
     */
    @Transactional
    public void withdraw(Member member) {
        // 회원과 연관된 데이터를 삭제
        deleteRelatedWithMember(member);
        log.debug("사용자 관련 데이터 삭제 완료");

        memberRepository.delete(member);
        log.debug("사용자 탈퇴완료");

    }

    /**
     * 회원과 연관된 데이터를 삭제하는 메서드.
     */

    private void deleteRelatedWithMember(Member member) {
        log.debug("사용자 관련 데이터 삭제 시작");
        tokenRepository.deleteById(member.getClientId());
        mailRepository.deleteById(member.getId());

        log.debug("토큰,메일 삭제 완료");

        memberStatRepository.deleteByMemberId(member.getId());
        memberStatPreferenceRepository.deleteByMemberId(member.getId());
        memberStatEqualityRepository.deleteByMemberAIdOrMemberBId(member.getId());

        log.debug("사용자 상세정보 및 관련 통계 삭제 완료");

        handleChatAndChatRoom(member);

        reportRepository.bulkDeleteReporter(member);

        log.debug("신고내역 처리 완료");

        fcmRepository.deleteAllByMemberId(member.getId());
        notificationLogRepository.deleteAllByMemberId(member.getId());

        log.debug("Fcm 토큰, 알림 내역 삭제 완료");

        mateRepository.findAllByMemberId(member.getId())
            .forEach(this::deleteRelatedWithMate);

        mateRepository.deleteAllByMemberId(member.getId());

        log.debug("mate 삭제 완료");


    }

    /**
     * 회원과 관련된 채팅 및 채팅방 데이터를 처리하는 메서드. 회원이 보낸 채팅의 sender를 null로 만든다. chatroom의 참여자를 null로 만든다.
     * chatroom의 참여자가 모두 null이면 chatroom을 삭제한다.
     */
    private void handleChatAndChatRoom(Member member) {
        chatRepository.bulkDeleteSender(member);


        log.debug("쪽지 처리 완료");

        chatRoomRepository.bulkDeleteMemberA(member);
        chatRoomRepository.bulkDeleteMemberB(member);


        log.debug("쪽지방 처리 완료");


    }

    /**
     * Mate와 관련된 데이터를 삭제하는 메서드. 작성한 게시물, 할당된 role, 할당된 to-do 등을 관리한다. role, to-do 할당자에서 제외한다. role,
     * to-do 할당자 리스트가 비었다면 role, to-do를 삭제한다. to-do에 대응되는 room_log의 to-do를 null 로 만든다.
     */
    private void deleteRelatedWithMate(Mate mate) {
        postRepository.findAllByWriterId(mate.getId())
            .forEach(this::deleteRelatedWithPost);

        postRepository.deleteAllByWriterId(mate.getId());

        log.debug("post 삭제 완료");

        postCommentRepository.deleteAllByCommenterId(mate.getId());

        roleRepository.findAllByMateId(mate.getId()).forEach(role -> {
            role.removeAssignee(mate.getId());

            if (role.isAssignedMateListEmpty()) {
                roleRepository.delete(role);
            }

        });

        log.debug("role 삭제 완료");

        todoRepository.findAllByMateId(mate.getId()).forEach(todo -> {
            todo.removeAssignee(mate.getId());

            roomLogRepository.bulkDeleteTodo(todo);

            if (todo.isAssignedMateListEmpty()) {
                todoRepository.delete(todo);
            }
        });

        roleRepository.bulkDeleteMate(mate);
        todoRepository.bulkDeleteMate(mate);

        log.debug("todo 삭제 완료");
        log.debug("mate 관련 엔티티 삭제 완료");
    }

    /**
     * Post를 삭제하기전, post image와 comment를 모두 삭제한다.
     */
    private void deleteRelatedWithPost(Post post) {
        postImageRepository.deleteAllByPostId(post.getId());
        postCommentRepository.deleteAllByPostId(post.getId());

        log.debug("post관련 엔티티 삭제 완료");
    }
}
