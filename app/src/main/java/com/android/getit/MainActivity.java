package com.android.getit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.getit.Utils.Utils;
import com.android.getit.fragment.BaseFragment;
import com.android.getit.fragment.FragmentHome;
import com.android.getit.fragment.FragmentLogin;
import com.android.getit.fragment.FragmentRegister;
import com.android.getit.fragment.FragmentShare;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseFragment.callBack{

    public FragmentManager mFragmentManager = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        if(test) {
//            final Uri proImageUri = Uri.parse("http://pooyak.com/p/progjpeg/jpegload.cgi?o=1"); // the best image to show loading progressive.
//            final Uri lowImageUri = Uri.parse("http://u4.tdimg.com/7/147/82/31804659546604080410941337579323207967.jpg");
////        ImageView imageView = (ImageView)findViewById(R.id.progressive);
//            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) findViewById(R.id.GetITImage);
//
//            try {
////            ImageLoaderManager.getInstance().displayImage("http://u4.tdimg.com/7/147/82/31804659546604080410941337579323207967.jpg", imageView);
////            imageView.setVisibility(View.GONE);
//                FrescoLoaderImage.loaderProgressively(simpleDraweeView, lowImageUri, FrescoLoaderImage.getProgressGDHHierarchy(getResources()));
//
//            } catch (Exception excetion) {
//                excetion.printStackTrace();
//            }
//        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, FragmentShare.newInstance(0))
                        .commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initView();

        // deal with the nav_header
    }

    public void initView() {
        ImageView loginView = (ImageView) findViewById(R.id.loginHead);
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.beginTransaction()
                        .replace(R.id.container, FragmentLogin.newInstance(0))
                        .commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            mFragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentHome.newInstance(item.getOrder() + 1))
                    .commit();

        } else if (id == R.id.nav_share) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentShare.newInstance(item.getOrder() + 1))
                    .commit();

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * this is for inner fragment navigation.
     * @param id
     * @return
     */
    @Override
    public boolean onNavigationFragmentSelected(int id) {
        if (id == R.id.nav_home) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentHome.newInstance(0))
                    .commit();
        } else if (id == Utils.REGISTER) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentRegister.newInstance(0))
                    .commit();
        }
        return false;
    }
}
