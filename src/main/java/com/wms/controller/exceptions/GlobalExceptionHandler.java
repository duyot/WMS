//package com.wms.controller.exceptions;
//
//import org.springframework.boot.autoconfigure.web.ErrorController;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.client.HttpClientErrorException;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * Created by duyot on 10/30/2016.
// */
//@ControllerAdvice
//public class GlobalExceptionHandler implements ErrorController {
//    private static final String PATH = "/error";
//
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
//    @ExceptionHandler(HttpClientErrorException.class)
//    public String handleConflict() {
//        System.out.println("Run in global exception");
//        return "/login";
//
//    }
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public String handleResourceNotFoundException() {
//        return "common/error/page_404";
//    }
//
//
//
//    @RequestMapping(value = PATH)
//    public String error(HttpServletRequest request) {
//        if(getStatus(request) == HttpStatus.FORBIDDEN){
//            return "common/error/page_403";
//        }else if(getStatus(request) == HttpStatus.NOT_FOUND){
//            return "common/error/page_404";
//        }else if(getStatus(request) == HttpStatus.FORBIDDEN){
//            return "/login";
//        }else if(getStatus(request) == HttpStatus.UNAUTHORIZED){
//            return "/login";
//        }else{
//            return "common/error/error";
//        }
//
//    }
//
//    @Override
//    public String getErrorPath() {
//        return PATH;
//    }
//
//    private HttpStatus getStatus(HttpServletRequest request) {
//        Integer statusCode = (Integer) request
//                .getAttribute("javax.servlet.error.status_code");
//        if (statusCode != null) {
//            try {
//                return HttpStatus.valueOf(statusCode);
//            }
//            catch (Exception ex) {
//            }
//        }
//        return HttpStatus.INTERNAL_SERVER_ERROR;
//    }
//
//}
