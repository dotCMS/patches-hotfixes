# README

This bundle plugin is an example of how to override dotCMS classes with our bundle plugin.

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

## How to create a bundle plugin to override dotCMS classes

In order to create this OSGI plugin, you must create a `META-INF/MANIFEST` to be inserted into OSGI jar.
This file is being created for you by Gradle. If you need you can alter our config for this but in general our out of the box config should work.
The Gradle plugin uses BND to generate the Manifest. The main reason you need to alter the config is when you need to exclude a package you are including on your Bundle-ClassPath

If you are building the MANIFEST on your own or desire more info on it below is a description of what is required in this MANIFEST you must specify (see template plugin):

```
    Bundle-Name: The name of your bundle
    Bundle-SymbolicName: A short an unique name for the bundle
    Bundle-Activator: Package and name of your Activator class (example: com.dotmarketing.osgi.override.Activator)
    Override-Classes: Comma separated list of classes.
    List of classes we are trying to override, in order to override any dotCMS class is mandatory to add it to this list because dotCMS implementation of the class will be already loaded by the dotCMS class loader so if we don't explicit specify the classes to override the class loader wont try to load them.
    Export-Package: Declares the packages that are visible outside the plugin. Any package not declared here has visibility only within the bundle.
    Import-Package: This is a comma separated list of the names of packages to import. In this list there must be the packages that you are using inside your osgi bundle plugin and are exported and exposed by the dotCMS runtime.
```

## Beware (!)

In order to work inside the Apache Felix OSGI runtime, the import and export directive must be bidirectional, there are two ways to accomplish this:

* **Exported Packages**

    The dotCMS must declare the set of packages that will be available to the OSGI plugins by changing the file: *dotCMS/WEB-INF/felix/osgi-extra.conf*.
This is possible also using the dotCMS UI (*CMS Admin->Dynamic Plugins->Exported Packages*).

    Only after that exported packages are defined in this list, a plugin can Import the packages to use them inside the OSGI blundle.
    
* **Fragment**

    A Bundle fragment, is a bundle whose contents are made available to another bundles exporting 3rd party libraries from dotCMS.
One notable difference is that fragments do not participate in the lifecycle of the bundle, and therefore cannot have an Bundle-Activator.
As it not contain a Bundle-Activator a fragment cannot be started so after deploy it will have its state as Resolved and NOT as Active as a normal bundle plugin.

---
## Components

### com.dotmarketing.portlets.folders.model.Folder

Copy of the original *com.dotmarketing.portlets.folders.model.Folder* that lives inside dotCMS but with small changes (Just logging code) inside the `getPath()` method in order to demonstrate we can override a dotCMS class with our implementation.

### Activator

This bundle activator extends from *com.dotmarketing.osgi.GenericBundleActivator* and implements `BundleActivator.start()`.

* PLEASE note the `initializeServices( context )` call, this call is MANDATORY (!) as it will allow us to share resources between the bundle and the host container (dotCMS) and override classes.

---
## Testing

For our redefinition of the class *com.dotmarketing.portlets.folders.model.Folder* we modified the body of the `getPath()` method, we added some debugging lines in order to prove it is working.
The *Folder* class is call it by our activator (*com.dotmarketing.osgi.override.Activator*) which prove the OSGI bundle is using our redefinition of the Folder class, but in order to demonstrate the dotCMS context is using it too go to:
*dotCMS Admin -> Site Browser -> Select any folder on the tree -> Add Folder*.

The *Add Folder* uses the *Folder* class and if you deployed your OSGI plugin the debugging code we added should be visible on the logs.

---

## Limitations (!)

There are limitations on the hot deploy functionality for the OSGI Override plugin, there are some rules for the code you can modify on the redefined classes in order to see those changes when you upload the plugin.

In order to support the overriding of a class inside ours OSGI plugins we use the java hot swapping, it allows to redefine classes, unfortunately, this redefinition is limited only to changing method bodies:

> The redefinition may change method bodies, the constant pool and attributes. The redefinition must not add, remove or rename fields or methods, change the signatures of methods, or change inheritance.

As long as you don't add, remove or change methods (ONLY the methods bodies) for your redefined classes you will have an OSGI plugin that will reflect you changes when a deploy is done.