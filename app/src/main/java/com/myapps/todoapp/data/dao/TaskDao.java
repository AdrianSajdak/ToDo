package com.myapps.todoapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.myapps.todoapp.data.model.Task;
import java.util.List;

@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskById(long taskId);

    // Pobiera zadania główne (bez rodzica) dla danej kategorii, posortowane wg priorytetu i daty utworzenia
    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId AND parentTaskId IS NULL ORDER BY priority DESC, creationDate ASC")
    LiveData<List<Task>> getTopLevelTasksForCategory(long categoryId);

    // Pobiera podzadania dla danego zadania (nie musi być LiveData, jeśli ładujemy je na żądanie)
    @Query("SELECT * FROM tasks WHERE parentTaskId = :taskId ORDER BY creationDate ASC")
    List<Task> getSubTasks(long taskId);

    // Wersja LiveData, jeśli preferujesz obserwację zmian w podzadniach
    @Query("SELECT * FROM tasks WHERE parentTaskId = :taskId ORDER BY creationDate ASC")
    LiveData<List<Task>> getSubTasksLiveData(long taskId);

    // Aktualizuje status wykonania zadania oraz datę ukończenia
    @Query("UPDATE tasks SET isCompleted = :isCompleted, completionDate = :completionDate WHERE id = :taskId")
    void updateTaskCompletion(long taskId, boolean isCompleted, Long completionDate);

    // Pobiera wszystkie wykonane zadania (niecykliczne), posortowane od najnowszych
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND isRecurring = 0 ORDER BY completionDate DESC")
    LiveData<List<Task>> getCompletedTasks();

    // Pobiera wszystkie zadania cykliczne (dla WorkManagera) - to nie jest LiveData, bo będzie używane w tle
    @Query("SELECT * FROM tasks WHERE isRecurring = 1")
    List<Task> getRecurringTasks();

    // Usuwa zadanie po ID
    @Query("DELETE FROM tasks WHERE id = :taskId")
    void deleteById(long taskId);
}