package com.zackmurry.nottteme.exceptions;

/**
 * used for sending 401s
 */
public final class UnauthorizedException extends Exception {

    public UnauthorizedException(String errorMessage) {
        super(errorMessage);
    }

}
