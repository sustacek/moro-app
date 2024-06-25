package cz.josefsustacek.moro.moroapp.service;

import cz.josefsustacek.moro.moroapp.dao.UserRepository;
import cz.josefsustacek.moro.moroapp.digest.HashGenerator;
import cz.josefsustacek.moro.moroapp.model.UserEntity;
import cz.josefsustacek.moro.moroapp.service.exception.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public long verifyLoginCredentials(String username, String password) {

        var user = userRepository.findByUsername(username);

        UserEntity userEntity =
                user.orElseThrow(() -> new AuthException("Authentication failed (1)."));

        if (passwordEncoder.matches(password, userEntity.getPasswordHash())) {
            return userEntity.getId();
        }

        throw new AuthException("Authentication failed (2).");
    }
}
