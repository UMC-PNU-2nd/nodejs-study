# PNU UMC Server Study 8주차
## Jpa 적용
DAO에서 SQL문 쓰는거 보고 갈아엎어야 겠다고 결심했다.    

일단 기존의 코드의 구조를 보자

> `Request` -> Route -> Controller -> Service/Provider -> DAO -> DB

> DB -> DAO -> Service/Provider -> Controller -> Route -> `Response`

Controller는 routing 하는 역할    
Service/Provider는 실제 동작하는 역할    
DAO는 sql문 처리해 Domain과 연결하는 역할    

그럼 JPA를 적용한 결과는 어떻게 바뀔까?

> `Request` -> Route -> Controller -> Service/Provider -> Repository -> DB

> DB -> Repository -> Service/Provider -> Controller -> Route -> `Response`

DAO의 역할이 Repository로 대체되었다.   
둘의 역할은 다르다고는 한다.   
그러나 아직 응애라서 더 찾아봐야 한다.   

하나하나 씩 적용해가자.   

### 일반 클래스에서 Entity로
```java
// BaseEntity.java
@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @Column
    protected LocalDateTime updatedAt;

    @PrePersist
    protected void onPersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

// User.java
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
```
Entity는 전의 7주차 때 설명을 했지만   
분명히 Annotation 남발로 인해 하면 안되는 짓을 했을 것이다.   

* 역할    

Entity는 도메인(spring에서 db의 각 테이블을 받는 클래스)을 관리한다.   
원하는 모델의 정보를 넣음으로서 DB에 자동적으로 table 생성이 가능하며   
자동적으로 업데이트도 되니, DB의 구조가 바뀌었다고 나머지 코드를 다 바꿀 필요도 없다.   

* `@Setter` 사용 금지     

`@Setter`를 사용하면 안된다.

@Setter는 Class의 Setter Method를 자동으로 만들어주는 Annotation인데   
편리는 하겠지만, 이 Setter 함수가 아래와 같은 trade-off를 가져온다
[여기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-06.md)에서 잘 설명하고 있지만 대략적으로 요악한다면    
* 객체에 대한 일관성을 해치게 되며    
* 각 setter 메소드의 의도가 가려진다.    

그렇기 때문에 @Setter의 사용을 지양하고
같은 이유로 `@Data`도 사용하지 않는다.    

대신 Set 하는 작업은 @Builder 패턴을 적용해 사용한다.

* `@Builder`와 `@NoArgsConstructor`    

`@Builder`와 `@NoArgsConstructor`는 서로 충돌이 일어난다.

먼저 `@Builder`는 객체의 생성을 일반적인 Constructor가 아닌 Builder 패턴을 적용해 만들어준다.    
이때 클래스 내부에 모든 인자를 동시에 받아 생성하는 All-Constructor(@AllArgsConstructor)가 필요한데    
`@NoArgsConstructor`만 명시되어 있는 경우에는 충돌이 일어난다.   

그렇기 때문에 추가적으로 `@AllArgsConstructor`을 명시하거나   
또는 `@Builder`를 메소드 레벨로 바꾸어준다.    
위의 코드와 같이 짜면 된다.    

또한 `access = AccessLevel.PROTECTED`을 부여해서    
생성자에 무분별한 호출을 막아야 한다.   

* BaseEntity.java    

Id나, createdAt, updatedAt 같이 모든 도메인에 사용되는 Attribute는   
추상 클래스로 빼주어 관리해준다.    
상속 받는 추상 클래스에는 `@MappedSuperclass`가 필요하다.     

`@PrePersist`는 이 클래스가 생성될 때 자동으로 동작되며   
`@PreUpdate`는 이 클래스가 Update 될 때 자동으로 동작된다.    

마지막으로 Id의 타입은 Long 으로 정의한다.    
Primitive type은 기본 값이 0이라, 0의 의미가 중첩되는 반에 비해    
Wrapper type은 기본 값이 null이라 진짜 0인지 아닌지를 구분할 수 있다.    
Integer 대신 Long을 쓰는 이유는 단순히 범위가 더 넓기 때문에.    

### Repository
Repository는 Entity(도메인)의 기본 CRUD 메소드를 생성해준다.

코드는 아래와 같다.

```java
// UserRepository.java
public interface UserRepository extends JpaRepository<User, Long> {

}
```

아무 내용 없이 interface class를 정의하는 것으로도 생성이 가능하다.    

