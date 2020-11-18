package com.example.proyectovinoteca;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class perfilViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public perfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is PROFILE");
    }

    public LiveData<String> getText() {
        return mText;
    }
}