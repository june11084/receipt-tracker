package com.jun.myproject;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ButtonsActivity extends AppCompatActivity {

    Database db;
    TextView overallbalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttonactivity);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                printTotalBalance();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        db = new Database(this);

        overallbalance = (TextView) findViewById(R.id.overallbalance);

        Button button = (Button) findViewById(R.id.grocerybutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Grocery.class));
            }
        });

        Button button2 = (Button) findViewById(R.id.gasolinebutton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Gasoline.class));
            }
        });

        Button button3 = (Button) findViewById(R.id.rentbutton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Rent.class));
            }
        });

        Button button4 = (Button) findViewById(R.id.electricitybutton);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Electricity.class));
            }
        });

        Button button5 = (Button) findViewById(R.id.entertainment);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Entertainment.class));
            }
        });

        Button button6 = (Button) findViewById(R.id.loansbutton);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Loans.class));
            }
        });

        Button button7 = (Button) findViewById(R.id.maintenancebutton);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Maintenance.class));
            }
        });

        Button button8 = (Button) findViewById(R.id.restaurantbutton);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Restaurant.class));
            }
        });

        Button button9 = (Button) findViewById(R.id.insurancebutton);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ButtonsActivity.this, Insurance.class));
            }
        });



        printTotalBalance();
    }

    public float getBalance() {

        float totalBalance = 0;
        db.open();
        Cursor c = db.findByColumn();

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                totalBalance += c.getFloat(c.getColumnIndex("price"));
                c.moveToNext();
            }
            c.close();
        }
        db.close();

        return totalBalance;
    }

    public void printTotalBalance() {
        String totalBalance = new Float(this.getBalance()).toString();
        overallbalance.setText(totalBalance);

    }
}
