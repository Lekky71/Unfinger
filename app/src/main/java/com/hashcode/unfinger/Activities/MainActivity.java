package com.hashcode.unfinger.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hashcode.unfinger.R;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    EditText ipAddressEditText ;
    Button unFingButton;
    Button saveIpButton;
    ProgressBar loggingOutBar;
    private static String IP_ADDRESS = "";
    private static String IP_ADDRESS_KEY = "";
    private final static int UNFING_ACTION = 2;
    Toast help;
    boolean isUnfinged;
    ProgressDialog progress;
    boolean isIPAddressSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ipAddressEditText = (EditText) findViewById(R.id.ip_address_edit_text);
        unFingButton = (Button) findViewById(R.id.unfing_button);
        saveIpButton = (Button) findViewById(R.id.save_ip_button);
        loggingOutBar = (ProgressBar) findViewById(R.id.loggingOutProgressBar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(help != null){
            help.cancel();
        }
        final SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if(!sharedPreferences.getString(IP_ADDRESS_KEY,"not found").equals("not found")){
            IP_ADDRESS = sharedPreferences.getString(IP_ADDRESS_KEY,"not found");
            ipAddressEditText.setText(IP_ADDRESS);
            isIPAddressSaved = true;
        }
        if(isIPAddressSaved) unFingButton.setVisibility(View.VISIBLE);
        else unFingButton.setVisibility(View.GONE);
        saveIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = ipAddressEditText.getText().toString();
                if(ipAddress.length() == 0){
                    ipAddressEditText.setError("Cannot be empty");
                }
                else{
                    editor.putString(IP_ADDRESS_KEY, ipAddress);
                    editor.commit();
                    Toast.makeText(MainActivity.this,"IP Address saved",Toast.LENGTH_SHORT).show();
                    unFingButton.setVisibility(View.VISIBLE);
                    if(!sharedPreferences.getString(IP_ADDRESS_KEY,"not found").equals("not found")){
                        IP_ADDRESS = sharedPreferences.getString(IP_ADDRESS_KEY,"not found");
                    }
                }
            }
        });
        unFingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflate = (LayoutInflater)
                        getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(settingsIntent, UNFING_ACTION);
                help = Toast.makeText(getBaseContext(),"Your IP Address is : "+IP_ADDRESS,Toast.LENGTH_SHORT);
                help.setGravity(Gravity.TOP,0,0);
                help.setText("Your IP Address is : "+IP_ADDRESS);

                fireLongToast(help);
            }
        });


        progress=new ProgressDialog(this);
        progress.setMessage("Unfinging you");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void makeRequest(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://gateway.oauife.edu.ng/logout";
        progress.show();
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        progress.cancel();
                        if(response.contains("SIGN IN TO USE OAUNET SERVICES"))
                            Snackbar.make(unFingButton,"You are now unfinged!!!",Snackbar.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                makeRequest();
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.nav_help){
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
            return true;
        }
        else if(itemId == R.id.nav_share){

        }
        else if(itemId == R.id.nav_review){
            startActivity(new Intent(MainActivity.this, ReviewActivity.class));
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;    }

    private void fireLongToast(final Toast toast) {
        Thread t = new Thread() {
            public void run() {
                int stopper = 30;
                int count = 0;
                try {
                    while (true && count < 100) {
                        toast.show();
                        sleep(1850);
                        count++;

                        // DO SOME LOGIC THAT BREAKS OUT OF THE WHILE LOOP
                        if(count >= stopper && isUnfinged){
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e("LongToast", "", e);
                }
            }
        };
        t.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UNFING_ACTION){
            if(isWifiConnected(MainActivity.this)){
                makeRequest();
                isUnfinged = true;
                final SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
            }
            else{
                Snackbar.make(saveIpButton,"You are not Connected on WiFi", Snackbar.LENGTH_LONG).show();
            }

        }
    }

    public static boolean isWifiConnected(Context context){
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
}
