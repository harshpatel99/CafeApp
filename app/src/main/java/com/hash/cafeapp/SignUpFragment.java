package com.hash.cafeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Date;
import java.util.Objects;

public class SignUpFragment extends Fragment {

    private FirebaseAuth auth;

    private TextInputEditText nameET, emailET, phoneET, passwordET, confpassET, compIDET;
    private ExtendedFloatingActionButton fab;
    private ImageView loadingIV;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        nameET = view.findViewById(R.id.signUpNameEditText);
        emailET = view.findViewById(R.id.signUpEmailEditText);
        phoneET = view.findViewById(R.id.signUpMobileNoEditText);
        passwordET = view.findViewById(R.id.signUpPasswordEditText);
        confpassET = view.findViewById(R.id.signUpConfirmPasswordEditText);
        compIDET = view.findViewById(R.id.signUpCompIDEditText);
        fab = view.findViewById(R.id.fabSignUp);
        loadingIV = view.findViewById(R.id.loadingIV);
        Glide.with(Objects.requireNonNull(getContext())).load(R.drawable.loading_circles)
                .placeholder(R.drawable.rounded_rectangle)
                .into(loadingIV);
        TextView login = view.findViewById(R.id.loginInSignUp);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SignInFragment());
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = Objects.requireNonNull(emailET.getText()).toString();
                final String password = Objects.requireNonNull(passwordET.getText()).toString();
                final String phone = Objects.requireNonNull(phoneET.getText()).toString();
                final String confPass = Objects.requireNonNull(confpassET.getText()).toString();
                final String compID = Objects.requireNonNull(compIDET.getText()).toString();
                final String name = Objects.requireNonNull(nameET.getText()).toString();
                final int userType = User.CUSTOMER_TYPE;
                final Long date = new Date().getTime();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    nameET.setError("Required");
                    return;
                }

                if (TextUtils.isEmpty(compID)) {
                    Toast.makeText(getContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    compIDET.setError("Required");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    phoneET.setError("Required");
                    return;
                }

                if(Utils.isValidMobile(phone)){
                    Toast.makeText(getContext(), "Entered mobile number is invalid", Toast.LENGTH_SHORT).show();
                    phoneET.setError("Invalid Mobile Number");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    emailET.setError("Required");
                    return;
                }

                if(Utils.isValidMail(email)){
                    Toast.makeText(getContext(), "Entered e-mail is invalid", Toast.LENGTH_SHORT).show();
                    emailET.setError("Invalid email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    passwordET.setError("Required");
                    return;
                }

                if (TextUtils.isEmpty(confPass)) {
                    Toast.makeText(getContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    confpassET.setError("Required");
                    return;
                }


                if (password.length() < 6) {
                    Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    passwordET.setError("Required");
                    return;
                }

                if (!password.equals(confPass)) {
                    Toast.makeText(getContext(), "Confirm password does not match with password", Toast.LENGTH_SHORT).show();
                    confpassET.setError("Required");
                    return;
                }

                loadingIV.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(fab, "Account Successfully Created", Snackbar.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                        User user = new User(name, email, phone, compID, userType, date);
                                        reference.child("users").child(firebaseUser.getUid()).setValue(user);

                                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                FirebaseDatabase.getInstance().getReference().child("users")
                                                        .child(auth.getCurrentUser().getUid())
                                                        .child("tokenID").setValue(instanceIdResult.getToken());
                                            }
                                        });

                                        loadingIV.setVisibility(View.GONE);
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                        Objects.requireNonNull(getActivity()).finish();
                                    }
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Snackbar.make(fab, "User Account already exists! Do you want to sign in?"
                                                , Snackbar.LENGTH_LONG).setAction("Sign In",
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        loadFragment(new SignInFragment());
                                                    }
                                                }).show();
                                    }
                                    Toast.makeText(getContext(), "Unable to create account", Toast.LENGTH_SHORT).show();
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
