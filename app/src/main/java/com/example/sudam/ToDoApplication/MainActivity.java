package com.example.sudam.ToDoApplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Sudam Chole on 22/02/19.
 */

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FloatingActionButton fabAddEvent;
    private String taskId;
    RecyclerView recyclerViewEvents;
    TaskAdapter tasksAdapter;
    private static FirebaseDatabase mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabAddEvent = findViewById(R.id.fabAddTask);
        setTitle(getResources().getString(R.string.tasks));

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);

        }
        //sync offline data
        sync();
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        //set up recycler view
        recyclerViewEvents = (RecyclerView) findViewById(R.id.recyclerViewEvents);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerViewEvents.setLayoutManager(manager);
        tasksAdapter = new TaskAdapter(getApplicationContext(), getList());//events);
        recyclerViewEvents.setAdapter(tasksAdapter);
    }


    private void sync(){
    DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
    scoresRef.keepSynced(true);
}

    /**
     * Creates a list of events based on server data and returns it
     *
     * @return arraylist
     */
    private ArrayList<Tasks> getList() {
        final ArrayList<Tasks> currTasks = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tasks");


        ref.orderByChild("Tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currTasks.clear();

                for (DataSnapshot x : dataSnapshot.getChildren()) {

                    Log.d(x.getKey(), x.getValue().toString());

                    Tasks e = new Tasks();
                    e.taskTittle = x.child("taskTittle").getValue(String.class);
                    e.taskDesc = x.child("taskDesc").getValue(String.class);
                    e.taskDate = x.child("taskDate").getValue(String.class);
                    e.taskTime = x.child("taskTime").getValue(String.class);

                    currTasks.add(e);
                    tasksAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return currTasks;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // menu_item click
        switch (item.getItemId()) {
            case R.id.logout:
                auth.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
