package cz.josefsustacek.moro.moroapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserPasswordInput(
        @NotNull @Size(min = 10, max = 200)
        String value,

        @NotNull @Size(min = 10, max = 200)
        String valueRepeated
) {}
