package com.recipe.teejay.pakrecipe.Activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.recipe.teejay.pakrecipe.DB.DbManager;
import com.recipe.teejay.pakrecipe.Models.Recipe;
import com.recipe.teejay.pakrecipe.R;

import java.io.IOException;

public class RecipedetailActivity extends AppCompatActivity {
    WebView recipewv;
    ImageView recipeiv;
    TextView recipename;
    Recipe recipe;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipedetail);
        recipewv =(WebView) findViewById(R.id.wvrecipe);
        recipeiv=(ImageView) findViewById(R.id.recipeiv);
        final DbManager dbhelper=new DbManager(this);

       // recipename=(TextView) findViewById(R.id.text_title);
        Intent i=getIntent();
        pos= Integer.valueOf(i.getStringExtra("Recipepos"));
        recipe=MainActivity.mrecipescopy.get(pos);
        recipewv.loadData(recipe.getRecipe(), "text/html; charset=UTF-8", null);
        try {
            recipe.setImage(BitmapFactory.decodeStream(this.getAssets().open("recipe_images/" + recipe.getFilename() + ".dat")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setTitle(recipe.getRecipeName());
        recipeiv.setImageBitmap(recipe.getImage());
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabbook);
        String book=dbhelper.checkBookmark(recipe.getRecipeID());
        if(book.contentEquals("0")){

            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
        }
        else if(book.contentEquals("1")){

            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String book=dbhelper.checkBookmark(recipe.getRecipeID());
                if(book.contentEquals("0")){
                    dbhelper.setBookmark(recipe.getRecipeID());
                    Snackbar.make(view, "Recipe Bookmarked! Use Menu to access later", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                }
               else if(book.contentEquals("1")){
                    dbhelper.unBookmark(recipe.getRecipeID());
                    fab.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
                }


            }
        });
//        recipename.setText(recipe.getRecipeName());
    }
}
