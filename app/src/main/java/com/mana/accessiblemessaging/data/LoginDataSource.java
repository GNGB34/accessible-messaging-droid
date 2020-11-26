package com.mana.accessiblemessaging.data;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private FirebaseAuth mAuth;

    public LoginDataSource() {

        mAuth = FirebaseAuth.getInstance();
    }

    public Result<FirebaseUser> login(String username, String password) {

        try {
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                mAuth.createUserWithEmailAndPassword(username, password);
                            }
                        }
                    });

            FirebaseUser userReference = mAuth.getCurrentUser();
            if (userReference != null) {
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