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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class ListExpensesActivity extends AppCompatActivity {

    private Map<String, Person> people;
    private ArrayList<Expense> expenses;
    private ArrayList<LinearLayout> productList;
    private int UPDATE_ID;
    private LinearLayout root;
    private boolean UPDATE;
    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_products);

        final Intent myIntent = getIntent();

        String ppl = myIntent.getStringExtra("peeps");
        Gson gson = new Gson();
        Type entityType = new TypeToken<LinkedHashMap<String, Person>>(){}.getType();
        people = gson.fromJson(ppl, entityType);

        total = myIntent.getDoubleExtra("tots", 0.0);

        expenses = new ArrayList<>();
        productList = new ArrayList<>();
        root = findViewById(R.id.rootListProd);
        UPDATE = false;
        UPDATE_ID = -1;

        setTitle("  Total: Rs." + total);

        populate();

    }

    private void  populate() {
        TextView pName, pPrice, subTotal;

        subTotal = findViewById(R.id.subTotal);
        subTotal.setText("Rs." + subTotal());

        for (int i = 0; i < productList.size(); i++) {
            LinearLayout l = productList.get(i);

            pName = l.findViewById(R.id.productNameList);
            pPrice = l.findViewById(R.id.productPriceList);

            pName.setText(expenses.get(i).getName());
            pPrice.setText("Rs." + expenses.get(i).getCost());

            final int k = i;
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(ListExpensesActivity.this, ExpenseActivity.class);
                    Gson gson = new Gson();
                    String ppl = gson.toJson(people);
                    myIntent.putExtra("updProd", expenses.get(k));
                    myIntent.putExtra("peeps", ppl);
                    UPDATE = true;
                    UPDATE_ID = k;
                    ListExpensesActivity.this.startActivityForResult(myIntent, 1);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Expense expense = (Expense) data.getSerializableExtra("result");
                boolean remFlag = data.getBooleanExtra("R.E.M.",false);
                if (UPDATE) {
                    if (!remFlag)
                        expenses.set(UPDATE_ID, expense);
                    else {
                        expenses.remove(UPDATE_ID);
                        root.removeView(productList.get(UPDATE_ID));
                        productList.remove(UPDATE_ID);
                    }
                    UPDATE = false;
                    UPDATE_ID = -1;
                }
                else {
                    if (!remFlag) {
                        expenses.add(expense);
                        newEntry();
                    }
                }
                populate();
            }
            if (resultCode == RESULT_CANCELED) {
                UPDATE = false;
                UPDATE_ID = -1;
            }
        }
    }

    private void newEntry () {
        LayoutInflater mInf = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInf.inflate(R.layout.product_entry, root, false);
        LinearLayout entry = v.findViewById(R.id.productEntry);
        productList.add(entry);
        root.addView(entry);
    }

    private boolean checkTotal (double total) {
        double aux = 0.0;
        for (Expense p : expenses) {
            aux += p.getCost();
        }
        return aux == total;
    }

    private double subTotal () {
        double aux = 0.0;
        for (Expense p : expenses) {
            aux += p.getCost();
        }
        return aux;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Gson gson = new Gson();
        String ppl = gson.toJson(people);

        switch (item.getItemId()) {
            case R.id.action_new:
                ArrayList<String> prodNames = new ArrayList<>();
                for (Expense p : expenses) {
                    prodNames.add(p.getName());
                }
                Intent myIntent = new Intent(ListExpensesActivity.this, ExpenseActivity.class);
                myIntent.putExtra("peeps", ppl);
                myIntent.putExtra("pNames", prodNames);
                ListExpensesActivity.this.startActivityForResult(myIntent, 1);
                return true;

            case R.id.action_accept:
                if (checkTotal(total)) {
                    Intent myIntent2 = new Intent(ListExpensesActivity.this, ResultActivity.class);
                    myIntent2.putExtra("peeps", ppl);
                    myIntent2.putExtra("prods", expenses);
                    ListExpensesActivity.this.startActivity(myIntent2);
                }
                else {
                    Toast.makeText(getApplicationContext(), "The product costs " +
                            "don't add up!",Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
