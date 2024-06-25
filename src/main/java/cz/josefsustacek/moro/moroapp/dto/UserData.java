package cz.josefsustacek.moro.moroapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// The validation is the same as on the respective entity's fields
public record UserData(
        @NotNull @Size(min = 1, max = 255)
        String name,

        @NotNull @Size(min = 1, max = 64) @Pattern(regexp = "[a-zA-Z0-9_\\.-]+")
        String username,

        @Valid
        UserPasswordInput password
) {}
