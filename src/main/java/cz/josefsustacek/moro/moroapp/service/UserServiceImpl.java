package cz.josefsustacek.moro.moroapp.service;

import cz.josefsustacek.moro.moroapp.dao.UserRepository;
import cz.josefsustacek.moro.moroapp.dto.NewUserFields;
import cz.josefsustacek.moro.moroapp.model.UserEntity;
import cz.josefsustacek.moro.moroapp.dto.UserData;
import cz.josefsustacek.moro.moroapp.dto.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public static final String PASSWORD_PLACEHOLDER = "***";

    @Autowired
    private UserRepository userRepository;

    // The same encoded used in the spring Security config to authenticate users (see SecurityConfig -> UserDetailsService)
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(long id) {
        var entity = userRepository.findById(id);

        return Optional.ofNullable(fromEntity(entity.orElse(null)));
    }

    @Override
    public User createUser(
            @Validated({Default.class, NewUserFields.class}) UserData userData) {

        UserEntity newUser = fromUserDataInput(userData, new UserEntity());

        newUser = userRepository.save(newUser);

        var dto = fromEntity(newUser);

        logger.info("User was created: {}", dto);

        return dto;
    }

    @Override
    public User updateUser(
            long id,
            @Valid UserData userData) {

        var user = userRepository.findById(id);

        if (user.isPresent()) {
            var userEntity = fromUserDataInput(userData, user.get());

            userEntity = userRepository.save(userEntity);

            var dto = fromEntity(userEntity);

            logger.info("User was updated: {}", dto);

            return dto;
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
        // will silently do nothing when ID is not found, which is okay I guess
        userRepository.deleteById(id);

        logger.info("User was deleted: id= {}", id);
    }

    @Override
    public void resetPassword(long id) {
        var user = userRepository.findById(id);

        var userEntity = user.orElseThrow(() -> new EntityNotFoundException("User not found: id= " + id));

        // ugly, but works
        var randomPassword = UUID.randomUUID().toString();
        var randomPasswordHash = passwordEncoder.encode(randomPassword);

        userEntity.setPasswordHash(randomPasswordHash);
        userRepository.save(userEntity);

        logger.info("The password of user id= {} (username= {}) was reset: {}", userEntity.getId(), userEntity.getUsername(), randomPassword);
    }

    private @Nullable User fromEntity(
            @Nullable UserEntity userEntity) {

        if (Objects.nonNull(userEntity)) {
            return new User(
                    userEntity.getId(),
                    userEntity.getName(),
                    userEntity.getUsername(),
                    PASSWORD_PLACEHOLDER);
        }

        return null;
    }

    private UserEntity fromUserDataInput(UserData sourceInput, UserEntity target) {
        Objects.requireNonNull(sourceInput);
        Objects.requireNonNull(target);

        // basic fields, update if present in input
        Optional.ofNullable(sourceInput.name()).ifPresent((newValue) -> target.setName(newValue));
        Optional.ofNullable(sourceInput.username()).ifPresent((newValue) -> target.setUsername(newValue));

        // special handling on the password fields
        String password = sourceInput.password(), passwordRepeated = sourceInput.passwordRepeated();

        if (Objects.nonNull(password)) {
            // TODO checks could probably be expressed using validation annotations as well,
            //  but this is easier solution for now
            if (Objects.isNull(passwordRepeated)) {
                throw new DataIntegrityViolationException("If setting 'password', 'passwordRepeated' is mandatory.");
            }

            if (!password.equals(passwordRepeated)) {
                throw new DataIntegrityViolationException("The fields 'password' and 'passwordRepeated' do not match.");
            }

            target.setPasswordHash(passwordEncoder.encode(password));
        }

        return target;
    }

}
