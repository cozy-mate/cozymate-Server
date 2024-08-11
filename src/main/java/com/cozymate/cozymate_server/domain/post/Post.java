package com.cozymate.cozymate_server.domain.post;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.post.dto.PostCreateDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostUpdateDTO;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mate writer;

    private String title;

    private String content;

    public void update(PostUpdateDTO postUpdateDTO) {
        this.title = postUpdateDTO.getTitle();
        this.content = postUpdateDTO.getContent();
    }

}
