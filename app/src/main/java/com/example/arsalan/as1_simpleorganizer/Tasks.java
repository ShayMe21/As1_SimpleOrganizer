package com.example.arsalan.as1_simpleorganizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Tasks extends Activity {
    private EditText taskName, taskLocation;
    private Button add, update;
    private DatabaseManager2 myTasksdb;
    private RadioGroup radiogroup;
    private ListView taskList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tasks);
        findViewByIds();
        setAdapter();

        add.setOnClickListener(new View.OnClickListener() {      /*Adding Task records here*/
            @Override
            public void onClick(View v) {
                String task_Name, task_Location, task_Status;
                if (radiogroup.getCheckedRadioButtonId() != -1) {
                    int id = radiogroup.getCheckedRadioButtonId();
                    View radioButton = radiogroup.findViewById(id);
                    int radioId = radiogroup.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                    task_Status = (String) btn.getText();
                } else
                    task_Status = "";


                task_Name = taskName.getText().toString();
                task_Location = taskLocation.getText().toString();


                if (task_Name.matches("") || task_Status.matches("")) {     /*Make sure all fields are filled*/
                    Toast.makeText(Tasks.this, "Please fill all the boxes!", Toast.LENGTH_SHORT).show();
                } else {    /*Add the information to the sql table as record */
                    boolean recInserted = myTasksdb.addRowTask(task_Name, task_Location, task_Status);
                    if (recInserted) {
                        Toast.makeText(getApplicationContext(), "Record Inserted Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                setAdapter();     /*Refresh listview after added new records*/
            }
        });

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = taskList.getItemAtPosition(position).toString();
                String[] parts = item.split("\\s+");       /* Extract the task name from listview selection */
                String first = parts[0];
                taskName.setText(myTasksdb.retrieveARow(first).get(0));
                taskLocation.setText(myTasksdb.retrieveARow(first).get(1));
                if (myTasksdb.retrieveARow(first).get(2).matches("Completed")) {
                    radiogroup.check(R.id.radioButton_com);
                } else {
                    radiogroup.check(R.id.radioButton_uncom);
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {  /* Update existing task */
            @Override
            public void onClick(View v) {
                String currentName, currentLocation, currentStatus;
                currentName = taskName.getText().toString();
                currentLocation = taskLocation.getText().toString();
                if (radiogroup.getCheckedRadioButtonId() != -1) {
                    int id = radiogroup.getCheckedRadioButtonId();
                    View radioButton = radiogroup.findViewById(id);
                    int radioId = radiogroup.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                    currentStatus = btn.getText().toString();
                } else
                    currentStatus = "";

                if (currentName.matches("") || currentStatus.matches("")) {     /*Make sure all fields are filled*/
                    Toast.makeText(Tasks.this, "Select a task to update please.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean recUpdated = myTasksdb.updateTask(currentName, currentLocation, currentStatus);
                    if (recUpdated) {      /* Toast message to show successful record update */
                        Toast.makeText(getApplicationContext(), "Record updated Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                setAdapter(); /* Refresh the tasks list */

            }
        });

    }


    public void findViewByIds(){
        taskName = (EditText)findViewById(R.id.taskName);
        taskLocation = (EditText) findViewById(R.id.taskLocation);
        add = (Button) findViewById(R.id.BtnAddTask);
        update = (Button) findViewById(R.id.BtnUpdateTask);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup2);
        taskList = (ListView) findViewById(R.id.listView2);
    }
    public void setAdapter(){   /* Refresh the list of tasks */
        myTasksdb = new DatabaseManager2(Tasks.this);
        myTasksdb.openReadable();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Tasks.this, android.R.layout.simple_list_item_multiple_choice, myTasksdb.retrieveRowsTasks());
        taskList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        taskList.setAdapter(adapter);
    }

}
