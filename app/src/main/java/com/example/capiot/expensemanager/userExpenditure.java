package com.example.capiot.expensemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.category;
import static com.example.capiot.expensemanager.R.id.expenseLimit;

public class userExpenditure extends AppCompatActivity {

    public List<String> categoryNames = new ArrayList<String>();
    public List<PieEntry> pieEntries = new ArrayList<>();
    public SQLiteDatabase myDatabase;
    public Spinner dropdown,dropdown2,dropdown3;
    public String selectedUser;
    public boolean callTotalExpense;
    public int totalExpense = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_expenditure);
        myDatabase = this.openOrCreateDatabase("ExpenseManager",MODE_PRIVATE, null);
        initializeDropDown();
        selectedUser = "'" + dropdown.getSelectedItem().toString() + "'";
        initializeExpenseValues();

    }



    private void initializeExpenseValues() {
        TextView expenselimit = (TextView) findViewById(R.id.expenseLimitField);
        TextView amountLeft = (TextView) findViewById(R.id.amountLeftField);

        String user = "'" + dropdown.getSelectedItem().toString() + "'";
        String month = "'" + dropdown2.getSelectedItem().toString() + "'";
        String year = "'" + dropdown3.getSelectedItem().toString() + "'";

        int expenses = 0;

        expenses = returnExpenseLimit(user,month,year);
        expenselimit.setText(String.valueOf(expenses));

        callTotalExpense = true;

        calculateTotalExpense(user,month,year);

        int leftamount = expenses - totalExpense;
        amountLeft.setText(String.valueOf(leftamount));
    }

    public int returnExpenseLimit(String User, String month, String year){
        int expenses = 0;
        ArrayList<String> expenseLimitNotDefined = new ArrayList<String>();
        Log.i("Call to function : ","returnExpenselLimit()");

        categoryNames.clear();
        expenseLimitNotDefined.clear();

        Cursor c = myDatabase.rawQuery("SELECT * FROM Tables", null);
        int tableIndex = c.getColumnIndex("tableName");
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            categoryNames.add(c.getString(tableIndex));
            c.moveToNext();
        }

        for(String category : categoryNames ) {
            Log.i("Category Name:",category);
            Cursor c2 = myDatabase.rawQuery("SELECT ExpenseLimit FROM " + User + " WHERE yr = " + year + " AND month = " + month + " AND Category = " + "'" + category + "'", null);
            if(c2.getCount() == 0){
                Log.i("Info","No Expense Defined");
                expenseLimitNotDefined.add(category);
            }
            else {
                int expenseIndex = c2.getColumnIndex("ExpenseLimit");
                c2.moveToFirst();
                expenses += c2.getInt(expenseIndex);
                Log.i("Expenses : ", String.valueOf(expenses) + "for " + category);
                }
            }
        if(expenseLimitNotDefined.size() > 0) {
            Toast.makeText(this, "Please Define Expense Limit for all Categories", Toast.LENGTH_SHORT).show();
        }
        return expenses;
    }

    private void initializeDropDown() {

        dropdown = (Spinner) findViewById(R.id.spinner);
        Cursor c = myDatabase.rawQuery("SELECT * FROM Users", null); //cursor helps to access result of the query executed
        int nameIndex = c.getColumnIndex("Name");
        ArrayList<String> items = new ArrayList<String>();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            items.add(c.getString(nameIndex));
            c.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown2 = (Spinner) findViewById(R.id.spinner2);
        ArrayList<String> months = new ArrayList<String>();
        for(int i=1;i<=12;i++){
            months.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, months);
        dropdown2.setAdapter(adapter2);

        dropdown3 = (Spinner) findViewById(R.id.spinner3);
        ArrayList<String> years = new ArrayList<String>();
        for(int i=2017;i<= 2020;i++){
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        dropdown3.setAdapter(adapter3);

    }

    public void showPie(View view) {

        pieEntries.clear();
        //Initialize the pie Data Set
        initializeExpenseValues();
        callTotalExpense = false;
        String user = "'" + dropdown.getSelectedItem().toString() + "'";
        String month = "'" + dropdown2.getSelectedItem().toString() + "'";
        String year = "'" + dropdown3.getSelectedItem().toString() + "'";
        calculateTotalExpense(user,month,year);

        PieDataSet dataSet = new PieDataSet(pieEntries, user + "'s Expenses");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);

        //Create Pie Chart
        PieChart piechart = (PieChart) findViewById(R.id.piechart);
        piechart.setData(data);
        piechart.animateY(1000);
        piechart.invalidate();

    }


    public void calculateTotalExpense(String user,String month, String year) {

        categoryNames.clear();
        totalExpense = 0;

        Cursor c = myDatabase.rawQuery("SELECT * FROM Tables", null);
        int tableIndex = c.getColumnIndex("tableName");
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            categoryNames.add(c.getString(tableIndex));
            c.moveToNext();
        }

        for (String Category : categoryNames) {
            String stmt = "SELECT SUM(Amount) FROM " + Category + " WHERE Name = " + user + " AND month = " + month + " AND yr = " + year;
            Log.i("SQl Statement : ", stmt);
            c = myDatabase.rawQuery(stmt, null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                if (c.getInt(0) != 0) {
                    if (callTotalExpense == false) {
                        pieEntries.add(new PieEntry(c.getInt(0), Category));
                    }
                    else{
                        totalExpense += c.getInt(0);
                        Log.i("Total Expense for " + Category + " : ",String.valueOf(c.getInt(0)));
                    }
                }
                Log.i("Total Expense : ",String.valueOf(totalExpense));
                c.moveToNext();
            }
        }
    }


}