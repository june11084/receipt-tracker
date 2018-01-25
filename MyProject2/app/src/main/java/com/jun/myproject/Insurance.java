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


public class Insurance extends AppCompatActivity {
    private EditText insuranceinput;
    private EditText insuranceinputquantity;
    EditText insuranceinputprice;
    TextView insuranceinputlist;
    TextView insuranceinputtotal;
    private Context instance;

    private Database db;

    public static final String TAG = "Database";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        instance = this;

        db = new Database(this);

        insuranceinput = (EditText) findViewById(R.id.nameinput);
        insuranceinputquantity = (EditText) findViewById(R.id.quantity);
        insuranceinputprice = (EditText) findViewById(R.id.price);
        insuranceinputlist = (TextView) findViewById(R.id.list);
        insuranceinputlist.setMovementMethod(new ScrollingMovementMethod());
        insuranceinputtotal = (TextView) findViewById(R.id.total);
        printInsuranceinputDatabase();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

    }


    private boolean validInput() {
        try {
            Integer.parseInt(insuranceinputquantity.getText().toString());
            Float.parseFloat(insuranceinputprice.getText().toString());
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
        InsuranceModel receipt = new InsuranceModel();
        receipt.set_receiptname(insuranceinput.getText().toString());
        receipt.setprice(Float.parseFloat(insuranceinputprice.getText().toString()));
        receipt.setQuantity(Integer.parseInt(insuranceinputquantity.getText().toString()));
        db.open();
        db.addReceipt(receipt);
        db.close();
        printInsuranceinputDatabase();
    }


    //Delete items
    public void deleteButtonClicked(View view) {
        String inputText = insuranceinput.getText().toString();
        db.open();
        db.deleteReceipt(inputText);
        db.close();
        printInsuranceinputDatabase();
    }

    public float getInsuranceinputbalance() {

        float insuranceinputBalance = 0;
        db.open();
        Cursor c = db.findByType(InsuranceModel.TYPE);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                insuranceinputBalance += c.getFloat(c.getColumnIndex("price"));
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        Log.i(TAG, "return balance called");

        return insuranceinputBalance;
    }


    public String insuranceDBToString() {
        String dbString = "";

        //Cursor points to a location in your results
        db.open();
        Cursor c = db.findByType(InsuranceModel.TYPE);
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
    public void printInsuranceinputDatabase() {
        String insuranceinputDBString = this.insuranceDBToString();
        insuranceinputlist.setText(insuranceinputDBString);
        String insuranceinputBalance = new Float(this.getInsuranceinputbalance()).toString();
        insuranceinputtotal.setText(insuranceinputBalance);
        insuranceinput.setText("");
        insuranceinputquantity.setText("");
        insuranceinputprice.setText("");
    }


}