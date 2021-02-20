package com.hash.cafeapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hash.cafeapp.activity.AboutActivity;
import com.hash.cafeapp.activity.HelpActivity;
import com.hash.cafeapp.activity.SettingsActivity;
import com.hash.cafeapp.activity.UserPrivilegeActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.net.InetAddress;
import java.util.Objects;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    NavigationView navigationView;

    int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (!isNetworkConnected()) {
            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Please Check Your Internet Connection And Try Again");

            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    finish();
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }

        SharedPreferences preferences = Objects.requireNonNull(this)
                .getSharedPreferences("userData", Context.MODE_PRIVATE);
        userType = preferences.getInt("userType", User.CUSTOMER_TYPE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer uType = dataSnapshot.child("userType").getValue(Integer.class);
                        if (uType == User.STORE_TYPE) {
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    FirebaseDatabase.getInstance().getReference().child("store")
                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                            .child("tokenID").setValue(instanceIdResult.getToken());
                                }
                            });
                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.activity_main_drawer_cafe);
                        } else {
                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.activity_main_drawer_customer);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        /*if (userType == User.STORE_TYPE) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    FirebaseDatabase.getInstance().getReference().child("store")
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .child("tokenID").setValue(instanceIdResult.getToken());
                }
            });
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_cafe);
        }else{
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_customer);
        }*/
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle("Home");
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        Fragment fragment = new HomeFragment();
        loadFragment(fragment);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_edit_menu) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            startActivity(new Intent(this, UpdateMenuActivity.class));
        } else if (id == R.id.nav_privilege) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            startActivity(new Intent(this, UserPrivilegeActivity.class));
        } else if (id == R.id.nav_settings) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_help) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            startActivity(new Intent(this, HelpActivity.class));
        }else if (id == R.id.nav_sign_out) {
            FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid())
                    .child("tokenID").removeValue();
            FirebaseDatabase.getInstance().getReference().child("store/" + FirebaseAuth.getInstance().getUid())
                    .child("tokenID").removeValue();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    toolbar.setTitle("Home");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_history:
                    toolbar.setTitle("History");
                    fragment = new HistoryFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    toolbar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

}
