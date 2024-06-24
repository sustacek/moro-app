# moro-app

## Quick start
1. `docker compose up`
   * starts database and anything else that's needed
   * see the [compose.yml](compose.yml) file for the port being exposed (:5432 for Postgres, :18080 for adminer)
2. `./gradlew bootRun`
   * the app itself
   * starts on the default SpringBoot port :8080, make sure it's not taken in your OS by e.g. Tomcat