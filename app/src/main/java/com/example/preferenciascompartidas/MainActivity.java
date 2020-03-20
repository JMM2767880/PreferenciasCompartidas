package com.example.preferenciascompartidas;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int PREGUNTAR_PERMISOS_CONTACTOS = 11;
    private final int PREGUNTAR_PERMISOS_MEMORIA_ESCRIBIR = 12;

    private Button btSettings, btGuardar, btLeer;
    private TextView tvTexto;

    SharedPreferences pref;

    List<Contacto> todosContactos;

    private File fichero;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permisos();
        initComponents();
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permisos() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},
                    PREGUNTAR_PERMISOS_CONTACTOS);

        }else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED){

        }
    }

    private void initComponents() {
        btSettings = findViewById(R.id.btSettings);
        btGuardar = findViewById(R.id.btGuardar);
        btLeer = findViewById(R.id.btLeer);
        tvTexto = findViewById(R.id.tvTexto);

        pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        todosContactos = new ArrayList<>();
    }

    private void init(){
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todosContactos = getContactList();
                boolean memoriaExterna = pref.getBoolean("me", false);;
                boolean memoriaInterna = pref.getBoolean("mi", false);

                if (memoriaExterna && !memoriaInterna){
                    crearYguardarFichero("externa");
                }else if (!memoriaExterna && memoriaInterna){
                    crearYguardarFichero("interna");
                }else if (memoriaExterna && memoriaInterna){
                    crearYguardarFichero("dos");
                }else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.noMemory), Toast.LENGTH_SHORT).show();
                }


            }
        });

        btLeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTexto.setText("");

                boolean memoriaExterna = pref.getBoolean("me", false);;
                boolean memoriaInterna = pref.getBoolean("mi", false);

                if (memoriaExterna && !memoriaInterna){
                    leerFichero("externa");
                }else if (!memoriaExterna && memoriaInterna){
                    leerFichero("interna");
                }else if (memoriaExterna && memoriaInterna){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.chageMemory), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.noMemory), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
            }
        });
    }

    private void leerFichero(String memoria) {
        switch (memoria){
            case "interna":
                fichero = new File(getFilesDir(),"COPIASEGURIDADCONTACTOS.CSV");
                break;
            case "externa":
                fichero = new File(getExternalFilesDir(null),"COPIASEGURIDADCONTACTOS.CSV");
                break;
            case "dos":
                System.out.println("Se guarda en las dos memorias");
                break;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(fichero));
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println("LECTURA: " + linea);
                tvTexto.append(linea + "\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void crearYguardarFichero(String memoria) {
        int memoriaSimple = 0;
        switch (memoria){
            case "interna":
                fichero = new File(getFilesDir(),"COPIASEGURIDADCONTACTOS.CSV");
                System.out.println("Se guarda en la memoria interna: " + getFilesDir());
                memoriaSimple = 1;
                break;
            case "externa":
                fichero = new File(getExternalFilesDir(null),"COPIASEGURIDADCONTACTOS.CSV");
                System.out.println("Se guarda en la memoria externa: " + getExternalFilesDir(null));
                memoriaSimple = 1;
                break;
            case "dos":
                System.out.println("Se guarda en las dos memorias");
                break;
        }

        if (memoriaSimple == 1) {
            try{
                FileWriter fw = new FileWriter(fichero);

                String texto = "";
                for (int i = 0; i < todosContactos.size(); i++){
                    texto += "Nombre: " + todosContactos.get(i).getNombre() + ", Teléfono: " + todosContactos.get(i).getNumero() + "\n";
                }

                System.out.println("FICHERO: " + texto);
                fw.write(texto);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            File fichero1 = new File(getExternalFilesDir(null),"COPIASEGURIDADCONTACTOS.CSV");

            try{
                FileWriter fw1 = new FileWriter(fichero1);

                String texto = "";
                for (int i = 0; i < todosContactos.size(); i++){
                    texto += "Nombre: " + todosContactos.get(i).getNombre() + ", Teléfono: " + todosContactos.get(i).getNumero() + "\n";
                }

                System.out.println("FICHERO: " + texto);
                fw1.write(texto);
                fw1.flush();
                fw1.close();

                File fichero2 = new File(getFilesDir(),"COPIASEGURIDADCONTACTOS.CSV");

                FileWriter fw2 = new FileWriter(fichero2);

                System.out.println("FICHERO: " + texto);
                fw2.write(texto);
                fw2.flush();
                fw2.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Toast.makeText(this, getResources().getString(R.string.memoryOk), Toast.LENGTH_SHORT).show();
    }


    private List<Contacto> getContactList() {
        List<Contacto> contactos = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Contacto contacto = new Contacto(name, phoneNo);
                        Log.i("AAA", "Name: " + name);
                        Log.i("AAA", "Phone Number: " + phoneNo);
                        contactos.add(contacto);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        return contactos;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("REQUESTCODE: " + requestCode);
        if(PREGUNTAR_PERMISOS_CONTACTOS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("GRANRESULTS: " + grantResults[0]);
                Toast.makeText(this, getResources().getString(R.string.permissionContact), Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
