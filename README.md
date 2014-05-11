countwords
==========
An HTTP word count utility in Java

System Requirements
===================
Windows 7/8
Netbeans IDE 8.0
Glassfish server 4.0 (usually bundled with netbeans IDE)
JDK 1.7.0_55

CONFIGURATION
=============
1. web.xml
i) init parameter - filePath: Update the parameter to point to the location of the directory
ii) init parameter - delimeter: The delimeter separating the words in the corpus. Default is ",". To use whitespace update whitespace enclosed in double quotes e.g " "


BUILD
=====
1. Set JAVA_HOME environment variable to point to latest version of jdk
e.g. c:\>set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_55
2. Build the war file from the directory containing the countwords folder
<Path to ant bin> -f <project directory location> -Dbrowser.context=<project directory location> -DforceRedeploy=false -Ddirectory.deployment.supported=true -Dnb.wait.for.caches=true -Dnb.internal.action.name=build dist
e.g.
c:\> cd C:\\Users\\nkbansal\\Documents\\GitHub
c:\> "C:\Program Files\NetBeans 8.0\extide\ant" -f countwords -Dbrowser.context=C:\\Users\\nkbansal\\Documents\\GitHub\\countwords -DforceRedeploy=false -Ddirectory.deployment.supported=true -Dnb.wait.for.caches=true -Dnb.internal.action.name=run run

3. Deploy
i)Open GlassFish server admin consol
ii) Navigate to applications
iii) Click Deploy and browse to the location to the war file e.g. C:\Users\nkbansal\Documents\GitHub\countwords\dist\countword.war
iv) Click OK to complete the deployment.

RUN
=====
1. From GlassFish server admin consol, navigate to Applications
2. countwords will be listed among other applications, click Launch to view the deployment Links
3. Click any of the deployment links, it will display {"count": -1} as the output
4. Edit the URL to add /?query=<word> and submit to test the application.

RUN using Netbeans
==================
1. Import the project into netbeans IDE
2. Right click on the project and select Run
3. The build log will display the URL to access the application as well
4. Copy paste the URL to the browser and append /?query=<searchword> and submit to test the application
