package cz.josefsustacek.moro.moroapp.service;

import cz.josefsustacek.moro.moroapp.dao.UserRepository;
import cz.josefsustacek.moro.moroapp.data.UserEntity;
import cz.josefsustacek.moro.moroapp.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getById(long id) {
        var entity = userRepository.findById(id);

        return fromEntity(entity);
    }

    private Optional<User> fromEntity(Optional<UserEntity> userEntity) {
        return userEntity.map(
                e -> new User(e.getId(), e.getName()));
    }

}
