package com.recipe.teejay.pakrecipe.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.recipe.teejay.pakrecipe.Models.Category;
import com.recipe.teejay.pakrecipe.Models.Recipe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Asim on 6/11/2016.
 */
public class DbManager extends SQLiteOpenHelper {
    SQLiteDatabase myDataBase;
    static String Dbname;
    public static String Rectable;
    public static String Cattable;
    private static int version;
    String Recipeid;
    String Recipename;
    String Recipefile;
    String Catid;
    String CatName;
    String Catimage;
    String Catorder;
    String bookmark;
    String rectablequery;
    String cattablequery;
    String f2612n;
    Context myContext;
    private static String DB_PATH = "/data/data/com.recipe.teejay.pakrecipe/databases/";
    static {
        version = 1;
        Dbname = "RecipeDB.sqlite";
        Rectable = "Recipes";
        Cattable = "Categories";
    }

    public DbManager(Context context) {

        super(context, Dbname, null, version);
        this.myContext=context;
        this.Recipeid = "RecipeID";
        this.Recipename = "RecipeName";
        this.Recipefile = "Filename";
        this.Catid = "CategoryID";
        this.CatName = "CategoryName";
        this.Catimage = "CategoryImage";
        this.Catorder = "CategoryOrder";
        this.bookmark = "Bookmark";
        this.rectablequery = "create table " + Rectable + "(" + this.Recipeid + " TEXT ," + this.Recipename + " TEXT, " + this.Recipefile + " TEXT," + this.Catid + " TEXT," + this.bookmark + " TEXT);";
        this.cattablequery = "create table " + Cattable + "(" + this.Catid + " TEXT ," + this.CatName + " TEXT ," + this.Catimage + " TEXT ," + this.Catorder + " TEXT);";
        this.f2612n = "drop table if exist " + Rectable + ";";
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + Dbname;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(Dbname);

        // Path to the just created empty db
        String outFileName = DB_PATH + Dbname;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + Dbname;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    public void db_delete()
    {
        File file = new File(DB_PATH + Dbname);
        if(file.exists())
        {
            file.delete();
            System.out.println("delete database file.");
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.


    public final Recipe findRecipebyId(String str) {
        Recipe recipe;
        SQLiteDatabase readableDatabase = getReadableDatabase();
        try {
            Cursor rawQuery = readableDatabase.rawQuery("select * from " + Rectable + " WHERE " + this.Recipeid + " = " + str, null);
            if (rawQuery.moveToFirst()) {
                recipe = new Recipe(rawQuery.getString(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3), rawQuery.getString(4));
                readableDatabase.close();
                return recipe;
            }
        } catch (Exception e) {
        }
        recipe = null;
        readableDatabase.close();
        return recipe;
    }
    public final ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> arrayList = new ArrayList();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        try {
            Cursor rawQuery = readableDatabase.rawQuery("select * from " + Rectable + ";", null);
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(new Recipe(rawQuery.getString(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3), rawQuery.getString(4)));
                } while (rawQuery.moveToNext());
            }
        } catch (Exception e) {
        }
        readableDatabase.close();
        return arrayList;
    }
    public final ArrayList<Recipe> getBookmarkedRecipes() {
        ArrayList<Recipe> arrayList = new ArrayList();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        try {
            Cursor rawQuery = readableDatabase.rawQuery("select * from " + Rectable + " WHERE " + this.bookmark + " = 1", null);
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(new Recipe(rawQuery.getString(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3), rawQuery.getString(4)));
                } while (rawQuery.moveToNext());
            }
        } catch (Exception e) {
        }
        readableDatabase.close();
        return arrayList;
    }
    public String checkBookmark(String recid){
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String result = null;
        try {
            Cursor rawQuery = readableDatabase.rawQuery("select Bookmark from " + Rectable + " WHERE " + this.Recipeid + " = " + recid, null);
            if (rawQuery.moveToFirst()) {
                result = (rawQuery.getString(0));


            }
        }
        catch (Exception e) {
        }
        readableDatabase.close();
        return result;
    }
    public final void setBookmark(String str) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            writableDatabase.execSQL("UPDATE " + Rectable + " SET " + this.bookmark + " = 1 WHERE " + this.Recipeid + " = " + str);
        } catch (Exception e) {
        }
    }
    public final void unBookmark(String str) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
            writableDatabase.execSQL("UPDATE " + Rectable + " SET " + this.bookmark + " = 0 WHERE " + this.Recipeid + " = " + str);
        } catch (Exception e) {
        }
    }
    public final ArrayList<Category> getallcategories() {
        ArrayList<Category> arrayList = new ArrayList();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        try {
            Cursor rawQuery = readableDatabase.rawQuery("select * from " + Cattable + " ORDER BY CAST(" + this.Catorder + " AS INTEGER) ASC ;", null);
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(new Category(rawQuery.getString(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3)));
                } while (rawQuery.moveToNext());
            }
        } catch (Exception e) {
        }
        readableDatabase.close();
        return arrayList;
    }
    public final ArrayList<Recipe> findRecipebyCat(String str) {

        ArrayList<Recipe> arrayList = new ArrayList();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        try {
            Cursor rawQuery = readableDatabase.rawQuery("select * from " + Rectable + " WHERE " + this.Catid +" like '" + str +"' or " +this.Catid+ " like '%,"+str+"' or CategoryID like '"+str+",%' or CategoryID like '%,"+str+",%'", null);
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(new Recipe(rawQuery.getString(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3), rawQuery.getString(4)));
                } while (rawQuery.moveToNext());
            }
        } catch (Exception e) {
        }
        readableDatabase.close();
        return arrayList;
    }
    public final void createdb() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('0','Aaloo Bharay Parathay','aaloo_bharay_parathay','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('1','Achaari Karela','achaari_karela','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('2','Achar Gosht','achar_gosht','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('3','Afghani Biryani','afghani_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('4','Afghani Pulao','afghani_pulao','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('5','Aloo Baingan','aloo_baingan','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('6','Aloo Chaat','aloo_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('7','Aloo Kachori','aloo_kachori','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('8','Aloo Kay Samosay','aloo_kay_samosay','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('9','Aloo Ki Bhujia','aloo_ki_bhujia','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('10','Aloo Ki Tikki','aloo_ki_tikki','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('11','Aloo Qeema','aloo_qeema','1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('12','Al Baik Chicken','al_baik_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('13','Anday Walay Shami Kabab','anday_walay_shami_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('14','Apple Smoothie','apple_smoothie','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('15','Arabian Chicken With Rice','arabian_chicken_with_rice','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('16','Arabian Style Chicken Sandwiches','arabian_style_chicken_sandwiches','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('17','Arabic Chicken Sajji','arabic_chicken_sajji','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('18','Badami Kheer','badami_kheer','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('19','Baghara Baingan And Mirchon Ka Salan','baghara_baingan_and_mirchon_ka_salan','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('20','Baisun Ka Halwa','baisun_ka_halwa','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('21','Bakre Ke Paye','bakre_ke_paye','2,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('22','Balti Chicken','balti_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('23','Balti Gosht','balti_gosht','1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('24','Bbq Chanp','bbq_chanp','2,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('25','Bbq Paratha Roll','bbq_paratha_roll','8,7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('26','Beef Behari Bbq','beef_behari_bbq','8,1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('27','Beef Boti','beef_boti','1,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('28','Beef Chowmein','beef_chowmein','1,14','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('29','Beef Haleem','beef_haleem','1,15,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('30','Beef Kay Pasanday','beef_kay_pasanday','1,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('31','Beef Nihari','beef_nihari','1,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('32','Beef Paya Curry','beef_paya_curry','1,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('33','Beef Pulao','beef_pulao','1,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('34','Beef Vegetable Masala','beef_vegetable_masala','1,9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('35','Beef With Fried Rice','beef_with_fried_rice','1,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('36','Behari Kabab','behari_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('37','Besani Paratha','besani_paratha','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('38','Besan Kay Pakoray','besan_kay_pakoray','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('39','Bhindi Gosht','bhindi_gosht','9,2,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('40','Bhindi Gosht Qorma','bhindi_gosht_qorma','9,2,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('41','Bhindi Masala','bhindi_masala','9,2,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('42','Bhuni Hui Kaleji','bhuni_hui_kaleji','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('43','Bihari Paratha','bihari_paratha','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('44','Black Forest Cake','black_forest_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('45','Black Pepper Chicken','black_pepper_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('46','Bohri Chicken Cutlets','bohri_chicken_cutlets','0,18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('47','Bombay Biryani','bombay_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('48','Bong','bong','1,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('49','Bong Nihari','bong_nihari','1,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('50','Boti Kabab Badami','boti_kabab_badami','8,18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('51','Boti Tikka','boti_tikka','8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('52','Brain Masala','brain_masala','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('53','Brown Rice With Chicken','brown_rice_with_chicken','0,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('54','Butter Chicken','butter_chicken','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('55','Cappuccino Muffins','cappuccino_muffins','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('56','Chana Bhaji','chana_bhaji','15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('57','Chana Chaat Recipe 01','chana_chaat_recipe_01','12,15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('58','Chana Chaat Recipe 02','chana_chaat_recipe_02','12,15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('59','Chana Pulao','chana_pulao','3,15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('60','Chapli Kabab','chapli_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('61','Chatpatay Kalay Chanay','chatpatay_kalay_chanay','15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('62','Chatpata Pulao','chatpata_pulao','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('63','Chatpata Qeema','chatpata_qeema','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('64','Cheeseburger','cheeseburger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('65','Cheese Chicken Cutlets','cheese_chicken_cutlets','0,18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('66','Cheese Omelet','cheese_omelet','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('67','Cheese Samosa','cheese_samosa','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('68','Cheesy Herbed Potato Croquettes','cheesy_herbed_potato_croquettes','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('69','Cheesy White Spaghetti','cheesy_white_spaghetti','5,0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('70','Chicken Almond Qorma','chicken_almond_qorma','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('71','Chicken And Masoor Ki Daal','chicken_and_masoor_ki_daal','0,15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('72','Chicken And Potato Cutlets','chicken_and_potato_cutlets','0,18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('73','Chicken And Rice Croquettes','chicken_and_rice_croquettes','0,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('74','Chicken And Vegetable Macaroni Salad','chicken_and_vegetable_macaroni_salad','5,17','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('75','Chicken Biryani','chicken_biryani','0,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('76','Chicken Bukhara Masala','chicken_bukhara_masala','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('77','Chicken Caesar Salad','chicken_caesar_salad','17','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('78','Chicken Cheesy Mushroom','chicken_cheesy_mushroom','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('79','Chicken Chili Walnut','chicken_chili_walnut','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('80','Chicken Chilli Stir Fry','chicken_chilli_stir_fry','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('81','Chicken Corn Soup','chicken_corn_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('82','Chicken Fajita Pizza','chicken_fajita_pizza','0,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('83','Chicken Fillet Sandwich','chicken_fillet_sandwich','0,7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('84','Chicken Ginger','chicken_ginger','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('85','Chicken Haleem','chicken_haleem','0,15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('86','Chicken Handi','chicken_handi','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('87','Chicken Hara Masala Kofta','chicken_hara_masala_kofta','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('88','Chicken Jalfrezi','chicken_jalfrezi','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('89','Chicken Keema Pakora','chicken_keema_pakora','0,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('90','Chicken Korma','chicken_korma','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('91','Chicken Lazania','chicken_lazania','0,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('92','Chicken Makhani Handi','chicken_makhani_handi','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('93','Chicken Malai Boti','chicken_malai_boti','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('94','Chicken Malai Tikka','chicken_malai_tikka','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('95','Chicken Manchurian','chicken_manchurian','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('96','Chicken Masala','chicken_masala','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('97','Chicken Meatballs In Tomato Sauce','chicken_meatballs_in_tomato_sauce','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('98','Chicken Nihari','chicken_nihari','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('99','Chicken Noodles Soup','chicken_noodles_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('100','Chicken Nuggets','chicken_nuggets','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('101','Chicken Omlete','chicken_omlete','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('102','Chicken Pakoray','chicken_pakoray','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('103','Chicken Paratha Roll','chicken_paratha_roll','0,7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('104','Chicken Pasta With White Sauce','chicken_pasta_with_white_sauce','5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('105','Chicken Pizza','chicken_pizza','0,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('106','Chicken Pulao','chicken_pulao','0,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('107','Chicken Roast','chicken_roast','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('108','Chicken Roll','chicken_roll','0,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('109','Chicken Sajji','chicken_sajji','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('110','Chicken Samosa','chicken_samosa','0,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('111','Chicken Sandwich','chicken_sandwich','0,7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('112','Chicken Seekh Kabab','chicken_seekh_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('113','Chicken Shashlik','chicken_shashlik','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('114','Chicken Shawarma','chicken_shawarma','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('115','Chicken Spaghetti With Cream Cheese Sauce','chicken_spaghetti_with_cream_cheese_sauce','5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('116','Chicken Steak','chicken_steak','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('117','Chicken Steaks With Pepper Sauce','chicken_steaks_with_pepper_sauce','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('118','Chicken Steak With Mushroom Sauce','chicken_steak_with_mushroom_sauce','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('119','Chicken Steak With Pepper Sauce','chicken_steak_with_pepper_sauce','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('120','Chicken Steak With Sauce','chicken_steak_with_sauce','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('121','Chicken Steam Roast','chicken_steam_roast','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('122','Chicken Tandoori Burger','chicken_tandoori_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('123','Chicken Tikka','chicken_tikka','8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('124','Chicken Tikka Biryani','chicken_tikka_biryani','0,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('125','Chicken Tikka Crispy','chicken_tikka_crispy','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('126','Chicken Tikka Pizza','chicken_tikka_pizza','0,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('127','Chicken White Karahi','chicken_white_karahi','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('128','Chicken White Korma','chicken_white_korma','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('129','Chicken With Sabzi','chicken_with_sabzi','0,9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('130','Chicken With Spaghetti','chicken_with_spaghetti','5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('131','Chikoo Shake','chikoo_shake','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('132','Chili Chicken Pizza','chili_chicken_pizza','0,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('133','Chinese Cashew Chicken','chinese_cashew_chicken','0,14','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('134','Chinese Chicken Pulao Biryani','chinese_chicken_pulao_biryani','0,14','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('135','Chinese Fried Fish','chinese_fried_fish','14,16','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('136','Chinese Rice','chinese_rice','14,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('137','Chinese Samose','chinese_samose','14,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('138','Chinese Tomato Beef','chinese_tomato_beef','14,1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('139','Chocolate Cake','chocolate_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('140','Chocolate Cake Without Baking','chocolate_cake_without_baking','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('141','Chocolate Mousse Cake','chocolate_mousse_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('142','Chocolate Mug Cake','chocolate_mug_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('143','Chops Masala','chops_masala','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('144','Creamy Chicken','creamy_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('145','Creamy Chicken Steak','creamy_chicken_steak','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('146','Creamy Fruit Chaat','creamy_fruit_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('147','Creamy Spice Chicken','creamy_spice_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('148','Crispy Chicken Broast','crispy_chicken_broast','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('149','Crispy Chicken Burger','crispy_chicken_burger','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('150','Crispy Chicken Cutlets','crispy_chicken_cutlets','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('151','Crispy Chicken Sandwich','crispy_chicken_sandwich','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('152','Crispy Chicken Wings','crispy_chicken_wings','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('153','Crispy Chilli Beef','crispy_chilli_beef','1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('154','Crispy Kaleji','crispy_kaleji','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('155','Crunchy Fillet Burger','crunchy_fillet_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('156','Crunchy Pakora','crunchy_pakora','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('157','Custard Cake','custard_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('158','Custard Ki Kheer','custard_ki_kheer','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('159','Custard Shireen','custard_shireen','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('160','Custard Vermicelli','custard_vermicelli','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('161','Daal Chawal Mix','daal_chawal_mix','15,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('162','Daal Gosht','daal_gosht','15,1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('163','Daal Ke Samose','daal_ke_samose','15,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('164','Daal Makhni','daal_makhni','15,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('165','Daal Mash Aur Keema','daal_mash_aur_keema','15,1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('166','Dahi Barey','dahi_barey','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('167','Dahi Chana Chaat','dahi_chana_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('168','Dal Chana Fry','dal_chana_fry','15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('169','Dates Lassi','dates_lassi','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('170','Deep Fried Chicken Cutlets','deep_fried_chicken_cutlets','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('171','Degi Biryani','degi_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('172','Degi Chicken Korma','degi_chicken_korma','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('173','Donuts','donuts','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('174','Double Cheese Burger','double_cheese_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('175','Dum Aloo','dum_aloo','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('176','Dum Ka Keema','dum_ka_keema','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('177','Eggless Vanilla Cake','eggless_vanilla_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('178','Egg Curry','egg_curry','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('179','Egg Fried Rice','egg_fried_rice','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('180','Egg Ghotala','egg_ghotala','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('181','Egg Khoya Halwa','egg_khoya_halwa','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('182','Egg Potato','egg_potato','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('183','Falafel Sandwich','falafel_sandwich','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('184','Fish Biryani','fish_biryani','16,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('185','Fish Curry','fish_curry','16,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('186','Fish Fingers With Tartar Sauce','fish_fingers_with_tartar_sauce','16','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('187','Fish Fry','fish_fry','16','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('188','Fish Katakat','fish_katakat','16,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('189','Fish Penne Pasta With Cheese','fish_penne_pasta_with_cheese','16,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('190','Fish Pulao','fish_pulao','16,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('191','Fluffy Mexican Omelette','fluffy_mexican_omelette','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('192','Four Seasons Pizza','four_seasons_pizza','5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('193','French Fries','french_fries','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('194','French Omelettes','french_omelettes','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('195','French Style Chicken Sandwich','french_style_chicken_sandwich','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('196','Fresh Cream Chat','fresh_cream_chat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('197','Fried Chanay','fried_chanay','15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('198','Fried Chicken Zinger Burger','fried_chicken_zinger_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('199','Fried Kaleji Shashlik','fried_kaleji_shashlik','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('200','Fried Spring Chicken','fried_spring_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('201','Fruits Mocktail','fruits_mocktail','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('202','Fruit Cake','fruit_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('203','Fruit Chaat','fruit_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('204','Fruit Salad','fruit_salad','17','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('205','Frutti Smoothie','frutti_smoothie','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('206','Gajar Ka Halwa','gajar_ka_halwa','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('207','Gajrela','gajrela','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('208','Garlic Mushroom','garlic_mushroom','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('209','Garma Garam Aloo Chaat','garma_garam_aloo_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('210','Golden Fish Balls','golden_fish_balls','16,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('211','Gol Gappay','gol_gappay','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('212','Gosht Ka Achari Pulao','gosht_ka_achari_pulao','1,2,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('213','Grapes Cocktail','grapes_cocktail','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('214','Green Chicken Karahi','green_chicken_karahi','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('215','Grilled Beef Koftay','grilled_beef_koftay','8,1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('216','Grilled Chicken Sandwiches','grilled_chicken_sandwiches','8,7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('217','Grilled Chicken With Garlic','grilled_chicken_with_garlic','8,0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('218','Grilled Fish','grilled_fish','8,16','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('219','Grilled Steak Sandwich','grilled_steak_sandwich','8,7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('220','Grill Vegetable Fish','grill_vegetable_fish','8,16,9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('221','Gulab Jamun','gulab_jamun','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('222','Gulab Ka Sharbat','gulab_ka_sharbat','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('223','Gulab Ki Lassi','gulab_ki_lassi','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('224','Hareesa','hareesa','15,1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('225','Hot And Sour Soup','hot_and_sour_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('226','Hunter Beef','hunter_beef','1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('227','Hyderabadi Chicken Biryani','hyderabadi_chicken_biryani','0,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('228','Icy Blue Lemonade','icy_blue_lemonade','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('229','Imli Aur Khajoor Ki Chutni','imli_aur_khajoor_ki_chutni','19','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('230','Imli Ki Chutney','imli_ki_chutney','19','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('231','Italian Spaghetti Meat','italian_spaghetti_meat','5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('232','Jalapeno Cheeseburger','jalapeno_cheeseburger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('233','Jalebi','jalebi','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('234','Juicy Chicken Steak','juicy_chicken_steak','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('235','Jumbo Beef Burger','jumbo_beef_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('236','Kabli Pulao','kabli_pulao','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('237','Kaddu Ka Halwa','kaddu_ka_halwa','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('238','Kadhi Chawal','kadhi_chawal','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('239','Kadhi Pakora','kadhi_pakora','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('240','Karahi Kaleji','karahi_kaleji','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('241','Karahi Keema','karahi_keema','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('242','Kashmiri Seekh Kabab','kashmiri_seekh_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('243','Kashmiri Tea','kashmiri_tea','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('244','Keema Karela','keema_karela','1,2,9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('245','Keema Macaroni','keema_macaroni','1,2,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('246','Keema Mutter Pulao','keema_mutter_pulao','1,2,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('247','Keema Pulao','keema_pulao','1,2,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('248','Kfc Spicy Arabian Rice With Chicken Hot Shots And Chili Sauce','kfc_spicy_arabian_rice_with_chicken_hot_shots_and_chili_sauce','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('249','Kfc Zinger Burger','kfc_zinger_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('250','Khajoor Ka Cake','khajoor_ka_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('251','Khatay Aloo','khatay_aloo','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('252','Khatta Gosht','khatta_gosht','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('253','Kheer','kheer','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('254','Khoay Kay Parathy','khoay_kay_parathy','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('255','Kit Kat Cheese Cake','kit_kat_cheese_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('256','Koftay Ka Salan','koftay_ka_salan','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('257','Kofta Biryani','kofta_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('258','Kung Pao Chicken','kung_pao_chicken','14,0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('259','Lab-E-Shireen','lab-e-shireen','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('260','Labnani Kabab','labnani_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('261','Lahori Chana Chaat','lahori_chana_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('262','Lahori Paya','lahori_paya','13,1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('263','Lahori Red Chicken Karahi','lahori_red_chicken_karahi','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('264','Lasagna Strips With Spicy Beef','lasagna_strips_with_spicy_beef','5,1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('265','Lemon And Mint Cooler','lemon_and_mint_cooler','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('266','Lemon Chilli Chicken','lemon_chilli_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('267','Low Fat Chana Chaat','low_fat_chana_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('268','Macaroni Chaat','macaroni_chaat','5,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('269','Macroni Roll','macroni_roll','5,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('270','Macroni Samosa','macroni_samosa','5,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('271','Makhadi Halwa','makhadi_halwa','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('272','Malai Chicken','malai_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('273','Malai Kofta Handi','malai_kofta_handi','1,2,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('274','Malai Tikka Biryani','malai_tikka_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('275','Mango Shake','mango_shake','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('276','Mango Squash','mango_squash','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('277','Masalaydar Khagina','masalaydar_khagina','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('278','Masalay Dar Chaat','masalay_dar_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('279','Masala Gosht','masala_gosht','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('280','Mashed Potatoes','mashed_potatoes','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('281','Mash Ki Daal','mash_ki_daal','15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('282','Mazedar Katakat','mazedar_katakat','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('283','Mazedar Pakoray','mazedar_pakoray','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('284','Meatball Biryani','meatball_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('285','Meethi Dahi Phulki','meethi_dahi_phulki','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('286','Milk And Dates Drink','milk_and_dates_drink','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('287','Milk Burfi','milk_burfi','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('288','Keema Gobhi','keema_gobhi','1,2,9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('289','Mini Beef Burger','mini_beef_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('290','Mint Lassi','mint_lassi','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('291','Mix Beans Chaat','mix_beans_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('292','Mix Fruit Chana Chaat','mix_fruit_chana_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('293','Mix Pakoray','mix_pakoray','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('294','Mix Vegetable Handi','mix_vegetable_handi','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('295','Mix Vegetable Soup','mix_vegetable_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('296','Molten Lava Cake','molten_lava_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('297','Mughal Karahi','mughal_karahi','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('298','Mughlai Butter Chicken','mughlai_butter_chicken','0,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('299','Mughlai Kofta','mughlai_kofta','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('300','Mulligatawny Soup','mulligatawny_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('301','Mushroom Chicken','mushroom_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('302','Mustard Mushroom Omelette','mustard_mushroom_omelette','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('303','Mutton Biryani','mutton_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('304','Mutton Chops','mutton_chops','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('305','Mutton Do Pyaza','mutton_do_pyaza','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('306','Mutton Fry','mutton_fry','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('307','Mutton Hara Masala','mutton_hara_masala','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('308','Mutton Karahi','mutton_karahi','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('309','Mutton Kata Kat','mutton_kata_kat','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('310','Mutton Korma','mutton_korma','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('311','Mutton Kunna','mutton_kunna','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('312','Mutton Nihari','mutton_nihari','2,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('313','Mutton Pulao','mutton_pulao','2,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('314','Mutton Raan Special','mutton_raan_special','2,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('315','Mutton Stew','mutton_stew','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('316','Mutton White Karahi','mutton_white_karahi','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('317','Mutton Zafrani','mutton_zafrani','2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('318','Mutton Zafrani Kabab','mutton_zafrani_kabab','2,18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('319','Naan Khatai','naan_khatai','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('320','Namkeen Lassi','namkeen_lassi','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('321','Namkeen Roast','namkeen_roast','8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('322','Nargisi Koftay','nargisi_koftay','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('323','Nawabi Tikian','nawabi_tikian','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('324','Noodles Roll','noodles_roll','5,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('325','Noodles Soup','noodles_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('326','Nuggets Burger','nuggets_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('327','Olive Chicken','olive_chicken','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('328','Palak Gosht','palak_gosht','9,1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('329','Palak Paneer','palak_paneer','9,1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('330','Pani Puri Gol Gappay','pani_puri_gol_gappay','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('331','Papri Chaat','papri_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('332','Pasta Chicken Salad','pasta_chicken_salad','5,17','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('333','Paya Mutton Handi','paya_mutton_handi','2,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('334','Paya Nihari','paya_nihari','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('335','Peach Colada','peach_colada','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('336','Pepper Chicken Steak','pepper_chicken_steak','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('337','Peshawari Karahi','peshawari_karahi','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('338','Phool Gobi Kay Pakore','phool_gobi_kay_pakore','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('339','Pineapple Fizz','pineapple_fizz','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('340','Pizza Without Oven','pizza_without_oven','5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('341','Pizza Sandwich','pizza_sandwich','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('342','Potato And Fish Croquettes','potato_and_fish_croquettes','16,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('343','Potato And Fish Pakora','potato_and_fish_pakora','16,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('344','Potato Cheese Balls','potato_cheese_balls','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('345','Prawn Biryani','prawn_biryani','16,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('346','Pulao Biryani','pulao_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('347','Quick Chicken Karahi','quick_chicken_karahi','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('348','Rabri','rabri','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('349','Rasmalai','rasmalai','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('350','Red Bean Chaat','red_bean_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('351','Reshmi Handi','reshmi_handi','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('352','Reshmi Kabab','reshmi_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('353','Roasted Chicken Paratha Roll','roasted_chicken_paratha_roll','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('354','Roast Mutton','roast_mutton','2,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('355','Russian Salad','russian_salad','17','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('356','Safaid Chanay Ki Chaat','safaid_chanay_ki_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('357','Samosa Chaat','samosa_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('358','Samose Keeme Bhare','samose_keeme_bhare','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('359','Sarson Ka Saag Aur Makki Ki Roti','sarson_ka_saag_aur_makki_ki_roti','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('360','Seekh Kabab','seekh_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('361','Seviyan Ka Zarda','seviyan_ka_zarda','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('362','Shaami Kabab First','shaami_kabab_first','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('363','Shaami Kabab Second','shaami_kabab_second','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('364','Shahi Haleem Khas','shahi_haleem_khas','13,15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('365','Shahi Mutter Pulao','shahi_mutter_pulao','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('366','Shahi Tukray','shahi_tukray','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('367','Shaami Kebab Third','shaami_kebab_third','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('368','Shanghai Chicken','shanghai_chicken','14,0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('369','Sheermal','sheermal','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('370','Shimla Murgh Makhni','shimla_murgh_makhni','9,0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('371','Simple Chicken Biryani','simple_chicken_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('372','Simple Pakora','simple_pakora','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('373','Sindhi Biryani','sindhi_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('374','Singaporean Fried Rice','singaporean_fried_rice','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('375','Sooji Halwa','sooji_halwa','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('376','Spanish Omelette','spanish_omelette','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('377','Special Chapli Kabab','special_chapli_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('378','Special Chicken Biryani','special_chicken_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('379','Special Karahi Kabab','special_karahi_kabab','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('380','Special Korma','special_korma','13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('381','Special Soup','special_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('382','Special Thai Noodles','special_thai_noodles','14','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('383','Special Winter Soup','special_winter_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('384','Spice Stuffed Karela','spice_stuffed_karela','9','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('385','Spicy Chicken Meat Balls Rice','spicy_chicken_meat_balls_rice','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('386','Spicy Garlic Beef','spicy_garlic_beef','1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('387','Spicy Noodles','spicy_noodles','14','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('388','Spicy Radish Paratha','spicy_radish_paratha','11','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('389','Spicy Vegetable Chowmein','spicy_vegetable_chowmein','14','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('390','Spicy Vegetables Pakoray','spicy_vegetables_pakoray','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('391','Spicy Vegitable Rolls','spicy_vegitable_rolls','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('392','Spicy Wings','spicy_wings','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('393','Spicy Zinger Burger','spicy_zinger_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('394','Sponge Cake','sponge_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('395','Spring Roll','spring_roll','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('396','Stove Or No-Oven Bake Chocolate Cake','stove_or_no-oven_bake_chocolate_cake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('397','Strawberry Colada','strawberry_colada','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('398','Student Biryani','student_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('399','Summer Peach Drink','summer_peach_drink','4','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('400','Sweet And Sour Beef Chow Mein','sweet_and_sour_beef_chow_mein','1,14','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('401','Sweet And Sour Chana Chaat','sweet_and_sour_chana_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('402','Tandoori Chicken','tandoori_chicken','0,8','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('403','Tandoori Chicken Pizza','tandoori_chicken_pizza','0,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('404','Tandoori Chops','tandoori_chops','8,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('405','Tandoori Fillet Burger','tandoori_fillet_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('406','Tarkay Wali Chane Ki Daal','tarkay_wali_chane_ki_daal','15','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('407','Tarragon Chicken Steak','tarragon_chicken_steak','0','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('408','Tasty Drum Sticks','tasty_drum_sticks','0,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('409','Three Beans Chaat','three_beans_chaat','12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('410','Tikki Burger','tikki_burger','7','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('411','Turkish Salad','turkish_salad','17','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('412','Vanilla Cupcake','vanilla_cupcake','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('413','Vegetable And Bean Soup','vegetable_and_bean_soup','10','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('414','Vegetable And Pulses Cutlets','vegetable_and_pulses_cutlets','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('415','Vegetable Cutlet','vegetable_cutlet','18','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('416','Vegetable Lasagna','vegetable_lasagna','9,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('417','Vegetable Malai Kofta','vegetable_malai_kofta','9,1','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('418','Vegetable Pulao','vegetable_pulao','9,3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('419','Vegetable Spaghetti','vegetable_spaghetti','9,5','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('420','Vegitable Chaat','vegitable_chaat','9,12','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('421','White Mutton Korma','white_mutton_korma','2,13','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('422','Wonton','wonton','14','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('423','Zafrani Biryani','zafrani_biryani','3','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('424','Zafrani Meatballs','zafrani_meatballs','1,2','0')");
        writableDatabase.execSQL("INSERT INTO " + Rectable + " (" + this.Recipeid + ", " + this.Recipename + ", " + this.Recipefile + ", " + this.Catid + ", " + this.bookmark + ") VALUES ('425','Zarda','zarda','6','0')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('8','B.B.Q/Grilled','cat_bbq_grilled','0')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('1','Beef','cat_beef','1')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('3','Biryani/Rice','cat_biryani_rice','2')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('11','Breakfast','cat_breakfast','3')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('0','Chicken','cat_chicken','4')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('14','Chinese','cat_chinese','5')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('19','Chutni','cat_chutni','6')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('13','Curry/Salan','cat_curry_salan','7')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('6','Desserts','cat_desserts','8')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('7','Fast Food/Burgers','cat_fast_food_burgers','9')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('15','Grains/Daal','cat_grains_daal','10')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('5','Italian','cat_italian','11')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('4','Juices/Drinks','cat_juices_drinks','12')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('18','Kabab','cat_kabab','13')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('2','Mutton','cat_mutton','14')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('17','Salads','cat_salads','15')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('16','Sea Foods','cat_sea_foods','16')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('12','Snacks','cat_snacks','17')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('10','Soup','cat_soup','18')");
        writableDatabase.execSQL("INSERT INTO " + Cattable + " (" + this.Catid + ", " + this.CatName + ", " + this.Catimage + ", " + this.Catorder + ") VALUES ('9','Vegetables','cat_vegetables','19')");
        writableDatabase.close();
    }

}
