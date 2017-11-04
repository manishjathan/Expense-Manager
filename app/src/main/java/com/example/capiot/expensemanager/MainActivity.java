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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.capiot.expensemanager.AddExpense.myDatabase;

public class MainActivity extends AppCompatActivity {

    static SQLiteDatabase myDatabase;
    private String m_Text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating Users Database
        myDatabase = this.openOrCreateDatabase("ExpenseManager",MODE_PRIVATE,null);

        String createTable = "CREATE TABLE IF NOT EXISTS Users(Name VARCHAR,id INTEGER PRIMARY KEY)";
        myDatabase.execSQL(createTable);

    }

    public void addExpense(View view){
        Intent intent = new Intent(getApplicationContext(),AddExpense.class);
        startActivity(intent);
    }



    public void crudUser(View view){

       Intent intent = new Intent (getApplicationContext(),userOperations.class);
        startActivity(intent);

    }

    public void viewExpense(View view){
        Intent intent = new Intent(getApplicationContext(),userExpenditure.class);
        startActivity(intent);
    }
}
