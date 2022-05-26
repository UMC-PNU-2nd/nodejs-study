package com.example.demo.src.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class userReq {
        private String name;
        private String nickName;
        private String phone;
        private String email;
        private String password;
        private String profileImgUrl;
        private String websiteUrl;
        private String introduce;

        @Builder
        public userReq(String name, String nickName, String phone, String email, String password,
                       String profileImgUrl, String websiteUrl, String introduce) {
            this.name = name;
            this.nickName = nickName;
            this.phone = phone;
            this.email = email;
            this.password = password;
            this.profileImgUrl = profileImgUrl;
            this.websiteUrl = websiteUrl;
            this.introduce = introduce;
        }

        public userReq(User user) {
            this.name = user.getName();
            this.nickName = user.getNickName();
            this.phone = user.getPhone();
            this.email = user.getEmail();
            this.password = user.getPassword();
            this.profileImgUrl = user.getProfileImgUrl();
            this.websiteUrl = user.getWebsiteUrl();
            this.introduce = user.getIntroduce();
        }

        public User toEntity(String pwd) {
            return User.builder()
                    .name(this.name)
                    .nickName(this.nickName)
                    .phone(this.phone)
                    .email(this.email)
                    .password(pwd)
                    .profileImgUrl(this.profileImgUrl)
                    .websiteUrl(this.websiteUrl)
                    .introduce(this.introduce)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginReq {
        private String email;
        private String password;

        @Builder
        LoginReq(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    @Getter
    public static class AuthUserRes {
        private Long userIdx;
        private String jwt;

        public AuthUserRes(Long userIdx, String jwt) {
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
        private String profileImgUrl;
        private String websiteUrl;
        private String introduce;

        public ReadUserRes(User user) {
            this.userIdx = user.getId();
            this.name = user.getName();
            this.nickName = user.getNickName();
            this.email = user.getEmail();
            this.profileImgUrl = user.getProfileImgUrl();
            this.websiteUrl = user.getWebsiteUrl();
            this.introduce = user.getIntroduce();
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
