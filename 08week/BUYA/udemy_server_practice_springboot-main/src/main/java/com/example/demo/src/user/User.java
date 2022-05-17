package com.example.demo.src.user;

import com.example.demo.config.BaseEntity;
import lombok.*;
import org.springframework.util.Assert;
import javax.persistence.*;

//@Data : 기본 Annotation의 묶음, 여기에도 Setter 있으므로 사용 금지
@Getter
//@Setter 무분별한 Setter 는 객체의 일관성을 해친다.
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

    @Builder
    public User(String name, String nickName, String phone, String email, String password) {
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
    }
}