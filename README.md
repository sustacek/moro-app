# moro-app

## Quick start
1. `docker compose up`
   * starts database and anything else that's needed
   * see the [compose.yml](compose.yml) file for the port being exposed (:5432 for Postgres, :18080 for adminer)
2. `./gradlew bootRun`
   * the app itself, runs in your OS
   * starts on the default SpringBoot port (:8080), make sure it's not taken in your OS by e.g. Tomcat
   * **Note**: this uses JVM from your OS, make sure you have Java 17+ JDK installed (required by SpringBoot 3.x being used)
      ```
      $ java -version
      openjdk version "17.0.5" 2022-10-18 LTS
      OpenJDK Runtime Environment Zulu17.38+21-CA (build 17.0.5+8-LTS)
      OpenJDK 64-Bit Server VM Zulu17.38+21-CA (build 17.0.5+8-LTS, mixed mode, sharing)
      ```
     
## Manual testing

### Phase 1:
Valid requests
   1. http://localhost:8080/users/by-id?id=1
   2. http://localhost:8080/users/by-id?id=2 ...

Invalid requests
   1. http://localhost:8080/users/by-id?id=123 (user of given id not found)
   2. http://localhost:8080/users/by-id?id=abd (malformed param)
   3. http://localhost:8080/users/by-id (mandatory param missing)
   4. http://localhost:8080/users/by-idZZZ (malformed URI)


## Hot-reload during development
TODO does not work :-)

Neat trick for development -- live reload of the SpringBoot app (
[source](https://stackoverflow.com/questions/31512195/does-gradle-continuous-build-support-springboot/41878387#41878387)): 
1. `./gradlew build --continuous` in one terminal
2. `./gradlew bootRun` in second terminal