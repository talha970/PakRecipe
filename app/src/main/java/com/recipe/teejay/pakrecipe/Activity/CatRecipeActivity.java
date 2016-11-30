package com.recipe.teejay.pakrecipe.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.recipe.teejay.pakrecipe.Adapter.RecyclerCardsAdapter;
import com.recipe.teejay.pakrecipe.DB.DbManager;
import com.recipe.teejay.pakrecipe.Models.Recipe;
import com.recipe.teejay.pakrecipe.R;

import java.util.ArrayList;

public class CatRecipeActivity extends AppCompatActivity {
String cat;
    ArrayList<Recipe> recipes;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allrec_layout);
        RecyclerView recipesRecycleView = (RecyclerView) findViewById(R.id.recycle_view);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        DbManager sqlhelper=new DbManager(this);
        Intent i=getIntent();
        cat= i.getStringExtra("Recipecat");
        sqlhelper.openDataBase();
        recipes=sqlhelper.findRecipebyCat(cat);
        sqlhelper.close();
        recipesRecycleView.setHasFixedSize(true);
        getSupportActionBar().setTitle("Dishes by Category");
        GridLayoutManager recycleLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.VERTICAL, false);
        recipesRecycleView.setLayoutManager(recycleLayoutManager);
        recipesRecycleView.setItemAnimator(new DefaultItemAnimator());
        if(recipes!=null){
            RecyclerCardsAdapter reciperAdapter = new RecyclerCardsAdapter(recipes,this);
            recipesRecycleView.setAdapter(reciperAdapter);}
        else{
            Log.d("Allrecfrag","mrecipes is null");

        }
    }
}
