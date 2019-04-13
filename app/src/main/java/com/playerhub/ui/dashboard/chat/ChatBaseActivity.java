package com.playerhub.ui.dashboard.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.playerhub.ui.base.BaseActivity;

public abstract class ChatBaseActivity extends BaseActivity {

    protected static final String TAG = "ChatBaseActivity";

    protected abstract class TextWatcherListener implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onChanged(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

            afterChanged(s);

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public abstract void onChanged(CharSequence s);

        public abstract void afterChanged(Editable s);
    }


    protected abstract class ValueListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            hideLoading();

            onSuccess(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

            hideLoading();

            Log.e(TAG, "onCancelled: " + databaseError.getMessage() + "  " + databaseError.getDetails());

        }

        public abstract void onSuccess(@NonNull DataSnapshot dataSnapshot);


    }


    protected abstract class ChildListener implements ChildEventListener {


        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            hideLoading();

            onAdded(dataSnapshot);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            hideLoading();
            Log.e(TAG, "onChildChanged: " + dataSnapshot.getValue());
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            hideLoading();
            Log.e(TAG, "onChildRemoved: " + dataSnapshot.getValue());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            hideLoading();
            Log.e(TAG, "onChildMoved: " + dataSnapshot.getValue());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            hideLoading();
            Log.e(TAG, "onCancelled: fetching message error " + databaseError.getMessage());

        }


        public abstract void onAdded(DataSnapshot dataSnapshot);
    }


}
