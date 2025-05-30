package com.cozymate.cozymate_server.domain.roomfavorite.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomFavoriteRepositoryService {

    private final RoomFavoriteRepository roomFavoriteRepository;

    public void createRoomFavorite(RoomFavorite roomFavorite) {
        roomFavoriteRepository.save(roomFavorite);
    }

    public RoomFavorite getRoomFavoriteByIdOrThrow(Long roomFavoriteId) {
        return roomFavoriteRepository.findById(roomFavoriteId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._ROOMFAVORITE_NOT_FOUND)
            );
    }

    public void deleteRoomFavorite(RoomFavorite roomFavorite) {
        roomFavoriteRepository.delete(roomFavorite);
    }

    public boolean existRoomFavoriteByMemberAndRoom(Member member, Room room) {
        return roomFavoriteRepository.existsByMemberAndRoom(member, room);
    }

    public Slice<RoomFavorite> getRoomFavoriteListByMember(Member member, Pageable pageable) {
        return roomFavoriteRepository.findPagingByMemberAndRoomStatusNot(member, RoomStatus.DISABLE,
            pageable);
    }
}
