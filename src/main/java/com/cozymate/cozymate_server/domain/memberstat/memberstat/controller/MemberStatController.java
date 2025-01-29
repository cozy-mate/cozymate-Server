package com.cozymate.cozymate_server.domain.memberstat.memberstat.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.service.MemberStatQueryService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.service.MemberStatCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/members/stat")
@RequiredArgsConstructor
@RestController
public class MemberStatController {


    private final MemberStatCommandService memberStatService;

    private final MemberStatQueryService memberStatQueryService;

    @Operation(
        summary = "[말즈] 사용자 상세정보 등록",
        description = "사용자의 토큰을 넣어 사용하고, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다.\n\n"
            + "성격은 다중 선택 변경되었으므로, 배열로 보내주시면 됩니다."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_EXISTS,
        ErrorStatus._UNIVERSITY_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_MERIDIAN_NOT_VALID
    })
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatService.createMemberStat(memberDetails.member(),
                    createMemberStatRequestDTO)
            ));
    }

    @Operation(
        summary = "[말즈] 사용자 상세정보 수정",
        description = "사용자의 토큰을 넣어 사용하고, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다.\n\n"
            + "성격은 다중 선택 변경되었으므로, 배열로 보내주시면 됩니다."
    )
    @SwaggerApiError({
        ErrorStatus._UNIVERSITY_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_MERIDIAN_NOT_VALID
    })
    @PutMapping("")
    public ResponseEntity<ApiResponse<Long>> modifyMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatService.modifyMemberStat(memberDetails.member(),
                    createMemberStatRequestDTO)));
    }

    @Operation(
        summary = "[말즈] 사용자 상세정보 조회",
        description = "사용자의 토큰을 넣어 사용합니다.\n\n"
            + "성격, 잠버릇은 다중 선택으로, 문자열 배열을 리턴합니다.\n\n"
            + "멤버 정보는 memberDetail, 멤버 스탯 정보는 memberStatDetail 객체로 리턴합니다."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    @GetMapping("")
    public ResponseEntity<ApiResponse<MemberStatDetailWithMemberDetailResponseDTO>> getMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStat(memberDetails.member())
            ));
    }

    @Operation(
        summary = "[말즈] 사용자 상세정보 조회",
        description = "사용자의 토큰을 넣어 사용합니다.\n\n"
            + "성격, 잠버릇은 다중 선택으로, 문자열 배열을 리턴합니다.\n\n"
            + "멤버 정보는 memberDetail, 멤버 스탯 정보는 memberStatDetail 객체로 리턴합니다."
            + "일치율과 roomId를 추가로 리턴합니다."
            + "일치율은 본인을 호출할 경우, 또는 없는 사람을 호출할 경우 null을 리턴합니다."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberStatDetailAndRoomIdAndEqualityResponseDTO>> getMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStatWithId(memberDetails.member(), memberId)
            ));
    }

    @Operation(
        summary = "[말즈] 기숙사 인원 미정 여부 조회",
        description = "사용자 토큰을 넣고 사용합니다. 결과 값으로 정수를 리턴합니다.\n\n"
            + "- 미정일 경우 : 0 반환\n"
            + "- 인실이 정해져 있을 경우 : 사용자의 인실 반환\n"
            + "- 필터링 기능에서 사용하면 됩니다.\n"
            + "(미정일 때는 다른 인실 상세 필터링 가능, 아닐 경우 자신이 입력한 인실에 대한 결과를 반환)"
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    @GetMapping("/numOfRoommate")
    public ResponseEntity<ApiResponse<Integer>> getNumOfRoommateStatus(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getNumOfRoommateStatus(memberDetails.member().getId())
            ));
    }

    @Operation(
        summary = "[말즈] 사용자 상세정보 완전 일치 필터링 및 일치율 조회",
        description = "사용자의 토큰을 넣어 사용합니다." +
            "filterList = 필터명1,필터명2,...으로 사용하고, 없을 경우 쿼리문에 아예 filterList를 넣지 않으셔도 됩니다.\n\n"
            + "사용 가능한 필터명(24개):\n"
            + "- birthYear: 출생년도\n"
            + "- admissionYear : 학번\n"
            + "- majorName : 학과\n"
            + "- acceptance : 합격여부\n"
            + "- wakeUpTime : 기상시간\n"
            + "- sleepingTime : 취침시간\n"
            + "- turnOffTime : 소등시간\n"
            + "- smoking : 흡연여부\n"
            + "- sleepingHabit : 잠버릇\n"
            + "- airConditioningIntensity : 에어컨 강도\n"
            + "- heatingIntensity : 히터 강도\n"
            + "- lifePattern : 생활패턴\n"
            + "- intimacy : 친밀도\n"
            + "- canShare : 물건공유\n"
            + "- isPlayGame : 게임여부\n"
            + "- isPhoneCall : 전화여부\n"
            + "- studying : 공부여부\n"
            + "- intake : 섭취여부\n"
            + "- cleanSensitivity : 청결예민도\n"
            + "- noiseSensitivity : 소음예민도\n"
            + "- cleaningFrequency : 청소 빈도\n"
            + "- drinkingFrequency : 음주 빈도\n"
            + "- personality : 성격\n"
            + "- mbti : mbti\n"
            + "- numOfRoommate : 신청실(Optional)\n\n" +
            "memberList는는 아래 object를 배열로 갖고 있습니다.\n" +
            "```json\n" +
            "{\n" +
            "  \"memberDetail\": {},\n" +
            "  \"equality\": 0,\n" +
            "  \"preferenceStats\": {},\n" +
            "}\n" +
            "```\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID,
        ErrorStatus._MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE
    })
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<MemberStatPageResponseDTO<List<?>>>> getFilteredMemberList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) List<String> filterList
    ) {
        Pageable pageable = PageRequest.of(page, 5);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStatList(
                    memberDetails.member(), filterList, pageable)
            ));
    }


    @Operation(
        summary = "[말즈] 사용자 상세정보를 키-값으로 필터링하고, 필터링에 맞는 인원 수를 리턴합니다",
        description = "사용자의 토큰을 넣어 사용합니다. " +
            "filterMap은 RequestBody에 다음과 같은 형식으로 전달됩니다:\n\n" +
            "Key는 넣어도 되고, 안 넣어도 됩니다. 다만 Value의 정보가 없을 때는 빈 배열로 주시면 됩니다.\n\n" +
            "```json\n" +
            "{\n" +
            "  \"birthYear\": [1995, 1996],\n" +
            "  \"major\": [\"컴퓨터공학과\", \"경영학과\"],\n" +
            "  \"smoking\": [true],\n" +
            "  \"mbti\": [\"INTJ\", \"ENTP\"]\n" +
            "}\n" +
            "```\n\n" +
            "사용 가능한 Key 목록과 데이터 형식은 다음과 같습니다 (총 25개):\n\n" +
            "- **birthYear** (출생년도) : `[Integer]` 예) `[1995, 1996]`\n" +
            "- **admissionYear** (학번) : `[String]` 예) `[\"09\", \"20\"]`\n" +
            "- **majorName** (학과) : `[String]` 예) `[\"컴퓨터공학과\", \"경영학과\"]`\n" +
            "- **acceptance** (합격여부) : `[String]` 예) `[\"합격\",\"대기중\"]`\n" +
            "- **wakeUpTime** (기상시간) : `[Integer]` 예) `[7, 8]`\n" +
            "- **sleepingTime** (취침시간) : `[Integer]` 예) `[2, 3]`\n" +
            "- **turnOffTime** (소등시간) : `[Integer]` 예) `[22]`\n" +
            "- **smoking** (흡연여부) : `[String]` 예) `[\"액상형 전자담배\",\"비흡연자\"]`\n" +
            "- **sleepingHabit** (잠버릇) : `[String]` 예) `[\"코골이\", \"이갈이\"]`\n" +
            "- **airConditioningIntensity** (에어컨 강도) : `[Integer]` 예) `[1,2]`\n" +
            "- **heatingIntensity** (히터 강도) : `[Integer]` 예) `[1,2]`\n" +
            "- **lifePattern** (생활패턴) : `[String]` 예) `[\"아침형 인간\", \"새벽형 인간\"]`\n" +
            "- **intimacy** (친밀도) : `[Integer]` 예) `[1, 5]` (1~5 사이의 숫자)\n" +
            "- **canShare** (물건 공유 가능여부) : `[String]` 예) `[\"아무것도 공유하고싶지 않아요\"]`\n" +
            "- **isPlayGame** (게임여부) : `[String]` 예) `[\"아예 하지 않았으면 좋겠어요\"]`\n" +
            "- **isPhoneCall** (전화여부) : `[String]` 예) `[\"아예 하지 않았으면 좋겠어요\", \"부모님과의 전화는 괜찮아요\"]`\n"
            +
            "- **studying** (공부여부) : `[String]` 예) `[\"아예 하지 않았으면 좋겠어요\"]`\n" +
            "- **intake** (음식 섭취 여부) : `[String]` 예) `[\"간단한 간식은 괜찮아요\"]`\n" +
            "- **cleanSensitivity** (청결 예민도) : `[Integer]` 예) `[3, 4]` (1~5 사이의 숫자)\n" +
            "- **noiseSensitivity** (소음 예민도) : `[Integer]` 예) `[2, 5]` (1~5 사이의 숫자)\n" +
            "- **cleaningFrequency** (청소 빈도) : `[String]` 예) `[\"주1회\", \"월2회\"]`\n" +
            "- **drinkingFrequency** (음주 빈도) : `[String]` 예) `[\"거의 안 마셔요\",\"한 달에 한 두번 마셔요\"]`\n" +
            "- **personality** (성격) : `[String]` 예) `[\"외향적\", \"내향적\"]`\n" +
            "- **mbti** (MBTI, 대소 무관) : `[String]` 예) `[\"INTJ\", \"ENTP\"]`\n" +
            "- **numOfRoommate : 신청실(Optional)\n\n"

    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID,
        ErrorStatus._MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE,
        ErrorStatus._MEMBERSTAT_NEEDS_DETAIL_NEEDS_PREFERENCES_CANNOT_COEXIST
    })
    @PostMapping("/filter/search/count")
    public ResponseEntity<ApiResponse<Integer>> getSizeOfAdvancedFilteredMemberList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestBody HashMap<String, List<?>> filterMap) {

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getNumOfSearchedAndFilteredMemberStatList(
                    memberDetails.member(), filterMap)
            )
        );
    }

    @Operation(
        summary = "[말즈] 사용자 상세정보를 키-값으로 필터링하고, 사용자 목록 받아오기(일치율 포함)",
        description = "사용자의 토큰을 넣어 사용합니다. " +
            "filterMap은 RequestBody에 다음과 같은 형식으로 전달됩니다:\n\n" +
            "Key는 넣어도 되고, 안 넣어도 됩니다. 다만 Value의 정보가 없을 때는 빈 배열로 주시면 됩니다.\n\n" +
            "```json\n" +
            "{\n" +
            "  \"birthYear\": [1995, 1996],\n" +
            "  \"major\": [\"컴퓨터공학과\", \"경영학과\"],\n" +
            "  \"smoking\": [true],\n" +
            "  \"mbti\": [\"INTJ\", \"ENTP\"]\n" +
            "}\n" +
            "```\n\n" +
            "사용 가능한 Key 목록과 데이터 형식은 다음과 같습니다 (총 25개):\n\n" +
            "- **birthYear** (출생년도) : `[Integer]` 예) `[1995, 1996]`\n" +
            "- **admissionYear** (학번) : `[String]` 예) `[\"09\", \"20\"]`\n" +
            "- **majorName** (학과) : `[String]` 예) `[\"컴퓨터공학과\", \"경영학과\"]`\n" +
            "- **acceptance** (합격여부) : `[String]` 예) `[\"합격\",\"대기중\"]`\n" +
            "- **wakeUpTime** (기상시간) : `[Integer]` 예) `[7, 8]`\n" +
            "- **sleepingTime** (취침시간) : `[Integer]` 예) `[2, 3]`\n" +
            "- **turnOffTime** (소등시간) : `[Integer]` 예) `[22]`\n" +
            "- **smoking** (흡연여부) : `[String]` 예) `[\"액상형 전자담배\",\"비흡연자\"]`\n" +
            "- **sleepingHabit** (잠버릇) : `[String]` 예) `[\"코골이\", \"이갈이\"]`\n" +
            "- **airConditioningIntensity** (에어컨 강도) : `[Integer]` 예) `[1,2]`\n" +
            "- **heatingIntensity** (히터 강도) : `[Integer]` 예) `[1,2]`\n" +
            "- **lifePattern** (생활패턴) : `[String]` 예) `[\"아침형 인간\", \"새벽형 인간\"]`\n" +
            "- **intimacy** (친밀도) : `[Integer]` 예) `[1, 5]` (1~5 사이의 숫자)\n" +
            "- **canShare** (물건 공유 가능여부) : `[String]` 예) `[\"아무것도 공유하고싶지 않아요\"]`\n" +
            "- **isPlayGame** (게임여부) : `[String]` 예) `[\"아예 하지 않았으면 좋겠어요\"]`\n" +
            "- **isPhoneCall** (전화여부) : `[String]` 예) `[\"아예 하지 않았으면 좋겠어요\", \"부모님과의 전화는 괜찮아요\"]`\n"
            +
            "- **studying** (공부여부) : `[String]` 예) `[\"아예 하지 않았으면 좋겠어요\"]`\n" +
            "- **intake** (음식 섭취 여부) : `[String]` 예) `[\"간단한 간식은 괜찮아요\"]`\n" +
            "- **cleanSensitivity** (청결 예민도) : `[Integer]` 예) `[3, 4]` (1~5 사이의 숫자)\n" +
            "- **noiseSensitivity** (소음 예민도) : `[Integer]` 예) `[2, 5]` (1~5 사이의 숫자)\n" +
            "- **cleaningFrequency** (청소 빈도) : `[String]` 예) `[\"주1회\", \"월2회\"]`\n" +
            "- **drinkingFrequency** (음주 빈도) : `[String]` 예) `[\"거의 안 마셔요\",\"한 달에 한 두번 마셔요\"]`\n" +
            "- **personality** (성격) : `[String]` 예) `[\"외향적\", \"내향적\"]`\n" +
            "- **mbti** (MBTI, 대소 무관) : `[String]` 예) `[\"INTJ\", \"ENTP\"]`\n" +
            "- **numOfRoommate : 신청실(Optional)\n\n" +
            "memberList는 아래 object를 배열로 갖고 있습니다.\n" +
            "```json\n" +
            "{\n" +
            "  \"memberDetail\": {},\n" +
            "  \"equality\": 0,\n" +
            "  \"preferenceStats\": {},\n" +
            "}\n" +
            "```\n\n"

    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID,
        ErrorStatus._MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE,
        ErrorStatus._MEMBERSTAT_NEEDS_DETAIL_NEEDS_PREFERENCES_CANNOT_COEXIST
    })
    @PostMapping("/filter/search")
    public ResponseEntity<ApiResponse<MemberStatPageResponseDTO<List<?>>>> getAdvancedFilteredMemberList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestBody HashMap<String, List<?>> filterMap) {

        Pageable pageable = PageRequest.of(page, 5);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getSearchedAndFilteredMemberStatList(
                    memberDetails.member(), filterMap, pageable)
            )
        );
    }

    @Operation(
        summary = "[말즈] 사용자 랜덤 추천",
        description = "요청자의 토큰을 넣고 사용합니다.\n\n"
            + "랜덤 사용자 5명을 리턴합니다.\n\n"
            + "사용자 멤버 스탯이 없을 때 사용합니다.\n\n" +
            "memberList는 아래 object를 배열로 갖고 있습니다.\n" +
            "```json\n" +
            "{\n" +
            "  \"memberDetail\": {},\n" +
            "  \"equality\": 0,\n" +
            "  \"preferenceStats\": {},\n" +
            "}\n" +
            "```\n\n"
    )
    @GetMapping("/random")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_EXISTS
    })
    public ResponseEntity<ApiResponse<MemberStatRandomListResponseDTO>> getRandomMemberStatPreference(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getRandomMemberStatWithPreferences(memberDetails.member())
            ));
    }

    @GetMapping("/search")
    @Operation(
        summary = "[말즈] 사용자 검색",
        description = "요청자의 토큰을 넣고 사용합니다.\n\n"
            + "검색어의 일부만 일치해도, 일치율이 높은 순으로 결과를 리턴합니다.\n\n"
            + "완전일치 순으로 주는게 좋긴 하지만, 우선 일치율, ID 순으로 구현했습니다.\n\n"
            + "QueryString으로 keyword=검색어 일부로 사용합니다.\n\n" +
            "memberList는 아래 object를 배열로 갖고 있습니다.\n" +
            "```json\n" +
            "{\n" +
            "  \"memberDetail\": {},\n" +
            "  \"equality\": 0,\n" +
            "}\n" +
            "```\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_EQUALITY_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<List<MemberStatSearchResponseDTO>>> getMemberSearchResponse(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberSearchResponse(
                    memberDetails.member(), keyword
                )
            )
        );
    }

}
