# moro-app

## Quick start
1. `docker compose up`
   * starts database and anything else that's needed
   * see the [compose.yml](compose.yml) file for the ports being exposed (:5432 for Postgres, :18080 for adminer)
2. `./gradlew bootRun`
   * the app itself, runs in your OS
   * starts on the default SpringBoot port (:8080), make sure it's not taken in your OS by e.g. Tomcat
   * **Note**: this uses JVM from your OS, make sure you have Java 17+ JDK installed (as required by SpringBoot 3.x being used)
      ```
      $ java -version
      openjdk version "17.0.5" 2022-10-18 LTS
      OpenJDK Runtime Environment Zulu17.38+21-CA (build 17.0.5+8-LTS)
      OpenJDK 64-Bit Server VM Zulu17.38+21-CA (build 17.0.5+8-LTS, mixed mode, sharing)
      ```
     
## Manual testing

### Phase 1:

**Valid requests**
* GET http://localhost:8080/users/by-id?id=1 (the spec said "id from URL parameter")
* Bonus: GET http://localhost:8080/users/1 (the way-nicer version, `id` from path)

Returns:
```json
{
  "id": 603,
  "name": "New User",
  "username": "new_user_2445",
  "password": "***"
}
```

**Invalid requests**
   1. GET http://localhost:8080/users/by-id?id=123 (user of given id not found)
   2. GET http://localhost:8080/users/by-id?id=abd (malformed param)
   3. GET http://localhost:8080/users/by-id (mandatory param missing)
   4. GET http://localhost:8080/users/by-idZZZ (malformed URI)

### Phase 2

**Valid requests**
   1. POST http://localhost:8080/users
      * create new user
      * you need to provide body of the request as the details of the user
      * request body (Note: phase 3 added new fields which are mandatory for new users: 
        ```json
        {  
          "name": "James" 
        }
        ```
        
   2. POST http://localhost:8080/users/1
      * update user with `id == 1`
      * you need to provide the body of the request as the details of the user
      * request body:
        ```json
        { 
          "name": "James" 
        }
        ```
        
   3. GET http://localhost:8080/users
      * returns list of all users
      * response body: 
        ```json
        [
          { "id": 1, "name": "James" },
          { "id": 2, "name": "Alice" }
        ]
        ```
        
   4. DELETE http://localhost:8080/users/1
       * delete user with `id == 1`, if it exists
       * since Spring Data does not complain if given ID does not exist in persistence (and silently does nothing), no error is thrown into the UI either

**Validation**

First _on frontend_:
 * Various `jakarta.validation.constraints.*` types used on the UserDataInput fields, method-level validation used on the REST controller (using `MethodValidationPostProcessor` and `LocalValidatorFactoryBean`).
 * same object is used for _create_ and _update_ operations, reusing the validation rules. But different validation group is used for the restrictions which should only be checked during create (~ all fields mandatory) and not during update (~ user is not forced to e.g. update password with every request; also the app supports updating a subset of fields for a user, so that unmodified fields don't hav eto be listed).

And then validation _on backend_: on the JPA entities, validated when they are about to be stored into the DB.

### Phase 3

* `password` field is storing hashed passwords, with salt, created using the default (recommended) `PasswordEncoder` from Spring Security, defaulting to bcrypt for new users
* Spring Security enabled, Basic Auth on selected paths
* POST http://localhost:8080/users/{id} 
  * only usable for authenticated users
  * but anyone can update anyone else, not just themselves...
* DELETE http://localhost:8080/users/{1}
  * only usable for authenticated users
  * but anyone can delete anyone else, not just themselves...)
* creating user
    * => all fields are mandatory (input is read as JSON in the request body);
      ```json
          { "name": "New User Michal",
            "username": "michal",
            "password": "aaaaabbbbb",
            "passwordRepeated": "aaaaabbbbb"
          }
      ```
* updating user
    * => no fields are mandatory (input is read as JSON in the request body);
    * the fields which match the UserDataInput type (record) AND are not null, must be valid (length, patter, see sources) 
    * password must be provided twice, to prevent typos
    * valid inputs (assuming target user to be updated exists and the requesting user is authenticated against DB)
      * ```json
          { 
            "name": "New User Michal",
            "username": "michal",
            "password": "aaaaabbbbb",
            "passwordRepeated": "aaaaabbbbb"
          }
        ```
      * ```json
          { 
            "name": "Warren Buffet",
            "username": "oracle_from_omaha"
          }
        ```   
      * ```json
          { 
            "name": "Jim Halpert"
          }
        ```
      * ```json
          { 
            "password": "passw0rd",
            "passwordRepeated": "passw0rd"
          }
        ```
      * ```json
          { 
          }
        ```        

### Phase 4 (extra)

**Extras in the app**

I've added a password-rest URI
* POST http://localhost:8080/resetUserPassword/603
    * random password is generated, hashed and updated for the user in DB (only the hash is stored) and clear-text password is printed into server log
    * the password could be emailed to the user instead
    * or the endpoint could generate a time-limited reset link, email it to the user who would then set a new password manually in the UI

* secure-by-default (whitelist)
  * everything requires authentication by default 
  * only following endpoints are allowed even without auth
      * POST http://localhost:8080/users (create new user)
      * GET http://localhost:8080/users (fetch all users)
      * GET http://localhost:8080/users/{id} (fetch one user)
      * POST http://localhost:8080/error so that error handling does not prompt the Basic auth login dialog
  * CSRF was disabled for now, to keep it easy for now and not require a roundtrip to get the token before the acutall call

* added compose.yml with simple Docker Compose to run the DB
  * latest Postgres + Adminer UI to access the DB
  * easy to use by anyone, only JDK17 required to build and run the app itself, the rest is hidden in Docker containers
* this whole docs describing the app, its use and implementation details

**Tips for the future**

* Dockerize the Spring Boot app and start it in Docker container even in development
  * this would remove the need to have JDK17 installed in the OS and used by Gradle

* add _authorization_ on top of authentication
  * e.g. create some RBAC system -- introduce _roles_ table and map the users 1-to-many to the roles
  * give each role different privileges within the app
  * pass the list of roles of a user to Spring Security as the _authorities_ in our `RepositoryBackedUserDetailsService` class 
  * this will allow users (holding only the role USER after successful login) to only update themselves while some ADMIN user(s) update / delete anyone

* Tests! 
  * There are currently none, but some more complicated pieces of code should be covered by unit tests. Then some integration tests across all the layers (using some embedded DB for testing on top of various states of the DB, sending HTTP to the REST endpoints).
  * like in this example: https://howtodoinjava.com/spring-boot2/testing/rest-controller-unit-test-example/

Describe the API using some schema / industry standard. OpenAPI, Swagger or similar. This will also make it easier to test.

**Deployment, runtime**

* package the app as a Docker image, create releases in some repo (docker repository)
* create a Dockerfile, basing it on some stable JDK17 image, build the app + add the app jars
* define clear configuration points, like the DB host:port and credentials, passing the secrets from the runtime environment and not storing it in the sources

**Automatization**

* Set up CI/CD (GitHub Actions, Jenkins or similar)
* in CI - build, run tests, create an immutable release
* check code quality - Sonar, FindBugs, run on every PR etc.
* enable automatic and continuous vulnerability testing -- using libraries with known flaws discovered after the code was commited

## Hot-reload during development
TODO does not work :-)

Neat trick for development -- live reload of the SpringBoot app (
[source](https://stackoverflow.com/questions/31512195/does-gradle-continuous-build-support-springboot/41878387#41878387)): 
1. `./gradlew build --continuous` in one terminal
2. `./gradlew bootRun` in second terminal