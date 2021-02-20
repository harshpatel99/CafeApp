package com.hash.cafeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Date;
import java.util.Objects;

public class AuthenticationSelectFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("lastSignIn").setValue(new Date().getTime());

            FirebaseDatabase.getInstance().getReference("cart")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("tokenID").setValue(instanceIdResult.getToken());
                }
            });

            startActivity(new Intent(getContext(),MainActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        }

        View view = inflater.inflate(R.layout.fragment_authentication_select, container, false);

        ExtendedFloatingActionButton signIn = view.findViewById(R.id.fabAuthSelectSignIn);
        ExtendedFloatingActionButton signUp = view.findViewById(R.id.fabAuthSelectSignUp);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SignInFragment());
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SignUpFragment());
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        transaction.replace(R.id.auth_frame_container, fragment);
        transaction.commit();
    }

}
