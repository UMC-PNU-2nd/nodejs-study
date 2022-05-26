package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.userfeed.UserFeed;
import com.example.demo.src.userfeed.UserFeedProvider;
import com.example.demo.src.userfeed.UserFeedService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtService jwtService;
    private final PostRepository postRepository;
    private final UserFeedProvider userFeedProvider;
    private final UserFeedService userFeedService;

    @Autowired
    public PostService(JwtService jwtService, PostRepository postRepository, UserFeedProvider userFeedProvider, UserFeedService userFeedService) {
        this.jwtService = jwtService;
        this.postRepository = postRepository;
        this.userFeedProvider = userFeedProvider;
        this.userFeedService = userFeedService;
    }

    public PostDto.postRes createPost(PostDto.postReq req, Long id) throws BaseException {
        try {
            UserFeed userFeed = userFeedProvider.getUserFeedById(id);
            Post post = req.toEntity(userFeed);

            Post newPost = postRepository.save(post);
            userFeedService.addPostAtUserFeed(userFeed, newPost);

            return new PostDto.postRes(newPost);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
