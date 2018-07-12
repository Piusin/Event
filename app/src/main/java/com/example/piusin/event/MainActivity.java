package com.example.piusin.event;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piusin.event.InterfacesPackage.SweetAlertClass;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity implements SweetAlertClass{

    DrawerLayout drawerlayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    MenuItem mCartIconMenuItem;
    TextView mCountTv;
    ImageButton mImageBtn;

    int count, totalCount = 0;
    SQLiteDatabase sqLiteDatabase;
    CartDbHelper cartDbHelper;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchCount();

        if(IsInternetOn() != true)
            displayNoInternetAlert();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerlayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        drawerlayout.setDrawerListener(actionBarDrawerToggle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("All Mart");

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Navigation Drawer Items
                if (IsInternetOn() != true) {
                    displayNoInternetAlert();
                }
                else{

                switch (item.getItemId()) {
                    case R.id.home:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new HomeFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("All Mart");
                        item.setChecked(true);
                        drawerlayout.closeDrawers();
                        break;

                    case R.id.cart:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new CartFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Cart");
                        item.setChecked(true);
                        drawerlayout.closeDrawers();
                        break;

                    case R.id.share:
                        Toast.makeText(MainActivity.this, "Share this app", Toast.LENGTH_SHORT).show();
                        break;

                    //open user account
                    case R.id.drawer_account:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new AccountFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Account");
                        item.setChecked(true);
                        drawerlayout.closeDrawers();
                        break;

                }
            }

                    return true;
                }

        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    //inflates overflow menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_overflows, menu);

        mCartIconMenuItem = menu.findItem(R.id.overflow_cart);
        View actionView = mCartIconMenuItem.getActionView();
        if (actionView != null) {
            mCountTv = actionView.findViewById(R.id.count_tv);
            mImageBtn = actionView.findViewById(R.id.cart_image);
        }


        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCountTv.setText(String.valueOf(totalCount));
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new CartFragment());
                fragmentTransaction.commit();
                getSupportActionBar().setTitle("Cart");
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(IsInternetOn() != true){
            displayNoInternetAlert();
        }else {
            //toolbar items
            switch (item.getItemId()) {
                case R.id.settings:
                    Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.search:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, new SettingFragment()); //must go here coz of products bundle checks
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle("Product Search");
                    return true;

                case R.id.account:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, new AccountFragment());
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle("Account");
                    return true;

                case R.id.overflow_cart:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, new CartFragment());
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle("Cart");
                    return true;
            }
        }
            return true;

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void fetchCount() {
        cartDbHelper = new CartDbHelper(this);
        sqLiteDatabase = cartDbHelper.getReadableDatabase();
        cursor = cartDbHelper.getInformations(sqLiteDatabase);

        if (cursor.moveToFirst()) {
            do {
                final String prodCount;
                prodCount = cursor.getString(2);
                count = Integer.valueOf(prodCount);
                totalCount = totalCount + count;
            } while (cursor.moveToNext());
        }
    }


    @Override
    public void displayNoInternetAlert() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Network Problem")
                .setContentText("Looks like you are offline!! " + "\n" + "Check Internet Connection..for better application use")
                .setConfirmText("Ok!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }


    @Override
    public boolean IsInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            //Toast.makeText(this, "There is internet", Toast.LENGTH_SHORT).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }


}






