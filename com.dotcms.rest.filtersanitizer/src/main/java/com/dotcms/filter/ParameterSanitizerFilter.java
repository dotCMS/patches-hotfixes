package com.dotcms.filter;

import com.dotcms.filter.validation.OrderByFilter;
import com.dotcms.filter.validation.Sanitizable;
import com.dotcms.filter.validation.SimpleFilter;
import com.dotcms.util.PaginationUtil;
import com.dotmarketing.util.UtilMethods;
import com.google.common.collect.ImmutableMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter created to wrap all the incoming requests to override the {@link
 * HttpServletRequest#getRequestURI()} method in order to normalize the requested URIs.
 *
 * @author Jose Castro
 * @since Oct 27th, 2020
 */
public class ParameterSanitizerFilter implements Filter {

    private static final ImmutableMap<String, Sanitizable<String>> FILTER_SANITIZERS = ImmutableMap.of(
            PaginationUtil.FILTER, new SimpleFilter(PaginationUtil.FILTER),
            PaginationUtil.ORDER_BY, new OrderByFilter(PaginationUtil.ORDER_BY));

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (containsFilteringParams(request)) {
            request = new SanitizedFiltersRequestWrapper(request, FILTER_SANITIZERS);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Traverses the parameters in the {@link HttpServletRequest} object and determines if one of them matches at least
     * on of the filtering parameters that need to be sanitized before passing the request down to the REST Endpoint.
     *
     * @param request The {@link HttpServletRequest} instance.
     *
     * @return If the request contains at least one of the filtering parameters that need to be sanitized, return {@code
     * true}. Otherwise, return {@code false}.
     */
    private boolean containsFilteringParams(final HttpServletRequest request) {
        final Object value = request.getParameterMap().keySet().stream().filter(param -> FILTER_SANITIZERS
                .containsKey(param.toLowerCase())).findFirst().orElse(null);
        return UtilMethods.isSet(value);
    }

    @Override
    public void destroy() {

    }

}