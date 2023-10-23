package com.hamidul.dummyproducts;

import static com.hamidul.dummyproducts.MainActivity.Product;
import static com.hamidul.dummyproducts.MainActivity.cartList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.random.Random;

public class CartItem extends AppCompatActivity {
    ListView listView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_item);

        listView = findViewById(R.id.listView);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();

        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

    }

    private class MyAdapter extends BaseAdapter{
        LayoutInflater layoutInflater;
        @Override
        public int getCount() {
            return MainActivity.cartList.size();
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
            View myView = layoutInflater.inflate(R.layout.add_cart_item,parent,false);

            TextView tvTitle = myView.findViewById(R.id.title);
            TextView tvPrice = myView.findViewById(R.id.price);
            ImageView imageView = myView.findViewById(R.id.imageView);
            Button delete = myView.findViewById(R.id.delete);

            String title = cartList.get(position).name;
            String price = cartList.get(position).price;
            String imageUrl = cartList.get(position).url;

            tvTitle.setText(title);
            tvPrice.setText(price);

            Picasso.get()
                    .load(imageUrl)
                    .into(imageView);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartList.remove(position);
                    Gson gson = new Gson();
                    String json = gson.toJson(cartList);
                    editor.putString("product_data",json);
                    editor.apply();
                    MyAdapter myAdapter = new MyAdapter();
                    listView.setAdapter(myAdapter);
                }
            });

            return myView;
        }
    }

    private void loadData (){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("product_data",null);
        Type type = new TypeToken<ArrayList<ModelClass>>(){
        }.getType();
        cartList = gson.fromJson(json,type);
        if (cartList==null){
            cartList=new ArrayList<>();
        }
    }

}