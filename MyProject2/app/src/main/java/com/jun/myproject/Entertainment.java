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


public class Entertainment extends AppCompatActivity {
    private EditText entertainmentinput;
    private EditText entertainmentquantity;
    EditText entertainmentprice;
    TextView entertainmentlist;
    TextView entertainmenttotal;
    private Context instance;

    private Database db;

    public static final String TAG = "Database";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        instance = this;

        db = new Database(this);

        entertainmentinput = (EditText) findViewById(R.id.nameinput);
        entertainmentquantity = (EditText) findViewById(R.id.quantity);
        entertainmentprice = (EditText) findViewById(R.id.price);
        entertainmentlist = (TextView) findViewById(R.id.list);
        entertainmentlist.setMovementMethod(new ScrollingMovementMethod());
        entertainmenttotal = (TextView) findViewById(R.id.total);
        printEntertainmentDatabase();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

    }


    private boolean validInput() {
        try {
            Integer.parseInt(entertainmentquantity.getText().toString());
            Float.parseFloat(entertainmentprice.getText().toString());
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
        EntertainmentModel receipt = new EntertainmentModel();
        receipt.set_receiptname(entertainmentinput.getText().toString());
        receipt.setprice(Float.parseFloat(entertainmentprice.getText().toString()));
        receipt.setQuantity(Integer.parseInt(entertainmentquantity.getText().toString()));
        db.open();
        db.addReceipt(receipt);
        db.close();
        printEntertainmentDatabase();
    }


    //Delete items
    public void deleteButtonClicked(View view) {
        String inputText = entertainmentinput.getText().toString();
        db.open();
        db.deleteReceipt(inputText);
        db.close();
        printEntertainmentDatabase();
    }

    public float getEntertainmentbalance() {

        float entertainmentBalance = 0;
        db.open();
        Cursor c = db.findByType(EntertainmentModel.TYPE);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                entertainmentBalance += c.getFloat(c.getColumnIndex("price"));
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        Log.i(TAG, "return balance called");

        return entertainmentBalance;
    }


    public String entertainmentDBToString() {
        String dbString = "";

        //Cursor points to a location in your results
        db.open();
        Cursor c = db.findByType(EntertainmentModel.TYPE);
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
    public void printEntertainmentDatabase() {
        String entertainmentDBString = this.entertainmentDBToString();
        entertainmentlist.setText(entertainmentDBString);
        String entertainmentBalance = new Float(this.getEntertainmentbalance()).toString();
        entertainmenttotal.setText(entertainmentBalance);
        entertainmentinput.setText("");
        entertainmentquantity.setText("");
        entertainmentprice.setText("");
    }


}