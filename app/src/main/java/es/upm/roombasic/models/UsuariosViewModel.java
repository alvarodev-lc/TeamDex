package es.upm.roombasic.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UsuariosViewModel extends AndroidViewModel {

    private UsuariosRepository mRepository;

    private LiveData<List<UsuariosEntity>> ldList;

    /**
     * Constructor
     *
     * @param application app
     */
    public UsuariosViewModel(Application application) {
        super(application);
        mRepository = new UsuariosRepository(application);
        ldList = mRepository.getAll();
    }

    /**
     * Obtiene todos los grupos
     *
     * @return lista de grupos
     */
    public LiveData<List<UsuariosEntity>> getAll() {
        return ldList;
    }

    public void insert(UsuariosEntity item) {
        mRepository.insert(item);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void delete(UsuariosEntity item) {
        mRepository.delete(item);
    }


}
