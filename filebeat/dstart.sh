#docker run --name filebeat docker.elastic.co/beats/filebeat:6.2.4

docker run   -v /Users/bodo/swe_projects/eclipse-webtools-test/arquillian-tutorial-gradle/filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml  -v /Users/bodo/swe_projects/wildfly-13.0.0.Beta1/standalone/log/server.log:/usr/share/filebeat/server.log -v /Users/bodo/swe_projects/eclipse-webtools-test/arquillian-tutorial-gradle/filebeat/logstash-beats.crt:/etc/pki/tls/certs/logstash-beats.crt --name filebeat  docker.elastic.co/beats/filebeat:6.2.4
