package org.laboration2;

import org.laboration2.warehouse.entities.Product;
import org.laboration2.warehouse.entities.ProductType;
import org.laboration2.warehouse.service.Warehouse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WarehouseTest {

    @Test
    void shouldThrowExceptionWhenNameIsEmptyOrNull() {
        Warehouse warehouse = new Warehouse();

        LocalDate now = LocalDate.now();

        assertThatThrownBy(() -> warehouse.newProduct(1, "", ProductType.ARMOR, 10, now, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be null or empty");
    }


    @Test
    void shouldAddNewProductSuccessfully() {
        Warehouse warehouse = new Warehouse();

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Broad sword", ProductType.WEAPON, 2, now, now);

        List<Product> products = warehouse.getProducts();
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
    void shouldThrowExceptionWhenTryingToFindProductThatDoesNotExist() {
        Warehouse warehouse = new Warehouse();

        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Broad sword", ProductType.WEAPON, 2, now, now);

        assertThatThrownBy(() -> warehouse.getProductById(2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product with id: 2, does not exist");
    }

    @Test
    void shouldGetProductById1ThenReturnProductNamedBroadsword() {
        Warehouse warehouse = new Warehouse();
        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Broad sword", ProductType.WEAPON, 2, now, now);

        assertThat(warehouse.getProductById(1))
                .extracting(Product::name)
                .isEqualTo("Broad sword");
    }

    @Test
    void shouldReturnAllProducts() {
        Warehouse warehouse = new Warehouse();
        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Broad sword", ProductType.ARMOR, 2, now, now);
        warehouse.newProduct(2, "Pendulum of doom", ProductType.ARTIFACT, 3, now, now);
        warehouse.newProduct(3, "Chain mail", ProductType.WEAPON, 4, now, now);

        List<Product> products = warehouse.getProducts();
        assertThat(products.size()).isEqualTo(3);
    }

    @Test
    void shouldThrowExceptionWhenListIsEmpty() {
        Warehouse warehouse = new Warehouse();

        assertThatThrownBy(() -> warehouse.getProductsByTypeSortedAtoZ(ProductType.ARMOR))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No products available!");
    }


    @Test
    void shouldReturnSortedAToZ() {
        Warehouse warehouse = new Warehouse();
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
        Warehouse warehouse = new Warehouse();

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
        Warehouse warehouse = new Warehouse();
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
        Warehouse warehouse = new Warehouse();

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
        Warehouse warehouse = new Warehouse();

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
        Warehouse warehouse = new Warehouse();
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
        Warehouse warehouse = new Warehouse();
        LocalDate date = LocalDate.of(2024, 7, 31);
        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, date, date);
        warehouse.newProduct(2, "Shiv", ProductType.WEAPON, 3, date, date);
        warehouse.newProduct(3, "Broad sword", ProductType.WEAPON, 4, date, date);

        warehouse.modifyProduct(1, "Chain mail", ProductType.ARMOR, 10);

        List<Product> modifiedList = warehouse.getProducts();

        assertThat(modifiedList.size()).isEqualTo(3);

        Product modifiedProduct = warehouse.getProductById(1);

        assertThat(modifiedProduct)
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
        Warehouse warehouse = new Warehouse();

        LocalDate date = LocalDate.of(2024, 7, 1);

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, date, date);

        assertThatThrownBy(warehouse::getAllModifiedProducts)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No modified products found!");
    }

    @Test
    void shouldReturnListWithAllProductsThatWhereModified() {
        Warehouse warehouse = new Warehouse();

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
        Warehouse warehouse = new Warehouse();
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
        Warehouse warehouse = new Warehouse();

        assertThatThrownBy(warehouse::getTypesWithAtLeastOneProduct)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No products available!");

    }

    @Test
    void shouldReturnCorrectProductCountForGivenCategory() {
        Warehouse warehouse = new Warehouse();
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
        Warehouse warehouse = new Warehouse();
        LocalDate now = LocalDate.now();

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 2, now, now);
        warehouse.newProduct(2, "Broad sword", ProductType.WEAPON, 4, now, now);

        assertThatThrownBy(() -> warehouse.countProductsInCategory(ProductType.ARMOR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category with type: ARMOR has no products available!");
    }

    @Test
    void shouldReturnProductCountByStartingLetterSuccessfully() {
        Warehouse warehouse = new Warehouse();
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
        Warehouse warehouse = new Warehouse();
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
        Warehouse warehouse = new Warehouse();
        LocalDate old = LocalDate.of(2024, 6, 1);
        LocalDate thisMonth = LocalDate.of(2024, 9, 30);

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
    void shouldReturnAllMaxRatedProductsCreatedThisMonthSortedNewestFirst() {
        Warehouse warehouse = new Warehouse();
        LocalDate oldest = LocalDate.of(2024, 9, 1);
        LocalDate middle = LocalDate.of(2024, 9, 15);
        LocalDate newest = LocalDate.of(2024, 9, 30);

        warehouse.newProduct(1, "Morning star", ProductType.WEAPON, 10, oldest, oldest);
        warehouse.newProduct(2, "Broad sword", ProductType.WEAPON, 10, newest, newest);
        warehouse.newProduct(3, "Chain mail", ProductType.ARMOR, 10, middle, middle);

        List<Product> maxRatedProductsThisMonth = warehouse.getThisMonthsMaxRankedProductsNewestFirst();

        assertThat(maxRatedProductsThisMonth.size()).isEqualTo(3);
        assertThat(maxRatedProductsThisMonth)
                .extracting(Product::name)
                .containsExactly("Broad sword", "Chain mail", "Morning star");
    }
}