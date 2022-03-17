package com.example.login.config;

import com.example.login.Handler.CustomLoginFailureHandler;
import com.example.login.Handler.CustomLoginSuccessHandler;
import com.example.login.filter.CustomAuthenticationFilter;
import com.example.login.provider.CustomAuthenticationProvider;
import com.example.login.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  //spring boot가 제공하는 Spring Security설정을 따라감
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    //WebSecurity는 FilterChainProxy를 생성하는 필터입니다. 다양한 Filter 설정을 적용할 수 있습니다.
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
        // Spring Security에서 해당 요청은 인증 대상에서 제외시킵니다. = 모두 접근 가능
    }


    //HttpSecurity를 통해 HTTP 요청에 대한 보안을 설정할 수 있습니다.
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

//        모든 작업 끝나면 경로마다 권한 부여해야 함
        http.authorizeRequests()
                .antMatchers("/user").authenticated()
                .antMatchers("/admin").hasAuthority("ADMIN")   //인증 사용자만 허용
//                .antMatchers("/welcome").authenticated()   //인증 사용자만 허용
                .antMatchers("/login").anonymous();    //인증되지 않은 사용자만 허용
//                .antMatchers("/join").permitAll()    //모든 사용자 허용
//                .anyRequest().authenticated();

        http.formLogin()
                .usernameParameter("userid")
                .passwordParameter("password")
                .loginPage("/login")
                .permitAll()
                .and()
                .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


        http.logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true);   // 세션 날리기

//        http.exceptionHandling()
//                .accessDeniedPage("/error");  // 에러 페이지 만들게되면 설정해도 좋을 듯
    }


    //1. 가장 첫 요청이 Filter를 통해 들어옴 (FilterChain을 거치면서 줄줄이 수행함)
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/loginProcess");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());
        customAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailureHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    //2. 반환한 데이터를 인증처리 후 인증된 토큰을 AuthenticationManager에게 반환
    @Override   // AuthenticationManagerBuilder : 인증을 수행하기 위해 ProviderManager를 생성
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean//CustomAuthenticationProvider : 인증 처리 핵심 로직
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userService, bCryptPasswordEncoder());
    }

    //    AuthenticationManager를 bean으로 등록하기 위한 메소드
    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    //AuthenticationManager는 AuthenticationFilter에게 토큰 전달
    @Bean
    @Override // AuthenticationManager 클래스를 오버라이딩해서 Bean으로 등록
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean(); // 회원수정 후에 세션을 유지
    }

    @Bean
    public CustomLoginSuccessHandler customLoginSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }

    @Bean
    public CustomLoginFailureHandler customLoginFailureHandler() {
        return new CustomLoginFailureHandler();
    }
}
