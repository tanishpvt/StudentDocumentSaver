package com.example.studentdatacard;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;


import com.example.studentdatacard.Document.DocumentActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DashboardActivity extends AppCompatActivity {

    //firebase auth
    FirebaseAuth firebaseAuth;


    //id for creation of channel
    private static final String ID="some_id";
    private static final String NAME="FirebaseAPP";
    ActionBar actionBar;

    String mUID;

    private BottomNavigationView navigationView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // createNotificationChannel();

        // action bar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("profile");

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        // bottom navigation
        navigationView =findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        // home fragment transacstion(default, on start)
        actionBar.setTitle("Home"); // change acyion bar title
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();

        checkUserStatus();


    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }




    private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    // handle item click
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            // home fragment transacstion
                            actionBar.setTitle("Home"); // change acyion bar title
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,fragment1,"");
                            ft1.commit();
                            return true;


                        case R.id.nav_profile:
                            // profile fragment transacstion
                            actionBar.setTitle("Profile"); // change acyion bar title
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,fragment2,"");
                            ft2.commit();
                            return true;


//                        case R.id.nav_users:
//                            // users fragment transacstion
//                            actionBar.setTitle("Users"); // change acyion bar title
//                            UsersFragment fragment3 = new UsersFragment();
//                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
//                            ft3.replace(R.id.content,fragment3,"");
//                            ft3.commit();
//                            return true;

                        case R.id.nav_document:
                            // users fragment transacstion
                            actionBar.setTitle("Document"); // change acyion bar title

                            Intent intent = new Intent(getApplicationContext(), DocumentActivity.class);
                            startActivity(intent);
//                            DocumentFragment fragment4 = new DocumentFragment();
//                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
//                            ft4.replace(R.id.content,fragment4,"");
//                            ft4.commit();
                            return true;
                    }
                    return false;
                }
            };


    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            // set email of logged in user
            // mProfileTv.setText(user.getEmail());
            mUID = user.getUid();

            // save uid of currently signed in user in shared prefrences
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

        }
        else{
            //user not signed in, go to main activity
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void onStart() {
        // check on start of app
        checkUserStatus();
        super.onStart();
    }

}

