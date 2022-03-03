# MultiPartRequest File Name Validator

This plugin scans `multipart/form-data` requests that use the `PUT` or `POST` methods to upload files.  It validates that the filename provided in the request is valid and not attempting directory traversal.  If it does find an invalid filename, it will throw an exception and the request will fail. 

The plugins attempts to use memory wisely.  Files uploaded are scanned in memory if they are < 50MB in size.  If the request/attachment is bigger than 50MB, the plugin will cache the request to disk in order to scan it.

## How to install this plugin

The jar files can be downloaded directly and added to your dotCMS instance. You can find the binary here.  This version works on dotCMS versions 5.1.6 and greater.
https://github.com/dotCMS/patches-hotfixes/blob/master/com.dotcms.security.multipartrequest/build/libs/

The sha256 values for the hotfix files are:
| File | sha256|
| ----------- | ----------- |
| com.dotcms.security.multipartrequest-0.1.jar | f530e04b8c17357884718865ea7649a304f6287da6fe12a65fdbb8d985102a1a |
| com.dotcms.security.multipartrequest.fragment-0.1.jar | b470319daad23349dafcea8a0a298e9e4b2d57cb1b11664a1544865827b34d3d |




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

