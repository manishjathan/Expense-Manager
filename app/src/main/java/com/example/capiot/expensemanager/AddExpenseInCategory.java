package com.example.capiot.expensemanager;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AddExpenseInCategory extends AppCompatActivity {

    String tableName;

    static ArrayList<String> tags = new ArrayList<>();
    static ArrayList<String> amounts = new ArrayList<>();
    static ArrayList<String> descriptions = new ArrayList<>();
    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<String> year = new ArrayList<>();
    static ArrayList<String> month = new ArrayList<>();
    static ArrayList<String> day = new ArrayList<>();

    static SQLiteDatabase myDatabase;
    ArrayAdapter arrayAdapter;
    static Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_in_category);
        // to get ID of the selected item of the list
        Intent intent = getIntent();
        int tableId = intent.getIntExtra("TableId",-1);
        if(tableId != -1){
            tableName = AddExpense.tables.get(tableId);
        }
        myDatabase = this.openOrCreateDatabase("ExpenseManager",MODE_PRIVATE,null);
        //to initialize the dropdown with the table values
         initializeDropDown();
    }

    public void initializeDropDown(){

        dropdown = (Spinner)findViewById(R.id.spinner1);
        Cursor c = AddExpense.myDatabase.rawQuery("SELECT * FROM Users",null); //cursor helps to access result of the query executed
        int nameIndex = c.getColumnIndex("Name");
        ArrayList<String> items = new ArrayList<String>();
        c.moveToFirst();
        for(int i = 0 ; i < c.getCount();i++){
            items.add(c.getString(nameIndex));
            c.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

    }

    public void insertIntoTable(View view) {

        EditText editText1 = (EditText) findViewById(R.id.editText1);
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        EditText editText3 = (EditText) findViewById(R.id.editText3);

        Log.i("Edit Text's ", editText1.getText().toString() + " " + editText2.getText().toString() + " " + editText3.getText().toString());

        if (editText1.getText().toString().isEmpty() || editText2.getText().toString().isEmpty() || editText3.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show();
        } else {
            final String tag = "'" + editText1.getText().toString() + "'";
            final Integer amt = Integer.parseInt(editText2.getText().toString());
            final String desc = "'" + editText3.getText().toString() + "'";
            final String name = "'" + dropdown.getSelectedItem().toString() + "'";


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String date = sdf.format(new Date());

            final String yr = "'" + date.substring(0, 4) + "'";
            final String month = "'" + date.substring(5, 7) + "'";
            final String day = "'" + date.substring(8, 10) + "'";


            int currtotal = calculateTotalExpense(name);
            int expenseLimit = getExpenseLimit(name, yr, month,"'"+tableName+"'");
            if (expenseLimit != -1) {

                Log.i("Amount Left", String.valueOf(expenseLimit - (currtotal + amt)));

                if ((expenseLimit - (currtotal + amt)) >= 0) {
                    String statement1 = "INSERT into " + tableName + "(yr,month,day,Tag,Amount,Description,Name) VALUES (" + yr + "," + month + "," + day + "," + tag + "," + amt + "," + desc + "," + name + ")";
                    AddExpense.myDatabase.execSQL(statement1);
                    Toast.makeText(this, "Row inserted", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Add Expense");
                    Context context = builder.getContext();
                    builder = new AlertDialog.Builder(context);

                    builder.setMessage("Current Expense greater than total Expense limit,Do you want to add?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String statement1 = "INSERT into " + tableName + "(yr,month,day,Tag,Amount,Description,Name) VALUES (" + yr + "," + month + "," + day + "," + tag + "," + amt + "," + desc + "," + name + ")";
                                    AddExpense.myDatabase.execSQL(statement1);
                                }
                            })

                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
            else{
                Toast.makeText(this, "Please Define Expense Limit for " + tableName + " in month " + month +  " Year " + yr, Toast.LENGTH_SHORT).show();
            }

            }
    }


    private int getExpenseLimit(String name,String yr, String month, String category) {

        String stmt = "SELECT ExpenseLimit FROM " + name + " WHERE yr = " + yr + " AND month = " + month + " AND Category = " + category;
        Log.i("SQL statement",stmt);
        Cursor c = myDatabase.rawQuery(stmt,null);
        if(c.getCount() == 0){
            return -1;
        }
        else {
            c.moveToFirst();
            return c.getInt(0);
        }
    }

    public void viewExpense(View view){
        try{
            AddExpenseInCategory.tags.clear();
            AddExpenseInCategory.amounts.clear();
            AddExpenseInCategory.descriptions.clear();
            AddExpenseInCategory.names.clear();
            AddExpenseInCategory.year.clear();
            AddExpenseInCategory.month.clear();
            AddExpenseInCategory.day.clear();

            Cursor c = AddExpense.myDatabase.rawQuery("SELECT * FROM " + tableName,null); //cursor helps to access result of the query executed

            int yearIndex = c.getColumnIndex("yr");
            int monthIndex = c.getColumnIndex("month");
            int dayIndex = c.getColumnIndex("day");

            int tagIndex = c.getColumnIndex("Tag");//accessing index of column name
            int amtIndex = c.getColumnIndex("Amount");
            int descIndex = c.getColumnIndex("Description");//accessing index of column age
            int nameIndex = c.getColumnIndex("Name");

                c.moveToFirst();
                for(int i = 0 ; i < c.getCount();i++){

                    AddExpenseInCategory.year.add(c.getString(yearIndex));
                    AddExpenseInCategory.month.add(c.getString(monthIndex));
                    AddExpenseInCategory.day.add(c.getString(dayIndex));
                    AddExpenseInCategory.tags.add(c.getString(tagIndex));
                    AddExpenseInCategory.amounts.add(Integer.toString(c.getInt(amtIndex)));
                    AddExpenseInCategory.descriptions.add(c.getString(descIndex));
                    AddExpenseInCategory.names.add(c.getString(nameIndex));
                    Log.i("Date"," " + c.getString(yearIndex) + " " + c.getString(monthIndex) + " " + c.getString(dayIndex));
                    c.moveToNext();
                }

            Intent intent = new Intent(getApplicationContext(),ViewExpense.class);
            intent.putExtra("Category",tableName);

            intent.putStringArrayListExtra("YearsList",year);
            intent.putStringArrayListExtra("MonthsList",month);
            intent.putStringArrayListExtra("DaysList",day);

            intent.putStringArrayListExtra("TagsList",tags);
            intent.putStringArrayListExtra("AmountsList",amounts);
            intent.putStringArrayListExtra("DescriptionsList",descriptions);
            intent.putStringArrayListExtra("NamesList",names);
            startActivity(intent);


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void clearAllText(View view){

        EditText editText1 = (EditText)findViewById(R.id.editText1);
        EditText editText2 = (EditText)findViewById(R.id.editText2);
        EditText editText3 = (EditText)findViewById(R.id.editText3);
        //EditText editText4 = (EditText)findViewById(R.id.editText4);

        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
        //editText4.setText("");

    }

    public int calculateTotalExpense(String user){
        List<String> categoryNames = new ArrayList<String>();
        int totalExpense = 0;
        Cursor c = myDatabase.rawQuery("SELECT * FROM Tables", null);
        int tableIndex = c.getColumnIndex("tableName");
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            categoryNames.add(c.getString(tableIndex));
            c.moveToNext();
        }

        for (String Category : categoryNames) {
            String stmt = "SELECT SUM(Amount) FROM " + Category + " WHERE Name = " + user;
            Log.i("SQl Statement : ", stmt);
            c = myDatabase.rawQuery(stmt, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                if (c.getInt(0) != 0) {
                        totalExpense += c.getInt(0);
                }
                c.moveToNext();
            }
        }
        return totalExpense;
    }






}


