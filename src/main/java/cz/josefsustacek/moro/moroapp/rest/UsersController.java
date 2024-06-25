package cz.josefsustacek.moro.moroapp.rest;

import cz.josefsustacek.moro.moroapp.dto.NewUserFields;
import cz.josefsustacek.moro.moroapp.dto.UserData;
import cz.josefsustacek.moro.moroapp.dto.User;
import cz.josefsustacek.moro.moroapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController {

    @Autowired
    private UserService userService;

    // spec says "z parametru URL", so create a method which does not use the path,
    // but actual parameter from the URL; let's deprecate this right away and drop with
    // next release :-)
    @Deprecated
    @GetMapping(path = "/users/by-id")
    public User getUserIdFromParam(
            @RequestParam(name= "id", required = true) long id) {

        return getUser(id);
    }

    @GetMapping(
            path = "/users/{id}")
    public User getUser(
            @PathVariable(name= "id", required = true) long id) {
        var user = userService.getById(id);

        return user.orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: id= " + id));
    }

    @PostMapping(
            path = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    // Validate using both the default restrictions both as the extra ones for new users
    public User createUser(
            @Validated({Default.class, NewUserFields.class}) @RequestBody UserData userData) {

        try {
            return userService.createUser(userData);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User cannot be added: " + e, e);
        }
    }

    @PostMapping(path = "/users/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(
            @PathVariable(name="id", required = true) long id,
            @Validated @RequestBody UserData userData) {

        try {
            return userService.updateUser(id, userData);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User not found: id= " + id, enfe);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User cannot be updated: " + e, e);
        }
    }

    @GetMapping(
            path = "/users")
    public List<User> findAll() {

        return userService.findAll();
    }

    @DeleteMapping(
            path = "/users/{id}")
    public void deleteUser(
            @PathVariable(name = "id", required = true) long id) {

        try {
            userService.deleteUser(id);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User not found: id= " + id, enfe);
        }
    }

    @PostMapping(
            path = "/resetUserPassword/{id}")
    public String resetUserPassword(
            @PathVariable(name = "id", required = true) long id) {

        try {
            userService.resetPassword(id);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User not found: id= " + id, enfe);
        }

        return "{ \"message\": \"Your password was reset, check the server logs to retrieve it.\" }";
    }

}
