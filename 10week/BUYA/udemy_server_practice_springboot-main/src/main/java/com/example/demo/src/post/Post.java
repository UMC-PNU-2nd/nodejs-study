package com.example.demo.src.post;

import com.example.demo.config.BaseEntity;
import com.example.demo.src.userfeed.UserFeed;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Post")
public class Post extends BaseEntity {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "feed_id")
    private UserFeed userFeed;

    @Column(nullable = false)
    private String postImgUrl;

    @Lob
    private String content;

    @Builder
    public Post(UserFeed userFeed, String postImgUrl, String content) {
        Assert.notNull(userFeed, "UserFeed should not be null");
        Assert.hasText(postImgUrl, "Image Url must not be empty");

        this.userFeed = userFeed;
        this.postImgUrl = postImgUrl;
        this.content = content;
    }
}
