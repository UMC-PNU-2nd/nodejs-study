package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/post")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtService jwtService;
    private final PostService postService;

    public PostController(JwtService jwtService, PostService postService) {
        this.jwtService = jwtService;
        this.postService = postService;
    }

    /**
     * Post create
     * [POST] /post
     */
    @PostMapping("")
    public BaseResponse<PostDto.postRes> createPost(@RequestBody PostDto.postReq req) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if(req.getContent() == null) {
                return new BaseResponse<>(POST_POSTS_EMPTY_CONTENT);
            }
            if(req.getPostImgUrl() == null) {
                return new BaseResponse<>(POST_POSTS_EMPTY_IMAGE);
            }

            PostDto.postRes postRes = postService.createPost(req, userIdxByJwt);
            return new BaseResponse<>(postRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
