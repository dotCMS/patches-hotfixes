package com.dotcms.filter;

import com.dotcms.filter.validation.Sanitizable;
import com.dotmarketing.util.UtilMethods;
import com.google.common.collect.ImmutableMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Request Wrapper sanitizes specific request parameters in order to prevent SQL Injection attacks. The parameters
 * in both the Request Parameter Map and the Query String are sanitized upon instantiation and returned to the user when
 * solicited.
 *
 * @author Jose Castro
 * @since Oct 27th, 2020
 */
public class SanitizedFiltersRequestWrapper extends HttpServletRequestWrapper {

    final Map<String, String[]> newQueryParamMap;
    String newQueryString;

    /**
     * Default class constructor. Initialization point of the Request Wrapper in which the respective filter parameters
     * are properly sanitized.
     *
     * @param request          The {@link HttpServletRequest} instance.
     * @param filterSanitizers The Set containing the specific list of filtering parameters that must be sanitized.
     */
    public SanitizedFiltersRequestWrapper(final HttpServletRequest request, final ImmutableMap<String,
            Sanitizable<String>> filterSanitizers) {
        super(request);
        final Map<String, String[]> originalParamMap = new HashMap<>(request.getParameterMap());
        newQueryString = request.getQueryString();

        for (final String requestParam : originalParamMap.keySet()) {
            final String paramValue = request.getParameter(requestParam);
            if (filterSanitizers.containsKey(requestParam.toLowerCase()) && UtilMethods.isSet(paramValue)) {
                final Sanitizable<String> filterSanitizer = filterSanitizers.get(requestParam);
                originalParamMap.replace(filterSanitizer.getFilterName(), new String[]{String.class.cast
                        (filterSanitizer.sanitize(paramValue))});
            }
        }
        if (UtilMethods.isSet(newQueryString)) {
            final List<NameValuePair> queryStringData = new ArrayList<>();
            for (final NameValuePair nvp : URLEncodedUtils.parse(newQueryString, Charset.forName(StandardCharsets
                    .UTF_8.name()))) {
                final String filterName = nvp.getName();
                String filterValue = nvp.getValue();
                if (filterSanitizers.containsKey(filterName.toLowerCase()) && UtilMethods.isSet(filterValue)) {
                    final Sanitizable<String> filterSanitizer = filterSanitizers.get(filterName);
                    filterValue = filterSanitizer.sanitize(filterValue);
                }
                queryStringData.add(new BasicNameValuePair(nvp.getName(), filterValue));
            }
            this.newQueryString = URLEncodedUtils.format(queryStringData, Charset.forName(StandardCharsets.UTF_8.name
                    ()));
        }
        this.newQueryParamMap = ImmutableMap.copyOf(originalParamMap);
    }

    @Override
    public String getQueryString() {
        return this.newQueryString;
    }

    @Override
    public String getParameter(final String name) {
        final String[] value = this.newQueryParamMap.get(name);
        return null != value && value.length > 0 ? value[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.newQueryParamMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.newQueryParamMap.keySet());
    }

    @Override
    public String[] getParameterValues(final String name) {
        return this.newQueryParamMap.get(name);
    }

}
