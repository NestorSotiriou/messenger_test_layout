package com.example.messengertestlayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.room.Room;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.example.messengertestlayout.Fragments.AFragment;
import com.example.messengertestlayout.Fragments.MessengerFragment;
import com.example.messengertestlayout.Room.RoomDB;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MyTag";

    FrameLayout mainContainerFL;
    RoomDB db;
    boolean doubleBackToExitPressedOnce = false;

    private AppBarConfiguration mAppBarConfiguration;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;

    TextView okay_text, cancel_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContainerFL = findViewById(R.id.main_containerFL);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        Dialog dialog = new Dialog(MainActivity.this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawer, toolbar, R.string.OpenDrawer, R.string.CloseDrawer);

        drawer.addDrawerListener(toggle);

        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

               if (id == R.id.nav_account) {
                    AFragment aFragment = new AFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_containerFL,aFragment, AFragment.TAG).addToBackStack(null).commit();
                } else if (id == R.id.nav_settings) {
                   customDialog(dialog);
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("warning");
                    builder1.setMessage("Are you sure you want to logout?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    MainActivity.this.finishAndRemoveTask();

                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }/*
                if (id == R.id.nav_settings) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    // set the custom layout
                    final View customLayout = getLayoutInflater().inflate(R.layout.custom_alertbox, null);
                    builder.setView(customLayout);

                        // send data from the AlertDialog to the Activity
                    EditText editText = customLayout.findViewById(R.id.editText);
                    sendDialogDataToActivity(editText.getText().toString());

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    //Next Line needed for round corners
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    customDialog(dialog);
                }*/

                drawer.closeDrawer(GravityCompat.START);


                return true;
            }
        });

        db = Room.databaseBuilder(getApplicationContext(),
                RoomDB.class, "NestorsDB").allowMainThreadQueries().build();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_containerFL,new MessengerFragment(db), MessengerFragment.TAG).commit();

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

       /* if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);*/
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Return whether touch the view.
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationOnScreen(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getRawX() > left && event.getRawX() < right
                    && event.getRawY() > top && event.getRawY() < bottom);
        }
        return false;
    }

    private void sendDialogDataToActivity(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

    public void customDialog(Dialog dialog){
        dialog.setContentView(R.layout.custom_alertbox);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.Theme_NavigationDrawerSample;

        okay_text = dialog.findViewById(R.id.okay_text);
        cancel_text = dialog.findViewById(R.id.cancel_text);

        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "okay clicked", Toast.LENGTH_SHORT).show();
            }
        });

        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }

}
