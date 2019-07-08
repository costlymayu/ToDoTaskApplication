package com.example.sudam.ToDoApplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Sudam Chole on 7/05/19.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.CustomViewHolder> {

    Context context;
    public static ArrayList<Tasks> tasks;

    public TaskAdapter(Context context, ArrayList<Tasks> tasks) {
        this.context = context;
        this.tasks = tasks;
    }


    @Override
    public TaskAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view_items, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskAdapter.CustomViewHolder holder, int position) {
        Tasks currentTasks = tasks.get(tasks.size() - position - 1);


        holder.taskTittle.setText(currentTasks.taskTittle);
        holder.taskDesc.setText(currentTasks.taskDesc);
        holder.taskDate.setText(currentTasks.taskDate);
        holder.taskTime.setText(currentTasks.taskTime);

        holder.taskComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTasks.taskComment.isEmpty()){
                    Toast.makeText(context,"No comment for this task",Toast.LENGTH_LONG).show();
                }else {
                    Intent intent=new Intent(context,DetailActivity.class);
                    intent.putExtra("comment",currentTasks.taskComment.toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        TextView taskTittle;
        TextView taskDesc;
        TextView taskDate;
        TextView taskTime;
        TextView taskComment;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            taskTittle = (TextView) itemView.findViewById(R.id.txtTaskTittle);
            taskDesc = (TextView) itemView.findViewById(R.id.txtTaskDesc);
            taskDate = (TextView) itemView.findViewById(R.id.txtTaskDate);
            taskTime = (TextView) itemView.findViewById(R.id.txtTaskTime);
            taskComment=(TextView) itemView.findViewById(R.id.txtTaskComment);

        }
    }
}
