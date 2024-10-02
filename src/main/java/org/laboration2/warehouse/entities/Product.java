package org.laboration2.warehouse.entities;

import java.time.LocalDate;

public record Product(int id, String name, ProductType type, int rating, LocalDate created, LocalDate modified) {}
