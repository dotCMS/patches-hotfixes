# MultiPartRequest File Name Validator

This plugin scans `multipart/form-data` requests that use the `PUT` or `POST` methods to upload files.  It validates that the filename provided in the request is valid and not attempting directory traversal.  If it does find an invalid filename, it will throw an exception and the request will fail. 

The plugins attempts to use memory wisely.  Files uploaded are scanned in memory if they are <50MB.  If the request is are bbigger than 50MB, the plugin will cache the request to disk in order to scan it.



## How to build this example

To build the OSGI jars run
`./gradlew clean jar`

This will build two jars in the `build/libs` directory: a bundle fragment (in order to expose needed 3rd party libraries from dotCMS) and the plugin jar 

* **To install this bundle:**

    Copy the bundle jar files inside the Felix OSGI container (*dotCMS/felix/load*).
        
    OR
        
    Upload the bundle jars files using the dotCMS UI (*CMS Admin->Dynamic Plugins->Upload Plugin*).

* **To uninstall this bundle:**
    
    Remove the bundle jars files from the Felix OSGI container (*dotCMS/felix/load*).

    OR

    Undeploy the bundle jars using the dotCMS UI (*CMS Admin->Dynamic Plugins->Undeploy*).

