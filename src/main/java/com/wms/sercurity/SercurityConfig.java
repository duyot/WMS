package com.wms.sercurity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    // Register HttpSessionEventPublisher
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.sessionManagement().sessionFixation().newSession()
//                .invalidSessionUrl("/login?message=timeout")
//                .maximumSessions(1).expiredUrl("/login?message=max_session").maxSessionsPreventsLogin(true)
//                .sessionRegistry(sessionRegistry());

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/WMS", "/language/**").permitAll()
                .antMatchers("/home_page/**", "/css/**", "/fonts/**", "/js/**", "/invoice/**", "/images/**", "/workspace_resource/**").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/workspace", "/workspace/").permitAll()
                //for sys_admin
                .antMatchers("/workspace/sysadmin/**").hasRole("SYS_ADMIN")
                .antMatchers("/failureLogin").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/workspace")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .permitAll();
        http
                .headers()
                .frameOptions()
                .sameOrigin();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(false).userDetailsService(wmsUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
