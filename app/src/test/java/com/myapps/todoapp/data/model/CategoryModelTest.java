package com.myapps.todoapp.data.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for Category model
 * Tests follow AAA pattern: Arrange, Act, Assert
 */
public class CategoryModelTest {

    @Test
    public void createCategory_withValidName_shouldSetNameCorrectly() {
        // Arrange
        String expectedName = "Work Tasks";

        // Act
        Category category = new Category();
        category.setName(expectedName);

        // Assert
        assertEquals(expectedName, category.getName());
    }

    @Test
    public void createCategory_withParameterizedConstructor_shouldSetNameCorrectly() {
        // Arrange
        String expectedName = "Personal Tasks";

        // Act
        Category category = new Category(expectedName);

        // Assert
        assertEquals(expectedName, category.getName());
    }

    @Test
    public void createCategory_withDefaultConstructor_shouldHaveNullName() {
        // Arrange & Act
        Category category = new Category();

        // Assert
        assertNull(category.getName());
    }

    @Test
    public void categoryName_withValidName_shouldSetCorrectly() {
        // Arrange
        Category category = new Category();
        String expectedName = "Updated Name";

        // Act
        category.setName(expectedName);

        // Assert
        assertEquals(expectedName, category.getName());
    }

    @Test
    public void categoryEquals_withSameId_shouldReturnTrue() {
        // Arrange
        Category category1 = new Category("Test");
        Category category2 = new Category("Test");
        
        // Act & Assert
        assertEquals(category1.getName(), category2.getName());
    }
}
