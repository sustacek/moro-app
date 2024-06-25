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
First on frontend:
 * Various `jakarta.validation.constraints.*` types used on the UserDataInput fields, method-level validation used on the REST controller.
 * same object is used for create and update, which different validation group used for the restrictions which should only be used during create (all fields mandatory) and not during update (user is not forced to e.g. update password with every request; also the app support updating a subset of fields)

And then validation on backend: on the JPA entities, validated when they are about to be stored into the DB.

### Phase 3

* `password` field is storing hashed passwords, with salt, using the default recommended `PasswordEncoder` from Spring Security
* Spring Security enabled, Basic Auth on selected paths
* POST http://localhost:8080/users/{id} only usable for authenticated users (but anyone can update anyone else, not just themselves...)
* DELETE http://localhost:8080/users/{1} only usable for authenticated users (but anyone can delete anyone else, not just themselves...)
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

### Phase 4 (extras)
* secure-by-default (whitelist)
  * everything requires authentication by default 
  * only following endpoints are allowed even without auth
      * POST http://localhost:8080/users (create new user)
      * GET http://localhost:8080/users (fetch all users)
      * GET http://localhost:8080/users/{id} (fetch one user)
      * POST http://localhost:8080/error so that error handling does not prompt the Basic auth login dialog
  * CSRF was disabled for now, to keep it easy for now and not require a roundtrip to get the token before the acutall call


#### Password reset link:
* POST http://localhost:8080/resetUserPassword/603
    * updated and clear-text value printed into server log 
#### Docs   


## Hot-reload during development
TODO does not work :-)

Neat trick for development -- live reload of the SpringBoot app (
[source](https://stackoverflow.com/questions/31512195/does-gradle-continuous-build-support-springboot/41878387#41878387)): 
1. `./gradlew build --continuous` in one terminal
2. `./gradlew bootRun` in second terminal