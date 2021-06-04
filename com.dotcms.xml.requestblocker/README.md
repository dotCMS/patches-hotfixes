# README

This osgi plugin blocks all requests of contenttype `application/xml` to the content endpoint, found here `/api/content/`.  Customers posting content as xml should instead use the `json` version which accepts `application/json` formatted content.



## How to build this example

To install all you need to do is build the JAR. to do this run
`./gradlew jar`

This will build two jars in the `build/libs` directory: a bundle fragment (in order to expose needed 3rd party libraries from dotCMS) and the plugin jar 
