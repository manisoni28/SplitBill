package udaan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class DetailsActivity extends AppCompatActivity {

    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        Intent myIntent = getIntent();
        ArrayList<MiniExpense> prods = (ArrayList<MiniExpense>)myIntent.getSerializableExtra("prods");
        Person prsn = (Person)myIntent.getSerializableExtra("prsn");
        String name = myIntent.getStringExtra("name");
        root = findViewById(R.id.rootDetails);

        setTitle(name);

        TextView payed = (TextView)findViewById(R.id.payedTV);
        payed.setText("Payed: " + prsn.getPaid());

        TextView share = (TextView)findViewById(R.id.shareTV);
        share.setText("Share: " + round(prsn.getOwes()));

        for (MiniExpense mp : prods) {
            newEntry(mp.getName(), mp.getQuantity(), mp.getCost());
        }

        TextView balance = findViewById(R.id.balanceTV);
        double bal = round(prsn.getOwes() - prsn.getPaid());

        if (bal < 0 )
            balance.setText("Balance: " + bal + "\n   (has to receive " + Math.abs(bal) + ")");
        else
            if (bal > 0)
                balance.setText("Balance: " + bal + "\n   (has to pay " + bal + ")");
             else
                balance.setText("Balance: 0\n   (is settled)");

    }

    private void newEntry (String prodName, double perc, double cost) {
        LayoutInflater mInf = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInf.inflate(R.layout.details_entry, root, false);
        LinearLayout entry = v.findViewById(R.id.prodEntry);

        TextView pName = entry.findViewById(R.id.prodNameTV);
        pName.setText(prodName + " (" + round(cost * (perc * 0.01)) + ") ");
        TextView percentage = (TextView)entry.findViewById(R.id.prodPercTV);
        percentage.setText(round(perc) + "%");

        root.addView(entry);
    }

    private Double round (Double val) {
        return Math.round(val * 100.0) / 100.0; //round up to 2 decimal places
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_accept:
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
