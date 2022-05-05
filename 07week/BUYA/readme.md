# PNU UMC Server Study 7주차
## Controller Anotation의 종류
**1) `@RestController`**

`@RestController` = `@Controller` + `@ResponseBody`   
`@Controller` 는 기본적으로 View를 반환하기 위해 사용하는 Annotation이다.    
여기에 Rest 통신을 위해 json으로 response하기 위해 `@ResponseBody`을 각 메소드 별로 붙혀 사용하는데    
이때, 두번 annotation 사용하느니, 하나로 줄인 것이 바로 `@RestController`이다.
    
그러므로, 이번 실습 코드에서 각 메소드 별로 `@ResponseBody` 없애도 된다.    
이미 Controller class는 `@RestController` 선언 되어 있으므로.    
```java
    //Query String
    @GetMapping("") // (GET) 127.0.0.1:9000/users
    // @Responsebody
    public BaseResponse<GetUserRes> getUsers(@RequestParam(required = true) String Email) {
        try{
            // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
            if(Email.length()==0){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            // 이메일 정규표현
            if(!isRegexEmail(Email)){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            GetUserRes getUsersRes = userProvider.getUsersByEmail(Email);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
```

**2) `@RequestMapping`**

요청에 대해 어떤 Controller나 Method가 처리할 지 매핑하는 annotation이다.      
Sprint 4.3 부터는 `@RequestMapping` 별로 인자로 method를 넣을 필요 없고   
Method 별로 Annotation이 분리되었다.   

4.2 이전에는 아래와 같고,    
```java
@RequestMapping(value="", method=@RequestMethod.GET)
```

4.3 부터는 아래와 같다.   
```java
@GetMapping("")
```

확실히 더 코드 길이가 적다.   

추가적으로 클래스 레벨의 `@RequestMapping`도 존재 하는데,   
이는 아래의 method들을 하나의 url로 묶어두기 위함이다.    

**3) `@RequestBody`, `@RequestParam`, `@PathVariable`**

각 메소드 별로 어떻게 내용을 받을 것(Request의 구조)인가 정리하는 부분이다.   
`@RequestBody` 는 body로   
`@RequestParam` 는 query param으로   
`@PathVariable` 는 url param으로 받는다.   

## BaseResponse.java
Controller는 `@RestController`이므로 json으로 return 해준다.   
각 메소드의 return문을 보변 BaseResponse로 보내는 것을 볼 수 있는데,   
이 BaseResponse는 어떻게 작동하는 것일까.   

일단 `@ResponseBody` 이므로 객체로 return을 하면 json으로 변환해 return 하는 것은 맞다.    
그러나 추가적으로 BaseResponse.java를 통해 바꾸어 return 하는 이유는    
간단히 객체를 넘어 성공 여부, code, message 도 전달하기 위함이다.    
**jackson** 도 사용하고 있는데, 이는 json 순서와 null 처리하기 위해 사용한다.

## Bean과 `@Autowired` 그리고 DI(의존성 주입)

모르겠음    
 
## 챌런지 과제
유저 삭제 delete api 작성    

[여기](https://peterdaugaardrasmussen.com/2020/11/14/rest-should-you-use-a-body-for-your-http-delete-requests/)에 따르면 많은 사람들이 DELETE method에서는 body를 사용하는 것을 꺼려한다.   
그러므로 url param으로 idx를 받아 삭제하는 것으로 한다.   

```
    명세서
    URL : /users/:userIdx
    method : DELETE
    Response : 
        Boolean:IsSuccess
        Integer:code
        String:message
        String:result
```

```java
    // UserController.java
    /**
     * 유저삭제 API
     * [DELETE] /users/:userIdx
     * @return BaseResponse<DeleteUserRes>
     */
    @DeleteMapping("/{userIdx}")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx")int userIdx) {
        try {
            DeleteUserReq deleteUserReq = new DeleteUserReq(userIdx);
            userService.deleteUser(deleteUserReq);

            String result = "삭제 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // UserService.java
    public void deleteUser(DeleteUserReq deleteUserReq) throws BaseException {
        try {
            int result = userDao.deleteUser(deleteUserReq);
            if(result == 0) {
                throw new BaseException(DELETE_FAIL);
            }
        } catch(Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // UserDao.java
    public int deleteUser(DeleteUserReq deleteUserReq) {
        String deleteUserQuery = "delete from User WHERE userIdx = ?";
        Object[] deleteUserParams = new Object[]{deleteUserReq.getUserIdx()};

        return this.jdbcTemplate.update(deleteUserQuery, deleteUserParams);
    }
```

결과 : 
<img src="pic/1.png" alt="1"/>   
<img src="pic/2.png" alt="2"/>   
잘 됩니다.