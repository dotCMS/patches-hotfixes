package com.dotcms.osgi;

import com.dotcms.filter.FilterOrder;
import com.dotcms.filter.ParameterSanitizerFilter;
import com.dotcms.filter.TomcatServletFilterUtil;
import com.dotmarketing.loggers.Log4jUtil;
import com.dotmarketing.osgi.GenericBundleActivator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.osgi.framework.BundleContext;

/**
 *
 *
 * @author Jose Castro
 * @since Oct 27th, 2020
 */
public class Activator extends GenericBundleActivator {

    private static final String FILTER_NAME = "paramSanitizerFilter";
    private static final String[] URL_PATTERNS = new String[]{
            "/api/v1/containers",
            "/api/v1/templates"
    };
    private LoggerContext pluginLoggerContext;

    /**
     *
     * @param bundleContext
     * @throws Exception
     */
    public void start(final BundleContext bundleContext) throws Exception {
        // Initializing log4j...
        final LoggerContext dotcmsLoggerContext = Log4jUtil.getLoggerContext();
        // Initialing the log4j context of this plugin based on the dotCMS logger context
        pluginLoggerContext = (LoggerContext) LogManager
                .getContext(this.getClass().getClassLoader(),
                        false,
                        dotcmsLoggerContext,
                        dotcmsLoggerContext.getConfigLocation());
        System.out.println("## [" + this.getClass() + "] installing filter " + ParameterSanitizerFilter.class
                .getCanonicalName());
        new TomcatServletFilterUtil().addFilter(FILTER_NAME, new ParameterSanitizerFilter(), FilterOrder.FIRST,
                URL_PATTERNS);
    }

    /**
     *
     * @param context
     */
    public void stop(final BundleContext context) {
        // Shutting down log4j in order to avoid memory leaks
        Log4jUtil.shutdown(pluginLoggerContext);
        System.out.println("## [" + this.getClass() + "] removing filter " + ParameterSanitizerFilter.class
                .getCanonicalName());
        new TomcatServletFilterUtil().removeFilter(FILTER_NAME);
    }

}
