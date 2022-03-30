package com.dotcms.security.multipartrequest;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.dotmarketing.util.UtilMethods;

/**
 * This web interceptor checks if the current request is a POST or PUT and multipart If it is, check
 * if the filename does not contains any malicious code
 * 
 * @author jsanca
 */
public class MultiPartRequestSecurityFilter implements Filter {

    static final String[] BLOCK_REQUESTS = {
            "/*"};

    public MultiPartRequestSecurityFilter() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        final String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {

            final String contentTypeHeader = request.getHeader("content-type");
            if (UtilMethods.isSet(contentTypeHeader) && contentTypeHeader.contains("multipart/form-data")) {

                final MultiPartSecurityRequestWrapper requestWrapper = new MultiPartSecurityRequestWrapper(request);


                chain.doFilter(requestWrapper, response);


                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }


    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }



}
