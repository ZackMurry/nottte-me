package com.zackmurry.nottteme.controller;

import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import javassist.NotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    /**
     * catches UnauthorizedExceptions and returns a 401 instead of a 500 (server error)
     *
     * @param exception exception provided
     * @param response http response to give to client
     * @return exception message
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorizedException(UnauthorizedException exception, HttpServletResponse response) {
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
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String handleUnsupportedEncodingException(UnsupportedEncodingException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleMalformedJwtException(MalformedJwtException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    /**
     * returns a 403 for expired JWTs
     *
     * @param exception exception thrown
     * @param response response to return to client
     * @return error message
     */
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleExpiredJwtException(ExpiredJwtException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    /**
     * imagine not catching your exceptions that occur whole catching your exceptions
     *
     * usually occurs when the port closes before the server can send a response. in the else case, it's just a normal IOException
     *
     * @param exception exception thrown
     * @return exception's message
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleIOException(IOException exception) {
        System.out.println("io");
        if (ExceptionUtils.getRootCauseMessage(exception).toUpperCase().contains("BROKEN PIPE")) {
            //this means that the socket is closed, so we can't return a response
            System.out.println(ExceptionUtils.getRootCauseMessage(exception));
            return null;
        } else {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleSQLException(SQLException exception) {
        return exception.getMessage();
    }

}
