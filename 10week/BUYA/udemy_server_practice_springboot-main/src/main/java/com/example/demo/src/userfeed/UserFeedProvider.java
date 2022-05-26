package com.example.demo.src.userfeed;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.UNKNOWN_ID;

@Service
public class UserFeedProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtService jwtService;
    private final UserFeedRepository userFeedRepository;

    @Autowired
    public UserFeedProvider(JwtService jwtService, UserFeedRepository userFeedRepository) {
        this.jwtService = jwtService;
        this.userFeedRepository = userFeedRepository;
    }

    public UserFeed getUserFeedById(Long id) throws BaseException{
        try {
            UserFeed userFeed = userFeedRepository.findById(id).get();
            return userFeed;
        } catch (Exception exception) {
            throw new BaseException(UNKNOWN_ID);
        }
    }

}
