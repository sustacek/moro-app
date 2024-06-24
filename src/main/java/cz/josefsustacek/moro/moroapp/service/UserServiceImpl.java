package cz.josefsustacek.moro.moroapp.service;

import cz.josefsustacek.moro.moroapp.dao.UserRepository;
import cz.josefsustacek.moro.moroapp.data.UserEntity;
import cz.josefsustacek.moro.moroapp.dto.UserData;
import cz.josefsustacek.moro.moroapp.dto.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getById(long id) {
        var entity = userRepository.findById(id);

        return Optional.ofNullable(fromEntity(entity.orElse(null)));
    }

    @Override
    public User createUser(UserData userData) {
        UserEntity newUser = fromUserData(userData, new UserEntity());

        newUser = userRepository.save(newUser);

        return fromEntity(newUser);
    }

    @Override
    public User updateUser(long id, UserData userData) {
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
        return userRepository.findAll()
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
                .map(e -> new User(e.getId(), e.getName()))
                .orElse(null);
    }

    private UserEntity fromUserData(UserData userData, UserEntity target) {
        Objects.requireNonNull(userData);
        Objects.requireNonNull(target);

        target.setName(userData.name());

        return target;
    }

}
