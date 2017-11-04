package com.example.capiot.expensemanager;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddExpense extends AppCompatActivity {

    static ArrayList<String> tables = new ArrayList<>();
    static SQLiteDatabase myDatabase;
    ListView listView;
    static ArrayAdapter arrayAdapter;
    String m_Text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        Intent intent = getIntent();

        myDatabase = this.openOrCreateDatabase("ExpenseManager",MODE_PRIVATE,null);
        //myDatabase.execSQL("DROP TABLE HomeExpense");
        //myDatabase.execSQL("DROP TABLE Test");
        //myDatabase.execSQL("DROP TABLE Tables");

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS Tables (id INTEGER PRIMARY KEY,tableName VARCHAR, yr CHAR, month CHAR, day CHAR)");
        //Create all the default tables
        //myDatabase.execSQL("CREATE TABLE IF NOT EXISTS Food (Tag VARCHAR,Amount INTEGER,Description VARCHAR,id INTEGER PRIMARY KEY)");

        listView = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,tables);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            public boolean onItemLongClick(AdapterView<?> adapterView, View  view,int i,long l){

                final int itemToDelete = i;
                new AlertDialog.Builder(AddExpense.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Item")
                        .setMessage("Do you want to delete?")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i){

                                String value = "'" + listView.getItemAtPosition(itemToDelete).toString() + "'";
                                tables.remove(itemToDelete);
                                myDatabase.execSQL("DELETE FROM Tables WHERE tableName = " + value);
                                arrayAdapter.notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("No",null)
                        .show();

                return false;
            }


        });

        try{

            tables.clear();
            Cursor c = myDatabase.rawQuery("Select * FROM Tables",null);
            c.moveToFirst();
            for(int i = 0 ; i < c.getCount(); i++) {
                int tableNameIndex = c.getColumnIndex("tableName");
                tables.add(c.getString(tableNameIndex));
                AddExpense.arrayAdapter.notifyDataSetChanged();
                c.moveToNext();
            }


        }catch(Exception e){

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                Intent intent = new Intent(getApplicationContext(),AddExpenseInCategory.class);
                intent.putExtra("TableId",i);
                startActivity(intent);
               // myDatabase

            }
        });


    }

    public void createNewCategory(View view){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Category");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();

                String tableName = "'"+ m_Text+ "'";

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String date = sdf.format(new Date());

                String yr = "'" + date.substring(0,4) + "'";
                String month = "'" + date.substring(5,7) + "'";
                String day = "'" + date.substring(8,10) + "'";

                String statement = "INSERT into tables(tableName,yr,month,day) VALUES (" + tableName + "," + yr + "," + month + ","+ day + ")";
                myDatabase.execSQL(statement);
                statement = "CREATE TABLE IF NOT EXISTS " + m_Text + "(yr CHAR, month CHAR, day CHAR,Tag VARCHAR,Amount INTEGER,Description VARCHAR,Name VARCHAR,id INTEGER PRIMARY KEY)";
                myDatabase.execSQL(statement);
                tables.add(m_Text);

                AddExpense.arrayAdapter.notifyDataSetChanged();

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
