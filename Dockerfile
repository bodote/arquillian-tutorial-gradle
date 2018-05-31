FROM jboss/wildfly
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0","-bmanagement", "0.0.0.0", "--debug" , "-c",  "standalone-ee8.xml"]
#run with
#docker run -p 8080:8080 -p 9990:9990 -p 8787:8787 -v /Users/bodo/swe_projects/eclipse-webtools-test/arquillian-tutorial-gradle/build/libs:/opt/jboss/wildfly/standalone/deployments/ wildfly12ee8