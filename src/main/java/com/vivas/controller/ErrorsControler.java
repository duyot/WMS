package com.vivas.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by duyot on 10/30/2016.
 */
@Controller
public class ErrorsControler implements ErrorController {
    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "common/page_404";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
