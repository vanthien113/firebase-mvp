package com.example.thienpro.mvp_firebase.model.Impl;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.thienpro.mvp_firebase.model.UserInteractor;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.ultils.SharedPreferencesUtil;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 *- Lớp M: xử lý dữ liệu -> Trả dữ liệu về P thông qua callback
 * */

/**
 * Created by ThienPro on 11/10/2017.
 */

public class UserInteractorImpl implements UserInteractor {
    private static final String USERS = "users";
    private static final String POSTS = "posts";
    private static final String AVATARS = "avatars";

    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String SEX = "sex";
    private static final String AVATAR = "avatar";
    private static final String COVER = "cover";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser users;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference ref;
    private SharedPreferencesUtil currentUser;

    public UserInteractorImpl(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        users = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        currentUser = new SharedPreferencesUtil(context);
    }

    @Override
    public void verifiEmail(final VerifiEmailCheckCallback callback) {
        if (users != null) {
            users.reload();
            if (users.isEmailVerified()) {
                callback.checker(null, null);
            } else if (!users.isEmailVerified()) {
                users.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                callback.checker(null, users.getEmail());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callback.checker(e, users.getEmail());
                            }
                        });
            }
        }
    }

    //Should Hard Avatar
    public void register(final String email, String password, final String name, final String address, final boolean sex, final RegisterCheckCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            users = mAuth.getCurrentUser();
                            User user = new User(email, name, address, sex, null, null);
                            mDatabase.child(USERS).child(users.getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            callback.checker(null);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            callback.checker(e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.checker(e);
                    }
                });
    }

    @Override
    public void loadCurrentLocalUser(LoadCurrentLocalUserCallback callback) {
        callback.currentLocalUser(currentUser.getUser());
    }

    @Override
    public void saveCurrentLocalUser(User user) {
        currentUser.setUser(user);
    }

    public void updateUser(final String name, String address, Boolean sex, final UpdateUserCallback callback) {
        String userId = users.getUid();

        HashMap<String, Object> result = new HashMap<>();
        result.put(NAME, name);
        result.put(ADDRESS, address);
        result.put(SEX, sex);

        mDatabase.child(USERS).child(userId).updateChildren(result).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.updateUser(e);
            }
        });

        //Edit name in post
        mDatabase.child(POSTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) dsp.getValue();
                    String id = (String) map.get("id");
                    String timePost = (String) map.get("timePost");

                    if (id.equals(users.getUid())) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put(NAME, name);

                        mDatabase.child(POSTS).child(timePost).updateChildren(result)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callback.updateUser(e);
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        callback.updateUser(null);
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

    public void addAvatar(final Uri uri, final AddAvatarCallback callback) {
        //Up im

        if (uri != null) {
            ref = storageReference.child("avatars/" + UUID.randomUUID().toString());
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            //add in usr
                                            String userId = users.getUid();

                                            HashMap<String, Object> result = new HashMap<>();
                                            result.put(AVATAR, uri.toString());

                                            mDatabase.child(USERS).child(userId).updateChildren(result)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            //Edit avatar in post
                                                            mDatabase.child(POSTS).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                                        @SuppressWarnings("unchecked")
                                                                        Map<String, Object> map = (Map<String, Object>) dsp.getValue();
                                                                        String id = (String) map.get("id");
                                                                        String timePost = (String) map.get("timePost");

                                                                        if (id.equals(users.getUid().toString())) {
                                                                            HashMap<String, Object> result = new HashMap<>();
                                                                            result.put(AVATAR, uri.toString());

                                                                            mDatabase.child(POSTS).child(timePost).updateChildren(result);
                                                                        }
                                                                    }
                                                                    callback.addAvatar(null, uri.toString());

                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }

                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            callback.addAvatar(e, null);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            callback.addAvatar(e, null);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.addAvatar(e, null);
                        }
                    });
        }
    }

    @Override
    public void addCover(final Uri coverUri, final AddCoverCallback callback) {
        //Up im

        if (coverUri != null) {
            ref = storageReference.child("covers/" + UUID.randomUUID().toString());
            ref.putFile(coverUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            //add in usr
                                            String userId = users.getUid();

                                            HashMap<String, Object> result = new HashMap<>();
                                            result.put(COVER, uri.toString());

                                            mDatabase.child(USERS).child(userId).updateChildren(result)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            callback.addCover(null, uri.toString());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            callback.addCover(e, null);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            callback.addCover(e, null);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.addCover(e, null);
                        }
                    });
        }
    }

    @Override
    public void signedInCheck(LoggedInCheckCallback loginCheck) {
        if (users != null) {
            users.reload();
            if (users.isEmailVerified()) {
                loginCheck.checker(1);
            } else if (!users.isEmailVerified()) {
                loginCheck.checker(2);
            }
        }
        loginCheck.checker(0);
    }

    @Override
    public void getUser(final GetUserCallback callback, boolean loadUser) {
        users = mAuth.getCurrentUser();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                String address = (String) map.get(ADDRESS);
                String email = (String) map.get(EMAIL);
                String name = (String) map.get(NAME);
                Boolean sex = (Boolean) map.get(SEX);
                String avatar = (String) map.get(AVATAR);
                String cover = (String) map.get(COVER);

                User user = new User(email, name, address, sex, avatar, cover);

                currentUser.setUser(user);

                callback.getUser(null, user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.getUser(databaseError, null);
            }
        };

        mDatabase.child(USERS).child(users.getUid()).addValueEventListener(valueEventListener);
    }

    public void sigIn(String email, String password, final LoginCheckCallback callback) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.checker(true, null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.checker(false, e);
                    }
                });
    }

    @Override
    public void logOut(LogoutCheckCallback callback) {
        FirebaseAuth.getInstance().signOut();
        callback.checker(true);
    }
}
