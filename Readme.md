# Arquillian "Getting Started" Guide using gradle instead of maven
since it is not straight forward to translate the pom.xml presented in http://arquillian.org/guides/getting_started/ to gradle, i made this minimal demo project, to demonstrate how the "Getting Started" can be done with gradle instead or using maven

Moreover it turned out, that even the pom.xml from the tutorial "Getting Started" does also NOT really work out of the box, mostly since the version numbers used a quite outdated. Even this:  https://github.com/arquillian/arquillian-examples/tree/master/arquillian-tutorial  did not really work for me.

So the main purpose of this project is to show how:

- gradle.build
- arquillian.xml
- persistence.xml

need to look like, in order to make arquillian plus java ee 8 work for CDI, JPA and JAX-RS

2018-03-07: added some Hints on how to get the "GreetingsTest" run in the Eclipse JUnit Runner (using "run as junit test")

2018-03-20: added Java ee 8 compatible arquillian container: payara5.181, since there is no wildfly-12 support in arquillian yet. 
added also IntegrationTest using JPA (an therefore a persistence.xml) and JAX-RS Webservices 

### gradle.build
in order to test different arquillian configurations we use the "buildProfile" variable , which has a default value in gradle.build but can also be set when calling gradle using the "-PbuildProfile=arquillian-<whatever>" command line option
look in gradle.build line , that start with "  switch ( buildProfile ) {" so see which options are possible
the most interesting one currently is "arquillian-container-chameleon-payara5" 

### arquillian.xml
the -PbuildProfile=arquillian-<whatever>"  in gradle.build corresponds to the relating parts in arquillian.xml
this works through the line in gradle.build:
- ..."test.systemProperties = ['arquillian.launch': 'arquillian-container-chameleon-payara5'	]	

this "arquillian.launch property corresponds to the line: 
- ...container qualifier="arquillian-container-chameleon-payara5" default="true"

because of default="true", this is the configuration, that is used by eclipse -> run/debug Junittest

###persistence.xml
in the IntegrationTest.java  line: 

- ...addAsResource("persistence-integration.xml", "META-INF/persistence.xml")

the persistence-integration.xml is copied into the war-file META-INF/persistence.xml, which is then used by the java ee application server to configure JPA

BUT: in order to make this work there need to be a predefined <jta-data-source>jdbc/__default</jta-data-source> with exactly this name "jdbc/__default". 
if your application server has a different default jta-data-source , you need to adapt this line in persistence-integration.xml

## eclipse integration:
first you need to execute the gradle task "build"  this will automatically download the required depencendies, also the payra5 . after this run you can also call eclipses "run/debug junit-test" from the menu.
 	

