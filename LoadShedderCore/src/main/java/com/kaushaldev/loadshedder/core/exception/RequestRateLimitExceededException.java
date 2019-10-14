package com.kaushaldev.loadshedder.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS, reason = "Request rate limit exceeded. Try again later.")
public class RequestRateLimitExceededException extends RuntimeException {
    private String methodName;

    public RequestRateLimitExceededException() {
    }

    public RequestRateLimitExceededException(final String methodName) {
        this();
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }
}
