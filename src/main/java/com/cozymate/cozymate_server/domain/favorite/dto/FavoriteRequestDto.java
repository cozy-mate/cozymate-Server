package com.cozymate.cozymate_server.domain.favorite.dto;

import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.global.utils.EnumValid;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteRequestDto {

    private Long targetId;
    @EnumValid(enumClass = FavoriteType.class)
    private String favoriteType;
}