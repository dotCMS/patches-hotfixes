package com.dotcms.security.matrixparams;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;

/**
 * This web interceptor blocks requests with URIs that contain semicolons
 * @author dotCMS
 *
 */
public class MatrixParametersSecurityWebInterceptor implements WebInterceptor {

    private static final long serialVersionUID = 1L;

    private static final String PATTERN_IS_NULL = "PATTERN_IS_NULL";

    private static final String[] BLOCK_REQUESTS = {"/*"};

    public MatrixParametersSecurityWebInterceptor() {

    }
    
    final String pattern = Config.getStringProperty("URI_NORMALIZATION_FORBIDDEN_REGEX", PATTERN_IS_NULL);
    
    Optional<Pattern> forbiddenRegex = (!pattern.equals(PATTERN_IS_NULL)) ? Optional.of(Pattern.compile(pattern)) : Optional.empty();
    
    
    
    /**
     * Default list of disallowed char sequences
     */
    final String[] DISALLOWED_URI_DEFAULT = new String[] {
            ";",
            "..",
            "/./",
            "\\",
            "?",
            "%3B", // encoded semi-colon
            "%2E", // encoded period '.'
            "%2F", // encoded forward slash '/'
            "%5C", // encoded back slash '\'
            "%3F", // encoded questionmark
            "%3D", // encoded equals
            "%00", // encoded null
            "\0",  // null
            "\r",  // carriage return
            "\n",  // line feed
            "\f"   // form feed
    };

    String[] forbiddenURIStrings = Config.getStringArrayProperty("URI_NORMALIZATION_FORBIDDEN_STRINGS", DISALLOWED_URI_DEFAULT);


    @Override
    public String[] getFilters() {
        return BLOCK_REQUESTS;
    }

    @Override
    public Result intercept(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final String originalUri = request.getRequestURI();

        try {
            if (forbiddenRegex.isPresent() && forbiddenRegex.get().matcher(originalUri).find()) {
                throw new IllegalArgumentException("Invalid URI passed:" + originalUri);
            }
            if (containsNastyChar(originalUri)) {
                throw new IllegalArgumentException("Invalid URI passed:" + originalUri);
            }
        } catch (IllegalArgumentException iae) {
            Logger.warnAndDebug(getClass(),
                            "Invalid URI from:" + request.getRemoteAddr() + ", returning a 404", iae);

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return Result.SKIP_NO_CHAIN;
        }
        
        return Result.NEXT;
    }


    boolean containsNastyChar(final String newNormal) {
        for (String reserved : forbiddenURIStrings) {
            if (newNormal.contains(reserved)) {
                return true;
            }
        }
        return false;
    }


}
