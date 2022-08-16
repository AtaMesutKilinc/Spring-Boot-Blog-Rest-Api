package com.works.configs;


import com.works.services.LoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final JwtFilter jwtFilter;
    final LoginService loginService;
    final Configs configs;

    public SecurityConfig(JwtFilter jwtFilter, LoginService loginService, Configs configs) {
        this.jwtFilter = jwtFilter;
        this.loginService = loginService;
        this.configs = configs;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService).passwordEncoder(configs.encoder());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().formLogin().disable();

        http
                .authorizeHttpRequests() //giriş rolleri ile çalış
                .antMatchers(
                        "/author/**",
                        "/writing/save","/writing/myList",
                        "/writing/update","/writing/delete").hasRole("author")
                .antMatchers("/reader/**").hasRole("reader")  //hangi servis hangi rolle çalışcak emrini veriyoruz.
                .antMatchers( "/writing/list","/writing/search").hasAnyRole("author","reader")
                .antMatchers("/login","/register","/registerAuthor").permitAll()
                .and()       //tanımlar dışında config var onları da koy
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);  //jwt nin üreteceği sessionun oluşturulmasına izin veriyor

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.cors();

    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }






}
