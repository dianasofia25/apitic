package com.example.apitic.ui.manuales;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apitic.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ManualAP extends AppCompatActivity {

    TextView t1;
    ImageView image;
    PDFView pdfViewAP;
    FirebaseAuth fAuth = FirebaseAuth.getInstance(); // Autorizacion firebase
    FirebaseUser User = fAuth.getCurrentUser();
    String user;
    {
        assert User != null;
        user = User.getEmail();
    }
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manualap);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StorageReference ManualAP1C = storageReference.child("Manuales/compressed/"+"ManualAP1.pdf");

        pdfViewAP = (PDFView) findViewById(R.id.pdfviewAP);
        t1 = (TextView) findViewById(R.id.textAP1);
        String value = getIntent().getExtras().getString("AP1"); // recibir bandera del fragment Gallery

        if (value.equals("1")){
            if(isInternetAvailable()){
                ManualAP1C.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String URL = uri.toString(); // obtener la URL de la firebase
                        t1.setText(URL);
                        new VistaPDF().execute(URL); // cargar la URL usando la clase VistaPD
                        Toast.makeText(ManualAP.this,"Actualizado",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManualAP.this,"Hubo un error en la actualización "
                                +e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                boolean granded = checkPermissionForReadExternalStorage();
                if(!granded){
                    requestPermissionForReadExternalStorage();
                } else {
                    readPdf();
                }
            }
        }
    }

    class VistaPDF extends AsyncTask<String,Void, InputStream> { // tarea asincrona para mostrar el pdf
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null; // declaracion de la variable de entrada
            try{
                URL url = new URL (strings[0]);// Crear una URL en string
                // crear un conexión con la url http
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode()==200){
                    // si la conexion es exitosa, da un 200 y se pasa de url a pdf
                    inputStream=new BufferedInputStream(urlConnection.getInputStream());

                }
            }catch (IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfViewAP.fromStream(inputStream).load(); // se muestra el pdf en la vista pdf
            super.onPostExecute(inputStream);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manuals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {

            StorageReference ManualAP1 = storageReference.child("Manuales/"+"ManualAP1.pdf");

            ManualAP1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String URL = uri.toString(); // obtener la URL de la firebase
                    downloadFile(ManualAP.this,"ManualAP",".pdf",DIRECTORY_DOWNLOADS,URL);
                    Toast.makeText(ManualAP.this,"Descargando...",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ManualAP.this,"Error! "
                            +e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /////// DESCARGAR UN ARCHIVO Y GUARDARLO EN LA MEMORIA LOCAL ///////////////
    public void downloadFile(Context context, String fileName, String fileExtension, String directory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, directory,fileName + fileExtension);
        downloadManager.enqueue(request);
    }

    /////////////// PERMITIR ACCEDER AL ALMACENAMIENTO LOCAL PARA MOSTRAR PDF /////////////
    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_STORAGE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted. Read Pdf from External Storage
                    readPdf();
                } else {
                    // permission denied. Disable the functionality that depends on this permission.
                }
            }
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void readPdf(){
        image = (ImageView) findViewById(R.id.imageAP1);
        File path = new File(Environment.getExternalStorageDirectory().getPath()
                + "/Android/data/com.example.apitic/files/Download/ManualAP.pdf");
        if(path.exists()){
            PDFView pdfView = findViewById(R.id.pdfviewAP);
            Toast.makeText(ManualAP.this,path.toString(),Toast.LENGTH_SHORT).show();
            pdfView.fromFile(path).load();
        } else {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.ic_error_outline);
            t1.setVisibility(View.VISIBLE);
            t1.setText("Manual no encontrado\n\nDescarguelo cuando tenga conexión estable");
            t1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
    }

    /////////////// CONEXION A INTERNET /////////////
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null){
            if(networkInfo.isConnected()){
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

}