package con.ycf.qianzhihe.section.account.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import con.ycf.qianzhihe.common.net.Resource;
import con.ycf.qianzhihe.common.repositories.EMClientRepository;
import com.hyphenate.easeui.domain.EaseUser;

public class LoginFragmentViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private MediatorLiveData<Resource<EaseUser>> loginObservable;

    public LoginFragmentViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        loginObservable = new MediatorLiveData<>();
    }

    /**
     * 登录环信
     */
    public void HXlogin() {

        loginObservable.addSource(mRepository.loginToServer(), response -> {
            loginObservable.setValue(response);
        });
    }

    public LiveData<Resource<EaseUser>> getLoginObservable() {
        return loginObservable;
    }
}
