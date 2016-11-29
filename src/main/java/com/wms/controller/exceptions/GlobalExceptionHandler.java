package com.wms.controller.exceptions;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by duyot on 10/30/2016.
 */
@Controller
public class GlobalExceptionHandler implements ErrorController{
    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error(HttpServletRequest request) {
        if(getStatus(request) == HttpStatus.FORBIDDEN){
            return "common/error/page_403";
        }else if(getStatus(request) == HttpStatus.NOT_FOUND){
            return "common/error/page_404";
        }else{
            return "common/error/error";
        }

    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            }
            catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

//    @ExceptionHandler(Exception.class)
//    public String handleCommonException(){
//        return "common/error";
//    }
//
//    @ExceptionHandler()
//    public String handleAccessDenied() {
//        return "common/page_403";
//    }
//
//    @ExceptionHandler(NoHandlerFoundException.class)
//    public String handleNotfound() {
//        return "common/page_404";
//    }


}
