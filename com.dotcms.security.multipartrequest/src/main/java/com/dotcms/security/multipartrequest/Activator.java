package com.dotcms.security.multipartrequest;



import org.osgi.framework.BundleContext;
import com.dotcms.security.multipartrequest.TomcatServletFilterUtil.FilterOrder;
import com.dotmarketing.osgi.GenericBundleActivator;


/**
 * This Activator adds the header configuration to the app portlet In addition to subscribes the
 * HeaderFilter
 *
 * When stop removes both things from the framework
 * 
 * @author jsanca
 */
public class Activator extends GenericBundleActivator {



    final MultiPartRequestSecurityFilter securityFilter = new MultiPartRequestSecurityFilter();



    @SuppressWarnings("unchecked")
    public void start(final BundleContext context) throws Exception {


        new TomcatServletFilterUtil().addFilter(securityFilter.getClass().getName()

                        , securityFilter, FilterOrder.FIRST, "*");



    }


    public void stop(final BundleContext context) throws Exception {

        new TomcatServletFilterUtil().removeFilter(securityFilter.getClass().getName());
    }

}
