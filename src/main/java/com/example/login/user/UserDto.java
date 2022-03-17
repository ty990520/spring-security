package com.example.login.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long code;
    private String userid;
    private String password;
    private String authRole;

    @Builder
    public UserDto(Long code, String userid, String password, String authRole) {
        this.code = code;
        this.userid = userid;
        this.password = password;
        this.authRole = authRole;
    }

    public User toEntity(){
        return User.builder()
                .code(code)
                .userid(userid)
                .password(password)
                .authRole(authRole)
                .build();
    }
}
