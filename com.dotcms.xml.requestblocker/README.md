# README

This osgi plugin blocks all requests of contenttype `application/xml` to the `/api/content endpoint`.  Customers posting content as xml should instead use the `json` version which accepts `application/json` formatted content.  While there is no know working expolit for that uses the xml content endpoint, it has already been deprecated and should not be used. 

## Versions
This plugin should work for versions 4.1 and higher.  This plugin is not needed in versions 21.05+ or LTS versions 5.3.8.5+ as the fix has been incorporated in the code.



## Installing 
You can download the precompiled jar file here:

https://github.com/dotCMS/patches-hotfixes/raw/master/com.dotcms.xml.requestblocker/build/libs/com.dotcms.xml.requestblocker-0.2.jar

or you can build it yourself by cloning this repository and running `./gradlew jar`

**Validating the .jar**

The jar file should have an sha256 hash of `72bfd556b34d1f3c90a9524bdf402effb3742bb0a99bee7cc79bf79240ce365c`

```
# shasum -a 256 build/libs/com.dotcms.xml.requestblocker-0.2.jar

72bfd556b34d1f3c90a9524bdf402effb3742bb0a99bee7cc79bf79240ce365c  build/libs/com.dotcms.xml.requestblocker-0.2.jar
```

Once you have the .jar file, you can install it by uploading it into your dotCMS using the "Plugins" screen.  Once it has been uploaded, you should see the plugin in you plugin list and it should be marked "Active"





## Testing the plugin

To test that the plugin is functioning properly, you can curl the affected endpoint.

```
curl -v -XPUT -H "Content-Type: application/xml" https://{yoursite.com}/api/content/publish/1 -d '
   <content>
      <title>testing</title>
   </content>
'

```

dotCMS should return a response code of 400 and you should see messages in the logs that say 
```
Insecure XML PUT or Post Detected - possible vulnerability probing
```

