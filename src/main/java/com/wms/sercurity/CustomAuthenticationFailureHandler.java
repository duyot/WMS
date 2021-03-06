package com.wms.sercurity;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wms.config.ProfileConfigInterface;
import com.wms.config.CustomerProdProfileConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private ProfileConfigInterface profileConfig;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // Login failed by max session
        if (exception.getClass().isAssignableFrom(SessionAuthenticationException.class)) {
            response.sendRedirect(request.getContextPath() + "/failureLogin?message=max_session");
            return;
        }
        if (profileConfig instanceof CustomerProdProfileConfig){
            this.redirectStrategy.sendRedirect(request, response, "/failureLogin?message=invalid_server");
        }
//        response.sendRedirect(request.getContextPath() + "/login?message=error");
//        redirectStrategy.sendRedirect(request, response, "/login?error");
        this.redirectStrategy.sendRedirect(request, response, "/failureLogin?message=login_error");
    }

}