package com.cozymate.cozymate_server.domain.room.dto;

import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatDifferenceResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RoomResponseDto {

    @Getter
    @AllArgsConstructor
    public static class RoomCreateResponse {

        private Long roomId;
        private String name;
        private String inviteCode;
        private Integer profileImage;
        List<CozymateInfoResponse> mateList;
        private Long managerId;
        private Boolean isRoomManager;
        private Integer maxMateNum;
        private Integer numOfArrival;
        private RoomType roomType;
        private List<String> hashtags;
        private Integer equality;
        private MemberStatDifferenceResponseDTO difference;
        // TODO: 기숙사 정보 추가

    }

    @Getter
    @Builder
    public static class RoomExistResponse {
        private Long roomId;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomJoinResponse {
        private Long roomId;
        private String name;
        private String managerName;
        private Integer maxMateNum;
    }

    @Getter
    @Builder
    public static class RoomListResponse {
        public Long roomId;
        public String name;
        public Integer roomEquality;
        private List<String> hashtags;
        private Integer numOfArrival;
    }

}
