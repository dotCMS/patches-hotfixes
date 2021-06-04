package com.dotcms.webinterceptors;

import com.dotcms.filters.interceptor.FilterWebInterceptorProvider;
import com.dotcms.filters.interceptor.WebInterceptorDelegate;
import com.dotmarketing.filters.AutoLoginFilter;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import org.osgi.framework.BundleContext;

public class Activator extends GenericBundleActivator {

    private String interceptorName;

    public void start(BundleContext context) throws Exception {

        Logger.info(this.getClass().getName(), "Adding the Request Blocker Filter");
        addWebInterceptor();

    }

    public void stop(BundleContext context) throws Exception {

        Logger.info(this.getClass().getName(), "Removing the Request Blocker Web Filter");
        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                        FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        delegate.remove(this.interceptorName, true);
    }

    private void addWebInterceptor() {

        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                        FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        final BlockRequestWebInterceptor webInterceptor = new BlockRequestWebInterceptor();
        this.interceptorName = webInterceptor.getName();

        delegate.add(webInterceptor);
    }
}
