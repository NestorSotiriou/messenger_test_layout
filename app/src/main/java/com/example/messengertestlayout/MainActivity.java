package com.example.messengertestlayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.example.messengertestlayout.RetroFit.Api;
import com.example.messengertestlayout.RetroFit.ItemModel;
import com.example.messengertestlayout.Room.RoomDB;
import com.example.messengertestlayout.Room.TableMessageItem;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MyTag";
    RoomDB db;
    CardView quickAnswersCV;

    TextView hSymbolTV;
    TextView contactTV;
    TextView cancelTV;
    TextInputLayout messageWindowET;

    ImageView sendIV;

    ProgressBar messagePBar;


    RecyclerView messageHistoryRV;
    CardView quickAnswerHintCV;


    ArrayList<MessagesItem> messageItems = new ArrayList<>();

    MessengerAdapter recyclerViewAdapter;

    private AppBarConfiguration mAppBarConfiguration;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

       // setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
        , drawer, toolbar, R.string.OpenDrawer, R.string.CloseDrawer);

        drawer.addDrawerListener(toggle);

        toggle.syncState();



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id==R.id.nav_account){
                    Toast.makeText(MainActivity.this, "Account", Toast.LENGTH_SHORT).show();
                } else if (id==R.id.nav_settings) {
                    Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                }else {
                  // Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();

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
                }
                drawer.closeDrawer(GravityCompat.START);


                return true;
            }
        });


        contactTV = findViewById(R.id.contact);
        cancelTV = findViewById(R.id.cancel_text_up);
        messageWindowET = findViewById(R.id.editTextTextPersonName);
        sendIV = findViewById(R.id.send);
        quickAnswersCV = findViewById(R.id.quick_answersFirstCV);
        messagePBar = findViewById(R.id.messages_progressBar);


        hSymbolTV = findViewById(R.id.hSymbol);
        messageHistoryRV = findViewById(R.id.recyclerView);


        db = Room.databaseBuilder(getApplicationContext(),
                RoomDB.class, "NestorsDB").allowMainThreadQueries().build();
        if (savedInstanceState != null) {
            messageItems = savedInstanceState.getParcelableArrayList("messages");
        } else {
            messageItems.clear();
            for (TableMessageItem item : db.myDao().getAll())
                messageItems.add(new MessagesItem(item.getMessage(), item.getTimeStamp(), item.getSender(), item.getId()));

        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new MessengerAdapter(messageItems);
        messageHistoryRV.setLayoutManager(layoutManager);
        messageHistoryRV.setAdapter(recyclerViewAdapter);

        contactTV.setText("Taxiplon");

        quickAnswersCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHintQuickMessage();
                QuickAnswersBottomSheetFragment quickAnswersBottomSheetFragmen = new QuickAnswersBottomSheetFragment();

                quickAnswersBottomSheetFragmen.show(getSupportFragmentManager(), QuickAnswersBottomSheetFragment.TAG);


            }
        });


        sendIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageWindowET.getEditText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(messageWindowET.getEditText().getText().toString());
            }

        });

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                db.myDao().deleteAll();
                messageItems.clear();
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/").addConverterFactory(GsonConverterFactory.create()).build();

        Api api = retrofit.create(Api.class);
        Call<List<ItemModel>> call;
        call = api.getItem();

        call.enqueue(new Callback<List<ItemModel>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<ItemModel>> call, Response<List<ItemModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (ItemModel item : response.body()) {
                            messageItems.add(new MessagesItem(item.getTitle(), System.currentTimeMillis(), false, Math.toIntExact(db.myDao().nextid())));
                            db.myDao().insertAll(new TableMessageItem(0, item.getTitle(), false, System.currentTimeMillis()));
                            recyclerViewAdapter.notifyItemInserted(messageItems.size());
                        }
                        runOnUiThread(() -> messagePBar.setVisibility(View.GONE));

                    }

                }
            }

            @Override
            public void onFailure(Call<List<ItemModel>> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "error downloading: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    messagePBar.setVisibility(View.GONE);
                });

            }
        });
        recyclerViewAdapter.setItemClickListener(new MessengerAdapter.ItemClickListener() {
            @Override
            public void OnClick(int position) {
                Toast.makeText(MainActivity.this, "clicked on: " + messageItems.get(position).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        initSwipeDismissAction(messageHistoryRV);


        messageWindowET.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() <= 0)
                    showHint();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    hideHintQuickMessage();

                } else {
                    showHint();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0)
                    showHint();

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.container, fragment);
        ft.commit();
    }


    public void sendMessage(String message) {
        messageItems.add(new MessagesItem(message, System.currentTimeMillis(), true, Math.toIntExact(db.myDao().nextid())));
        recyclerViewAdapter.notifyItemInserted(messageItems.size());
        messageHistoryRV.smoothScrollToPosition(messageItems.size());
        db.myDao().insertAll(new TableMessageItem(
                0,
                message,
                true, System.currentTimeMillis()));
        messageWindowET.getEditText().setText("");
        KeyboardUtils.hideSoftInput(MainActivity.this);
    }


    private void initSwipeDismissAction(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftLabel("διαγραφή")
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .setSwipeLeftLabelTextSize(1, 24)
                        .setSwipeLeftActionIconTint(Color.BLACK)
                        .setSwipeLeftLabelColor(Color.BLACK)
                        .addBackgroundColor(Color.RED)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (direction == ItemTouchHelper.LEFT | direction == ItemTouchHelper.RIGHT) {
                    if (position != RecyclerView.NO_POSITION)
                        shoeDeleteDialog(position);

                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    boolean clickedOk;

    private void shoeDeleteDialog(int position) {

        clickedOk = false;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("warning");
        alert.setMessage("Are you sure you want to delete this?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickedOk = true;
                removeItem(position);
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickedOk = false;
                dialog.dismiss();
            }
        });
        alert.setCancelable(true);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!clickedOk)
                    recyclerViewAdapter.notifyItemChanged(position);
            }
        });
        alert.show();
    }


    private void removeItem(int position) {
        db.myDao().delete(messageItems.get(position).getId());
        messageItems.remove(position);
        recyclerViewAdapter.notifyItemRemoved(position);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("messages", messageItems);
    }


    private void hideHintQuickMessage() {
        quickAnswersCV.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        quickAnswersCV.setVisibility(View.GONE);
        hSymbolTV.setVisibility(View.GONE);
    }


    public void showHint() {
        if (quickAnswersCV.getVisibility() == View.VISIBLE && hSymbolTV.getVisibility() == View.VISIBLE)
            return;
        quickAnswersCV.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
        quickAnswersCV.setVisibility(View.VISIBLE);
        hSymbolTV.setVisibility(View.VISIBLE);
    }


}