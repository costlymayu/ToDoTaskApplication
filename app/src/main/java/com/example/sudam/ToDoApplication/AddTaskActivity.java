package com.example.sudam.ToDoApplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Sudam Chole on 22/02/19.
 */

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText editTextTaskTittle,editTextTaskDesc,editTextDate,editTextTime;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String eventId;
    final Calendar myCalendar = Calendar.getInstance();
    private CheckBox addReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTextTaskTittle=findViewById(R.id.editTextTaskTittle);
        editTextTaskDesc=findViewById(R.id.editTextDesc);
        editTextDate=findViewById(R.id.editTextDate);
        editTextTime=findViewById(R.id.editTextTime);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        addReminder=findViewById(R.id.checkBoxAddReminder);

      /*  addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        // get reference to 'Tasks' node
        mFirebaseDatabase = mFirebaseInstance.getReference("Tasks");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Add Tasks");

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }


        };
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddTaskActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });
        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTextTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


    }
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }
    public void onClick(View view) {
        if (view.getId() == R.id.buttonAddEvent) {
            //processing data and sending to server
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(ProgressBar.VISIBLE);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.addingTask), Toast.LENGTH_SHORT).show();
            if(!editTextTaskTittle.getText().toString().isEmpty()&&!editTextTaskDesc.getText().toString().isEmpty()
                    &&!editTextDate.getText().toString().isEmpty()&&!editTextTime.getText().toString().isEmpty()){
                createEvent(editTextTaskTittle.getText().toString().trim(),editTextTaskDesc.getText().toString().trim(),editTextDate.getText().toString().trim(),editTextTime.getText().toString().trim());
                pb.setVisibility(ProgressBar.GONE);
                if (((CheckBox) view).isChecked()) {
                    addReminderInCalendar(editTextTaskTittle.getText().toString());
                }
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.taskAdded), Toast.LENGTH_SHORT).show();
                editTextTaskTittle.setText("");
                editTextTaskDesc.setText("");
                editTextDate.setText("");
                editTextTime.setText("");
                this.finish();
            } else {
                Toast.makeText(getApplicationContext(), "All fields are mandatory.", Toast.LENGTH_SHORT).show();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        }
    }

    /**
     * Creating new event node under 'Tasks'
     */
    private void createEvent(String eventName, String eventAgenda,String eventDate,String eventTime) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(eventId)) {
            eventId = mFirebaseDatabase.push().getKey();
        }

        Tasks user = new Tasks(eventName, eventAgenda,eventDate,eventTime);

        mFirebaseDatabase.child(eventId).setValue(user);
   }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /** Adds Events and Reminders in Calendar. */
    private void addReminderInCalendar(String tittle) {
        Calendar cal = Calendar.getInstance();
        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
        ContentResolver cr = getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();

        /** Inserting an event in calendar. */
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, tittle);
       // values.put(CalendarContract.Events.DESCRIPTION, );
        values.put(CalendarContract.Events.ALL_DAY, 0);
        // event starts at 11 minutes from now
        values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis() + 1 * 60 * 1000);
        // ends 60 minutes from now
        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis() + 2 * 60 * 1000);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri event = cr.insert(EVENTS_URI, values);

        // Display event id.
        Toast.makeText(getApplicationContext(), "Task added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        /** Adding reminder for event added. */
        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 10);
        cr.insert(REMINDERS_URI, values);
    }

    /** Returns Calendar Base URI, supports both new and old OS. */
    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }

}
