package com.fj.aux.network;

import android.util.Log;

import com.fj.aux.network.api.DeviceApi;
import com.fj.aux.network.api.UserApi;
import com.fj.aux.network.entity.BaseResult;
import com.fj.aux.network.entity.notification.Notification;
import com.fj.aux.network.entity.notification.NotificationResponse;
import com.fj.aux.network.entity.user.User;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.stardust.util.Objects;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by Stardust on 2017/9/20.
 */

public class DeviceService {

    public static class LoginStateChange {
        private final boolean mOnline;

        public LoginStateChange(boolean online) {
            mOnline = online;
        }

        public boolean isOnline() {
            return mOnline;
        }
    }

    private static final DeviceService sInstance = new DeviceService();
    private final Retrofit mRetrofit;
    private DeviceApi mDeviceApi;
    private volatile User mUser;

    DeviceService() {
        mRetrofit = NodeBB.getInstance().getRetrofit();
        mDeviceApi = mRetrofit.create(DeviceApi.class);
    }

    public static DeviceService getInstance() {
        return sInstance;
    }

    public Observable<BaseResult> verify(String deviceKey, final String ts, final String sign) {
        return mDeviceApi.verify(deviceKey, ts, sign)
                .doOnError(error -> {
                    Log.e("xpf",error.getMessage());
                })
                .map(responseBody -> new Gson().fromJson(responseBody.string(), BaseResult.class));
    }


    private void setUser(User user) {
        User old = mUser;
        mUser = user;
        if(mUser != null){
            CrashReport.setUserId(mUser.getUid());
        }
        if (!Objects.equals(old, mUser)) {
            if (user == null) {
                NodeBB.getInstance().invalidateXCsrfToken();
            }
        }
    }

    public Observable<Boolean> refreshOnlineStatus() {
        PublishSubject<Boolean> online = PublishSubject.create();
        mRetrofit.create(UserApi.class)
                .me()
                .subscribeOn(Schedulers.io())
                .subscribe(user -> {
                    setUser(user);
                    online.onNext(true);
                    online.onComplete();
                }, error -> {
                    setUser(null);
                    online.onNext(false);
                    online.onComplete();
                });
        return online;
    }

    public Observable<Boolean> refreshVerifyStatus() {
        PublishSubject<Boolean> online = PublishSubject.create();
        return online;
    }

    public Observable<User> me() {
        return NodeBB.getInstance().getRetrofit()
                .create(UserApi.class)
                .me()
                .doOnNext(this::setUser)
                .doOnError(error -> setUser(null));
    }


    public Observable<List<Notification>> getNotifications() {
        return NodeBB.getInstance().getRetrofit()
                .create(UserApi.class)
                .getNotifitions()
                .map(NotificationResponse::getNotifications);
    }

}
