package com.example.demo.src.user;

import com.example.demo.src.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateUserReq {
        private String name;
        private String nickName;
        private String phone;
        private String email;
        private String password;

        @Builder
        public CreateUserReq(String name, String nickName, String phone, String email, String password) {
            this.name = name;
            this.nickName = nickName;
            this.phone = phone;
            this.email = email;
            this.password = password;
        }

        public User toEntity(String pwd) {
            return User.builder()
                    .name(this.name)
                    .nickName(this.nickName)
                    .phone(this.phone)
                    .email(this.email)
                    .password(pwd)
                    .build();
        }
    }

    @Getter
    public static class CreateUserRes {
        private Long userIdx;
        private String jwt;

        public CreateUserRes(Long userIdx, String jwt) {
            this.userIdx = userIdx;
            this.jwt = jwt;
        }
    }

    @Getter
    public static class ReadUserRes {
        private Long userIdx;
        private String name;
        private String nickName;
        private String email;

        public ReadUserRes(User user) {
            this.userIdx = user.getId();
            this.name = user.getName();
            this.nickName = user.getNickName();
            this.email = user.getEmail();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateUserNameReq {
        private String nickName;

        @Builder
        public UpdateUserNameReq(String nickName) {
            this.nickName = nickName;
        }
    }

}
