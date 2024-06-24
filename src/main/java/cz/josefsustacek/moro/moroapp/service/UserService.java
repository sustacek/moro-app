package cz.josefsustacek.moro.moroapp.service;

import cz.josefsustacek.moro.moroapp.dto.UserData;
import cz.josefsustacek.moro.moroapp.dto.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getById(long id);

    User createUser(UserData userData);

    User updateUser(long id, UserData userData);

    List<User> findAll();

    void deleteUser(long id);

}
