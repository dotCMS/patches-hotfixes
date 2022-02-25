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

    private static final String ALL_REQUEST = "/*";

    public MultiPartRequestSecurityWebInterceptor() {

    }

    @Override
    public String[] getFilters() {
        return new String[] { ALL_REQUEST };
    }

    @Override
    public Result intercept(final HttpServletRequest request,
                            final HttpServletResponse response) throws IOException {

        final String method = request.getMethod();

        if (HttpMethod.POST.equalsIgnoreCase(method) || HttpMethod.PUT.equalsIgnoreCase(method)) {

            final String contentTypeHeader = request.getHeader("content-type");
            if (UtilMethods.isSet(contentTypeHeader) && contentTypeHeader.contains("multipart/form-data")) {

                final MultiPartSecurityRequestWrapper requestWrapper = new MultiPartSecurityRequestWrapper(request);

                this.checkSecurityRequest(requestWrapper);

                return new Result.Builder().wrap(requestWrapper).next().build();
            }
        }

        return Result.NEXT;
    }

    private void checkSecurityRequest(final MultiPartSecurityRequestWrapper request) throws IOException {

        try {

            final String body = new String(request.getBody()).toLowerCase();
            final int filenameIndex  = body.indexOf("filename=");
            final int finalLineIndex = body.indexOf("\n", filenameIndex);
            final String fileName    = body.substring(filenameIndex, finalLineIndex);

            if (UtilMethods.isSet(fileName) &&
                    (fileName.indexOf("/") != -1 || fileName.indexOf("\\")  != -1)) {

                SecurityLogger.logInfo(this.getClass(),
                        "The filename: " + fileName + " is invalid");
                throw new IllegalArgumentException("Illegal Request");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {

            throw new IOException(e);
        }
    }
}
