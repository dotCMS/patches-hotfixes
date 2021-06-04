package com.dotcms.xml.requestblocker;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotmarketing.util.SecurityLogger;

public class BlockRequestWebInterceptor implements WebInterceptor {

    public final static String APPLICATION_XML = "application/xml";

    @Override
    public String[] getFilters() {
        return new String[] {"/api/content/*"};
    }

    @Override
    public Result intercept(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        if ((request.getMethod().equalsIgnoreCase("PUT") || request.getMethod().equalsIgnoreCase("POST"))
                        && request.getContentType().startsWith(APPLICATION_XML)) {

            SecurityLogger.logInfo(this.getClass(),
                            "Insecure XML PUT or Post Detected - possible vulnerability probing: "
                                            + request.getRequestURI());
            
            SecurityLogger.logInfo(this.getClass(),
                            "Insecure XML PUT or Post Detected - possible vulnerability probing: "
                                            + request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST,"{\"message\":\"Unable to deserialize XML\"}");
            return Result.SKIP_NO_CHAIN;
        }

        return Result.NEXT;
    }
}
