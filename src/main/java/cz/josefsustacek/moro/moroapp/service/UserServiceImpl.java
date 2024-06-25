package cz.josefsustacek.moro.moroapp.service;

import cz.josefsustacek.moro.moroapp.dao.UserRepository;
import cz.josefsustacek.moro.moroapp.digest.HashGenerator;
import cz.josefsustacek.moro.moroapp.dto.UserPasswordInput;
import cz.josefsustacek.moro.moroapp.model.UserEntity;
import cz.josefsustacek.moro.moroapp.dto.UserData;
import cz.josefsustacek.moro.moroapp.dto.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(long id) {
        var entity = userRepository.findById(id);

        return Optional.ofNullable(fromEntity(entity.orElse(null)));
    }

    @Override
    public User createUser(
            @Valid UserData userData) {

        UserEntity newUser = fromUserData(userData, new UserEntity());

        newUser = userRepository.save(newUser);

        return fromEntity(newUser);
    }

    @Override
    public User updateUser(
            long id,
            @Valid UserData userData) {

        var user = userRepository.findById(id);

        if (user.isPresent()) {
            var userEntity = fromUserData(userData, user.get());

            userEntity = userRepository.save(userEntity);

            return fromEntity(userEntity);
        } else {
            throw new EntityNotFoundException("User not found: id= " + id);
        }
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll(Sort.by("id"))
                .stream()
                .map(this::fromEntity)
                .toList();
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    private @Nullable User fromEntity(
            @Nullable UserEntity userEntity) {

        return Optional.ofNullable(userEntity)
                .map(e ->
                        new User(
                                e.getId(),
                                e.getName(),
                                e.getUsername()))
                .orElse(null);
    }

    private UserEntity fromUserData(UserData userData, UserEntity target) {
        Objects.requireNonNull(userData);
        Objects.requireNonNull(target);

        // basic fields, mandatory
        target.setName(userData.name());
        target.setUsername(userData.username());

        // the optional fields
        UserPasswordInput passwordInput;
        if ((passwordInput = userData.password()) != null && (passwordInput.value() != null)) {
            if (!passwordInput.value().equals(passwordInput.valueRepeated())) {
                throw new ConstraintViolationException(
                        "The passwords do not match", Collections.emptySet());
            }

            target.setPasswordHash(passwordEncoder.encode(passwordInput.value()));
        }

        return target;
    }

}
