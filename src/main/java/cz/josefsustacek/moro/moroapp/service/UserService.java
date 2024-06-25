package cz.josefsustacek.moro.moroapp.service;

import cz.josefsustacek.moro.moroapp.dto.UserDataInput;
import cz.josefsustacek.moro.moroapp.dto.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getById(long id);

    User createUser(UserDataInput userDataInput);

    User updateUser(long id, UserDataInput userDataInput);

    List<User> findAll();

    void deleteUser(long id);

    void resetPassword(long id);
}
