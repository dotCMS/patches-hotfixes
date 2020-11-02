package com.dotcms.filter.validation;

/**
 * Defines the different types of filtering parameters in a REST Endpoint that must go through a verification or
 * sanitation process before being passed down to the actual REST Endpoint.
 *
 * @author Jose Castro
 * @since Oct 28th, 2020
 */
public interface Sanitizable<T> {

    String getFilterName();

    T sanitize(final T filterValue);
}
