package cz.josefsustacek.moro.moroapp.rest;

import cz.josefsustacek.moro.moroapp.dto.User;
import cz.josefsustacek.moro.moroapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/users/by-id", produces = "application/json")
    public User getUser(@RequestParam("id") long id) {
        // spec says "z parametru URL", so don't use the path, but actual parameter

        var user = userService.getById(id);

        return user.orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: id = " + id));
    }

}
