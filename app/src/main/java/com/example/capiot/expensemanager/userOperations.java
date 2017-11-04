package com.example.capiot.expensemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.capiot.expensemanager.AddExpenseInCategory.myDatabase;
import static com.example.capiot.expensemanager.R.id.listView;
import static java.security.AccessController.getContext;

public class userOperations extends AppCompatActivity {


    ArrayList<String> users = new ArrayList<String>();
    SQLiteDatabase myDatabase;
    ListView listView;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> expenseLimitList = new ArrayList<>();
    static ArrayList<String> monthList = new ArrayList<>();
    static ArrayList<String> yearList = new ArrayList<>();
    static ArrayList<String> categoryList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_operations2);


        myDatabase = this.openOrCreateDatabase("ExpenseManager",MODE_PRIVATE,null);
        //myDatabase.execSQL("DROP TABLE Users");
        Cursor c = myDatabase.rawQuery("SELECT * FROM Users",null);
        int nameIndex = c.getColumnIndex("Name");
        c.moveToFirst();
        for(int i = 0 ; i < c.getCount(); i++){
            users.add(c.getString(nameIndex));
            c.moveToNext();
        }
        listView = (ListView)findViewById(R.id.userList);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,users);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){

                expenseLimitList.clear();
                monthList.clear();
                yearList.clear();
                categoryList.clear();

                String tableName = String.valueOf(listView.getItemAtPosition(i));
                Intent intent = new Intent(getApplicationContext(),showExpenseLimit.class);


                Cursor c = myDatabase.rawQuery("SELECT * FROM " + tableName,null); //cursor helps to access result of the query executed

                int expenseIndex = c.getColumnIndex("ExpenseLimit");
                int yearIndex = c.getColumnIndex("Yr");
                int monthIndex = c.getColumnIndex("Month");
                int categoryIndex = c.getColumnIndex("Category");

                c.moveToFirst();
                for( int j = 0 ; j < c.getCount() ; j++){
                    Log.i("ExpenseLimit Row", c.getString(expenseIndex) +  " " + c.getString(monthIndex) + " " + c.getString(yearIndex) +  " " + c.getString(categoryIndex));
                    expenseLimitList.add(Integer.toString(c.getInt(expenseIndex)));
                    monthList.add(c.getString(monthIndex));
                    yearList.add(c.getString(yearIndex));
                    categoryList.add(c.getString(categoryIndex));
                    c.moveToNext();
                }

                intent.putStringArrayListExtra("expenseLimitList",expenseLimitList);
                intent.putStringArrayListExtra("yearList",yearList);
                intent.putStringArrayListExtra("monthList",monthList);
                intent.putStringArrayListExtra("categoryList",categoryList);
                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            public boolean onItemLongClick(AdapterView<?> adapterView, View  view,int i,long l){

                final int itemSelected = i;

                new AlertDialog.Builder(userOperations.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Make Changes")
                        .setMessage("Add/Edit will allow to add a new Expense Limit for the user. Delete will result in deleting the User")
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i){
                                String value = "'" + listView.getItemAtPosition(itemSelected).toString() + "'";
                                users.remove(itemSelected);
                                myDatabase.execSQL("DELETE FROM Users WHERE Name = " + value);
                                arrayAdapter.notifyDataSetChanged();
                            }

                        })

                        .setNegativeButton("Add/Edit",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i){
                                String createTable = "CREATE TABLE IF NOT EXISTS "+ listView.getItemAtPosition(itemSelected) +"(Id INTEGER PRIMARY KEY, ExpenseLimit INTEGER, Month CHAR, Yr CHAR, Category CHAR)";
                                Log.i("SQL statement : ", createTable);
                                myDatabase.execSQL(createTable);
                                // give a call to a New Activity
                                Intent intent = new Intent(getApplicationContext(),SetExpenseLimits.class);
                                String user = listView.getItemAtPosition(itemSelected).toString();
                                intent.putExtra("User",user);
                                startActivity(intent);

                            }

                        })
                        .show();

                return false;
            }


        });

    }

    public void addUser(View view) {
        String m_text;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add User");

        Context context = builder.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText input1 = new EditText(context);
        input1.setHint("User Name");
        layout.addView(input1);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String m_Text = input1.getText().toString();
                String statement = "INSERT into Users (Name) VALUES (" + "'" + m_Text + "'" + ")";
                myDatabase.execSQL(statement);
                users.add(m_Text);
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(userOperations.this, "Added User", Toast.LENGTH_SHORT).show();
                Cursor c = myDatabase.rawQuery("Select * FROM Users", null);
                int nameIndex = c.getColumnIndex("Name");

                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    Log.i("Name : ", c.getString(nameIndex));
                    c.moveToNext();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }




}
