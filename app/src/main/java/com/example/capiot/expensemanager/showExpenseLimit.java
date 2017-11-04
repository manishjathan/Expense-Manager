package com.example.capiot.expensemanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.capiot.expensemanager.AddExpenseInCategory.month;
import static com.example.capiot.expensemanager.AddExpenseInCategory.year;

public class showExpenseLimit extends AppCompatActivity {

    SQLiteDatabase myDatabase;
    static ArrayList<String> expenseLimitList = new ArrayList<>();
    static ArrayList<String> monthList = new ArrayList<>();
    static ArrayList<String> yearList = new ArrayList<>();
    static ArrayList<String> categoryList = new ArrayList<>();
    TableLayout table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TableRow row;
        TextView year,month,expense,category;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expense_limit);

        Intent intent = getIntent();
        expenseLimitList = intent.getStringArrayListExtra("expenseLimitList");
        yearList = intent.getStringArrayListExtra("yearList");
        monthList = intent.getStringArrayListExtra("monthList");
        categoryList = intent.getStringArrayListExtra("categoryList");
        //Clear all the Lists

        table = (TableLayout) findViewById(R.id.tableLayout);

        row=new TableRow(this);

        month = new TextView(this);
        month.setText("Month");

        year = new TextView(this);
        year.setText("Year");

        expense = new TextView(this);
        expense.setText(" Expense Limit");

        category = new TextView(this);
        category.setText(" Category");

        row.addView(year);
        row.addView(month);
        row.addView(expense);
        row.addView(category);

        table.addView(row);

        for(int i=0; i < expenseLimitList.size(); i++)
        {
            row=new TableRow(this);

            month = new TextView(this);
            month.setText(monthList.get(i) + "   ");

            year = new TextView(this);
            year.setText(yearList.get(i) + "   ");

            expense = new TextView(this);
            Log.i("Expense",expenseLimitList.get(i));
            expense.setText("  " + expenseLimitList.get(i) + "   ");

            category = new TextView(this);
            category.setText(categoryList.get(i) + "  ");

            row.addView(year);
            row.addView(month);
            row.addView(expense);
            row.addView(category);

            table.addView(row);
        }


    }
}
