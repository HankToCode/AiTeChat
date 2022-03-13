package com.android.nanguo.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.nanguo.common.livedatas.SingleSourceLiveData;
import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.repositories.EMContactManagerRepository;

public class AddContactViewModel extends AndroidViewModel {
    private final EMContactManagerRepository mRepository;
    private final SingleSourceLiveData<Resource<Boolean>> addContactObservable;

    public AddContactViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        addContactObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getAddContact() {
        return addContactObservable;
    }

    public void addContact(String username, String reason) {
        addContactObservable.setSource(mRepository.addContact(username, reason));
    }

}
