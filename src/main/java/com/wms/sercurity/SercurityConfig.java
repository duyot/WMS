package com.wms.sercurity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
        http    .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/WMS","/language/**").permitAll()
                .antMatchers("/css/**", "/fonts/**","/js/**","/images/**","/workspace_resource/**").permitAll()
                .antMatchers("/workspace","/workspace/").permitAll()
                //for sys_admin
                .antMatchers("/workspace/sysadmin/**").hasRole("SYS_ADMIN")
                //for each function
                .antMatchers("/workspace/cat_goods_group_ctr","/workspace/cat_goods_group_ctr/**").hasAnyRole("SYS_ADMIN","CUS_ADMIN","ADMIN")
                .antMatchers("/workspace/user/**").hasAnyRole("SYS_ADMIN","CUS_ADMIN","ADMIN","USER")
                //
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
            auth.eraseCredentials(false).userDetailsService(wmsUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
