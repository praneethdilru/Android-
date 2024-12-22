package com.theempires.jetshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,NavigationBarView.OnItemSelectedListener {


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolBar);
        bottomNavigationView = findViewById(R.id.bottomNavigation);


        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }



        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);
    }



    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : "";
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.sideNavHome) {
            loadFragment(new HomeFragment());}
            else if (itemId == R.id.sideNavProfile) {
                loadActivity(new UserActivity());
        }else if (itemId == R.id.bottomNavHome) {
            loadFragment(new HomeFragment());
        }else if (itemId == R.id.sideNavLogout) {
            loadActivity(new LoginActivity());
            finish();
        }else if (itemId == R.id.bottomNavCategory) {
            loadFragment(new CategoryFragment());
        }else if (itemId == R.id.bottomNavProfile) {
            loadActivity(new UserActivity());
        }else if (itemId == R.id.bottomNavCart) {
            loadActivity(new CartActivity());
        }else if (itemId == R.id.sideNavOrders) {
            loadActivity(new CartActivity());
        }
        return true;
    }




    public void loadActivity(Activity activityToLoad) {
        Intent intent = new Intent(this, activityToLoad.getClass());
        startActivity(intent);
    }


    public void loadFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putString("userId", getCurrentUserId());
        fragment.setArguments(bundle);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }
}