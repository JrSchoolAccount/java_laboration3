package org.laboration2.entities;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record Product(
        @NotNull
        @Min(1)
        int id,

        @NotEmpty(message = "Empty product name not allowed")
        String name,

        @NotNull
        ProductType type,

        @Min(value = 1, message = "Minimum rating can not be less than 1")
        @Max(value = 10, message = "Max rating can not be more than 10")
        int rating,

        LocalDate created,

        LocalDate modified) {

}
