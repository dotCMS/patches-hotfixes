# MultiPartRequest File Name Validator

This plugin scans `multipart/form-data` requests that use the `PUT` or `POST` methods to upload files.  It validates that the filename provided in the request is valid and not attempting directory traversal.  If it does find an invalid filename, it will throw an exception and the request will fail. 

The plugins attempts to use memory wisely.  Files uploaded are scanned in memory if they are < 50MB in size.  If the request/attachment is bigger than 50MB, the plugin will cache the request to disk in order to scan it.

## How to install this plugin

The jar files can be downloaded directly and added to your dotCMS instance. You can find the binaries under the `build/libs` directory.  

https://github.com/dotCMS/patches-hotfixes/tree/v.4.x/com.dotcms.security.multipartrequest/build/libs


## How to build the plugin

To build the OSGI jars run
`./gradlew clean jar`

This will build two jars in the `build/libs` directory: a bundle fragment (in order to expose needed 3rd party libraries from dotCMS) and the plugin jar 

* **To install this plugin:**

    Copy the bundle jar files inside the Felix OSGI container (*dotCMS/felix/load*).
        
    OR
        
    Upload the bundle jars files using the dotCMS UI (*CMS Admin->Dynamic Plugins->Upload Plugin*).

* **To uninstall this plugin:**
    
    Remove the bundle jars files from the Felix OSGI container (*dotCMS/felix/load*).

    OR

    Undeploy the bundle jars using the dotCMS UI (*CMS Admin->Dynamic Plugins->Undeploy*).

