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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Map<String, Person> people;
    private ArrayList<LinearLayout> entryList = new ArrayList<>();
    private ScrollView scrlv;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        people = new LinkedHashMap<>();

        scrlv = findViewById(R.id.scrollV);

        root = findViewById(R.id.root);

        newEntry(root);
        newEntry(root);

    }

    private void newEntry (LinearLayout root) {
        LayoutInflater mInf = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInf.inflate(R.layout.name_entry, root, false);
        LinearLayout entry = v.findViewById(R.id.entry);
        entryList.add(entry);
        root.addView(entry);
        scrlv.fullScroll(View.FOCUS_DOWN);
    }

    private void error () {
        Toast.makeText(getApplicationContext(), "Something's wrong! Check your " +
                        "values and names. Remember that the payments must add up to the total!",
                Toast.LENGTH_LONG).show();
        people.clear();
    }

    private void submit() {

        EditText nameET, paidET, totalET;
        String name;
        Double total, each;
        total = 0.0;
        for (LinearLayout l : entryList) {
            nameET = l.findViewById(R.id.nameET);
            name = nameET.getText().toString();

            if (!name.matches("")) {
                paidET = l.findViewById(R.id.paidET);
                if (!paidET.getText().toString().matches(""))
                    each = Double.parseDouble(paidET.getText().toString());
                else
                    each = 0.0;

                if (people.containsKey(name))
                    error();
                else
                    people.put(name, new Person(each, 0.0));

                total += each;
            }

        }
        totalET = findViewById(R.id.totalPaidET);
        if (totalET.getText().toString().matches("") ||
                total.compareTo(Double.parseDouble(totalET.getText().toString())) != 0 ||
                people.size() < 2)
            error();
        else {
            Gson gson = new Gson();
            String ppl = gson.toJson(people);
            Intent myIntent = new Intent(MainActivity.this, ListExpensesActivity.class);
            myIntent.putExtra("peeps", ppl);
            myIntent.putExtra("tots", total);
            MainActivity.this.startActivity(myIntent);

        }
    }

    private void openPrevious () {

        SharedPreferences sp = getApplicationContext().getSharedPreferences("BillSave", 0);
        String save = sp.getString("saves", null);

        Gson gson = new Gson();
        Type entityType = new TypeToken<ArrayList<Save>>(){}.getType();
        final ArrayList<Save> saves = gson.fromJson(save, entityType);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.ic_action_accept);
        builderSingle.setTitle("Select Bill by Date:");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.select_dialog_singlechoice);

        if (saves != null) {
            for (Save s : saves) {
                arrayAdapter.add(s.getDate().toString().substring(0,19));
            }
        }

        builderSingle.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(MainActivity.this, ResultActivity.class);
                        assert saves != null;
                        myIntent.putExtra("save", saves.get(which));
                        MainActivity.this.startActivity(myIntent);

                    }
                });
        builderSingle.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        people.clear();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_new:
                    newEntry(root);
                return true;

            case R.id.action_remove:
                    root.removeView(entryList.get(entryList.size() - 1));
                    entryList.remove(entryList.size() - 1);
                    scrlv.fullScroll(View.FOCUS_DOWN);
                return true;

            case R.id.action_accept:
                submit();
                return true;


            case R.id.action_load:
                openPrevious();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
