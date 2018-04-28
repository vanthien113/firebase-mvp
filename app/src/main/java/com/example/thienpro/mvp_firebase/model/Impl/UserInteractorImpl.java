package com.example.thienpro.mvp_firebase.model.Impl;

import android.support.annotation.NonNull;

import com.example.thienpro.mvp_firebase.model.UserInteractor;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 *- Lớp M: xử lý dữ liệu -> Trả dữ liệu về P thông qua callback
 * */

/**
 * Created by ThienPro on 11/10/2017.
 */

public class UserInteractorImpl extends BaseInteractorImpl implements UserInteractor {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser users;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference ref;

    public UserInteractorImpl() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        users = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public void verifiEmail(final StringCallback callback) {
        if (users != null) {
            users.reload();
            if (users.isEmailVerified()) {
                callback.onFinish(null, null);
            } else if (!users.isEmailVerified()) {
                users.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                callback.onFinish(null, users.getEmail());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callback.onFinish(e, users.getEmail());
                            }
                        });
            }
        }
    }

    public void register(final String email, String password, final String name, final String address, final boolean sex, final ExceptionCheckCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            users = mAuth.getCurrentUser();
                            User user = new User(users.getUid(), email, name, address, sex, null, null);
                            mDatabase.child(USERS).child(users.getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            callback.onFinish(null);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            callback.onFinish(e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFinish(e);
                    }
                });
    }

    @Override
    public void changePassword(String password, final ExceptionCheckCallback callback) {
        users.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.onFinish(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFinish(e);
            }
        });
    }

    @Override
    public void forgotPassword(String email, final ExceptionCheckCallback callback) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.onFinish(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFinish(e);
            }
        });
    }

    @Override
    public void getFriendInfomation(String userId, final UserCallback callback) {
        mDatabase.child(USERS).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onFinish(null, user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFinish(databaseError, null);
            }
        });
    }

    @Override
    public void searchUser(final String userName, final SearchUserCallBack callBack) {
        mDatabase.child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> list = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user = dsp.getValue(User.class);
                    if (user.getName().toLowerCase().contains(userName.toLowerCase())) {
                        list.add(user);
                    }
                }
                if (list.size() == 0) {
                    callBack.onFinish(null, null);
                } else {
                    callBack.onFinish(null, list);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callBack.onFinish(databaseError, null);
            }
        });
    }

    public void updateUser(final String name, String address, Boolean sex, final ExceptionCheckCallback callback) {
        String userId = users.getUid();

        HashMap<String, Object> result = new HashMap<>();
        result.put(NAME, name);
        result.put(ADDRESS, address);
        result.put(SEX, sex);

        mDatabase.child(USERS).child(userId).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUseInfoInPost(name, callback);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFinish(e);
                    }
                });
    }

    private void updateUseInfoInPost(final String name, final ExceptionCheckCallback callback) {
        mDatabase.child(POSTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) dsp.getValue();
                    String id = (String) map.get(ID);
                    String timePost = (String) map.get(TIMEPOST);

                    if (id.equals(users.getUid())) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put(NAME, name);

                        mDatabase.child(POSTS).child(timePost).updateChildren(result)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callback.onFinish(e);
                                    }
                                });
                    }
                }
                callback.onFinish(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void addAvatar(String avatarUrl, ExceptionCheckCallback callback) {
        editAvatarInUser(avatarUrl, callback);
    }

    @Override
    public void addCover(final String coverUrl, final ExceptionCheckCallback callback) {
        String userId = users.getUid();

        HashMap<String, Object> result = new HashMap<>();
        result.put(COVER, coverUrl);

        mDatabase.child(USERS).child(userId).updateChildren(result)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.onFinish(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFinish(e);
                    }
                });
    }

    private void editAvatarInPost(final String avatarUrl, final ExceptionCheckCallback callback) {
        mDatabase.child(POSTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) dsp.getValue();
                    String id = (String) map.get(ID);
                    String timePost = (String) map.get(TIMEPOST);

                    if (id.equals(users.getUid().toString())) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put(AVATAR, avatarUrl);

                        mDatabase.child(POSTS).child(timePost).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                callback.onFinish(null);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callback.onFinish(e);

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    private void editAvatarInUser(final String avatarUrl, final ExceptionCheckCallback callback) {
        String userId = users.getUid();

        HashMap<String, Object> result = new HashMap<>();
        result.put(AVATAR, avatarUrl);

        mDatabase.child(USERS).child(userId).updateChildren(result)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editAvatarInPost(avatarUrl, callback);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFinish(e);
                    }
                });
    }

    @Override
    public void signedInCheck(BooleanCheckCallback loginCheck) {
        users = FirebaseAuth.getInstance().getCurrentUser();
        if (users != null) {
            users.reload();
            if (users.isEmailVerified()) {
                loginCheck.onFinish(true);
            } else if (!users.isEmailVerified()) {
                loginCheck.onFinish(false);
            }
        }
    }

    @Override
    public void getUser(final UserCallback callback, boolean loadUser) {
        users = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase.child(USERS).child(users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onFinish(null, user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFinish(databaseError, null);
            }
        });
    }

    public void sigIn(String email, String password, final ExceptionCheckCallback callback) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.onFinish(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFinish(e);
                    }
                });
    }

    @Override
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
