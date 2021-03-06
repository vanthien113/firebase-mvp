package com.example.thienpro.mvp_firebase.model;

import com.example.thienpro.mvp_firebase.model.entity.UserLocation;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public interface LocationInteractor {
    interface GetLocationCallback {
        void onFinish(DatabaseError e, UserLocation location);
    }

    interface PushLocationCallback {
        void onFinish(Exception e);
    }

    interface GetListLocationCallback {
        void onFinish(List<UserLocation> locations, DatabaseError e);
    }

    void pushLocation(String userId, double lat, double lng, String pushTime, PushLocationCallback callback);

    void getLocation(String userId, GetLocationCallback callback);

    void getListLocation(GetListLocationCallback callback);
}
