package com.example.capiot.expensemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.name;
import static com.example.capiot.expensemanager.AddExpenseInCategory.dropdown;

public class SetExpenseLimits extends AppCompatActivity {

    String selectedUser;
    SQLiteDatabase myDatabase;
    ArrayList<String> categoryNames = new ArrayList<String>();
    Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_expense_limits);
        Intent intent = getIntent();
        selectedUser = intent.getStringExtra("User");
        Toast.makeText(this, "Selected User : " + selectedUser, Toast.LENGTH_SHORT).show();
        myDatabase = this.openOrCreateDatabase("ExpenseManager",MODE_PRIVATE,null);
        initializeDropdown();
    }

    public void initializeDropdown(){

        dropdown = (Spinner)findViewById(R.id.dropdown);
        Cursor c = myDatabase.rawQuery("SELECT * FROM Tables", null);
        int tableIndex = c.getColumnIndex("tableName");
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            categoryNames.add(c.getString(tableIndex));
            c.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        dropdown.setAdapter(adapter);
    }

    public void saveEdit(View view){

        EditText editText1 = (EditText) findViewById(R.id.editText1);
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        EditText editText3 = (EditText) findViewById(R.id.editText3);

        final int expense = Integer.parseInt(editText1.getText().toString());
        Log.i("Expense ",String.valueOf(expense));
        final String month = "'" + editText2.getText().toString() + "'";
        final String year = "'" + editText3.getText().toString() + "'";
        final String category = "'" + dropdown.getSelectedItem().toString() + "'";

        if(editText1.getText().toString().isEmpty() || editText2.getText().toString().isEmpty() || editText3.getText().toString().isEmpty()){
            Toast.makeText(this,"Please Enter all the fields",Toast.LENGTH_SHORT).show();
        }
        else{

            String stmt = "SELECT * FROM " + selectedUser + " WHERE Month = " + month + " AND Yr = " + year + " AND Category = " + category;
            Log.i("Statement to check Edit",stmt);

            Cursor c = myDatabase.rawQuery(stmt, null);
            if(c.getCount() == 0){
                // New Entry into the Database
                // Insert the selected Values into the database
                String statement1 = "INSERT into " + selectedUser + "(ExpenseLimit, Month, Yr, Category) VALUES (" + expense + "," + month + "," + year + "," + category + ")";
                Log.i("Sql statement : ", statement1);
                myDatabase.execSQL(statement1);
                Toast.makeText(this, "New ExpenseLimit Added for Month : " + month + "& Year : " + year, Toast.LENGTH_SHORT).show();
            }
            else{

                new AlertDialog.Builder(SetExpenseLimits.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Edit")
                        .setMessage("Do you want to Edit the Current Information")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i){
                                 String stmt = "UPDATE " + selectedUser + " SET ExpenseLimit = " + expense + " WHERE Month = " + month + " AND Yr = " + year + " AND Category = " + category;
                                 Log.i("Sql Statement : ",stmt);
                                 myDatabase.execSQL(stmt);
                            }

                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i){


                            }

                        })
                        .show();



            }

        }

    }

}
