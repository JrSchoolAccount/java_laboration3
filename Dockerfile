FROM bitnami/wildfly:33.0.2
COPY target/java_laboration3-1.0-SNAPSHOT.war /opt/bitnami/wildfly/standalone/deployments/