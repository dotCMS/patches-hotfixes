# REST Filtering Parameter Sanitizer

Compatible with dotCMS 5.0.3 up to 5.3.9.

This plugin is meant to prevent SQL Injection attacks by sanitizing specific filtering parameters that are passed down to certain REST Endpoints. It works by programmatically adding a custom `javax.servlet.Filter` class to the Tomcat Container. This Filter wraps the existing HTTP Request object into a custom `javax.servlet.http.HttpServletRequestWrapper` object, which takes care of sanitizing request or query String parameters that might contain harmful code. 

Related GitHub ticket: https://github.com/dotCMS/core/issues/19500

## Plugin Setup
### Adding/Removing REST Endpoints to Sanitize

If you need to add or remove REST Endpoints whose specific filtering parameters will be sanitized, you just need to update the value of the `URL_PATTERNS` array in the `com.dotcms.osgi.Activator` class. Make sure you include the appropriate root path of the REST Endpoint:
```
/api/{ENDPOINT-VERSION}/{ENDPOINT-NAME}
```
For example, if you want to sanitize filtering parameters for the `ContainerResource` and the `TemplateResource`, the configuration must be:
```
private static final String[] URL_PATTERNS = new String[]{
            "/api/v1/containers",
            "/api/v1/templates"
};
```
If you only need to sanitize the `ContainerResource`, just remove the other one form the array:
```
private static final String[] URL_PATTERNS = new String[]{
            "/api/v1/containers"
};
```

### Adding new Filtering Parameters for Sanitation

Even though this plugin is meant to filter out SQL Injection attacks, you can add or change the sanitation process you want to run on specific filtering parameters. By default, this plugin sanitizes two specific filtering parameters:

- The `orderby` parameter, via the `com.dotcms.filter.validation.OrderByFilter` class.
- The `filter` parameter, via the `com.dotcms.filter.validation.SimpleFilter` class.

All validation class implement the `com.dotcms.filter.validation.Sanitizable` interface. Therefore, if you need to add your own validation for a filtering parameter, you just need to:

1. Create your validation class implementing the `com.dotcms.filter.validation.Sanitizable` interface.
2. Update the `FILTER_SANITIZERS` map in the `com.dotcms.filter.ParameterSanitizerFilter` class. This map takes two parameters:
  - The actual name of the filtering parameter (all lower-case).
  - An instance of the filter validation class you created.

Just like the array of REST Endpoints to sanitize, you can remove unnecessary filter validation classes from the map by just removing its entry. 

### Compiling the Plugin

As with any other OSGi Plugin, you just need to run the `./gradlew clean jar` command to build the plugin. Remember to upload the fragment .JAR file first!
