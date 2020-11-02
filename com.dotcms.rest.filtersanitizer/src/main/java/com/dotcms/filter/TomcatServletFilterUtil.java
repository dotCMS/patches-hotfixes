package com.dotcms.filter;

import java.lang.reflect.Field;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;

import com.dotmarketing.util.UtilMethods;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.ApplicationFilterRegistration;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import com.dotmarketing.business.DotStateException;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;

/**
 * This utility class allows you to easily add Filters and Servlets to the Tomcat Context without requiring you to
 * restart the dotCMS instance at all. This is very useful for tasks such as adding custom code, or implementing
 * specific hotfixes.
 *
 * @author Jose Castro
 * @since Oct 27th, 2020
 */
public class TomcatServletFilterUtil {

    private final StandardContext standardContext;

    /**
     * Creates an instance of this class based on a specific Context.
     *
     * @param con The startdard Tomcat Context.
     */
    public TomcatServletFilterUtil(final StandardContext con) {
        this.standardContext = con;
    }

    /**
     * Public class constructor.
     */
    public TomcatServletFilterUtil() {
        try {
            if (!(Config.CONTEXT instanceof ApplicationContextFacade)) {
                throw new DotStateException("This plugin requires you run the bundled tomcat");
            }
            final ApplicationContextFacade fappContext = (ApplicationContextFacade) Config.CONTEXT;
            final Field appField = fappContext.getClass().getDeclaredField("context");
            appField.setAccessible(true);
            ApplicationContext appContext = (ApplicationContext) appField.get(fappContext);
            final Field stdField = appContext.getClass().getDeclaredField("context");
            stdField.setAccessible(true);
            this.standardContext = (StandardContext) stdField.get(appContext);
        } catch (final Exception e) {
            throw new DotStateException(String.format("An error occurred when changing accessibility on 'context' " +
                    "field: %s", e.getMessage()), e);
        }
    }

    /**
     * Adds a new {@link Servlet} to the Tomcat context.
     *
     * @param servletName The human-readable name of the Servlet.
     * @param servlet     The Filter class, which implements the {@link Servlet} interface.
     * @param urlPatterns The URL patterns that will match the new Servlet.
     *
     * @return The associated registration of the Servlet.
     *
     * @throws Exception An error occurred when registering the new Servlet.
     */
    public ServletRegistration.Dynamic addServlet(final String servletName, final Servlet servlet, final String...
            urlPatterns) throws Exception {
        removeServlet(servletName);
        final Wrapper wrapper = this.standardContext.createWrapper();
        wrapper.setServletClass(servlet.getClass().getName());
        wrapper.setServlet(servlet);
        wrapper.setOverridable(true);
        wrapper.setName(servletName);
        this.standardContext.addChild(wrapper);
        final Dynamic dynamic = this.standardContext.dynamicServletAdded(wrapper);
        for (final String mapping : urlPatterns) {
            dynamic.addMapping(mapping);
        }
        Logger.info(this.getClass(), String.format("Servlet '%s' has been ADDED", servletName));
        return dynamic;
    }

    /**
     * @param servletName
     */
    public void removeServlet(final String servletName) {
        try {
            final StandardContext stdContext = this.standardContext;
            final Wrapper wrapper = (Wrapper) stdContext.findChild(servletName);
            if (wrapper != null) {
                stdContext.removeChild(wrapper);

            }
            Logger.info(this.getClass(), String.format("Servlet '%s' has been REMOVED", servletName));
        } catch (final Exception e) {
            throw new DotStateException(String.format("An error occurred when removing servlet '%s': %s",
                    servletName, e.getMessage()), e);
        }
    }

    /**
     * Removes the specified {@link Filter} from the Tomcat context.
     *
     * @param filterName The human-readable name of the Filter.
     */
    public void removeFilter(final String filterName) {
        removeFilter(filterName, null, true);
    }

    /**
     * Removes the specified {@link Filter} from the Tomcat context.
     *
     * @param filterName The human-readable name of the Filter.
     * @param filter     The Filter class to remove.
     * @param restart    If the list of Filters in the Context must be restarted, set this to {@code true}. Otherwise,
     *                   set to {@code false}.
     */
    private void removeFilter(final String filterName, final Filter filter, final boolean restart) {
        try {
            final Wrapper wrapper = (Wrapper) this.standardContext.findChild(filterName);
            if (wrapper != null) {
                this.standardContext.removeChild(wrapper);
                Logger.info(this.getClass(), String.format("Filter '%s' has been REMOVED", filterName));
            }
            final FilterDef filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            if (filter != null) {
                filterDef.setFilterClass(filter.getClass().getName());
                filterDef.setFilter(filter);
            }
            this.standardContext.removeFilterDef(filterDef);
            final FilterMap map = new FilterMap();
            map.setFilterName(filterName);
            this.standardContext.removeFilterMap(map);
        } catch (final Exception e) {
            throw new DotStateException(String.format("An error occurred when removing filter '%s': %s", filterName,
                    e.getMessage()), e);
        }
        if (restart) {
            this.standardContext.filterStart();
        }
    }

    /**
     * Adds a new {@link Filter} to the Tomcat context. It's the same as adding a filter via the {@code web.xml} file.
     *
     * @param filterName  The human-readable name of the Filter.
     * @param filter      The Filter class, which implements the {@link Filter} interface.
     * @param order       The order in which the Filter will be placed.
     * @param urlPatterns The URL patterns that will match the new Filter.
     *
     * @throws IllegalStateException An error occurred when adding the specified Filter.
     */
    public void addFilter(final String filterName, final Filter filter, final FilterOrder order, final String... urlPatterns)
            throws IllegalStateException {
        if (!UtilMethods.isSet(filterName)) {
            throw new IllegalArgumentException("Filter name parameter is required");
        }
        removeFilter(filterName, filter, false);
        final FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(filterName);
        filterDef.setFilterClass(filter.getClass().getName());
        filterDef.setFilter(filter);
        this.standardContext.addFilterDef(filterDef);
        final boolean last = order == FilterOrder.LAST;
        final FilterRegistration.Dynamic app = new ApplicationFilterRegistration(filterDef, this.standardContext);
        app.addMappingForUrlPatterns(null, last, urlPatterns);
        this.standardContext.filterStart();
    }

}
