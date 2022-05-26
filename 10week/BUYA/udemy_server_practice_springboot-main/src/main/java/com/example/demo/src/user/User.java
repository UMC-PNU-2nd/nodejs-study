package com.example.demo.src.user;

import com.example.demo.config.BaseEntity;
import lombok.*;
import org.springframework.util.Assert;
import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "User")
public class User extends BaseEntity {
    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 45)
    private String nickName;

    @Column(nullable = false, length = 45)
    private String phone;

    @Column(nullable = false, length = 45)
    private String email;

    @Column(nullable = false, length = 256)
    private String password;

    @Column()
    private String profileImgUrl;

    @Column()
    private String websiteUrl;

    @Lob
    private String introduce;

    @Builder
    public User(String name, String nickName, String phone, String email, String password,
                String profileImgUrl, String websiteUrl, String introduce) {
        Assert.hasText(name, "Name must not be empty");
        Assert.hasText(nickName, "NickName must not be empty");
        Assert.hasText(phone, "phone must not be empty");
        Assert.hasText(email, "email must not be empty");
        Assert.hasText(password, "password must not be empty");

        this.name = name;
        this.nickName = nickName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
        this.websiteUrl = websiteUrl;
        this.introduce = introduce;
    }

    public void updateName(UserDto.UpdateUserNameReq dto) {
        this.nickName = dto.getNickName();
    }
}