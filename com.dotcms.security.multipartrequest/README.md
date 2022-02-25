# README

This plugin is an app to add multiple headers to the response.
The header could be add to a specific urls by using url patterns

## How to build this example

To install all you need to do is build the JAR. to do this run
`./gradlew jar`

This will build two jars in the `build/libs` directory: a bundle fragment (in order to expose needed 3rd party libraries from dotCMS) and the plugin jar 

* **To install this bundle:**

    Copy the bundle jar files inside the Felix OSGI container (*dotCMS/felix/load*).
        
    OR
        
    Upload the bundle jars files using the dotCMS UI (*CMS Admin->Dynamic Plugins->Upload Plugin*).

* **To uninstall this bundle:**
    
    Remove the bundle jars files from the Felix OSGI container (*dotCMS/felix/load*).

    OR

    Undeploy the bundle jars using the dotCMS UI (*CMS Admin->Dynamic Plugins->Undeploy*).

## How to add a header

1. Go to Apps
2. Go to dotCMS Header App
3. Go to the site you want to configure
4. Set some name, for instance, "Security Headers"
5. Add a header such as

/*          Content-Security-Policy:default-src 'self' cdn.example.com;


1. The first one is a pattern to catch all urls, you can change it to be more specific on filtering.
2. Set the response headers for this url pattern, the headers are separated by ";". 

The header name is separated by the header value by ":"
