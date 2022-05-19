package es.upm.roombasic.models;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class UsuariosRepository {
    private IUsuariosDAO iItemDAO;
    private LiveData<List<UsuariosEntity>> ldList;

    /**
     * Constructor
     *
     * @param application app
     */
    public UsuariosRepository(Application application) {
        UsuariosRoomDatabase db = UsuariosRoomDatabase.getDatabase(application);
        iItemDAO = db.grupoDAO();
        ldList = iItemDAO.getAll();
    }

    public LiveData<List<UsuariosEntity>> getAll() {
        return ldList;
    }

    public long insert(UsuariosEntity item) {
        return iItemDAO.insert(item);
    }

    public void deleteAll() {
        iItemDAO.deleteAll();
    }

    public void delete(UsuariosEntity item)  {
        iItemDAO.delete(item);
    }
}
