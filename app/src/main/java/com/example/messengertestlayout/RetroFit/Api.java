package com.example.messengertestlayout.RetroFit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    @GET("users/1/todos/")
    Call<List<ItemModel>> getItem();
}
