package org.laboration2.warehouse.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.laboration2.warehouse.entities.Product;
import org.laboration2.warehouse.entities.ProductType;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class Warehouse {
    private final List<Product> products = Collections.synchronizedList(new ArrayList<>());

    private void checkIfProductsEmpty() {
        if (products.isEmpty()) {
            throw new IllegalStateException("No products available!");
        }
    }

    public void newProduct(int id, String name, ProductType type, int rating, LocalDate created, LocalDate modified) {
        if (getProductById(id).isPresent()) {
            throw new IllegalArgumentException("Product with id: " + id + " already exists");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        try {
            Product product = new Product(id, name, type, rating, created, modified);
            products.add(product);
        } catch (Exception e) {
            throw new IllegalArgumentException("Product creation failed");
        }
    }

    public List<Product> getAllProducts() {
        checkIfProductsEmpty();

        return new ArrayList<>(products);
    }

    public Optional<Product> getProductById(int id) {

        return  products.stream()
                .filter(product -> product.id() == id)
                .findFirst();
    }

    public List<Product> getProductsByTypeSortedAtoZ(ProductType type) {
        checkIfProductsEmpty();

        List<Product> productsByType = products.stream()
                .filter(product -> product.type().equals(type))
                .sorted(Comparator.comparing(Product::name))
                .toList();

        if (productsByType.isEmpty()) {
            throw new IllegalArgumentException("No products with type: " + type + " found!");
        }

        return productsByType;
    }

    private void checkDate(int year, int month, int day) {
        LocalDate inputDate = LocalDate.of(year, month, day);
        LocalDate now = LocalDate.now();

        if (inputDate.isAfter(now)) {
            throw new IllegalArgumentException("Wrong date format!");
        }
    }

    public List<Product> getProductsCreatedAfter(int year, int month, int day) {
        checkDate(year, month, day);
        checkIfProductsEmpty();

        LocalDate targetDate = LocalDate.of(year, month, day);

        List<Product> productsCreatedAfter = products.stream()
                .filter(product -> product.created()
                        .isAfter(targetDate))
                .toList();

        if (productsCreatedAfter.isEmpty()) {
            throw new IllegalArgumentException("No products created after: " + targetDate + " found!");
        }

        return productsCreatedAfter;
    }

    public void modifyProduct(int id, String newName, ProductType newType, int newRating) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty!");
        }

        if (newRating < 0 || newRating > 10) {
            throw new IllegalArgumentException("Invalid rating value: " + newRating + ". Rating must be between 0 and 10");
        }
        Optional<Product> oldProduct = getProductById(id);

        if (oldProduct.isPresent()) {
            Product updatedProduct = new Product(
                    oldProduct.get().id(),
                    newName,
                    newType,
                    newRating,
                    oldProduct.get().created(),
                    LocalDate.now()
            );

            products.remove(oldProduct.get());
            products.add(updatedProduct);

        } else  {
            throw new IllegalArgumentException("Product creation failed");
        }
    }

    public List<Product> getAllModifiedProducts() {
        checkIfProductsEmpty();

        List<Product> modified = products.stream()
                .filter(product -> !product.created().equals(product.modified()))
                .toList();

        if (modified.isEmpty()) {
            throw new IllegalArgumentException("No modified products found!");
        }

        return modified;
    }

    public List<ProductType> getTypesWithAtLeastOneProduct() {
        checkIfProductsEmpty();

        return products.stream()
                .map(Product::type)
                .distinct()
                .toList();
    }

    public long countProductsInCategory(ProductType type) {
        checkIfProductsEmpty();

        long result = products.stream()
                .filter(product -> product.type().equals(type))
                .count();
        if (result == 0) {
            throw new IllegalArgumentException("Category with type: " + type + " has no products available!");
        }

        return result;
    }

    public Map<Character, Long> getProductMapWithStartingLettersAndCount() {
        checkIfProductsEmpty();

        return products.stream()
                .collect(Collectors.groupingBy(
                        product -> product.name().toUpperCase().charAt(0),
                        Collectors.counting()
                ));
    }

    public List<Product> getThisMonthsMaxRankedProductsNewestFirst() {
        checkIfProductsEmpty();

        LocalDate now = LocalDate.now();

        List<Product> thisMonthsMaxRatedProducts = products.stream()
                .filter(product -> product.rating() == 10)
                .filter(product -> product.created().getMonth() == now.getMonth() &&
                        product.created().getYear() == now.getYear())
                .sorted(Comparator.comparing(Product::created).reversed())
                .toList();

        if (thisMonthsMaxRatedProducts.isEmpty()) {
            throw new IllegalArgumentException("No products with rating 10 created this month!");
        }

        return thisMonthsMaxRatedProducts;
    }
}

