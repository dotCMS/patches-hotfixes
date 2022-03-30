package com.dotcms.security.multipartrequest;

import java.lang.reflect.Field;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;

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



public class TomcatServletFilterUtil {
    public enum FilterOrder {
        FIRST, LAST;
      }

  private final StandardContext standardContext;


  public TomcatServletFilterUtil(StandardContext con) {
    standardContext = con;
  }

  public TomcatServletFilterUtil() {
    try {
      if (!(Config.CONTEXT instanceof ApplicationContextFacade)) {
        throw new DotStateException("This plugin requires you run the bundled tomcat");
      }
      ApplicationContextFacade fappContext = (ApplicationContextFacade) Config.CONTEXT;
      Field appField;

      appField = fappContext.getClass().getDeclaredField("context");

      appField.setAccessible(true);
      ApplicationContext appContext = (ApplicationContext) appField.get(fappContext);


      Field stdField = appContext.getClass().getDeclaredField("context");
      stdField.setAccessible(true);

      standardContext = (StandardContext) stdField.get(appContext);
    } catch (Exception e) {
      throw new DotStateException(e.getMessage(), e);
    }
  }

  ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet, String... urlPatterns) throws Exception {


    removeServlet(servletName);

    Wrapper wrapper = standardContext.createWrapper();
    wrapper.setServletClass(servlet.getClass().getName());
    wrapper.setServlet(servlet);
    wrapper.setOverridable(true);
    wrapper.setName(servletName);
    standardContext.addChild(wrapper);

    Dynamic dynamic = standardContext.dynamicServletAdded(wrapper);
    for (String map : urlPatterns) {
      dynamic.addMapping(map);
    }
    Logger.info(this.getClass(), "Servlet added:" + servletName);
    return dynamic;
  }



  public void removeServlet(String servletName) {
    try {
      StandardContext stdContext = standardContext;

      Wrapper wrapper = (Wrapper) stdContext.findChild(servletName);
      if (wrapper != null) {
        stdContext.removeChild(wrapper);

      }
      Logger.info(this.getClass(), "Servlet removed:" + servletName);
    } catch (Exception e) {
      throw new DotStateException(e.getMessage(), e);
    }
  }

  public void removeFilter(String filterName) {
    removeFilter(filterName, null, true);
  }


  public void removeFilter(String filterName, Filter filter, boolean restart) {
    try {
      Wrapper wrapper = (Wrapper) standardContext.findChild(filterName);
      if (wrapper != null) {
        standardContext.removeChild(wrapper);
        Logger.info(this.getClass(), "Filter removed:" + filterName);
      }

      FilterDef filterDef = new FilterDef();
      filterDef.setFilterName(filterName);
      if (filter != null) {
        filterDef.setFilterClass(filter.getClass().getName());
        filterDef.setFilter(filter);
      }
      standardContext.removeFilterDef(filterDef);
      FilterMap map = new FilterMap();
      map.setFilterName(filterName);
      standardContext.removeFilterMap(map);
    } catch (Exception e) {
      throw new DotStateException(e.getMessage(), e);
    }
    if (restart)
      standardContext.filterStart();

  }

  public void addFilter(String filterName, Filter filter, FilterOrder order, String... urlPatterns) throws IllegalStateException {

    if (filterName == null || filterName.equals("")) {
      throw new IllegalArgumentException("filter name required");
    }


    removeFilter(filterName, filter, false);

    FilterDef filterDef = new FilterDef();
    filterDef.setFilterName(filterName);
    filterDef.setFilterClass(filter.getClass().getName());
    filterDef.setFilter(filter);



    standardContext.addFilterDef(filterDef);

    boolean last = order == FilterOrder.LAST;

    FilterRegistration.Dynamic app = new ApplicationFilterRegistration(filterDef, standardContext);

    app.addMappingForUrlPatterns(null, last, urlPatterns);
    standardContext.filterStart();


  }
}
