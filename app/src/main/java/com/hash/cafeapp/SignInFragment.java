package com.hash.cafeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;

public class SignInFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference reference;

    private TextInputEditText emailET, passwordET;
    private ExtendedFloatingActionButton fab;
    private ImageView loadingIV;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        emailET = view.findViewById(R.id.signInEmailEditText);
        passwordET = view.findViewById(R.id.signInPasswordEditText);
        fab = view.findViewById(R.id.fabSignIn);
        loadingIV = view.findViewById(R.id.loadingIV);
        Glide.with(Objects.requireNonNull(getContext())).load(R.drawable.loading_circles)
                .placeholder(R.drawable.rounded_rectangle)
                .into(loadingIV);
        TextView signUp = view.findViewById(R.id.signUpInLogin);
        TextView forgotPass = view.findViewById(R.id.forgotPasswordTextView);

        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SignUpFragment());
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialogBuilder = new AlertDialog
                        .Builder(Objects.requireNonNull(getContext())).create();
                LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);

                final TextInputEditText editText = dialogView.findViewById(R.id.forgotPassEmailEditText);
                MaterialButton buttonCancel = dialogView.findViewById(R.id.forgotPassCancelButton);
                MaterialButton buttonSend = dialogView.findViewById(R.id.forgotPassSendButton);

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                buttonSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String email = Objects.requireNonNull(editText.getText()).toString();

                        if(TextUtils.isEmpty(email)){
                            Toast.makeText(getContext(),"Please fill e-mail",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(),"Password reset link was sent on your email address",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getContext(),"Error while sending mail",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = Objects.requireNonNull(emailET.getText()).toString();
                final String password = Objects.requireNonNull(passwordET.getText()).toString();
                final Long date = new Date().getTime();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    emailET.setError("Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    passwordET.setError("Required");
                    return;
                }

                loadingIV.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(fab, "Sign In Successful", Snackbar.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        reference = FirebaseDatabase.getInstance().getReference();
                                        reference.child("users").child(firebaseUser.getUid()).child("lastSignIn").setValue(date);
                                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                FirebaseDatabase.getInstance().getReference().child("users")
                                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance()
                                                                .getCurrentUser()).getUid())
                                                        .child("tokenID").setValue(instanceIdResult.getToken());
                                            }
                                        });


                                        SharedPreferences preferences = Objects.requireNonNull(getContext())
                                                .getSharedPreferences("userData", Context.MODE_PRIVATE);
                                        final SharedPreferences.Editor editor = preferences.edit();
                                        reference.child("users").child(firebaseUser.getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                assert user != null;
                                                editor.putInt("userType",user.getUserType());
                                                editor.apply();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        loadingIV.setVisibility(View.GONE);
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                        Objects.requireNonNull(getActivity()).finish();
                                    }
                                } else {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        loadingIV.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Email or Password doesn't match", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loadingIV.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Error in authentication", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
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
