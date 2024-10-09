package org.laboration2;

import org.laboration2.entities.Product;
import org.laboration2.entities.ProductType;
import org.laboration2.service.Warehouse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WarehouseTest {
    Warehouse warehouse = new Warehouse();

    @Test
    void shouldThrowExceptionWhenNameIsEmptyOrNull() {


        LocalDate now = LocalDate.now();

        assertThatThrownBy(() -> warehouse.newProduct(1, "", ProductType.ARMOR, 10, now, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be null or empty");
    }


    @Test
    void shouldAddNewProductSuccessfully() {


        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Broad sword", ProductType.WEAPON, 2, now, now);

        List<Product> products = warehouse.getAllProducts();
        assertThat(products.size()).isEqualTo(1);

        Product product = products.getFirst();
        assertThat(product.id()).isEqualTo(1);
        assertThat(product.name()).isEqualTo("Broad sword");
        assertThat(product.type()).isEqualTo(ProductType.WEAPON);
        assertThat(product.rating()).isEqualTo(2);
        assertThat(product.created()).isEqualTo(now);
        assertThat(product.modified()).isEqualTo(now);
    }

    @Test
    void shouldReturnEmptyOptionalWhenTryingToFindProductThatDoesNotExist() {
        Warehouse warehouse = new Warehouse();

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Broad sword", ProductType.WEAPON, 2, now, now);

        Optional<Product> result = warehouse.getProductById(2);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetProductById1ThenReturnProductNamedBroadsword() {

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Broad sword", ProductType.WEAPON, 2, now, now);

        assertThat(warehouse.getProductById(1))
                .isPresent()
                .get()
                .extracting(Product::name)
                .isEqualTo("Broad sword");
    }

    @Test
    void shouldReturnAllProducts() {

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Broad sword", ProductType.ARMOR, 2, now, now);
        warehouse.newProduct(2, "Pendulum of doom", ProductType.ARTIFACT, 3, now, now);
        warehouse.newProduct(3, "Chain mail", ProductType.WEAPON, 4, now, now);

        List<Product> products = warehouse.getAllProducts();
        assertThat(products.size()).isEqualTo(3);
    }

    @Test
    void shouldThrowExceptionWhenListIsEmpty() {


        assertThatThrownBy(() -> warehouse.getProductsByTypeSortedAtoZ(ProductType.ARMOR))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No products available!");
    }


    @Test
    void shouldReturnSortedAToZ() {

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, now, now);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, now, now);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, now, now);

        List<Product> sortedProducts = warehouse.getProductsByTypeSortedAtoZ(ProductType.WEAPON);


        List<Product> expectedSortedProducts = List.of(
                new Product(3, "Broad sword", ProductType.WEAPON, 4, now, now),
                new Product(1, "Morning star", ProductType.WEAPON, 2, now, now),
                new Product(2, "Shiv", ProductType.WEAPON, 3, now, now)
        );

        assertThat(sortedProducts)
                .containsExactlyElementsOf(expectedSortedProducts);
    }

    @Test
    void shouldThrowExceptionWhenTypeNotFound() {


        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, now, now);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, now, now);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, now, now);

        assertThatThrownBy(() -> warehouse.getProductsByTypeSortedAtoZ(ProductType.ARMOR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No products with type: ARMOR found!");
    }

    @Test
    void shouldThrowExceptionWrongDateFormat() {

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, now, now);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, now, now);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, now, now);

        assertThatThrownBy(() -> warehouse.getProductsCreatedAfter(2024, 12, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wrong date format!");

    }

    @Test
    void shouldReturnAllProductsCreatedAfter1ofAugust() {


        LocalDate now = LocalDate.now();
        LocalDate july = LocalDate.of(2024, 7, 31);
        LocalDate august = LocalDate.of(2024, 8, 30);

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, july, july);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, august, august);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, now, now);

        List<Product> sortedProducts = warehouse.getProductsCreatedAfter(2024, 8, 1);

        List<Product> expectedSortedProducts = List.of(
                new Product(2, "Shiv", ProductType.WEAPON, 3, august, august),
                new Product(3, "Broad sword", ProductType.WEAPON, 4, now, now)
        );

        assertThat(sortedProducts).isEqualTo(expectedSortedProducts);
    }

    @Test
    void shouldThrowExceptionNoProductsCreatedAfter1ofAugust() {


        LocalDate july = LocalDate.of(2024, 7, 31);

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, july, july);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, july, july);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, july, july);

        assertThatThrownBy(() -> warehouse.getProductsCreatedAfter(2024, 8, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No products created after: 2024-08-01 found!");
    }

    @Test
    void shouldThrowExceptionsForNameTypeRating() {

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, now, now);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, now, now);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, now, now);

        assertThatThrownBy(() -> warehouse.modifyProduct(1, "", ProductType.ARTIFACT, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be null or empty!");

        assertThatThrownBy(() -> warehouse.modifyProduct(2, "Shank", ProductType.ARMOR, 11))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid rating value: 11. Rating must be between 0 and 10");
    }

    @Test
    void shouldGetProductByIdAndModifyNameProductTypeRatingAndSetModified() {

        LocalDate date = LocalDate.of(2024, 7, 31);
        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, date, date);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, date, date);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, date, date);

        warehouse.modifyProduct(1, "Chain mail", ProductType.ARMOR, 10);

        List<Product> modifiedList = warehouse.getAllProducts();

        assertThat(modifiedList.size()).isEqualTo(3);

        Optional<Product> modifiedProduct = warehouse.getProductById(1);

        assertThat(modifiedProduct)
                .isPresent()
                .get()
                .extracting(Product::name, Product::type, Product::rating, Product::created, Product::modified)
                .containsExactly(
                        "Chain mail",
                        ProductType.ARMOR,
                        10,
                        LocalDate.of(2024, 7, 31),
                        now
                );
    }

    @Test
    void shouldThrowExceptionNoModifiedProductsFound() {


        LocalDate date = LocalDate.of(2024, 7, 1);

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, date, date);

        assertThatThrownBy(warehouse::getAllModifiedProducts)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No modified products found!");
    }

    @Test
    void shouldReturnListWithAllProductsThatWhereModified() {


        LocalDate date = LocalDate.of(2024, 7, 31);
        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, date, date);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, date, now);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, date, now);

        List<Product> modifiedProducts = warehouse.getAllModifiedProducts();

        List<Product> expectedProducts = List.of(
                new Product(2, "Shiv", ProductType.WEAPON, 3, date, now),
                new Product(3, "Broad sword", ProductType.WEAPON, 4, date, now)
        );

        assertThat(modifiedProducts).isEqualTo(expectedProducts);
    }

    @Test
    void shouldReturnTypesWithAtLeastOneProduct() {

        LocalDate date = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, date, date);
        warehouse.newProduct(2, "Chain mail", ProductType.ARMOR, 3, date, date);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, date, date);

        List<ProductType> categoriesWithProducts = warehouse.getTypesWithAtLeastOneProduct();

        assertThat(categoriesWithProducts.size()).isEqualTo(2);

        assertThat(categoriesWithProducts).containsExactlyInAnyOrder(ProductType.WEAPON, ProductType.ARMOR);
    }

    @Test
    void shouldThrowExceptionWhenNoProductsAvailable() {


        assertThatThrownBy(warehouse::getTypesWithAtLeastOneProduct)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No products available!");

    }

    @Test
    void shouldReturnCorrectProductCountForGivenCategory() {

        LocalDate now = LocalDate.now();


        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, now, now);
        warehouse.newProduct(2, "Chain mail", ProductType.ARMOR, 3, now, now);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, now, now);

        long weaponCount = warehouse.countProductsInCategory(ProductType.WEAPON);
        assertThat(weaponCount).isEqualTo(2);

        long armorCount = warehouse.countProductsInCategory(ProductType.ARMOR);
        assertThat(armorCount).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWhenNoProductsAvailableForCounting() {

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, now, now);
        warehouse.newProduct(2, "Broad sword", ProductType.WEAPON, 4, now, now);

        assertThatThrownBy(() -> warehouse.countProductsInCategory(ProductType.ARMOR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category with type: ARMOR has no products available!");
    }

    @Test
    void shouldReturnProductCountByStartingLetterSuccessfully() {

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, now, now);
        warehouse.newProduct(2, "Broad sword", ProductType.WEAPON, 3, now, now);
        warehouse.newProduct(3, "Chain mail", ProductType.ARMOR, 4, now, now);

        Map<Character, Long> productCountByLetter = warehouse.getProductMapWithStartingLettersAndCount();

        assertThat(productCountByLetter.size()).isEqualTo(3);
        assertThat(productCountByLetter.get('M')).isEqualTo(1);
        assertThat(productCountByLetter.get('B')).isEqualTo(1);
        assertThat(productCountByLetter.get('C')).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionsNoProductsWithMaxRatingCreatedThisMonth() {

        LocalDate old = LocalDate.of(2024, 6, 1);

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 10, old, old);
        warehouse.newProduct(2, "Broad sword", ProductType.WEAPON, 10, old, old);
        warehouse.newProduct(3, "Chain mail", ProductType.ARMOR, 4, old, old);

        assertThatThrownBy(warehouse::getThisMonthsMaxRankedProductsNewestFirst)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No products with rating 10 created this month!");
    }

    @Test
    void shouldReturnAllMaxRatedProductsCreatedThisMonth() {

        LocalDate old = LocalDate.of(2024, 6, 1);
        LocalDate thisMonth = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 10, thisMonth, thisMonth);
        warehouse.newProduct(2, "Broad sword", ProductType.WEAPON, 10, old, old);
        warehouse.newProduct(3, "Chain mail", ProductType.ARMOR, 9, thisMonth, thisMonth);

        List<Product> maxRatedProductsThisMonth = warehouse.getThisMonthsMaxRankedProductsNewestFirst();

        assertThat(maxRatedProductsThisMonth.size()).isEqualTo(1);
        assertThat(maxRatedProductsThisMonth.getFirst().name()).isEqualTo("Morning star");
        assertThat(maxRatedProductsThisMonth.getFirst().rating()).isEqualTo(10);
        assertThat(maxRatedProductsThisMonth.getFirst().created()).isEqualTo(thisMonth);
    }

    @Test
    void shouldReturnAllProductsWithPaginationPage1Size2() {
        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 10, now, now);
        warehouse.newProduct(2, "Broad sword", ProductType.WEAPON, 10, now, now);
        warehouse.newProduct(3, "Chain mail", ProductType.ARMOR, 9, now, now);

        List<Product> allProductsPaginated = warehouse.paginateAllProducts(1, 2);

        assertThat(allProductsPaginated.size()).isEqualTo(2);
        assertThat(allProductsPaginated.get(0).name()).isEqualTo("Morning star");
        assertThat(allProductsPaginated.get(1).name()).isEqualTo("Broad sword");
    }
}