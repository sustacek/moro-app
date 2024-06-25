package cz.josefsustacek.moro.moroapp.dto;

/**
 * For transferring data from service to the frontend / UI.
 * @param id
 * @param name
 * @param username
 * @param password
 */
public record User(
        Long id,
        String name,
        String username,
        String password
) {}
