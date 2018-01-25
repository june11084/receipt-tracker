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


public class Maintenance extends AppCompatActivity {
    private EditText maintinput;
    private EditText maintquantity;
    EditText maintprice;
    TextView maintlist;
    TextView mainttotal;
    private Context instance;

    private Database db;

    public static final String TAG = "Database";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        instance = this;

        db = new Database(this);

        maintinput = (EditText) findViewById(R.id.nameinput);
        maintquantity = (EditText) findViewById(R.id.quantity);
        maintprice = (EditText) findViewById(R.id.price);
        maintlist = (TextView) findViewById(R.id.list);
        maintlist.setMovementMethod(new ScrollingMovementMethod());
        mainttotal = (TextView) findViewById(R.id.total);
        printMaintDatabase();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

    }


    private boolean validInput() {
        try {
            Integer.parseInt(maintquantity.getText().toString());
            Float.parseFloat(maintprice.getText().toString());
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
        MaintenanceModel receipt = new MaintenanceModel();
        receipt.set_receiptname(maintinput.getText().toString());
        receipt.setprice(Float.parseFloat(maintprice.getText().toString()));
        receipt.setQuantity(Integer.parseInt(maintquantity.getText().toString()));
        db.open();
        db.addReceipt(receipt);
        db.close();
        printMaintDatabase();
    }


    //Delete items
    public void deleteButtonClicked(View view) {
        String inputText = maintinput.getText().toString();
        db.open();
        db.deleteReceipt(inputText);
        db.close();
        printMaintDatabase();
    }

    public float getMaintbalance() {

        float maintBalance = 0;
        db.open();
        Cursor c = db.findByType(MaintenanceModel.TYPE);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                maintBalance += c.getFloat(c.getColumnIndex("price"));
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        Log.i(TAG, "return balance called");

        return maintBalance;
    }


    public String maintDBToString() {
        String dbString = "";

        //Cursor points to a location in your results
        db.open();
        Cursor c = db.findByType(MaintenanceModel.TYPE);
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
    public void printMaintDatabase() {
        String maintDBString = this.maintDBToString();
        maintlist.setText(maintDBString);
        String maintBalance = new Float(this.getMaintbalance()).toString();
        mainttotal.setText(maintBalance);
        maintinput.setText("");
        maintquantity.setText("");
        maintprice.setText("");
    }


}