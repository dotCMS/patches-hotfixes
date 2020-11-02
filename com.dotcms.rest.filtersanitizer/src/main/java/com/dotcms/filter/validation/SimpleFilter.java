package com.dotcms.filter.validation;

import com.dotmarketing.common.util.SQLUtil;

/**
 * Sanitizer class for the simple {@code filter} parameter.
 *
 * @author Jose Castro
 * @since Oct 2th, 2020
 */
public class SimpleFilter implements Sanitizable<String> {

    private String filterName;

    /**
     * Creates an instance of this Filter Sanitizer.
     *
     * @param filterName The filter name that will be sanitized.
     */
    public SimpleFilter(final String filterName) {
        this.filterName = filterName;
    }


    @Override
    public String getFilterName() {
        return this.filterName;
    }

    @Override
    public String sanitize(final String filterValue) {
        return SQLUtil.sanitizeCondition(filterValue);
    }

}
