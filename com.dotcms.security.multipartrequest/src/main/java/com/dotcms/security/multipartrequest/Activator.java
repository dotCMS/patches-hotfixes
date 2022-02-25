package com.dotcms.security.multipartrequest;

import com.dotcms.filters.interceptor.FilterWebInterceptorProvider;
import com.dotcms.filters.interceptor.WebInterceptorDelegate;
import com.dotmarketing.filters.InterceptorFilter;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Config;
import org.osgi.framework.BundleContext;

/**
 * This Activator adds the header configuration to the app portlet
 * In addition to subscribes the HeaderFilter
 *
 * When stop removes both things from the framework
 * @author jsanca
 */
public class Activator extends GenericBundleActivator {

    private String interceptorName;

    @SuppressWarnings ("unchecked")
    public void start (final BundleContext context) throws Exception {

        final MultiPartRequestSecurityWebInterceptor headerWebInterceptor = new MultiPartRequestSecurityWebInterceptor();

        // add the filter
        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate =
                    filterWebInterceptorProvider.getDelegate(InterceptorFilter.class);

        this.interceptorName = headerWebInterceptor.getName();
        delegate.addFirst(headerWebInterceptor);
    }


    public void stop (final BundleContext context) throws Exception {

        //Unregister the servlet
        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate =
                filterWebInterceptorProvider.getDelegate(InterceptorFilter.class);

        delegate.remove(this.interceptorName, true);
    }

}
