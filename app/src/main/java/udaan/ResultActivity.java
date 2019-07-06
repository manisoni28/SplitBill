package udaan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class ResultActivity extends AppCompatActivity {

    private Map<String, Person> people;
    private ArrayList<Expense> expenses;
    private LinearLayout root;
    private boolean saveOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setTitle("Result");
        root = (LinearLayout) findViewById(R.id.resultRoot);

        Intent myIntent = getIntent();

        Save sv = (Save)myIntent.getSerializableExtra("save");

        if (sv == null) {
            String ppl = myIntent.getStringExtra("peeps");
            Gson gson = new Gson();
            Type entityType = new TypeToken<LinkedHashMap<String, Person>>() {}.getType();

            people = gson.fromJson(ppl, entityType);
            expenses = (ArrayList<Expense>) myIntent.getSerializableExtra("prods");

            for (int i = 0; i < expenses.size(); i++) {
                Expense pAux = expenses.get(i);
                calculate(normalize(pAux.getSeeks()), i);
            }
        }

        else {
            people = sv.getPeople();
            expenses = sv.getExpenses();
            saveOn = true;
        }

        populate();

    }

    private void populate() {

        for (String name : people.keySet()) {
            Person per = people.get(name);

            StringBuilder strBld = new StringBuilder();

            double val = round(per.getPaid() - per.getOwes());

            strBld.append(name);
            if (val > 0) {
                strBld.append(" has to receive ");
                strBld.append(Math.abs(val));
            }
            else if (val < 0) {
                strBld.append(" has to pay ");
                strBld.append(Math.abs(val));
            }
            else {
                strBld.append(" is settled");
            }

            LinearLayout l = newEntry();

            TextView res = (TextView)l.findViewById(R.id.resultTV);
            res.setText(strBld.toString());

            ImageButton more = (ImageButton)l.findViewById(R.id.moreIB);
            final String nameAux = name;
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    details(nameAux);
                }
            });
        }

    }

    private HashMap<String,Double> normalize (HashMap<String,Integer> seeks) {
        int acc = 0;
        double aux;
        int num = 0;
        HashMap<String,Double> sAux = new HashMap<String, Double>();

        for (String name : seeks.keySet()) {
            acc += seeks.get(name);
            if (seeks.get(name) != 0) num++;
        }

        if (acc > 100) {
            acc -= 100;
            aux = (double) acc / (double) num;
            aux = -aux;
        }
        else {
            if (acc < 100) {
                acc = 100 - acc;
                aux = (double) acc / (double) num;
            }
            else {
                aux = 0.0;
            }
        }

        for (String name : seeks.keySet()) {
            if (seeks.get(name) != 0) sAux.put(name, (double)seeks.get(name) + aux);
        }

        return sAux;
    }

    private void calculate (HashMap<String,Double> sAux, int ind) {
        for (String name : sAux.keySet()) {
            people.get(name).setOwes(people.get(name).getOwes() + expenses.get(ind).getCost() * (sAux.get(name) * 0.01));
        }
    }


    private Double round (Double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    private LinearLayout newEntry () {
        LayoutInflater mInf = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInf.inflate(R.layout.result_entry, root, false);
        LinearLayout entry = (LinearLayout)v.findViewById(R.id.resultEntry);
        root.addView(entry);
        return entry;
    }

    private void details (String name) {
        Person prsn = people.get(name);
        ArrayList<MiniExpense> prods = new ArrayList<MiniExpense>();
        for (Expense pdct : expenses) {
            if (pdct.getChecks().get(name))
                prods.add(new MiniExpense(pdct.getName(),pdct.getCost(),normalize(pdct.getSeeks()).get(name)));
        }
        Intent myIntent = new Intent(ResultActivity.this, DetailsActivity.class);
        myIntent.putExtra("prsn",prsn);
        myIntent.putExtra("prods",prods);
        myIntent.putExtra("name",name);
        ResultActivity.this.startActivity(myIntent);
    }

    private void save() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent myIntent = new Intent(ResultActivity.this, MainActivity.class);
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        SharedPreferences sp = getApplicationContext().getSharedPreferences("BillSave", 0);
                        String save = sp.getString("saves", null);

                        Gson gson = new Gson();
                        Type entityType = new TypeToken<ArrayList<Save>>(){}.getType();
                        ArrayList<Save> saves = gson.fromJson(save, entityType);

                        if (saves==null)
                            saves = new ArrayList<Save>();

                        saves.add(new Save(new Date(),people, expenses));

                        save = gson.toJson(saves);

                        SharedPreferences.Editor spe = sp.edit();
                        spe.clear();
                        spe.putString("saves",save);
                        spe.apply();

                        ResultActivity.this.startActivity(myIntent);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        ResultActivity.this.startActivity(myIntent);
                        finish();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to save this bill?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_accept:
                if (!saveOn)
                    save();
                else
                    finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
