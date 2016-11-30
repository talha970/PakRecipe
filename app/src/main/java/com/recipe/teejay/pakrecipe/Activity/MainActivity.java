package com.recipe.teejay.pakrecipe.Activity;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.recipe.teejay.pakrecipe.DB.DbManager;
import com.recipe.teejay.pakrecipe.Fragments.AllRecFrag;
import com.recipe.teejay.pakrecipe.Fragments.BookmarkFrag;
import com.recipe.teejay.pakrecipe.Fragments.CatFrag;
import com.recipe.teejay.pakrecipe.Models.Category;
import com.recipe.teejay.pakrecipe.Models.Recipe;
import com.recipe.teejay.pakrecipe.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

public static ArrayList<Recipe> mrecipes;
    public static ArrayList<Recipe> mrecipescopy;
    public static ArrayList<Category> mcategories;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CatFrag recfrag=new CatFrag();
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        android.support.v4.app.FragmentTransaction fragtrans = getSupportFragmentManager().beginTransaction();
        fragtrans.replace(R.id.fragcontainer,recfrag);
fragtrans.commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
       drawer.openDrawer(GravityCompat.START);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DbManager myDbHelper = new DbManager(this);


        try {

            myDbHelper.createDataBase();


        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();
            mrecipes=myDbHelper.getAllRecipes();
            mcategories=myDbHelper.getallcategories();
            for(Recipe recipe:mrecipes)
            {
                try {
                    decryptrecipe(recipe);
                 //   recipe.setImage(BitmapFactory.decodeStream(getAssets().open("recipe_images/" + recipe.getFilename() + ".dat")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mrecipescopy=new ArrayList<Recipe>();
mrecipescopy.addAll(mrecipes);
        }catch(SQLException sqle){

            throw sqle;

        }
        myDbHelper.close();
    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
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

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
       // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }*/

    public void decryptrecipe(Recipe rec) throws IOException {

        String string =getResources().getString(R.string.sc1)+" "+"@"+getResources().getString(R.string.sc3)+" "+getResources().getString(R.string.sc4);
        Key secretKeySpec = new SecretKeySpec(string.getBytes(), "AES");
        try {
            Cipher instance = Cipher.getInstance("AES");
            try {
                instance.init(2, secretKeySpec);
                InputStream open = getResources().getAssets().open("recipe/"+rec.getFilename()+".dat");
                byte[] bArr = new byte[open.available()];
                open.read(bArr);
                try {
                    string = new String(instance.doFinal(bArr));
                    rec.setRecipe(string);
                    //rec.setImage(BitmapFactory.decodeStream(getAssets().open("recipe_images/" + rec.getFilename() + ".dat")));
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
                open.close();

            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cat) {
            CatFrag recfrag=new CatFrag();
            android.support.v4.app.FragmentTransaction fragcat = getSupportFragmentManager().beginTransaction();
            fragcat.replace(R.id.fragcontainer,recfrag);
            fragcat.commit();

            // Handle the camera action
        } else if (id == R.id.nav_allrec) {
            AllRecFrag recfrag=new AllRecFrag();
            android.support.v4.app.FragmentTransaction fragtrans = getSupportFragmentManager().beginTransaction();
            fragtrans.replace(R.id.fragcontainer,recfrag);
            fragtrans.commit();

        } else if (id == R.id.nav_bookmarked) {
            BookmarkFrag bookfrag=new BookmarkFrag();
            android.support.v4.app.FragmentTransaction fragtrans = getSupportFragmentManager().beginTransaction();
            fragtrans.replace(R.id.fragcontainer,bookfrag);
            fragtrans.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
