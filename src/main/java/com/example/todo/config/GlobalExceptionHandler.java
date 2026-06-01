package com.example.todo.config;

import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {
        if (ex instanceof NotLoginException) {
            ModelAndView mv = new ModelAndView();
            mv.setViewName("redirect:/login");
            return mv;
        }

        if (ex instanceof RuntimeException) {
            ModelAndView mv = new ModelAndView();
            mv.addObject("error", ex.getMessage());
            mv.setViewName("redirect:/");
            return mv;
        }

        return null;
    }
}
