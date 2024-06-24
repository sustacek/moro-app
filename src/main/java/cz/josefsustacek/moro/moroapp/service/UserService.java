package cz.josefsustacek.moro.moroapp.service;

import cz.josefsustacek.moro.moroapp.dto.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {
    Optional<User> getById(long id);
}
