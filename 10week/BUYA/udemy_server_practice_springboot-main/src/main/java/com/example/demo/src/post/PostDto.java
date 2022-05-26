package com.example.demo.src.post;

import com.example.demo.src.userfeed.UserFeed;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class postReq {
        private String postImgUrl;
        private String content;

        @Builder
        public postReq(String postImgUrl, String content) {
            this.postImgUrl = postImgUrl;
            this.content = content;
        }

        public Post toEntity(UserFeed userFeed) {
            return Post.builder()
                    .userFeed(userFeed)
                    .postImgUrl(postImgUrl)
                    .content(content)
                    .build();
        }
    }

    @Getter
    public static class postRes {
        private Long id;
        private String postImgUrl;
        private String content;

        public postRes(Post post) {
            this.id = post.getId();
            this.postImgUrl = post.getPostImgUrl();
            this.content = post.getContent();
        }
    }

}
