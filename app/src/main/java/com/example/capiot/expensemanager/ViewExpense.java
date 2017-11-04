package com.example.capiot.expensemanager;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class ViewExpense extends AppCompatActivity {
    static ArrayList<String> yearList = new ArrayList<>();
    static ArrayList<String> monthList = new ArrayList<>();
    static ArrayList<String> dayList = new ArrayList<>();

    static ArrayList<String> tagList = new ArrayList<>();
    static ArrayList<String> amountList = new ArrayList<>();
    static ArrayList<String> descList = new ArrayList<>();
    static ArrayList<String> nameList = new ArrayList<>();

    TableLayout table;
    static String Category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TableRow row;
        TextView year,month,day,tag,amount,desc,name;

        double totalamt = 0.0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        Intent intent = getIntent();

        Category = intent.getStringExtra("Category");

        yearList = intent.getStringArrayListExtra("YearsList");
        monthList = intent.getStringArrayListExtra("MonthsList");
        dayList = intent.getStringArrayListExtra("DaysList");

        tagList = intent.getStringArrayListExtra("TagsList");
        amountList = intent.getStringArrayListExtra("AmountsList");
        descList = intent.getStringArrayListExtra("DescriptionsList");
        nameList = intent.getStringArrayListExtra("NamesList");

        table = (TableLayout) findViewById(R.id.tableLayout);
        for(int i=0; i < tagList.size(); i++)
        {
            row=new TableRow(this);

            year = new TextView(this);
            year.setText(yearList.get(i));

            month = new TextView(this);
            month.setText(monthList.get(i));

            day = new TextView(this);
            day.setText(dayList.get(i));

            tag = new TextView(this);
            tag.setText(tagList.get(i));

            amount = new TextView(this);
            amount.setText(amountList.get(i));
            totalamt += Double.parseDouble(amountList.get(i));

            desc = new TextView(this);
            desc.setText(descList.get(i));

            name = new TextView(this);
            name.setText(nameList.get(i));

            row.addView(year);
            row.addView(month);
            row.addView(day);

            row.addView(tag);
            row.addView(amount);
            row.addView(desc);
            row.addView(name);

            table.addView(row);
        }

        row = new TableRow(this);

        tag = new TextView(this);
        tag.setText("Total Expense");

        amount = new TextView(this);
        amount.setText(Double.toString(totalamt));

        row.addView(tag);
        row.addView(amount);

        table.addView(row);
    }


    public void showGraph(View view){

        Intent intent = new Intent(getApplicationContext(),Graph.class);
        intent.putExtra("Category",Category);
        startActivity(intent);
    }


}
