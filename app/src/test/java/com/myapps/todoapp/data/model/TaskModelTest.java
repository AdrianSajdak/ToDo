package com.myapps.todoapp.data.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for Task model
 * Tests follow AAA pattern: Arrange, Act, Assert
 */
public class TaskModelTest {

    @Test
    public void createTask_withValidData_shouldSetPropertiesCorrectly() {
        // Arrange
        String expectedTitle = "Test Task";
        String expectedDescription = "Test Description";
        int expectedPriority = 1;
        boolean expectedIsCompleted = false;

        // Act
        Task task = new Task();
        task.setTitle(expectedTitle);
        task.setDescription(expectedDescription);
        task.setPriority(expectedPriority);
        task.setCompleted(expectedIsCompleted);

        // Assert
        assertEquals(expectedTitle, task.getTitle());
        assertEquals(expectedDescription, task.getDescription());
        assertEquals(expectedPriority, task.getPriority());
        assertEquals(expectedIsCompleted, task.isCompleted());
    }

    @Test
    public void createTask_withDefaultConstructor_shouldHaveDefaultValues() {
        // Arrange & Act
        Task task = new Task();

        // Assert
        assertNull(task.getTitle());
        assertNull(task.getDescription());
        assertEquals(0, task.getPriority()); // Default priority should be 0 (low)
        assertFalse(task.isCompleted()); // Default should be not completed
        assertFalse(task.isRecurring()); // Default should be non-recurring
    }

    @Test
    public void setTaskCompletion_whenCompleted_shouldSetCompletionDate() {
        // Arrange
        Task task = new Task();
        long expectedCompletionTime = System.currentTimeMillis();

        // Act
        task.setCompleted(true);
        task.setCompletionDate(expectedCompletionTime);

        // Assert
        assertTrue(task.isCompleted());
        assertEquals(expectedCompletionTime, task.getCompletionDate().longValue());
    }

    @Test
    public void setTaskCompletion_whenNotCompleted_shouldClearCompletionDate() {
        // Arrange
        Task task = new Task();
        task.setCompleted(true);
        task.setCompletionDate(System.currentTimeMillis());

        // Act
        task.setCompleted(false);
        task.setCompletionDate(null);

        // Assert
        assertFalse(task.isCompleted());
        assertNull(task.getCompletionDate());
    }

    @Test
    public void setPriority_withValidValues_shouldSetCorrectly() {
        // Arrange
        Task task = new Task();

        // Act & Assert for low priority
        task.setPriority(0);
        assertEquals(0, task.getPriority());

        // Act & Assert for medium priority  
        task.setPriority(1);
        assertEquals(1, task.getPriority());

        // Act & Assert for high priority
        task.setPriority(2);
        assertEquals(2, task.getPriority());
    }

    @Test
    public void setRecurring_shouldToggleRecurringStatus() {
        // Arrange
        Task task = new Task();
        
        // Act
        task.setRecurring(true);
        
        // Assert
        assertTrue(task.isRecurring());
        
        // Act
        task.setRecurring(false);
        
        // Assert
        assertFalse(task.isRecurring());
    }

    @Test
    public void setCreationDate_shouldSetTimestamp() {
        // Arrange
        Task task = new Task();
        long expectedCreationTime = System.currentTimeMillis();

        // Act
        task.setCreationDate(expectedCreationTime);

        // Assert
        assertEquals(expectedCreationTime, task.getCreationDate());
    }
}
