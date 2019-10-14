package com.kaushaldev.loadshedder.core.springbootconfiguration;

import com.kaushaldev.loadshedder.core.algorithm.LoadShedder;
import com.kaushaldev.loadshedder.core.algorithm.TokenBasedLoadShedder;
import com.kaushaldev.loadshedder.core.annotation.RequestThrottler;
import com.kaushaldev.loadshedder.core.exception.RequestRateLimitExceededException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoadShedderRequestIntercepter extends HandlerInterceptorAdapter {
    private static final Map<String, LoadShedder> loadShedderInstances = new ConcurrentHashMap<String, LoadShedder>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HandlerMethod.class.isInstance(handler)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            RequestThrottler annotation = handlerMethod.getMethod()
                                                       .getAnnotation(RequestThrottler.class);

            if (annotation != null) {
                final String methodName = handlerMethod.getMethod().getName();
                final String className = handlerMethod.getBeanType().getName();

                final String loadShedderKey = getLoadShedderInstanceKey(className, methodName);

                final long rateLimitPerSecond = annotation.limitPerSecond();

                loadShedderInstances.putIfAbsent(loadShedderKey, new TokenBasedLoadShedder(rateLimitPerSecond));
                final LoadShedder loadShedder = loadShedderInstances.get(loadShedderKey);

                if (!loadShedder.throttle()) {
                    throw new RequestRateLimitExceededException(loadShedderKey);
                }
            }
        }

        return true;
    }

    /**
     * Get key for getting load shedder instance.
     * @param className Bean class name.
     * @param methodName Method name
     * @return Key for load-shedder instance.
     */
    private String getLoadShedderInstanceKey(final String className, final String methodName) {
        return String.format("%s.%s", className, methodName);
    }
}
