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


public class Restaurant extends AppCompatActivity {
    private EditText restaurantinput;
    private EditText restaurantquantity;
    EditText restaurantprice;
    TextView restaurantlist;
    TextView restauranttotal;
    private Context instance;

    private Database db;

    public static final String TAG = "Database";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        instance = this;

        db = new Database(this);

        restaurantinput = (EditText) findViewById(R.id.nameinput);
        restaurantquantity = (EditText) findViewById(R.id.quantity);
        restaurantprice = (EditText) findViewById(R.id.price);
        restaurantlist = (TextView) findViewById(R.id.list);
        restaurantlist.setMovementMethod(new ScrollingMovementMethod());
        restauranttotal = (TextView) findViewById(R.id.total);
        printRestaurantDatabase();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

    }


    private boolean validInput() {
        try {
            Integer.parseInt(restaurantquantity.getText().toString());
            Float.parseFloat(restaurantprice.getText().toString());
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
        RestaurantModel receipt = new RestaurantModel();
        receipt.set_receiptname(restaurantinput.getText().toString());
        receipt.setprice(Float.parseFloat(restaurantprice.getText().toString()));
        receipt.setQuantity(Integer.parseInt(restaurantquantity.getText().toString()));
        db.open();
        db.addReceipt(receipt);
        db.close();
        printRestaurantDatabase();
    }


    //Delete items
    public void deleteButtonClicked(View view) {
        String inputText = restaurantinput.getText().toString();
        db.open();
        db.deleteReceipt(inputText);
        db.close();
        printRestaurantDatabase();
    }

    public float getRestaurantbalance() {

        float gasBalance = 0;
        db.open();
        Cursor c = db.findByType(RestaurantModel.TYPE);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                gasBalance += c.getFloat(c.getColumnIndex("price"));
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        Log.i(TAG, "return gasbalance called");

        return gasBalance;
    }


    public String restaurantDBToString() {
        String dbString = "";

        //Cursor points to a location in your results
        db.open();
        Cursor c = db.findByType(RestaurantModel.TYPE);
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
    public void printRestaurantDatabase() {
        String restaurantDBString = this.restaurantDBToString();
        restaurantlist.setText(restaurantDBString);
        String restaurantBalance = new Float(this.getRestaurantbalance()).toString();
        restauranttotal.setText(restaurantBalance);
        restaurantinput.setText("");
        restaurantquantity.setText("");
        restaurantprice.setText("");
    }


}