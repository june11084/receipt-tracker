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


public class Rent extends AppCompatActivity {
    private EditText rentinput;
    private EditText monthsrented;
    EditText rentprice;
    TextView rentlist;
    TextView renttotal;
    private Context instance;

    private Database db;

    public static final String TAG = "Database";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        instance = this;

        db = new Database(this);

        rentinput = (EditText) findViewById(R.id.nameinput);
        monthsrented = (EditText) findViewById(R.id.quantity);
        rentprice = (EditText) findViewById(R.id.price);
        rentlist = (TextView) findViewById(R.id.list);
        rentlist.setMovementMethod(new ScrollingMovementMethod());
        renttotal = (TextView) findViewById(R.id.total);
        printRentDatabase();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

    }


    private boolean validInput() {
        try {
            Integer.parseInt(monthsrented.getText().toString());
            Float.parseFloat(rentprice.getText().toString());
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
        RentModel receipt = new RentModel();
        receipt.set_receiptname(rentinput.getText().toString());
        receipt.setprice(Float.parseFloat(rentprice.getText().toString()));
        receipt.setQuantity(Integer.parseInt(monthsrented.getText().toString()));
        db.open();
        db.addReceipt(receipt);
        db.close();
        printRentDatabase();
    }


    //Delete items
    public void deleteButtonClicked(View view) {
        String inputText = rentinput.getText().toString();
        db.open();
        db.deleteReceipt(inputText);
        db.close();
        printRentDatabase();
    }

    public float getRentbalance() {

        float rentBalance = 0;
        db.open();
        Cursor c = db.findByType(RentModel.TYPE);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                rentBalance += c.getFloat(c.getColumnIndex("price"));
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        Log.i(TAG, "return rent called");

        return rentBalance;
    }


    public String rentDBToString() {
        String dbString = "";

        //Cursor points to a location in your results
        db.open();
        Cursor c = db.findByType(RentModel.TYPE);
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
    public void printRentDatabase() {
        String rentDBString = this.rentDBToString();
        rentlist.setText(rentDBString);
        String rentBalance = new Float(this.getRentbalance()).toString();
        renttotal.setText(rentBalance);
        rentinput.setText("");
        monthsrented.setText("");
        rentprice.setText("");
    }


}