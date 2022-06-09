package es.upm.miw.virgolini;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TeamBuilderListAdapter extends ArrayAdapter<String> {

    String[] names;
    Context context;

    public TeamBuilderListAdapter(@NonNull Context context, String[] poke_names) {
        super(context, R.layout.row_list);
        this.names = poke_names;
        this.context = context;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Viewolder viewHolder = new Viewolder();
        if (convertView == null) {

            LayoutInflater mInflator = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflator.inflate(R.layout.row_list, parent, false);
            viewHolder.name = convertView.findViewById(R.id.poke_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Viewolder) convertView.getTag();
        }
        viewHolder.name.setText(names[position]);

        return convertView;
    }

    static class Viewolder {
        TextView name;
    }
}