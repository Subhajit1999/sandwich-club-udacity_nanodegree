package com.udacity.sandwichclub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import lt.neworld.spanner.Spanner;

import static lt.neworld.spanner.Spans.bold;
import static lt.neworld.spanner.Spans.font;
import static lt.neworld.spanner.Spans.foreground;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    private TextView name, knownAs, origin, description, ingredients;
    private ImageView imageSandwich;
    private Sandwich sandwich;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private boolean isRegistered=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageSandwich = findViewById(R.id.iv_sandwich);
        name = findViewById(R.id.tv_name);
        knownAs = findViewById(R.id.tv_knownAs);
        origin = findViewById(R.id.tv_origin);
        description = findViewById(R.id.tv_desc);
        ingredients = findViewById(R.id.tv_ingredients);

        receiveNetworkBroadcast();

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        sandwich = JsonUtils.parseSandwichJson(this,json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }
        loadImage(sandwich.getImage());
        populateUI();

        setTitle(sandwich.getMainName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRegistered){
            registerReceiver(broadcastReceiver,intentFilter);
            isRegistered = true;
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI() {
        //show the json-parsed data into the respective textViews
        name.setText(createSpannable("Name:\t\t",sandwich.getMainName()));
        String alsoKnownAs="";
        for (String s: sandwich.getAlsoKnownAs()){
            if(sandwich.getAlsoKnownAs().indexOf(s)!=sandwich.getAlsoKnownAs().size()-1) {
                alsoKnownAs = alsoKnownAs.concat(s+", ");
            }else{
                alsoKnownAs = alsoKnownAs.concat(s);
            }
        }
        knownAs.setText(createSpannable("Also known as:\t\t", alsoKnownAs));
        origin.setText(createSpannable("Place of origin:\t\t",sandwich.getPlaceOfOrigin()));
        description.setText(createSpannable("Description:\n\n",sandwich.getDescription()));
        String ingredientStr="";
        for (String s: sandwich.getIngredients()){ingredientStr = ingredientStr.concat("* "+s+"\n");}
        ingredients.setText(createSpannable("Ingredients:\n\n",ingredientStr));
    }

    private Spannable createSpannable(String str1, String str2){
        Log.d(TAG, "createSpannedHtml: creating spanned string with different configs for the same textView");

        return new Spanner()
                .append(str1,bold(), foreground(Color.BLACK))
                .append(str2, foreground(Color.GRAY));
    }

    private void loadImage(String imageUrl){
        Log.d(TAG, "loadImage: loading sandwich image");
        /*Error placeholder image from www.delhicourses.in
        * and
        * Sandwich placeholder image from http://www.manna.nf.ca/product/vegetarian-sandwich
        * by Google Image search
         */
        Picasso.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.sandwich_placeholder)
                .error(R.drawable.error)
                .fit().centerCrop()
                .into(imageSandwich);
    }

    private void receiveNetworkBroadcast(){
        Log.d(TAG, "receiveNetworkBroadcast: checking network state");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                NetworkInfo.State state = info.getState();

                if (state == NetworkInfo.State.CONNECTED) {
                    //when connection is back
                    loadImage(sandwich.getImage());
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegistered){
            unregisterReceiver(broadcastReceiver);
        }
    }
}
