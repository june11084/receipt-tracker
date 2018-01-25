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


public class Gasoline extends AppCompatActivity {
    private EditText gasinput;
    private EditText gasquantity;
    EditText gasprice;
    TextView gaslist;
    TextView gastotal;
    private Context instance;

    private Database db;

    public static final String TAG = "Database";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        instance = this;

        db = new Database(this);

        gasinput = (EditText) findViewById(R.id.nameinput);
        gasquantity = (EditText) findViewById(R.id.quantity);
        gasprice = (EditText) findViewById(R.id.price);
        gaslist = (TextView) findViewById(R.id.list);
        gaslist.setMovementMethod(new ScrollingMovementMethod());
        gastotal = (TextView) findViewById(R.id.total);
        printGasDatabase();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

    }


    private boolean validInput() {
        try {
            Integer.parseInt(gasquantity.getText().toString());
            Float.parseFloat(gasprice.getText().toString());
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
        GasModel receipt = new GasModel();
        receipt.set_receiptname(gasinput.getText().toString());
        receipt.setprice(Float.parseFloat(gasprice.getText().toString()));
        receipt.setQuantity(Integer.parseInt(gasquantity.getText().toString()));
        db.open();
        db.addReceipt(receipt);
        db.close();
        printGasDatabase();
    }


    //Delete items
    public void deleteButtonClicked(View view) {
        String inputText = gasinput.getText().toString();
        db.open();
        db.deleteReceipt(inputText);
        db.close();
        printGasDatabase();
    }

    public float getGasbalance() {

        float gasBalance = 0;
        db.open();
        Cursor c = db.findByType(GasModel.TYPE);

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


    public String gasDBToString() {
        String dbString = "";

        //Cursor points to a location in your results
        db.open();
        Cursor c = db.findByType(GasModel.TYPE);
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
    public void printGasDatabase() {
        String gasDBString = this.gasDBToString();
        gaslist.setText(gasDBString);
        String gasBalance = new Float(this.getGasbalance()).toString();
        gastotal.setText(gasBalance);
        gasinput.setText("");
        gasquantity.setText("");
        gasprice.setText("");
    }


}