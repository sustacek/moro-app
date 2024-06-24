package cz.josefsustacek.moro.moroapp.dao;

import cz.josefsustacek.moro.moroapp.data.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
