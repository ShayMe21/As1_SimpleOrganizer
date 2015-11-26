package com.example.arsalan.as1_simpleorganizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Events extends Activity {
    private EditText eventName, eventDate, eventTime, eventLocation;
    private ListView eventsList;
    private Button add, update, delete;
    private DatabaseManager3 myEventsdb;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.events);
        findViewByIds();
        setAdapter();
        MyCalenderDate myCalender = new MyCalenderDate(Events.this, eventDate.getId());

        add.setOnClickListener(new View.OnClickListener() {      /*Adding Events records here*/
            @Override
            public void onClick(View v) {
                String event_name, event_Date, event_Time, event_loc;

                event_name = eventName.getText().toString();
                event_Date = eventDate.getText().toString();
                event_Time = eventTime.getText().toString();
                event_loc = eventLocation.getText().toString();


                if (event_name.matches("") || event_Date.matches("")) {     /*Make sure all fields are filled*/
                    Toast.makeText(Events.this, "Please fill all the boxes!", Toast.LENGTH_SHORT).show();
                } else {    /*Add the information to the sql table as record */
                    boolean recInserted = myEventsdb.addRowEvent(event_name, event_Date, event_Time, event_loc);
                    if (recInserted) {
                        Toast.makeText(getApplicationContext(), "Record Inserted Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                setAdapter();     /*Refresh listview after added new records*/
            }
        });

        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {   /*Show record info once selected */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = eventsList.getItemAtPosition(position).toString();
                String[] parts = item.split("\\s+");       /* Extract the task name from listview selection */
                String first = parts[0];
                eventName.setText(myEventsdb.retrieveARow(first).get(0));
                eventDate.setText(myEventsdb.retrieveARow(first).get(1));
                eventTime.setText(myEventsdb.retrieveARow(first).get(2));
                eventLocation.setText(myEventsdb.retrieveARow(first).get(3));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {  /* Update existing event */
            @Override
            public void onClick(View v) {
                String currentName, currentDate, currentTime, currentLocation;
                currentName = eventName.getText().toString();
                currentDate = eventDate.getText().toString();
                currentTime = eventTime.getText().toString();
                currentLocation = eventLocation.getText().toString();

                if (currentName.matches("") || currentDate.matches("")) {     /*Make sure all fields are filled*/
                    Toast.makeText(Events.this, "Select an event first.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean recUpdated = myEventsdb.updateEvent(currentName, currentDate, currentTime, currentLocation);
                    if (recUpdated) {      /* Toast message to show successful record update */
                        Toast.makeText(getApplicationContext(), "Record updated Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                setAdapter(); /* Refresh the tasks list */

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SparseBooleanArray checked = eventsList.getCheckedItemPositions();

                if (eventsList.getCount() > 0) {
                    for (int i = 0; i < checked.size(); i++) {
                        if (checked.valueAt(i)) {
                            String item = eventsList.getAdapter().getItem(
                                    checked.keyAt(i)).toString();
                            String[] parts = item.split("\\s+");       /* Extract the event name */
                            String name = parts[0];
                            myEventsdb.deleteRow(name);
                        }
                    }
                    setAdapter();
                    Toast.makeText(getApplicationContext(), "Record(s) Deleted Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No records to delete!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void findViewByIds(){
        eventName = (EditText)findViewById(R.id.eventName);
        eventDate = (EditText) findViewById(R.id.eventDate);
        eventTime = (EditText) findViewById(R.id.eventTime);
        eventLocation = (EditText) findViewById(R.id.eventLocation);
        add = (Button) findViewById(R.id.btnAddEvent);
        update = (Button) findViewById(R.id.btnEditEvent);
        delete = (Button) findViewById(R.id.btnDeleteEvent);
        eventsList = (ListView) findViewById(R.id.listView3);
    }
    public void setAdapter(){   /* Refresh the list of tasks */
        myEventsdb = new DatabaseManager3(Events.this);
        myEventsdb.openReadable();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Events.this, android.R.layout.simple_list_item_multiple_choice, myEventsdb.retrieveRowsEvents());
        eventsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        eventsList.setAdapter(adapter);
    }




}
