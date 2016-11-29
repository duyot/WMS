package com.wms.sercurity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

/**
 * Created by duyot on 11/17/2016.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SercurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    UserDetailsService wmsUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/WMS").permitAll()
                .antMatchers("/css/**", "/js/**","/images/**","/workspace_resource/**").permitAll()
                .antMatchers("/workspace","/workspace/").hasRole("SYS_ADMIN")
                .antMatchers("/workspace/sysadmin/**").hasRole("SYS_ADMIN")
                .antMatchers("/workspace/cusadmin/**").hasAnyRole("SYS_ADMIN","CUS_ADMIN")
                .antMatchers("/workspace/admin/**").hasAnyRole("SYS_ADMIN","CUS_ADMIN","ADMIN")
                .antMatchers("/workspace/user/**").hasAnyRole("SYS_ADMIN","CUS_ADMIN","ADMIN","USER")
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/workspace")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
            .and()
                .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(wmsUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
//          auth.inMemoryAuthentication().withUser("duyot").password("123456a@").roles("SYS_ADMIN");
//          auth.inMemoryAuthentication().withUser("cusadmin").password("123456").roles("CUS_ADMIN");
//          auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
//          auth.inMemoryAuthentication().withUser("user").password("123456").roles("USER");
    }
}
