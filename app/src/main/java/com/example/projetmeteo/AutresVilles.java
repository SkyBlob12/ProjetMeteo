package com.example.projetmeteo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.appelhttp.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AutresVilles extends AppCompatActivity {

    RelativeLayout layoutVille;
    private static final String EDIT_TEXT = "EditText"; //Permet d'enregistrer et récupérer la valeur de l'EditText
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autres_villes);

        Button validerRecherche = findViewById(R.id.boutonValiderRecherche);
        EditText rechercheVille = findViewById(R.id.rechercheVilles); //EditText de la recherche

        DataBase database = new DataBase(AutresVilles.this);
        validerRecherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Ajout d'une ville dans la base
                String ville = rechercheVille.getText().toString();
                database.addNewVille(ville);
                Toast.makeText(AutresVilles.this, "Ville has been added.", Toast.LENGTH_SHORT).show();
                rechercheVille.setText("");

                MainActivity mainActivity = new MainActivity();
                mainActivity.villeRecherche = rechercheVille.getText().toString();
                Log.d("XXXX", rechercheVille.getText().toString());
                startActivity(new Intent(AutresVilles.this, MainActivity.class));
            }
        });

        //Ajout d'une ville dans les préférences
        Button boutonVilleSave = findViewById(R.id.boutonVilleSave);
        boutonVilleSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //Préferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Ville", "" + rechercheVille.getText());
                editor.commit();
                rechercheVille.setText("");
            }
        });

        //Récupération de la ville précédente avec les préférences
        Button villePrecedente = findViewById(R.id.boutonVillePrecedente);
        villePrecedente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //Préferences
                String ville = prefs.getString("Ville", null);
                if (ville != null) {
                    rechercheVille.setText(ville);
                }
            }
        });

        //Ouverture de l'accueil avec un swipe à gauche
        layoutVille = findViewById(R.id.ecranRecherche);
        layoutVille.setOnTouchListener(new OnSwipeTouchListener(AutresVilles.this){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                startActivity(new Intent(AutresVilles.this, MainActivity.class));
            }
        });

        //Enregistrer la ville dans une fichier
        Button saveVilleFichier = findViewById(R.id.boutonSaveFile);
        saveVilleFichier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToSave = rechercheVille.getText().toString();
                File file = new File(getFilesDir(), "ville_recherché.txt"); //Fichier des villes recherchées
                // Vérifiez si le texte à enregistrer n'est pas vide
                if (!textToSave.isEmpty()) {
                    try {
                        // Créez un objet BufferedWriter pour écrire dans le fichier
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                        // Écrivez le texte dans le fichier
                        writer.write(textToSave);

                        // Fermez le BufferedWriter
                        writer.close();

                        // Affichez un message de réussite
                        Toast.makeText(AutresVilles.this, "Texte enregistré avec succès", Toast.LENGTH_SHORT).show();
                        rechercheVille.setText("");
                    } catch (IOException e) {
                        // Gérez les erreurs liées à l'écriture dans le fichier
                        e.printStackTrace();
                        Toast.makeText(AutresVilles.this, "Erreur lors de l'enregistrement du texte", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Affichez un message si le texte à enregistrer est vide
                    Toast.makeText(AutresVilles.this, "Veuillez entrer du texte avant d'enregistrer", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Suppression du fichier lorsqu'on quitte l'application
        File file = new File(getFilesDir(), "ville_recherché.txt"); //Fichier des villes recherchées
        file.delete();
    }

    //Sauvegarde la valeur dans les préférences lorsqu'on met en pause l'application
    @Override
    protected void onPause(){
        super.onPause();

        // Sauvegardez la valeur actuelle de l'EditText dans SharedPreferences
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        EditText rechercheVille = findViewById(R.id.rechercheVilles); //EditText de la recherche
        editor.putString(EDIT_TEXT, rechercheVille.getText().toString());
        editor.apply();
    }

    //Récupère la valeur à partir des préférences lorsqu'on réouvre l'application
    protected void onResume(){
        super.onResume();
        // Restaurez la valeur de l'EditText depuis SharedPreferences lors de la création de l'activité
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String savedText = preferences.getString(EDIT_TEXT, "");
        EditText rechercheVille = findViewById(R.id.rechercheVilles); //EditText de la recherche
        rechercheVille.setText(savedText);
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