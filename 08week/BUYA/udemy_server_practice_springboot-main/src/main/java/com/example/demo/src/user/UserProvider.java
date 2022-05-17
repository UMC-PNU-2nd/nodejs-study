package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.UNKNOWN_ID;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final JwtService jwtService;
    private final UserRepository userRepository;



    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }


    public UserDto.ReadUserRes getUsersByEmail(String email) throws BaseException{
        try{
            User user = userRepository.findByEmail(email);
            return new UserDto.ReadUserRes(user);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public UserDto.ReadUserRes getUsersByIdx(Long userIdx) throws BaseException{
        try{
            User user = userRepository.findById(userIdx).get();
            return new UserDto.ReadUserRes(user);
        }
        catch (NoSuchElementException noSuchElementException) {
            throw new BaseException(UNKNOWN_ID);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public Long checkEmail(String email) throws BaseException{
        try{
            return userRepository.countByEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
