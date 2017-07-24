package com.lijiankun24.architecturepractice.data.local.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.lijiankun24.architecturepractice.data.local.db.entity.Girl;

import java.util.ArrayList;
import java.util.List;

/**
 * AppDatabaseManager.java
 * <p>
 * Created by lijiankun on 17/7/5.
 */

public class AppDatabaseManager {

    private static final String DATABASE_NAME = "architecture-practice-db";

    private final MutableLiveData<Boolean> mIsLoadingGirlList;

    private final MutableLiveData<List<Girl>> mGirlList;

    private static AppDatabaseManager INSTANCE = null;

    private AppDatabase mDB = null;

    {
        mIsLoadingGirlList = new MutableLiveData<>();
        mGirlList = new MutableLiveData<>();
    }

    private AppDatabaseManager() {
    }

    public static AppDatabaseManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AppDatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppDatabaseManager();
                }
            }
        }
        return INSTANCE;
    }

    public void createDB(Context context) {
        new AsyncTask<Context, Void, Void>() {
            @Override
            protected Void doInBackground(Context... params) {
                Context context = params[0].getApplicationContext();
                mDB = Room.databaseBuilder(context,
                        AppDatabase.class, DATABASE_NAME).build();
                return null;
            }
        }.execute(context.getApplicationContext());
    }

    public void insertGirls(final List<Girl> girls) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mDB.beginTransaction();
                try {
                    mDB.girlDao().insertGirls(girls);
                    mDB.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDB.endTransaction();
                }
                return null;
            }
        }.execute();

    }

    public LiveData<List<Girl>> loadGirlList() {
        mIsLoadingGirlList.setValue(true);
        new AsyncTask<Void, Void, List<Girl>>() {
            @Override
            protected List<Girl> doInBackground(Void... voids) {
                List<Girl> results = new ArrayList<>();
                mDB.beginTransaction();
                try {
                    results.addAll(mDB.girlDao().loadAllGirls());
                    mDB.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDB.endTransaction();
                }
                return results;
            }

            @Override
            protected void onPostExecute(List<Girl> aVoid) {
                super.onPostExecute(aVoid);
                mIsLoadingGirlList.setValue(false);
                mGirlList.setValue(aVoid);
            }

            @Override
            protected void onCancelled(List<Girl> aVoid) {
                super.onCancelled(aVoid);
                mIsLoadingGirlList.setValue(false);
            }
        }.execute();
        return mGirlList;
    }
}
