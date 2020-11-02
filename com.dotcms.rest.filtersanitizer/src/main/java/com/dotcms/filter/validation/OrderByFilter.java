package com.dotcms.filter.validation;

import com.dotmarketing.common.util.SQLUtil;

/**
 * Sanitizer class for the {@code orderby} parameter.
 *
 * @author Jose Castro
 * @since Oct 28th, 2020
 */
public class OrderByFilter implements Sanitizable<String> {

    private String filterName;

    /**
     * Creates an instance of this Filter Sanitizer.
     *
     * @param filterName The filter name that will be sanitized.
     */
    public OrderByFilter(final String filterName) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return this.filterName;
    }

    @Override
    public String sanitize(final String filterValue) {
        return SQLUtil.sanitizeSortBy(filterValue);
    }

}
