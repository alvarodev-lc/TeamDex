package es.upm.roombasic.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IUsuariosDAO {
    @Query("SELECT * FROM " + UsuariosEntity.TABLA)
    LiveData<List<UsuariosEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(UsuariosEntity grupo);

    @Query("DELETE FROM " + UsuariosEntity.TABLA)
    void deleteAll();

    @Delete
    void delete(UsuariosEntity grupo);
}
