ais-maven-enforcer
==================

Rules for [Maven Enforcer Plugin](http://maven.apache.org/enforcer/maven-enforcer-plugin/).
JAR file is available in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cais-enforcer-rules)

DependencyGroupVersionCheck
---------------------------

Verify, that dependencies sharing the same `groupId` share the same `version`.

Example: making sure, that all `org.springframework` dependencies are defined with the same version:

    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-enforcer-plugin</artifactId>
      <version>1.3.1</version>
      <executions>
        <execution>
          <id>enforce-spring/id>
          <goals>
            <goal>enforce</goal>
          </goals>
          <configuration>
            <rules>
              <dependencyGroupVersionCheck implementation="pl.ais.maven.enforcer.rules.DependencyGroupVersionCheck">
                <groupIds>
                  <groupId>org.springframework</groupId>
                </groupIds>
              </dependencyGroupVersionCheck>
            </rules>
          </configuration>
        </execution>
      </executions>
      <dependencies>
          <dependency>
          <groupId>pl.ais.maven</groupId>
          <artifactId>ais-enforcer-rules</artifactId>
          <version>1.0</version>
        </dependency>
      </dependencies>
    </plugin>

Rule checks, if there's the same version within a group, so with multiple groups:

    <rules>
      <dependencyGroupVersionCheck implementation="pl.ais.maven.enforcer.rules.DependencyGroupVersionCheck">
        <groupIds>
          <groupId>org.springframework</groupId>
          <groupId>org.slf4j</groupId>
        </groupIds>
      </dependencyGroupVersionCheck>
    </rules>

`org.springframework` and `org.slf4j` will be checked independently.
