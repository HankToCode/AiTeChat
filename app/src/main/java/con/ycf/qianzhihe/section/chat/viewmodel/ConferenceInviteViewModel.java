package con.ycf.qianzhihe.section.chat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import con.ycf.qianzhihe.common.livedatas.SingleSourceLiveData;
import con.ycf.qianzhihe.common.net.Resource;
import con.ycf.qianzhihe.common.repositories.EMConferenceManagerRepository;
import con.ycf.qianzhihe.section.chat.model.KV;

import java.util.List;

public class ConferenceInviteViewModel extends AndroidViewModel {
    EMConferenceManagerRepository repository;
    private SingleSourceLiveData<Resource<List<KV<String, Integer>>>> conferenceInviteObservable;

    public ConferenceInviteViewModel(@NonNull Application application) {
        super(application);
        repository = new EMConferenceManagerRepository();
        conferenceInviteObservable = new SingleSourceLiveData<>();
    }

    public void getConferenceMembers(String groupId,String[] existMember) {
        conferenceInviteObservable.setSource(repository.getConferenceMembers(groupId,existMember));
    }

    public LiveData<Resource<List<KV<String, Integer>>>> getConferenceInvite() {
        return conferenceInviteObservable;
    }
}
