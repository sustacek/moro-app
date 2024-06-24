package cz.josefsustacek.moro.moroapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// The validation is the same as on the respective entity's fields
public record UserData(
        @NotNull @Size(min = 1, max = 255) String name
) {}
