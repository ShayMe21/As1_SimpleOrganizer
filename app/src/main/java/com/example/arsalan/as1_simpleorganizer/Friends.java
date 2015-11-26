package com.example.arsalan.as1_simpleorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
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

import java.util.ArrayList;

public class Friends extends Activity {
    private DatabaseManager myFriendsdb;
    private EditText f_name, l_name, age, address;
    private RadioGroup radioGroup;
    private Button add, delete, update, maps;
    private ListView myFriends;
    private boolean recInserted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.friends);
        findViewByIds();    /* Set all views by fetching their IDs */
        setAdapter();       /* Retrieve Friend records from SQL database and show in listview */


        add.setOnClickListener(new View.OnClickListener() {     /* Adding Friend records here */
            @Override
            public void onClick(View v) {
                String fname_value, lname_value, gender_value, address_value;
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    int id = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(id);
                    int radioId = radioGroup.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
                    gender_value = btn.getText().toString();
                } else
                    gender_value = "";

                /*Get all the values of Form filled in */
                fname_value = f_name.getText().toString();
                lname_value = l_name.getText().toString();
                String age_value = age.getText().toString();
                address_value = address.getText().toString();


                if (fname_value.matches("") || lname_value.matches("") || gender_value.matches("")) {     /*Make sure all required fields are filled*/
                    Toast.makeText(Friends.this, "Please fill all the boxes!", Toast.LENGTH_SHORT).show();
                } else {    /*Add the information to the sql table as record*/
                    recInserted = myFriendsdb.addRow(fname_value, lname_value, gender_value, age_value, address_value);
                    if (recInserted) {      /* Toast message to show successful record inserted */
                        Toast.makeText(getApplicationContext(), "Record Inserted Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                setAdapter();    /* Refresh listview after added new records */
            }
        });
        myFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {    /* Updating existing friends records here*/
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = myFriends.getItemAtPosition(position).toString();
                String[] parts = item.split("\\s+");       /* Separate the listview Item into first name and last name */
                String first = parts[0];
                String second = parts[1];
                f_name.setText(myFriendsdb.retrieveARowInfo(first, second).get(0));
                l_name.setText(myFriendsdb.retrieveARowInfo(first, second).get(1));
                if (myFriendsdb.retrieveARowInfo(first, second).get(2).matches("Male")) {
                    radioGroup.check(R.id.radioButton);
                } else {
                    radioGroup.check(R.id.radioButton2);
                }
                age.setText(myFriendsdb.retrieveARowInfo(first, second).get(3));
                address.setText(myFriendsdb.retrieveARowInfo(first, second).get(4));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {  /* Update Existing records */
            @Override
            public void onClick(View v) {
                String currentFirstName, currentLastName, currentGender, currentAge, currentAddress;
                currentFirstName = f_name.getText().toString();
                currentLastName = l_name.getText().toString();
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    int id = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(id);
                    int radioId = radioGroup.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
                    currentGender = btn.getText().toString();
                } else
                    currentGender = "";
                currentAge = age.getText().toString();
                currentAddress = address.getText().toString();

                if (currentFirstName.matches("") || currentLastName.matches("") || currentGender.matches("")) {     /*Make sure required fields are filled*/
                    Toast.makeText(Friends.this, "Select a record to update please.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean recUpdated = myFriendsdb.UpdateRow(currentFirstName, currentLastName, currentGender, currentAge, currentAddress);
                    if (recUpdated) {      /* Toast message to show successful record update */
                        Toast.makeText(getApplicationContext(), "Record updated Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                setAdapter(); /* Refresh the friends list */


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {      /* Deleting Friends here from listview and Database */
            @Override
            public void onClick(View v) {
                final SparseBooleanArray checked = myFriends.getCheckedItemPositions();

                if (myFriends.getCount() > 0) {
                    for (int i = 0; i < checked.size(); i++) {
                        if (checked.valueAt(i)) {
                            String item = myFriends.getAdapter().getItem(
                                    checked.keyAt(i)).toString();
                            String[] parts = item.split("\\s+");       /* Separate the listview Item into first name and last name */
                            String first = parts[0];
                            String second = parts[1];
                            myFriendsdb.deleteRow(first, second);       /* Delete the row corresponding to the two PKs (firstName, lastName) from sql database */
                        }
                    }
                    setAdapter();
                    Toast.makeText(getApplicationContext(), "Record(s) Deleted Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No records to delete!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   /* Open Google maps activity to show the friend's address */
                Intent intent = new Intent(Friends.this, MapsActivity.class);
                String address_details = address.getText().toString();
                intent.putExtra("Address", address_details);    /* Put the address with the intent to send */
                startActivity(intent);
            }
        });
    }

    public void setAdapter(){   /* Refresh the list of friends */
        myFriendsdb = new DatabaseManager(Friends.this);
        myFriendsdb.openReadable();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Friends.this, android.R.layout.simple_list_item_multiple_choice, myFriendsdb.retrieveRows());
        myFriends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myFriends.setAdapter(adapter);
    }

    public void findViewByIds(){    /* Get all views IDs here */
        f_name = (EditText) findViewById(R.id.firstName);
        l_name = (EditText) findViewById(R.id.lastName);
        age = (EditText) findViewById(R.id.age);
        address = (EditText) findViewById(R.id.addr);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        add = (Button) findViewById(R.id.BtnAddFriend);
        delete = (Button) findViewById(R.id.BtnDelete);
        update = (Button) findViewById(R.id.BtnUpdate);
        myFriends = (ListView) findViewById(R.id.listView);
        maps = (Button)findViewById(R.id.BtnShowMap);
    }
}


