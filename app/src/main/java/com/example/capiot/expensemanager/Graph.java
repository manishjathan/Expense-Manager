package com.example.capiot.expensemanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.capiot.expensemanager.AddExpenseInCategory.myDatabase;

public class Graph extends AppCompatActivity {

    Spinner dropdown, dropdown2,dropdown3,dropdown4 ;
    String Category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        myDatabase = this.openOrCreateDatabase("ExpenseManager", MODE_PRIVATE, null);

        Intent intent = getIntent();
        Category = intent.getStringExtra("Category");

        initializeDropDown();

    }


    public void initializeDropDown() {

        dropdown = (Spinner) findViewById(R.id.spinner1);
        Cursor c = AddExpense.myDatabase.rawQuery("SELECT * FROM Users", null); //cursor helps to access result of the query executed
        int nameIndex = c.getColumnIndex("Name");
        ArrayList<String> items = new ArrayList<String>();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            items.add(c.getString(nameIndex));
            c.moveToNext();
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter1);

        dropdown2 = (Spinner) findViewById(R.id.spinner2);
        ArrayList<String> hierarchy = new ArrayList<String>();
        hierarchy.add("day");
        hierarchy.add("month");
        hierarchy.add("yr");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, hierarchy);
        dropdown2.setAdapter(adapter);


        dropdown3 = (Spinner) findViewById(R.id.spinner3);
        ArrayList<String> months = new ArrayList<String>();
        for(int i=1;i<=12;i++){
            months.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, months);
        dropdown3.setAdapter(adapter2);

        dropdown4 = (Spinner) findViewById(R.id.spinner4);
        ArrayList<String> years = new ArrayList<String>();
        for(int i=2017;i<= 2020;i++){
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        dropdown4.setAdapter(adapter3);
    }


    public void compute(View view) {

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();


        String uname = "'" + dropdown.getSelectedItem().toString() + "'";
        String hierarchy = dropdown2.getSelectedItem().toString();
        String month = "'" + dropdown3.getSelectedItem().toString() + "'";
        String year = "'" + dropdown4.getSelectedItem().toString() + "'";

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getData(uname,hierarchy,month,year));

        graph.addSeries(series);
        if(hierarchy == "day") {
            graph.getViewport().setMinX(1);
            graph.getViewport().setMaxX(31);
            graph.getViewport().setMinY(1000);
            graph.getViewport().setMaxY(50000);
        }
        else if(hierarchy == "month") {
            graph.getViewport().setMinX(1);
            graph.getViewport().setMaxX(12);
            graph.getViewport().setMinY(1000);
            graph.getViewport().setMaxY(100000);
        }
        else if(hierarchy == "yr") {
            graph.getViewport().setMinX(2017);
            graph.getViewport().setMaxX(2020);
            graph.getViewport().setMinY(1000);
            graph.getViewport().setMaxY(100000);
        }
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        int aggregateValues[] = calculateMaxMinAvg(uname,hierarchy,month,year);

        /*TextView maxExpense = (TextView) findViewById(R.id.maxExpense);
        maxExpense.setText(String.valueOf(aggregateValues[0]));

        TextView minExpense = (TextView) findViewById(R.id.minExpense);
        minExpense.setText(String.valueOf(aggregateValues[1]));

        TextView avgExpense = (TextView) findViewById(R.id.avgExpense);
        avgExpense.setText(String.valueOf(aggregateValues[2]));
        */

    }

    private DataPoint[] getData(String uname,String hierarchy,String month,String year){

        DataPoint[] dp = new DataPoint[0];
        if(hierarchy == "day"){

          String statement = "SELECT day,month,yr,SUM(Amount) FROM " + Category + " WHERE Name = " + uname + " AND month = " + month + " AND yr = " + year + " GROUP BY day";
          Log.i("Statement for Day : ",statement);
          Cursor c = myDatabase.rawQuery(statement, null);
          dp = new DataPoint[c.getCount()];
          c.moveToFirst();
          for(int i = 0; i < c.getCount(); i++){
              Log.i("Day Amount : ",c.getString(0)+ " " + c.getString(3));
              dp[i] = new DataPoint(Integer.parseInt(c.getString(0)),c.getInt(3));
              c.moveToNext();
          }
          return dp;
      }
      else if(hierarchy == "month"){
          String statement = "SELECT month,yr,SUM(Amount) FROM " + Category + " WHERE Name = " + uname +  " AND yr = " + year + " GROUP BY month";
          Log.i("Statement for Month : ", statement);
          Cursor c = myDatabase.rawQuery(statement, null);
            dp = new DataPoint[c.getCount()];
            c.moveToFirst();
          for(int i = 0; i < c.getCount(); i++){
              Log.i("Month Amount : ",c.getString(0)+ " " + c.getString(2));
              dp[i] = new DataPoint(Integer.parseInt(c.getString(0)),c.getInt(2));
              c.moveToNext();
          }
            return dp;
      }
      else if(hierarchy == "yr"){
          String statement = "SELECT yr,SUM(Amount) FROM " + Category + " WHERE Name = " + uname +"GROUP BY yr";
          Log.i("Statement for Year : ", statement);
          Cursor c = myDatabase.rawQuery(statement, null);
          c.moveToFirst();
          for(int i = 0; i < c.getCount(); i++){
              Log.i("Year Amount : ",c.getString(0)+ " " + c.getString(1));
              c.moveToNext();
          }
      }

        return dp;
    }

    private int[] calculateMaxMinAvg(String uname,String hierarchy,String month, String year){
        int maxminavg[] = {0,0,0};

        String statement1 = null;
        String statement2 = null;
        String  statement3 = null;

        if(hierarchy == "day") {
            statement1 = "SELECT day,month,yr,MAX(Amount) FROM " + Category + " WHERE Name = " + uname + " AND month = " + month + " AND yr = " + year + " GROUP BY day ORDER BY day DESC";
            statement2 = "SELECT day,month,yr,MIN(Amount) FROM " + Category + " WHERE Name = " + uname + " AND month = " + month + " AND yr = " + year + " GROUP BY day ORDER BY day ASC";
            statement3 = "SELECT AVG(Amount) FROM " + Category + " WHERE Name = " + uname + " AND month = " + month + " AND yr = " + year + " GROUP BY month";

        }
        else if(hierarchy == "month") {
            statement1 = "SELECT day,month,yr,MAX(Amount) FROM " + Category + " WHERE Name = " + uname +  " AND yr = " + year + " GROUP BY month ORDER BY month DESC";
            statement2 = "SELECT day,month,yr,MIN(Amount) FROM " + Category + " WHERE Name = " + uname +  " AND yr = " + year + " GROUP BY month ORDER BY month ASC";
            statement3 = "SELECT AVG(Amount) FROM " + Category + " WHERE Name = " + uname +  " AND yr = " + year + " GROUP BY yr";
        }
        else if(hierarchy  == "yr"){
            statement1 = "SELECT day,month,yr,MAX(Amount),MIN(Amount),AVG(Amount) FROM " + Category + " WHERE Name = " + uname +"GROUP BY yr";

        }
        Cursor c1 = myDatabase.rawQuery(statement1, null);
        c1.moveToFirst();
            String day = c1.getString(0);
            month = c1.getString(1);
            year = c1.getString(2);

            Log.i("Max : ",day + " " + month + " " + year + " " + String.valueOf(c1.getInt(3)));
            maxminavg[0] = c1.getInt(3);

        Cursor c2 = myDatabase.rawQuery(statement2, null);
        c2.moveToFirst();
        day = c2.getString(0);
        month = c2.getString(1);
        year = c2.getString(2);

            Log.i("Min : ",day + " " + month + " " + year + " " + String.valueOf(c2.getInt(3)));
            maxminavg[1] = c2.getInt(3);

        Cursor c3 = myDatabase.rawQuery(statement3, null);
        c3.moveToFirst();
            Log.i("Avg : ",String.valueOf(c3.getInt(0)));
            maxminavg[2] = c3.getInt(0);


        /*
            Log.i("Min : ",day + " " + month + " " + year + " " + String.valueOf(c.getInt(4)));
            maxminavg[1] = c.getInt(1);
            Log.i("Avg : ",day + " " + month + " " + year + " " + String.valueOf(c.getInt(5)));
            maxminavg[2] = c.getInt(2);
        */

        return maxminavg;
    }

}

