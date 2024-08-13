package com.cozymate.cozymate_server.domain.postimage.service;

import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.postimage.PostImage;
import com.cozymate.cozymate_server.domain.postimage.PostImageRepository;
import com.cozymate.cozymate_server.domain.postimage.converter.PostImageConverter;
import com.cozymate.cozymate_server.global.s3.service.S3CommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostImageCommandService {

    private final PostImageRepository postImageRepository;
    private final S3CommandService s3CommandService;

    public void deleteImages(Post post) {

        List<PostImage> currentImage = postImageRepository.findByPostId(post.getId());

        if (!currentImage.isEmpty()) {
            s3CommandService.deleteByPostImages(currentImage);
            postImageRepository.deleteAll(currentImage);
        }

    }
    public void saveImages(Post post, List<String> imageUriList) {
        if (!imageUriList.isEmpty()) {
            List<PostImage> postImages = imageUriList.stream()
                .filter(uri -> uri.contains("/"))
                .map(uri -> uri.substring(uri.lastIndexOf('/') + 1))
                .map(content -> PostImageConverter.toEntity(post, content))
                .toList();
            postImageRepository.saveAll(postImages);
        }
    }

}
