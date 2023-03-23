package com.example.messengertestlayout.Fragments;

import static com.blankj.utilcode.util.ThreadUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.example.messengertestlayout.AFragment;
import com.example.messengertestlayout.MainActivity;
import com.example.messengertestlayout.MessagesItem;
import com.example.messengertestlayout.MessengerAdapter;
import com.example.messengertestlayout.QuickAnswersBottomSheetFragment;
import com.example.messengertestlayout.R;
import com.example.messengertestlayout.RetroFit.Api;
import com.example.messengertestlayout.RetroFit.ItemModel;
import com.example.messengertestlayout.Room.RoomDB;
import com.example.messengertestlayout.Room.TableMessageItem;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessengerFragment extends Fragment {
    public static final String TAG = "MessengerFragment";
    CardView quickAnswersCV;

    TextView hSymbolTV;
    TextView cancelTV;
    TextInputLayout messageWindowET;

    ImageView sendIV;

    RoomDB db;


    ProgressBar messagePBar;

    Spinner contactsSpinner;


    RecyclerView messageHistoryRV;
    CardView quickAnswerHintCV;


    ArrayList<MessagesItem> messageItems = new ArrayList<>();

    MessengerAdapter recyclerViewAdapter;


    public MessengerFragment() {
        // Required empty public constructor
    }

    public MessengerFragment(RoomDB db) {
        this.db = db;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger, container, false);
        // Inflate the layout for this fragment

        contactsSpinner = view.findViewById(R.id.contacts_spinner);
        cancelTV = view.findViewById(R.id.cancel_text_up);
        messageWindowET = view.findViewById(R.id.editTextTextPersonName);
        sendIV = view.findViewById(R.id.send);
        quickAnswersCV = view.findViewById(R.id.quick_answersFirstCV);
        messagePBar = view.findViewById(R.id.messages_progressBar);


        hSymbolTV = view.findViewById(R.id.hSymbol);
        messageHistoryRV = view.findViewById(R.id.recyclerView);



        if (savedInstanceState != null) {
            messageItems = savedInstanceState.getParcelableArrayList("messages");
        } else {
            messageItems.clear();
            for (TableMessageItem item : db.myDao().getAll())
                messageItems.add(new MessagesItem(item.getMessage(), item.getTimeStamp(), item.getSender(), item.getId()));

        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAdapter = new MessengerAdapter(messageItems);
        messageHistoryRV.setLayoutManager(layoutManager);
        messageHistoryRV.setAdapter(recyclerViewAdapter);

        ArrayAdapter<CharSequence> spinnerArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, contactsAList());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactsSpinner.setAdapter(spinnerArrayAdapter);



        contactsSpinner.setSelection(0,false);
        contactsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        quickAnswersCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHintQuickMessage();
                QuickAnswersBottomSheetFragment quickAnswersBottomSheetFragmen = new QuickAnswersBottomSheetFragment();

                quickAnswersBottomSheetFragmen.show(getParentFragmentManager(), QuickAnswersBottomSheetFragment.TAG);


            }
        });


        sendIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageWindowET.getEditText().toString().equals("")) {
                    Toast.makeText(getContext(), "empty", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "error downloading: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    messagePBar.setVisibility(View.GONE);
                });

            }
        });
        recyclerViewAdapter.setItemClickListener(new MessengerAdapter.ItemClickListener() {
            @Override
            public void OnClick(int position) {
                Toast.makeText(getContext(), "clicked on: " + messageItems.get(position).getMessage(), Toast.LENGTH_SHORT).show();
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


        return view;

    }

    private void hideHintQuickMessage() {
        quickAnswersCV.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
        quickAnswersCV.setVisibility(View.GONE);
        hSymbolTV.setVisibility(View.GONE);
    }


    public void showHint() {
        if (quickAnswersCV.getVisibility() == View.VISIBLE && hSymbolTV.getVisibility() == View.VISIBLE)
            return;
        quickAnswersCV.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        quickAnswersCV.setVisibility(View.VISIBLE);
        hSymbolTV.setVisibility(View.VISIBLE);
    }

    private ArrayList<String> contactsAList() {
        ArrayList<String> contacts_array = new ArrayList<>();
        contacts_array.add("Taxiplon");
        contacts_array.add("IQ Taxi");
        contacts_array.add("Mobility");
        contacts_array.add("Orestis");

        return contacts_array;
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
        KeyboardUtils.hideSoftInput(getActivity());
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
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
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


}