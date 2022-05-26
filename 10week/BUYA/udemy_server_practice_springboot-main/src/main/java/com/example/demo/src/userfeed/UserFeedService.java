package com.example.demo.src.userfeed;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.Post;
import com.example.demo.src.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class UserFeedService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserFeedRepository userFeedRepository;

    @Autowired
    public UserFeedService(UserFeedRepository userFeedRepository) {
        this.userFeedRepository = userFeedRepository;
    }

    public void createUserFeed(User user) throws BaseException {
        try {
            UserFeed userFeed = UserFeed.builder()
                    .user(user)
                    .build();
            userFeedRepository.save(userFeed);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void addPostAtUserFeed(UserFeed userFeed, Post post) throws BaseException {
        try {
            userFeed.addPost(post);
            userFeedRepository.save(userFeed);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
