package cz.josefsustacek.moro.moroapp;

import cz.josefsustacek.moro.moroapp.rest.UsersController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private UsersController controller;

    @Test
    void contextLoads() {
        Assertions.assertThat(controller).isNotNull();
    }

}