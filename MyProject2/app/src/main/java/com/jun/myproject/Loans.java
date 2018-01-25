package com.jun.myproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Loans extends AppCompatActivity {
    private EditText loaninput;
    private EditText loanquantity;
    EditText loanprice;
    TextView loanlist;
    TextView loantotal;
    private Context instance;

    private Database db;

    public static final String TAG = "Database";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        instance = this;

        db = new Database(this);

        loaninput = (EditText) findViewById(R.id.nameinput);
        loanquantity = (EditText) findViewById(R.id.quantity);
        loanprice = (EditText) findViewById(R.id.price);
        loanlist = (TextView) findViewById(R.id.list);
        loanlist.setMovementMethod(new ScrollingMovementMethod());
        loantotal = (TextView) findViewById(R.id.total);
        printLoansDatabase();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

    }


    private boolean validInput() {
        try {
            Integer.parseInt(loanquantity.getText().toString());
            Float.parseFloat(loanprice.getText().toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    //Add a product to the database
    public void addButtonClicked(View view) {

        if (!validInput()) {
            Toast.makeText(instance, "Invalid inputs", Toast.LENGTH_SHORT).show();
            return;
        }
        LoansModel receipt = new LoansModel();
        receipt.set_receiptname(loaninput.getText().toString());
        receipt.setprice(Float.parseFloat(loanprice.getText().toString()));
        receipt.setQuantity(Integer.parseInt(loanquantity.getText().toString()));
        db.open();
        db.addReceipt(receipt);
        db.close();
        printLoansDatabase();
    }


    //Delete items
    public void deleteButtonClicked(View view) {
        String inputText = loaninput.getText().toString();
        db.open();
        db.deleteReceipt(inputText);
        db.close();
        printLoansDatabase();
    }

    public float getLoanbalance() {

        float loanBalance = 0;
        db.open();
        Cursor c = db.findByType(LoansModel.TYPE);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                loanBalance += c.getFloat(c.getColumnIndex("price"));
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        Log.i(TAG, "return balance called");

        return loanBalance;
    }


    public String loanDBToString() {
        String dbString = "";

        //Cursor points to a location in your results
        db.open();
        Cursor c = db.findByType(LoansModel.TYPE);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("receiptname")) != null) {
                dbString += c.getString(c.getColumnIndex("receiptname"));
                dbString += "/Months:";
                dbString += c.getString(c.getColumnIndex("quantity"));
                dbString += "/$:";
                dbString += c.getString(c.getColumnIndex("price"));
                dbString += "/ID: ";
                dbString += c.getString(c.getColumnIndex("_id"));
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    //Print the database
    public void printLoansDatabase() {
        String loanDBString = this.loanDBToString();
        loanlist.setText(loanDBString);
        String loanBalance = new Float(this.getLoanbalance()).toString();
        loantotal.setText(loanBalance);
        loaninput.setText("");
        loanquantity.setText("");
        loanprice.setText("");
    }


}