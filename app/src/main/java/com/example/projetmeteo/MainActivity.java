package com.example.projetmeteo;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appelhttp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public FusedLocationProviderClient fusedLocationClient;
    RelativeLayout layoutMain;

    public String villeRecherche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Demande permission localisation
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        //Récupération des coordonnees current localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Recherche d'autres villes
        Button recherche = findViewById(R.id.recherche_villes);
        recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AutresVilles.class));
            }
        });

        //Ouverture des recherches avec un swipe à droite
        layoutMain = findViewById(R.id.ecran);
        layoutMain.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                startActivity(new Intent(MainActivity.this, AutresVilles.class));
            }
        });
    }

    //Si autorisation accordé on continue sinon on ferme l'application
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Only one permission is asked here
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            /*try {
                GetMeteo(v)
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
        }
        else{
            finish();
        }
    }

    //Récupérer les informations de l'API
    //Problème sur l'api vu en cours avec les coordonnées
    //Code de récupération des infos de l'api qui est limité
    /*
    public void GetMeteo(View v) {
        TextView Ville = findViewById(R.id.ville);
        TextView Status = findViewById(R.id.status);
        TextView Temperature = findViewById(R.id.temperature);
        //ImageView ImageMeteo = findViewById(R.id.imageViewIcone);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.prevision-meteo.ch/services/json/Toulouse" /*+ Ville.getText();
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
            }, new Response.ErrorListener() { */


    //Retour en arrière
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Etes-vous sur de quitter l'application")
                .setTitle("Attention !")
                .setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflater le menu à partir du fichier XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gérer les clics sur les éléments du menu
        int id = item.getItemId();

        if(id==R.id.main_menu_maps){
            openGoogleMaps();
        } else if (id==R.id.main_menu_accueil) {
            openAccueil();
        } else{
            return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private void openGoogleMaps() {
        // Créer une URI pour lancer Google Maps
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=Googleplex");

        // Créer une intention avec l'URI
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Vérifier si l'application Google Maps est installée
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            // Lancer l'intention
            startActivity(mapIntent);
        } else {
            // Afficher un message si Google Maps n'est pas installé
            Toast.makeText(this, "Google Maps n'est pas installé.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAccueil() {
        Intent appPageIntent = new Intent(this, MainActivity.class);
        startActivity(appPageIntent);
    }
}