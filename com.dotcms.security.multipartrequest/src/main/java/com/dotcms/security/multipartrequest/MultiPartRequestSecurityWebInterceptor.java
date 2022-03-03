package com.dotcms.security.multipartrequest;

import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.util.SecurityLogger;
import com.dotmarketing.util.UtilMethods;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.Locale;

/**
 * This web interceptor checks if the current request is a POST or PUT and multipart
 * If it is, check if the filename does not contains any malicious code
 * @author jsanca
 */
public class MultiPartRequestSecurityWebInterceptor implements WebInterceptor {

    private static final String[] BLOCK_REQUESTS = {"/*"};

    public MultiPartRequestSecurityWebInterceptor() {

    }

    @Override
    public String[] getFilters() {
        return BLOCK_REQUESTS;
    }

    @Override
    public Result intercept(final HttpServletRequest request,
                            final HttpServletResponse response) throws IOException {

        final String method = request.getMethod();

        if (HttpMethod.POST.equalsIgnoreCase(method) || HttpMethod.PUT.equalsIgnoreCase(method)) {

            final String contentTypeHeader = request.getHeader("content-type");
            if (UtilMethods.isSet(contentTypeHeader) && contentTypeHeader.contains("multipart/form-data")) {

                final MultiPartSecurityRequestWrapper requestWrapper = new MultiPartSecurityRequestWrapper(request);


                return new Result.Builder().wrap(requestWrapper).next().build();
            }
        }

        return Result.NEXT;
    }


    
    
}
