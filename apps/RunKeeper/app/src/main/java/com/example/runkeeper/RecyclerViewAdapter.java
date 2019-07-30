package com.example.runkeeper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runkeeper.model.DatabaseHelper;

import java.util.ArrayList;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    ArrayList<String> distance = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<String> cdate = new ArrayList<>();
    Context context;
    boolean delChecked = false;

    public RecyclerViewAdapter(Context mcontext, ArrayList<String> mdistance, ArrayList<String> mtime, ArrayList<String> mdate) {
        distance = mdistance;
        time = mtime;
        cdate = mdate;
        context = mcontext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_of_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.dist.setText(distance.get(position)+"км");
        holder.time.setText(time.get(position)+"ч");
        holder.date.setText(cdate.get(position)+"");

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String [] list = {"delete"};
                builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i)
                        {
                            case 0:
                                delChecked = true;
                                break;
                        }
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(delChecked)
                        {
                            delete(cdate.get(position));
                            holder.layout.removeAllViews();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    private void delete(String s) {
        DatabaseHelper myDB = new DatabaseHelper(context);
        boolean delete = myDB.deleteRow(s);

        if(delete)
        {
            Toast.makeText(context, "row succcessfully deleted", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(context, "row is not deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return distance.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView time, dist, date;
        LinearLayout layout;
        public ViewHolder(@NonNull View v) {
            super(v);

            time = v.findViewById(R.id.t_id);
            dist = v.findViewById(R.id.s_id);
            date = v.findViewById(R.id.date_id);
            layout = v.findViewById(R.id.lin_lay_item);
        }
    }
}
