package com.zackmurry.nottteme.controller;

import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import io.jsonwebtoken.MalformedJwtException;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestControllerAdvice
public class WebRestControllerAdvice {

    /**
     * gives ability to throw NotFoundExceptions in RestControllers
     *
     * @param exception exception given
     * @param response message to return to client
     * @return message to return
     */
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException exception, HttpServletResponse response) {
        try {
            response.sendError(HttpStatus.NOT_FOUND.value());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exception.getMessage();
    }

    /**
     * catches UnauthorizedExceptions and returns a 4014 instead of a 500 (server error)
     *
     * @param exception exception provided
     * @param response http response to give to client
     * @return exception message
     */
    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorizedException(UnauthorizedException exception, HttpServletResponse response) {
        try {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exception.getMessage();
    }

    /**
     * catches UnsupportedEncodingExceptions, which are thrown by URLDecoders in controllers
     * a URLDecoder is used for decoding encoded stuff in URLs like %20 and +
     *
     * @param exception exception provided
     * @param response http response to give to client
     * @return exception message
     */
    @ExceptionHandler(UnsupportedEncodingException.class)
    public String handleUnsupportedEncodingException(UnsupportedEncodingException exception, HttpServletResponse response) {
        try {
            response.sendError(HttpStatus.NOT_ACCEPTABLE.value());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exception.getMessage();
    }

    @ExceptionHandler(MalformedJwtException.class)
    public String handleMalformedJwtException(MalformedJwtException exception, HttpServletResponse response) {
        try {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exception.getMessage();
    }

}
