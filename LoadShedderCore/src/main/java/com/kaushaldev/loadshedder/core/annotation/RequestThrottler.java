package com.kaushaldev.loadshedder.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation for rate limiting number of request to API.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface RequestThrottler {
    /**
     * Number of request allowed per second.
     * @return API rate limit.
     */
    long limitPerSecond();
}
