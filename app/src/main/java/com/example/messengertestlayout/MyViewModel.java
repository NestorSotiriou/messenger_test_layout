package com.example.messengertestlayout;

import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private boolean isSheetExpanded;

    public MyViewModel() {
        isSheetExpanded = false;
    }

    public boolean isSheetExpanded() {
        return isSheetExpanded;
    }

    public void setSheetExpanded(boolean sheetExpanded) {
        isSheetExpanded = sheetExpanded;
    }
}
