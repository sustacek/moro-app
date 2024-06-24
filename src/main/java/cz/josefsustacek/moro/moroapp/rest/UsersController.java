package cz.josefsustacek.moro.moroapp.rest;

import cz.josefsustacek.moro.moroapp.dto.UserData;
import cz.josefsustacek.moro.moroapp.dto.User;
import cz.josefsustacek.moro.moroapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @GetMapping(value = "/users/by-id")
    public User getUserIdFromParam(
            @RequestParam(name= "id", required = true) long id) {

        return getUser(id);
    }

    @GetMapping(
            value = "/users/{id}")
    public User getUser(
            @PathVariable(name= "id", required = true) long id) {
        var user = userService.getById(id);

        return user.orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: id= " + id));
    }

    @PostMapping(
            value = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(
            @Valid @RequestBody UserData userData) {

        return userService.createUser(userData);
    }

    @PostMapping(value = "/users/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(
            @PathVariable(name="id", required = true) long id,
            @Valid @RequestBody UserData userData) {

        try {
            return userService.updateUser(id, userData);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User not found: id= " + id, enfe);
        }
    }

    @GetMapping(
            value = "/users")
    public List<User> findAll() {

        return userService.findAll();
    }

    @DeleteMapping(
            value = "/users/{id}")
    public void deleteUser(
            @PathVariable(name = "id", required = true) long id) {

        try {
            userService.deleteUser(id);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User not found: id= " + id, enfe);
        }
    }

}
