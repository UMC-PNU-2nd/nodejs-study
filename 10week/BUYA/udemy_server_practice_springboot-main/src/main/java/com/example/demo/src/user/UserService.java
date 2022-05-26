package com.example.demo.src.user;

import com.example.demo.config.BaseException;

import com.example.demo.src.userfeed.UserFeedService;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserFeedService userFeedService;

    @Autowired
    public UserService(UserProvider userProvider, JwtService jwtService, UserRepository userRepository, UserFeedService userFeedService) {
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userFeedService = userFeedService;
    }

    public UserDto.AuthUserRes createUser(UserDto.userReq req) throws BaseException {
        if(userProvider.checkEmail(req.getEmail()) == 1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try {
            pwd = new SHA256().encrypt(req.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try {
            User user = req.toEntity(pwd);
            Long userIdx = userRepository.save(user).getId();

            userFeedService.createUserFeed(user);

            String jwt = jwtService.createJwt(userIdx);
            return new UserDto.AuthUserRes(userIdx, jwt);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public UserDto.AuthUserRes loginUser(UserDto.LoginReq req) throws BaseException {
        String pwd;
        try {
            pwd = new SHA256().encrypt(req.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try {
            User user = userProvider.getUsersByEmail(req.getEmail());
            if(!pwd.equals(user.getPassword())) { throw new BaseException(LOGIN_FAIL); }

            Long userIdx = user.getId();
            String jwt = jwtService.createJwt(userIdx);
            return new UserDto.AuthUserRes(userIdx, jwt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public UserDto.ReadUserRes modifyUserName(Long userIdx, UserDto.UpdateUserNameReq req) throws BaseException, NoSuchElementException {
        User user;
        try {
            // user = userRepository.findById(userIdx).get();
            user = userProvider.getUsersByIdx(userIdx);
        } catch(NoSuchElementException noSuchElementException) {
            throw new BaseException(UNKNOWN_ID);
        }
        try {
            user.updateName(req);
            userRepository.save(user);
            return new UserDto.ReadUserRes(user);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteUser(Long userIdx) throws BaseException {
        if (!userRepository.existsById(userIdx)) {
            throw new BaseException(DELETE_FAIL);
        }

        try {
            userRepository.deleteById(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
