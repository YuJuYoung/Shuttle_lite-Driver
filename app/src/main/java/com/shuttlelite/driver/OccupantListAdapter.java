package com.shuttlelite.driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OccupantListAdapter extends RecyclerView.Adapter {

    private MyAppInfo myAppInfo = MyAppInfo.getInstance();

    private List<Occupant> occupants;
    private boolean[] isChecked;

    public OccupantListAdapter(List<Occupant> occupants) {
        this.occupants = occupants;
        isChecked = new boolean[occupants.size()];
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.occupant_info, parent, false);
        RecyclerView.ViewHolder viewHolder = new OccupantInfoViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Occupant occupant = occupants.get(position);
        OccupantInfoViewHolder viewHolder = (OccupantInfoViewHolder) holder;

        viewHolder.occupantName.setText(occupant.getName());
        viewHolder.protectorName.setText(occupant.getProtectorName());
        viewHolder.checkBox.setChecked(isChecked[position]);

        viewHolder.checkBox.setOnClickListener(new CheckBoxListener(position));
    }

    private class CheckBoxListener implements View.OnClickListener {
        private int position;

        public CheckBoxListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            isChecked[position] = ((CheckBox) view).isChecked();
        }
    }

    @Override
    public int getItemCount() {
        return occupants.size();
    }

    public boolean[] getIsChecked() {
        return isChecked;
    }

    private class OccupantInfoViewHolder extends RecyclerView.ViewHolder {
        TextView occupantName, protectorName;
        CheckBox checkBox;

        public OccupantInfoViewHolder(@NonNull View itemView) {
            super(itemView);

            occupantName = itemView.findViewById(R.id.occupant_name_desc);
            protectorName = itemView.findViewById(R.id.protector_name_desc);
            checkBox = itemView.findViewById(R.id.check_boarding);
        }
    }
}
