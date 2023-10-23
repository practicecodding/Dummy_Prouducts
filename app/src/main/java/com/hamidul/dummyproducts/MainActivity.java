package com.hamidul.dummyproducts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    GridView gridView;
    HashMap <String,String> hashMap;
    public static ArrayList < HashMap<String,String> > Product = new ArrayList<>();
    public static ArrayList <ModelClass> cartList = new ArrayList();
    Toast toast;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static String sp;
    ImageView cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        progressBar = findViewById(R.id.progressBar);
        cart = findViewById(R.id.cart);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CartItem.class));
            }
        });

        String url = "https://dummyjson.com/products";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONArray products = response.getJSONArray("products");
                    for (int x=0; x<products.length(); x++){
                        JSONObject jsonObject = products.getJSONObject(x);
                        String thumbnail = jsonObject.getString("thumbnail");
                        String title = jsonObject.getString("title");
                        String price = jsonObject.getString("price");

                        hashMap = new HashMap<>();
                        hashMap.put("thumbnail",thumbnail);
                        hashMap.put("title",title);
                        hashMap.put("price",price);
                        Product.add(hashMap);

                    }//for loop end

                    MyAdapter myAdapter = new MyAdapter();
                    gridView.setAdapter(myAdapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private class MyAdapter extends BaseAdapter{
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return Product.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = layoutInflater.inflate(R.layout.item,parent,false);

            TextView tvTitle = myView.findViewById(R.id.title);
            TextView tvPrice = myView.findViewById(R.id.price);
            ImageView imageView = myView.findViewById(R.id.imageView);
            LinearLayout addCart = myView.findViewById(R.id.addCart);

            HashMap <String,String> hashMap1 = Product.get(position);

            String title = hashMap1.get("title");
            String price = hashMap1.get("price");
            String imageUrl = hashMap1.get("thumbnail");

            int x = position+1;
            sp = Integer.toString(x);

            tvTitle.setText(title);
            tvPrice.setText("TK : "+price);

            Picasso.get()
                    .load(imageUrl)
                    .into(imageView);

            int s = sharedPreferences.getInt("size",0);

            addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedPreferences.getInt(sp,0)==x){
                        toastMessage("Al ready Added");
                    }else {
                        saveData(title,price,imageUrl);
                        editor.putInt(sp,x);
                        editor.apply();

                    }

                }
            });


            return myView;
        }
    }

    public void toastMessage (String message){
        if (toast!=null) toast.cancel();
        toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
        toast.show();
    }

    private void saveData(String name, String price, String url) {

        Gson gson = new Gson();
        cartList.add(new ModelClass(name,price,url));
        String json = gson.toJson(cartList);
        editor.putString("product_data",json);
        editor.apply();

    }

}