다만 이걸로는 기능이 많이 부족하니    
추가적인 메소드 생성이 가능하다.    
```java
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String Email);
}
```

위와 같이 findBy 뒤에 Attribute의 이름을 넣어주는 것으로   
Email이 같은 data를 찾을 수 있는 메소드가 생성된다.    
솔직히 말해서 이게 가장 신기하다.    

[여기](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords)에 custom Method의 이름을 적는 방법이 기재되어 있다.   

## DTO
DTO는 계층 간 데이터 교환을 하기 위해 사용하는 객체이다.   
즉 Request나 Response에 들어갈 구조를 적는 공간인데    
제공 되던 코드 중에서 끝에 ~Req 또는 ~Res로 끝나는 클래스들을 말한다.   

```java
// UserDto.java
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

        // @Builder
        // Response에서는 Builder 패턴을 적용안하던데 이유는 몰?루
        public CreateUserRes(Long userIdx, String jwt) {
            this.userIdx = userIdx;
            this.jwt = jwt;
        }
    }
}
```
먼저 DTO는 sub class로 관리하는 것을 추천한다.
API가 많아질 수록 그만큼의 Request, Response 패턴이 생성되는데,
각 패턴 별로 한 개씩의 java 파일을 만들기에는 너무 많아진다.   

Entity와 같은 이유로 @Setter 사용 금지하고    
대신 @Builder 패턴을 넣어준다.    

`User toEntity(String pwd)`는 DTO에서 User 도메인으로 바꾸어 주는 역할을 맡는다.    
pwd는 암호화 후 받아야 하기 때문에 인자로 추가해준다.    

### Controller 작성

이제 Controller Provider/Service를 jpa에 맞추어 작성할 것이다.    
회원 가입 코드만 보겠다.   

```java
// UserController.java
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // jpa 적용 완료
    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @PostMapping("") // (POST) 127.0.0.1:9000/users
    public BaseResponse<UserDto.CreateUserRes> createUser(@RequestBody UserDto.CreateUserReq req) {
        if(req.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(req.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            UserDto.CreateUserRes createUserRes = userService.createUser(req);
            return new BaseResponse<>(createUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
```

솔직히 많이 바뀐 것이 없다. 메소드의 인자와 리턴 타입만 바꿔주면 된다.


### Service 작성
```java
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserProvider userProvider, JwtService jwtService, UserRepository userRepository) {
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public UserDto.CreateUserRes createUser(UserDto.CreateUserReq req) throws BaseException {
        if(userProvider.checkEmail(req.getEmail()) ==1){
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

            String jwt = jwtService.createJwt(userIdx);
            return new UserDto.CreateUserRes(userIdx, jwt);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
```

먼저 Repository는 bean 객체로 취급되기 때문에 `@Autowired`로 미리 선언해준다.   

위에서 설명한대로 DTO를 객체로 바꾸어 주는 toEntity를 받아 User 객체를 만들어 준다.   
그 후 CRUD Repository로 부터 만들어진 Create method인 save()를 통해    
실제 db에 data를 넣어준다.    

save()로 부터 받은 User instance에서 id와    
새로 발급받은 jwt 토큰을 받아서    
Userdto.CreateUserRes(userIdx, jwt)로 만들어서 리턴해준다.    

이렇게 하면 간단하게 jpa 적용이 가능하다.

### 추가 : DTO-domain 변환 시점
Controller에서 미리 domain로 바꿔준 후 넘겨주거나   
또는 위의 코드 처럼 Service가 받은 후 domain으로 바꾸는 방법을 선택할 수 있다.    
(toEntity의 호출 시점을 말하는 거임)

[여기](https://velog.io/@minide/Spring-boot-DTO%EC%9D%98-%EC%82%AC%EC%9A%A9-%EB%B2%94%EC%9C%84)에 정말 자세히 적혀있다.    

Controller가 Service로 domain을 넘길 경우 생기는 문제점을 요약한다면    
* Controller가 여러 Service 객체에 의존하게 된다.   
* Controller가 불필요한 추가적인 코드를 가지게 된다.   
* Controller의 의도는 Routing이지 객체 생성이 아니다.   

정상적으로는 될지라도 코드의 상태가 나빠지니   
Service에서 관리하는 쪽으로 바꾸어주자.   

## 참고해볼만한 사이트
https://github.com/cheese10yun/spring-jpa-best-practices

단계별로 잘 설명되어 있습니다.    
이번 주차 공부 때도 여기서 가장 많이 도움이 된것 같아 올립니다.   