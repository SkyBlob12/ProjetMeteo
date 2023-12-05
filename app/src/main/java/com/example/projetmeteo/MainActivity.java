package com.example.projetmeteo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appelhttp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button btnValider = findViewById(R.id.buttonValider);
        //btnValider.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //GetMeteo(v);
           //}
        //});
    }

    public void GetMeteo(View v) {
        TextView Ville = findViewById(R.id.ville);
        TextView Status = findViewById(R.id.status);
        TextView Temperature = findViewById(R.id.temperature);
        //ImageView ImageMeteo = findViewById(R.id.imageViewIcone);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.prevision-meteo.ch/services/json/Toulouse" /*+ Ville.getText()*/;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jObj = new JSONObject(response);
                            JSONObject jObjCurrent = jObj.getJSONObject("current_condition");
                            String temperature = jObjCurrent.getString("tmp");
                            String status = jObjCurrent.getString("condition");
                            String icon = jObjCurrent.getString("icon_big");
                            Temperature.setText("Température : " + temperature + "°C");
                            Status.setText(status);
                            //Picasso.get().load(icon).resize(300,300).into(ImageMeteo);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }
}