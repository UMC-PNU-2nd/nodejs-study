package com.example.demo.src.userfeed;

import com.example.demo.config.BaseEntity;
import com.example.demo.src.post.Post;
import com.example.demo.src.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "UserFeed")
public class UserFeed extends BaseEntity {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy="userFeed")
    private List<Post> posts = new ArrayList<Post>();

    @Builder
    public UserFeed(User user) {
        Assert.notNull(user, "User shouldn't be Null");

        this.user = user;
    }

    public void addPost(Post post) {
        posts.add(post);
    }
}
