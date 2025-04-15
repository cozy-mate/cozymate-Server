package com.cozymate.cozymate_server.domain.roommemberstat.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomMemberStatDetailListDTO;
import com.cozymate.cozymate_server.domain.roommemberstat.service.RoomMemberStatService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomMemberStatController {
    private final RoomMemberStatService roomMemberStatService;

    @GetMapping("/{roomId}/memberStat/{memberStatKey}")
    @Operation(summary = "[포비] 방에 속해 있는 메이트 멤버 상세정보 조회",
        description = "방의 멤버 상세정보 조회하기\n"+
            "사용 가능한 Key는 다음과 같습니다 :\n\n" +
            "- **birthYear**\n" +
            "- **dormJoiningStatus**\n" +
            "- **admissionYear** \n" +
            "- **majorName** \n" +
            "- **wakeUpTime** \n" +
            "- **sleepingTime** \n" +
            "- **turnOffTime** \n" +
            "- **smokingStatus** \n" +
            "- **sleepingHabits** \n" +
            "- **coolingIntensity** \n" +
            "- **heatingIntensity** \n" +
            "- **lifePattern** \n" +
            "- **intimacy**\n" +
            "- **sharingStatus** \n" +
            "- **gamingStatus** \n" +
            "- **callingStatus**\n" +
            "- **studyingStatus** \n" +
            "- **eatingStatus** \n" +
            "- **cleannessSensitivity**\n" +
            "- **noiseSensitivity**\n" +
            "- **cleaningFrequency**\n" +
            "- **drinkingFrequency**\n" +
            "- **personalities**\n" +
            "- **mbti**\n\n" +
            "memberList는 아래 object를 배열로 갖고 있습니다.\n" +
            "```json\n" +
            "{\n" +
            "  \"memberDetail\": {},\n" +
            "  \"memberStat\": {\n"
            + "   \"mbti\" : \"INFP\"\n "
            + " }\n" +
            "}\n" +
            "```\n\n"
        + "")
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_PREFERENCE_NOT_EXISTS,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._PRIVATE_ROOM,

    })
    public ResponseEntity<ApiResponse<RoomMemberStatDetailListDTO>> get(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @PathVariable String memberStatKey
        ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            roomMemberStatService.getRoomMemberStatDetailList(
                roomId,
                memberStatKey
            )));
    }
}
