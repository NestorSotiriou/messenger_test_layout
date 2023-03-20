package com.example.messengertestlayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class QuickAnswersBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = QuickAnswersBottomSheetFragment.class.getCanonicalName();
    BottomSheetDialog dialog;
    View rootView;
    BottomSheetBehavior<View> bottomSheetBehavior;

    QuickAnswersAdapter quickAnswersAdapter;

    RecyclerView quickAnswersListRV;
    ArrayList<String> quickMessages = fakeqal();

    private MyViewModel viewModel;



    public QuickAnswersBottomSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyViewModel viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        boolean isSheetExpanded = viewModel.isSheetExpanded();


        rootView = inflater.inflate(R.layout.quick_message_bottom_sheetfragm, container, false);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.setSheetExpanded(viewModel.isSheetExpanded());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);


        CoordinatorLayout layout = dialog.findViewById(R.id.quick_message_bottomsheetfragment_CL);
        RecyclerView recyclerView = dialog.findViewById(R.id.quick_answersRV);


        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);


        RecyclerView.LayoutManager QMlayoutManager = new LinearLayoutManager(requireContext());
        quickAnswersAdapter = new QuickAnswersAdapter(quickMessages);

        recyclerView.setLayoutManager(QMlayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(quickAnswersAdapter);

        quickAnswersAdapter.setItemClickListener(new QuickAnswersAdapter.ItemClickListener() {
            @Override
            public void OnClick(int position) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null)
                    mainActivity.sendMessage(quickMessages.get(position));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (mainActivity != null)
                    mainActivity.showHint();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null)
                    mainActivity.showHint();
            }
        });


    }



    private ArrayList<String> fakeqal() {
        ArrayList<String> quickMessages = new ArrayList<>();
        quickMessages.add("paok");
        quickMessages.add("ekdromes");
        quickMessages.add("narkotika");
        quickMessages.add("etsi mathame");
        quickMessages.add("apo");
        quickMessages.add("paidia");
        quickMessages.add("paidia1");
        quickMessages.add("paidia2");
        quickMessages.add("paidia3");
        quickMessages.add("paidia4");
        quickMessages.add("paidia5");
        quickMessages.add("paidia6");

        return quickMessages;
    }
}
