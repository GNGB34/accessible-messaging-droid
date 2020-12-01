package com.mana.accessiblemessaging.data;

import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.accessiblemessaging.NotificationWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.ImmutableSortedMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private FirebaseAuth mAuth;
    private FirebaseDatabase linker;
    private DatabaseReference reference;

    public LoginDataSource() {

        linker = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public Result<FirebaseUser> login(String username, String password, String androidID) {

        try {
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnFailureListener(new OnFailureListener() {
                        @RequiresApi(api = Build.VERSION_CODES.R)
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mAuth.createUserWithEmailAndPassword(username, password);
                            reference = linker.getReference();

                            //since we are creating a new account from scratch we need to push the entire data-wrapper object that stores the preferences
                            //and notifications of the user-account within the firebase database
                            //  L-> this is a static implementation of the default settings for every user account
                            HashMap<String, Boolean> apps = new HashMap<>();
                            apps.put("facebook", true);
                            apps.put("messenger", true);
                            apps.put("whatsapp", true);

                            PermissionsWrapper newAccountPermissions = new PermissionsWrapper();
                            newAccountPermissions.setApps(apps);
                            newAccountPermissions.setLanguage("en");

                            DataWrapper newAccountSettingsWrapper = new DataWrapper();
                            newAccountSettingsWrapper.setPermissions(newAccountPermissions);
                            newAccountSettingsWrapper.setNotifications(new HashMap<String, NotificationWrapper>());
                            //end of static implementation of user-data in the wrapper object

                            //we are creating a new hashmap which takes an Object-value, this is to mimic a json-sheet used by the Firebase Database
                            HashMap<String, Object> newAccountBranch = new HashMap<>();
                            while (mAuth.getCurrentUser() == null) {
                                //fucking asynchronous bullshit, burn in here till it is updated
                            }
                            newAccountBranch.put(mAuth.getCurrentUser().getUid(), newAccountSettingsWrapper); //we use the UiD of the device as the reference to this new embedded json sheet
                            reference.updateChildren(newAccountBranch);
                        }
                    });
            FirebaseUser userReference = mAuth.getCurrentUser();
            if (userReference != null) {

                //reference = linker.getReference(mAuth.getCurrentUser().getUid());
                //TODO - we need to check if the device we logged into (for its device-security-id) matches a one in the database.
                //String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                //reference.child()

                return new Result.Success<>(userReference);
            }
            else {
                throw new Resources.NotFoundException();
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {

        mAuth.signOut();
    }
}