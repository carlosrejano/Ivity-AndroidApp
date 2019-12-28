package com.example.ivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText querySelected;
    private TableLayout mainTable;
    private ProgressBar progressbarView;
    private TextView resultsView;
    private LinearLayout tableLayout;
    private boolean isTable;
    private TableLayout table;
    private TextView nameUser;
    private Map<String, String[]> tittleRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        nameUser = (TextView) findViewById(R.id.nameUser);
        querySelected = (EditText) findViewById(R.id.query_selected);
        progressbarView = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        //resultsView = (TextView) findViewById(R.id.results);
        tableLayout = (LinearLayout) findViewById(R.id.tableLayout);
        isTable = false;
        nameUser.setText("Welcome: "+ sessionId.replace("[", "").replace("]", "").replace("\"",""));
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.app_bar_search) {
            makeSearchQuery();
            return true;
        }else if (itemThatWasClickedId == R.id.logoutButton){
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    private void makeSearchQuery(){
        String query = querySelected.getText().toString();
        URL searchUrl = ParseUtils.buildUrl(query);
        new QueryTask(this).execute(searchUrl);
    }
    public class QueryTask extends AsyncTask<URL, Void, String> {

        private MainActivity activity;

        public QueryTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbarView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String results = null;
            try {
                results = ParseUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }


        @Override
        protected void onPostExecute(String results) {
            progressbarView.setVisibility(View.INVISIBLE);
            if (!(results.contains("error") || results.contains("ERROR"))) {
                String resultsTable[][] = transformResultsToTable(results);
                createTable(resultsTable);
            }
        }
        public void createTable(String results[][]){

            if (isTable){
                tableLayout.removeView(table);

            }
            table = new TableLayout(activity);
            table.setStretchAllColumns(true);
            table.setShrinkAllColumns(true);
            isTable = true;
            TableRow rowTitle = new TableRow(activity);
            createTittleRow();
            rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            rowTitle.setBackgroundColor(Color.parseColor("#00AAFF"));
            String tittle = querySelected.getText().toString().split("\\?")[0];
            for(int l =0; l<tittleRow.get(tittle).length; l++){
                TextView text = new TextView(activity);
                text.setTextColor(Color.parseColor("#000000"));
                text.setText(tittleRow.get(tittle)[l]);
                rowTitle.addView(text);
            }
            table.addView(rowTitle);
            for(int k = 0; k<results[0].length; k++){
                TableRow row = new TableRow(activity);
                row.setGravity(Gravity.CENTER_HORIZONTAL);
                if(k%2 == 0) {
                    row.setBackgroundColor(Color.parseColor("#B9ECFF"));
                }else{row.setBackgroundColor(Color.parseColor("#E7F9FF"));}
                for(int m = 0; m<results.length; m++){
                    TextView text = new TextView(activity);
                    text.setText(results[m][k]);
                    row.addView(text);
                }
                table.addView(row);
            }
            tableLayout.addView(table);


        }
        public String[][] transformResultsToTable(String results){
            String s= results;
            s=s.replace("\"", "");
            s=s.replace("[","");
            s=s.substring(0,s.length()-2);
            String s1[]=s.split("],");

            String my_matrics[][] = new String[s1.length][s1[0].split(",").length];

            for(int i=0;i<s1.length;i++){
                s1[i]=s1[i].trim();
                String single_int[]=s1[i].split(",");

                for(int j=0;j<single_int.length;j++){
                    my_matrics[i][j]=single_int[j];
                }
            }
            return my_matrics;
        }
        public void createTittleRow(){
            tittleRow = new HashMap<String, String[]>();
            String[] timetables = {"Day", "Hour", "Subject", "Room"};
            String[] tasks = {"Date", "Subject", "Name"};
            String[] marks = {"Subject", "Name", "Mark"};
            tittleRow.put("timetables",timetables);
            tittleRow.put("marks",marks);
            tittleRow.put("tasks",tasks);

        }

    }
}
