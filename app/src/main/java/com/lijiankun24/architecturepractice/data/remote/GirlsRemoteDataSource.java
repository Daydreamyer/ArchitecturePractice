package com.lijiankun24.architecturepractice.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.lijiankun24.architecturepractice.data.GirlsDataSource;
import com.lijiankun24.architecturepractice.data.local.db.entity.Girl;
import com.lijiankun24.architecturepractice.data.remote.api.ApiGirl;
import com.lijiankun24.architecturepractice.data.remote.api.ApiManager;
import com.lijiankun24.architecturepractice.data.remote.api.ApiZhihu;
import com.lijiankun24.architecturepractice.data.remote.model.GirlData;
import com.lijiankun24.architecturepractice.data.remote.model.ZhihuData;
import com.lijiankun24.architecturepractice.data.remote.model.ZhihuStory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * GirlsRemoteDataSource.java
 * <p>
 * Created by lijiankun on 17/7/7.
 */

public class GirlsRemoteDataSource implements GirlsDataSource {

    private static GirlsRemoteDataSource INSTANCE = null;

    private final MutableLiveData<Boolean> mIsLoadingGirlList;

    private final MutableLiveData<Boolean> mIsLoadingZhihuList;

    private final MutableLiveData<List<Girl>> mGirlList;

    private final MutableLiveData<List<ZhihuStory>> mZhihuList;

    private final ApiGirl mApiGirl;

    private final ApiZhihu mApiZhihu;

    {
        mIsLoadingGirlList = new MutableLiveData<>();
        mIsLoadingZhihuList = new MutableLiveData<>();
        mGirlList = new MutableLiveData<>();
        mZhihuList = new MutableLiveData<>();
    }

    private GirlsRemoteDataSource() {
        mApiGirl = ApiManager.getInstance().getApiGirl();
        mApiZhihu = ApiManager.getInstance().getApiZhihu();
    }

    public static GirlsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (GirlsRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GirlsRemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<Girl>> getGirlList(int index) {
        mIsLoadingGirlList.setValue(true);
        mApiGirl.getGirlsData(index)
                .enqueue(new Callback<GirlData>() {
                    @Override
                    public void onResponse(Call<GirlData> call, Response<GirlData> response) {
                        mIsLoadingGirlList.setValue(false);
                        if (response.isSuccessful() || !response.body().error) {
                            mGirlList.setValue(response.body().results);
                        }
                    }

                    @Override
                    public void onFailure(Call<GirlData> call, Throwable t) {
                        mIsLoadingGirlList.setValue(false);
                    }
                });
        return mGirlList;
    }

    @Override
    public LiveData<Girl> getGirl(@NonNull String id) {
        return null;
    }

    @Override
    public MutableLiveData<Boolean> isLoadingGirlList() {
        return mIsLoadingGirlList;
    }


    @Override
    public LiveData<List<ZhihuStory>> getLastZhihuList() {
        mIsLoadingZhihuList.setValue(true);
        mApiZhihu.getLatestNews()
                .enqueue(new Callback<ZhihuData>() {
                    @Override
                    public void onResponse(Call<ZhihuData> call, Response<ZhihuData> response) {
                        mIsLoadingZhihuList.setValue(false);
                        if (response.isSuccessful()) {
                            mZhihuList.setValue(response.body().getStories());
                        }
                    }

                    @Override
                    public void onFailure(Call<ZhihuData> call, Throwable t) {
                        mIsLoadingZhihuList.setValue(false);
                    }
                });
        return mZhihuList;
    }

    @Override
    public LiveData<List<ZhihuStory>> getMoreZhihuList(String date) {
        mIsLoadingZhihuList.setValue(true);
        mApiZhihu.getTheDaily(date)
                .enqueue(new Callback<ZhihuData>() {
                    @Override
                    public void onResponse(Call<ZhihuData> call, Response<ZhihuData> response) {
                        mIsLoadingZhihuList.setValue(false);
                        if (response.isSuccessful()) {
                            mZhihuList.setValue(response.body().getStories());
                        }
                    }

                    @Override
                    public void onFailure(Call<ZhihuData> call, Throwable t) {
                        mIsLoadingZhihuList.setValue(false);
                    }
                });
        return mZhihuList;
    }

    @Override
    public MutableLiveData<Boolean> isLoadingZhihuList() {
        return mIsLoadingZhihuList;
    }
}
