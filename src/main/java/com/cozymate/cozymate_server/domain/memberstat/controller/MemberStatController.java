package com.cozymate.cozymate_server.domain.memberstat.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatPageResponseDto;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatCommandService;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatQueryService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private final MemberStatCommandService memberStatCommandService;
    private final MemberStatQueryService memberStatQueryService;


    @Operation(
        summary = "[포비] 사용자 상세정보 등록",
        description = "사용자의 토큰을 넣어 사용하고, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_EXISTS,
        ErrorStatus._UNIVERSITY_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_MERIDIAN_NOT_VALID
    })
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody MemberStatRequestDTO.MemberStatCommandRequestDTO memberStatCommandRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatCommandService.createMemberStat(memberDetails.member(),
                    memberStatCommandRequestDTO)));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 수정",
        description = "사용자의 토큰을 넣어 사용하고, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다."
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
        @Valid @RequestBody MemberStatRequestDTO.MemberStatCommandRequestDTO memberStatCommandRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatCommandService.modifyMemberStat(memberDetails.member(),
                    memberStatCommandRequestDTO)));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 조회",
        description = "사용자의 토큰을 넣어 사용합니다.\n"
            + "시간 관련 처리를 유의해주세요."
            + "성격이 다중 선택으로 변경되어, 문자열 배열로 드릴 예정입니다."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    @GetMapping("")
    public ResponseEntity<ApiResponse<MemberStatQueryResponseDTO>> getMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStat(memberDetails.member())
            ));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 조회",
        description = "사용자 토큰을 넣고, memberId를 PathVariable로 사용합니다.\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberStatDetailResponseDTO>> getMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStatWithId(memberDetails.member(), memberId)
            ));
    }

    @Operation(
        summary = "[포비] 기숙사 인원 미정 여부 조회",
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
        summary = "[포비] 사용자 상세정보 완전 일치 필터링 및 일치율 조회",
        description = "사용자의 토큰을 넣어 사용합니다." +
            "needsDetail : 필수는 아님, true시 해당 멤버의 모든 상세정보도 같이 출력함\n" +
            "needsPreferences: 필수 아님, true시 멤버 ID, 닉네임, 일치율, 요청자의 선호도 4가지 설정 출력함\n" +
            "현재는 needsDetail과 needsPreferences가 둘 다 true일 수 없음. 예외처리 해놨습니다." +
            "filterList = 필터명1,필터명2,...으로 사용하고, 없을 경우 쿼리문에 아예 filterList를 넣지 않으셔도 됩니다.\n\n"
            + "사용 가능한 필터명(25개):\n"
            + "- birthYear: 출생년도"
            + "- acceptance : 합격여부\n"
            + "- admissionYear : 학번\n"
            + "- dormitoryNames: 기숙사 이름\n"
            + "- major : 학과\n"
            + "- numOfRoommate : 신청실\n"
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
            + "- mbti : mbti"
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID,
        ErrorStatus._MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE
    })
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<MemberStatPageResponseDto<List<?>>>> getFilteredMemberList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) List<String> filterList,
        @RequestParam(defaultValue = "false", required = false) boolean needsDetail,
        @RequestParam(defaultValue = "false", required = false) boolean needsPreferences
    ) {
        Pageable pageable = PageRequest.of(page, 5);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStatList(
                    memberDetails.member(), filterList, pageable, needsDetail, needsPreferences)
            ));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보를 키-값으로 필터링하고, 필터링에 맞는 인원 수 리턴받기",
        description = "사용자의 토큰을 넣어 사용합니다. " +
            "filterMap은 RequestBody에 다음과 같은 형식으로 전달됩니다:\n\n" +
            "```json\n" +
            "{\n" +
            "  \"birthYear\": [1995, 1996],\n" +
            "  \"major\": [\"컴퓨터공학과\", \"경영학과\"],\n" +
            "  \"smoking\": [true],\n" +
            "  \"mbti\": [\"INTJ\", \"ENTP\"]\n" +
            "}\n" +
            "```\n\n" +
            "Key는 넣어도 되고, 안 넣어도 됩니다. 다만 Value의 정보가 없을 때는 빈 배열로 주시면 됩니다." +
            "needsDetail : 필수는 아님, true시 해당 멤버의 모든 상세정보도 같이 출력함\n" +
            "needsPreferences: 필수 아님, true시 멤버 ID, 닉네임, 일치율, 요청자의 선호도 4가지 설정 출력함\n" +
            "현재는 needsDetail과 needsPreferences가 둘 다 true일 수 없음. 예외처리 해놨습니다." +
            "사용 가능한 Key 목록과 데이터 형식은 다음과 같습니다 (총 25개):\n\n" +
            "- **birthYear** (출생년도) : `[Integer]` 예) `[1995, 1996]`\n" +
            "- **acceptance** (합격여부) : `[String]` 예) `[\"합격\",\"대기중\"]`\n" +
            "- **admissionYear** (학번) : `[String]` 예) `[\"09\", \"20\"]`\n" +
            "- **dormitoryNames** (기숙사 이름): `[String]` `[\"1기숙사\"]`\n" +
            "- **major** (학과) : `[String]` 예) `[\"컴퓨터공학과\", \"경영학과\"]`\n" +
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
            "- **drinkingFrequency** (음주 빈도) : `[String]` 예) `[\"거의 안 마셔요\",\"한 달에 한 두번 마셔요\"]`" +
            "- **personality** (성격) : `[String]` 예) `[\"외향적\", \"내향적\"]`\n" +
            "- **mbti** (MBTI, 대소 무관) : `[String]` 예) `[\"INTJ\", \"ENTP\"]`\n"

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
        summary = "[포비] 사용자 상세정보를 키-값으로 필터링하고, 사용자 목록 받아오기(일치율 포함)",
        description = "사용자의 토큰을 넣어 사용합니다. " +
            "검색하고자 하는 정보를 RequestBody에 다음과 같은 형식으로 전달하면 됩니다:\n\n" +
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
            "- **acceptance** (합격여부) : `[String]` 예) `[\"합격\",\"대기중\"]`\n" +
            "- **admissionYear** (학번) : `[String]` 예) `[\"09\", \"20\"]`\n" +
            "- **dormitoryNames(기숙사 이름): `[String]` 예) `[\"1기숙사\"]`\n" +
            "- **major** (학과) : `[String]` 예) `[\"컴퓨터공학과\", \"경영학과\"]`\n" +
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
            "- **drinkingFrequency** (음주 빈도) : `[String]` 예) `[\"거의 안 마셔요\",\"한 달에 한 두번 마셔요\"]`" +
            "- **personality** (성격) : `[String]` 예) `[\"외향적\", \"내향적\"]`\n" +
            "- **mbti** (MBTI, 대소 무관) : `[String]` 예) `[\"INTJ\", \"ENTP\"]`\n" +
            "Key는 넣어도 되고, 안 넣어도 됩니다. 다만 Value의 정보가 없을 때는 빈 배열로 주시면 됩니다." +
            "needsDetail : 필수는 아님, true시 해당 멤버의 모든 상세정보도 같이 출력함\n" +
            "needsPreferences: 필수 아님, true시 멤버 ID, 닉네임, 일치율, 요청자의 선호도 4가지 설정 출력함\n" +
            "현재는 needsDetail과 needsPreferences가 둘 다 true일 수 없음. 예외처리 해놨습니다."

    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID,
        ErrorStatus._MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE,
        ErrorStatus._MEMBERSTAT_NEEDS_DETAIL_NEEDS_PREFERENCES_CANNOT_COEXIST
    })
    @PostMapping("/filter/search")
    public ResponseEntity<ApiResponse<MemberStatPageResponseDto<List<?>>>> getAdvancedFilteredMemberList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestBody HashMap<String, List<?>> filterMap,
        @RequestParam(defaultValue = "false", required = false) boolean needsDetail,
        @RequestParam(defaultValue = "false", required = false) boolean needsPreferences) {

        Pageable pageable = PageRequest.of(page, 5);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getSearchedAndFilteredMemberStatList(
                    memberDetails.member(), filterMap, pageable, needsDetail, needsPreferences)
            )
        );
    }


    @Deprecated(since = "2024-10-15, 상세정보 검색 기능 Update")
    @Operation(
        summary = "[포비] 사용자 상세정보 필터링, 일치율 조회(상세 정보 포함)",
        description = "사용자의 토큰을 넣어 사용합니다."
            + "filterList = 필터명1,필터명2,...으로 사용하고, 없을 경우 쿼리문에 아예 filterList를 넣지 않으셔도 됩니다.\n\n"
            + "사용 가능한 필터명(20개):\n"
            + "- acceptance : 합격여부\n"
            + "- admissionYear :  학번\n"
            + "- major : 전공\n"
            + "- dormitoryNames: 기숙사 이름\n"
            + "- numOfRoommate : 신청실\n"
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
            + "- cleaningFrequency : 청소예민도\n"
            + "- drinkingFrequency : 음주 빈도\n"
            + "- personality : 성격\n"
            + "- mbti : mbti"
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID
    })
    @GetMapping("/filter/details")
    public ResponseEntity<ApiResponse<MemberStatPageResponseDto<List<?>>>> getFilteredMemberListWithMemberDetails(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) List<String> filterList) {

        Pageable pageable = PageRequest.of(page, 5);

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStatList(
                    memberDetails.member(), filterList, pageable, true, false)
            ));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 삭제(관리자용)",
        description = "요청자의 토큰을 넣고, 삭제하고자 하는 사용자의 ID를 넣어 사용합니다.\n\n"
    )
    @DeleteMapping("/{memberId}")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    public ResponseEntity<ApiResponse<Boolean>> deleteMemberStat(
        @PathVariable Long memberId
    ) {
        // TODO : 관리자 권한 분리시 일반 사용자가 삭제하는 것을 제한하기
        memberStatCommandService.deleteMemberStat(memberId);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                true
            ));
    }

    @Operation(
        summary = "[포비] 사용자 랜덤 추천",
        description = "요청자의 토큰을 넣고 사용합니다.\n\n"
            + "랜덤 사용자 5명을 리턴합니다.\n\n"
            + "사용자 멤버 스탯이 없을 때 사용합니다.\n\n"
    )
    @GetMapping("/random")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
    })
    public ResponseEntity<ApiResponse<MemberStatRandomListResponseDTO>> getRandomMemberStatPreference(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getRandomMemberStatWithPreferences(memberDetails.getMember())
            ));
    }

    @GetMapping("/search")
    @Operation(
        summary = "[포비] 사용자 검색",
        description = "요청자의 토큰을 넣고 사용합니다.\n\n"
            + "검색어의 일부만 일치해도, 일치율이 높은 순으로 결과를 리턴합니다.\n\n"
            + "완전일치 순으로 주는게 좋긴 하지만, 우선 일치율, ID 순으로 구현했습니다.\n\n"
            + "QueryString으로 keyword=검색어 일부로 사용합니다."
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
                    keyword,
                    memberDetails.member()
                )
            )
        );
    }

}
