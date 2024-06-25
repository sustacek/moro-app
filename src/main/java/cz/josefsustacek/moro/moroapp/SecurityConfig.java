package cz.josefsustacek.moro.moroapp;

import cz.josefsustacek.moro.moroapp.dao.UserRepository;
import cz.josefsustacek.moro.moroapp.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // csrf is enabled by default on all POST / DELETE requests, since they change the data in backend
                .csrf(csfr ->
                        csfr.disable()) //  let's not overcomplicate things for now, but definitely enable in next iteration;
                .authorizeHttpRequests((requests) -> requests
                        // enable selected URIs for anonymous acces
                        .requestMatchers(HttpMethod.GET, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // URI to create new user
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/resetUserPassword/**").anonymous()

                         // require auth for everything else
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Provide a bean to hash plain-text passwords. Used both by Spring Security, e.g.
     * after a form login or Basic auth headers are read, and also by our code in
     * {@link cz.josefsustacek.moro.moroapp.service.UserServiceImpl} when creating / updating
     * a user in database.
     * @return
     */
    @Bean
    public static PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

/**
 * Provide a service which will be called by Spring Security, whenever it needs to fetch a user and compare
 * its password (hashed by the provided PasswordEncoder, see above) to the value stored in the database.
 */
@Service
class RepositoryBackedUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(RepositoryBackedUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);

        UserEntity userEntity =
                user.orElseThrow(() -> new UsernameNotFoundException(username));

        // For now, we have just one role - USER; every authenticated user hold just this role
        // This might become backed by some database table instead, adding 'authorities' based on the
        // roles assigned to users

        // No need to specify encoder, our password is already encoded as stored in the DB;
        // the default encoder in User.UserBuilder is identity, which works as expected
        return User.withUsername(username)
                .password(userEntity.getPasswordHash())
                .authorities("USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

}
