package cz.josefsustacek.moro.moroapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * For transferring data from frontend to the service. Both new users and updated users will use these objects.
 *
 * @param name
 * @param username
 * @param password
 * @param passwordRepeated
 */
// The validation is the same as on the respective entity's fields and must be kept in sync manually.
public record UserDataInput(
        // Can be null during update, meaning user does not want to update the name
        @NotNull(groups = NewUserFields.class)
        @Size(min = 1, max = 255)
        String name,

        // Can be null during update, meaning user does not want to update the username
        @NotNull(groups = NewUserFields.class)
        @Size(min = 1, max = 64)
        @Pattern(regexp = "[a-zA-Z0-9_\\.-]+")
        String username,

        // Can be null during update, meaning user does not want to update the password
        @NotNull(groups = NewUserFields.class)
        @Size(min = 10, max = 200)
        String password,

        // Can be null during update, meaning user does not want to update the password
        @NotNull(groups = NewUserFields.class)
        @Size(min = 10, max = 200)
        String passwordRepeated
) {}
