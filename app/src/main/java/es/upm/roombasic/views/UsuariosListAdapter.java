package es.upm.roombasic.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.roombasic.R;
import es.upm.roombasic.models.UsuariosEntity;

public class UsuariosListAdapter extends RecyclerView.Adapter<UsuariosListAdapter.UsuarioViewHolder> {

    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView userItemView;

        private UsuarioViewHolder(View itemView) {
            super(itemView);
            userItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<UsuariosEntity> itemsList;

    /**
     * Constructor
     *
     * @param context context
     */
    public UsuariosListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new UsuarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        if (itemsList != null) {
            UsuariosEntity current = itemsList.get(position);
            holder.userItemView.setText(current.getNombre());
        } else {
            // Covers the case of data not being ready yet.
            holder.userItemView.setText("No item");
        }
    }

    public void setItems(List<UsuariosEntity> userList){
        itemsList = userList;
        notifyDataSetChanged();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return (itemsList == null)
                ? 0
                : itemsList.size();
    }

    public UsuariosEntity getGrupoAtPosition (int position) {
        return itemsList.get(position);
    }
}
