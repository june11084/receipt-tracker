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


public class Grocery extends AppCompatActivity {
    private EditText groceryinput;
    private EditText groceryquantity;
    EditText groceryprice;
    TextView grocerylist;
    TextView grocerytotal;
    private Context instance;

    private Database db;

    public static final String TAG = "Database";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        instance = this;

        db = new Database(this);

        groceryinput = (EditText) findViewById(R.id.nameinput);
        groceryquantity = (EditText) findViewById(R.id.quantity);
        groceryprice = (EditText) findViewById(R.id.price);
        grocerylist = (TextView) findViewById(R.id.list);
        grocerylist.setMovementMethod(new ScrollingMovementMethod());
        grocerytotal = (TextView) findViewById(R.id.total);
        printGroceryDatabase();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

    }


    private boolean validInput() {
        try {
            Integer.parseInt(groceryquantity.getText().toString());
            Float.parseFloat(groceryprice.getText().toString());
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
        GroceryModel receipt = new GroceryModel();
        receipt.set_receiptname(groceryinput.getText().toString());
        receipt.setprice(Float.parseFloat(groceryprice.getText().toString()));
        receipt.setQuantity(Integer.parseInt(groceryquantity.getText().toString()));
        db.open();
        db.addReceipt(receipt);
        db.close();
        printGroceryDatabase();

    }


    //Delete items
    public void deleteButtonClicked(View view) {
        String inputText = groceryinput.getText().toString();
        db.open();
        db.deleteReceipt(inputText);
        db.close();
        printGroceryDatabase();
    }

    public float getGrocerybalance() {

        float groceryBalance = 0;
        db.open();
        Cursor c = db.findByType(GroceryModel.TYPE);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                groceryBalance += c.getFloat(c.getColumnIndex("price"));
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        Log.i(TAG, "return grocerybalance called");

        return groceryBalance;
    }


    public String groceryDBToString() {
        String dbString = "";

        //Cursor points to a location in your results
        db.open();
        Cursor c = db.findByType(GroceryModel.TYPE);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("receiptname")) != null) {
                dbString += c.getString(c.getColumnIndex("receiptname"));
                dbString += "/QT:";
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
    public void printGroceryDatabase() {
        String groceryDBString = this.groceryDBToString();
        grocerylist.setText(groceryDBString);
        String groceryBalance = new Float(this.getGrocerybalance()).toString();
        grocerytotal.setText(groceryBalance);
        groceryinput.setText("");
        groceryquantity.setText("");
        groceryprice.setText("");
    }


}