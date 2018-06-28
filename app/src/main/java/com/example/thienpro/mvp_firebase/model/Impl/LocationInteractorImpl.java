package com.example.thienpro.mvp_firebase.model.Impl;

import android.support.annotation.NonNull;

import com.example.thienpro.mvp_firebase.model.LocationInteractor;
import com.example.thienpro.mvp_firebase.model.entity.UserLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationInteractorImpl extends BaseInteractorImpl implements LocationInteractor {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public LocationInteractorImpl() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public void pushLocation(String userId, double lat, double lng, String pushTime, final PushLocationCallback callback) {
        Map<String, Object> childUpdates = new HashMap<>();
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("time", pushTime);
        childUpdates.put(userId, result);

        mDatabase.child(LOCATIONS).updateChildren(childUpdates)
                .addOnFailureListener(e -> callback.onFinish(e))
                .addOnSuccessListener(aVoid -> callback.onFinish(null));
    }

    @Override
    public void getLocation(String userId, final GetLocationCallback callback) {
        mDatabase.child(LOCATIONS).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserLocation userLocation = dataSnapshot.getValue(UserLocation.class);
                callback.onFinish(null, userLocation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFinish(databaseError, null);

            }
        });
    }

    @Override
    public void getListLocation(final GetListLocationCallback callback) {
        mDatabase.child(LOCATIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<UserLocation> listLocation = new ArrayList<>();

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    UserLocation userLocation = dsp.getValue(UserLocation.class);
                    listLocation.add(userLocation);
                }
                callback.onFinish(listLocation, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFinish(null, databaseError);
            }
        });
    }
}
