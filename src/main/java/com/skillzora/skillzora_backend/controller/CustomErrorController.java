package com.skillzora.skillzora_backend.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        
        StringBuilder errorDetails = new StringBuilder();
        errorDetails.append("<h1>Error Details</h1>");
        errorDetails.append("<div>Status: ").append(status).append("</div>");
        
        if (message != null) {
            errorDetails.append("<div>Message: ").append(message).append("</div>");
        }
        
        if (exception != null) {
            errorDetails.append("<div>Exception: ").append(exception).append("</div>");
        }
        
        return errorDetails.toString();
    }
}