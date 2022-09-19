package com.example.apitic.ui.manuales;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.example.apitic.databinding.ActivityMainBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ManualHughesnet extends AppCompatActivity {

    private ActivityMainBinding binding;

    TextView t1,t2,t3;
    ImageView image;
    PDFView pdfView;
    DatabaseReference url;
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
        setContentView(R.layout.activity_manualhughesnet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StorageReference ManualH1C = storageReference.child("Manuales/compressed/"+"ManualH1.pdf");
        StorageReference ManualH2C = storageReference.child("Manuales/compressed/"+"ManualH2.pdf");
        StorageReference ManualH3C = storageReference.child("Manuales/compressed/"+"ManualH3.pdf");

        pdfView = (PDFView) findViewById(R.id.pdfview);
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);
        t3 = (TextView) findViewById(R.id.text3);

        String value = getIntent().getExtras().getString("H1"); // recibir bandera del fragment Gallery

        switch (value) {
            case "1":
                if(isInternetAvailable()){
                    ManualH1C.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String URL = uri.toString(); // obtener la URL de la firebase
                            t1.setText(URL);
                            new VistaPDF().execute(URL); // cargar la URL usando la clase VistaPD
                            Toast.makeText(ManualHughesnet.this, "Actualizado", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManualHughesnet.this, "Hubo un error en la actualización "
                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                } else{
                    boolean granded = checkPermissionForReadExternalStorage();
                    if(!granded){
                        requestPermissionForReadExternalStorage();
                    }
                    else {
                        readPdf();
                    }
                }

            case "2":
                if(isInternetAvailable()){
                    ManualH2C.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String URL = uri.toString(); // obtener la URL de la firebase
                            t2.setText(URL);
                            new VistaPDF().execute(URL); // cargar la URL usando la clase VistaPD
                            Toast.makeText(ManualHughesnet.this, "Actualizado", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManualHughesnet.this, "Hubo un error en la actualización "
                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                } else{
                    boolean granded = checkPermissionForReadExternalStorage();
                    if(!granded){
                        requestPermissionForReadExternalStorage();
                    }
                    else {
                        readPdf();
                    }
                }

            case "3":
                if(isInternetAvailable()){
                    ManualH3C.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String URL = uri.toString(); // obtener la URL de la firebase
                            t3.setText(URL);
                            new VistaPDF().execute(URL); // cargar la URL usando la clase VistaPD
                            Toast.makeText(ManualHughesnet.this, "Actualizado", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManualHughesnet.this, "Hubo un error en la actualización "
                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                } else{
                    boolean granded = checkPermissionForReadExternalStorage();
                    if(!granded){
                        requestPermissionForReadExternalStorage();
                    }
                    else {
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
            pdfView.fromStream(inputStream).load(); // se muestra el pdf en la vista pdf
            super.onPostExecute(inputStream);
        }
    }

     ///////////////// MENU ////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manuals_hughesnet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        url = FirebaseDatabase.getInstance().getReference("url/APKOASIS"); // Autorizacion firebase database

        String value = getIntent().getExtras().getString("H1"); // recibir bandera del fragment Gallery

        StorageReference ManualH1 = storageReference.child("Manuales/"+"ManualH1.PDF");
        StorageReference ManualH2 = storageReference.child("Manuales/"+"ManualH2.pdf");
        StorageReference ManualH3 = storageReference.child("Manuales/"+"ManualH3.pdf");

        //// boton de atras en el fragment ////
        if(id == android.R.id.home){
            onBackPressed();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            switch (value) {
                case "1":
                    ManualH1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String URL = uri.toString(); // obtener la URL de la firebase
                            downloadFile(ManualHughesnet.this, "ManualH1", ".pdf", DIRECTORY_DOWNLOADS, URL);
                            Toast.makeText(ManualHughesnet.this, "Descargando...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManualHughesnet.this, "Error! "
                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                case "2":
                    ManualH2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String URL = uri.toString(); // obtener la URL de la firebase
                            downloadFile(ManualHughesnet.this, "ManualH2", ".pdf", DIRECTORY_DOWNLOADS, URL);
                            Toast.makeText(ManualHughesnet.this, "Descargando...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManualHughesnet.this, "Error! "
                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                case "3":
                    ManualH3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String URL = uri.toString(); // obtener la URL de la firebase
                            downloadFile(ManualHughesnet.this, "ManualH3", ".pdf", DIRECTORY_DOWNLOADS, URL);
                            Toast.makeText(ManualHughesnet.this, "Descargando...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManualHughesnet.this, "Error! "
                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
            return true;
        }

        /////////// DESCARGAR APK OASIS //////////
        if(id == R.id.action_downloadAPK){

            url.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String URL = snapshot.getValue(String.class);
                        downloadFile(ManualHughesnet.this,URL,"",DIRECTORY_DOWNLOADS, URL);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

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


    /////////////// PERMITIR ACCEDER AL ALMACENAMIENTO LOCAL /////////////
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
        String value = getIntent().getExtras().getString("H1"); // recibir bandera del fragment Gallery
        image = (ImageView) findViewById(R.id.imageH1);

        File path = new File("");

        switch (value){
            case "1":
                path = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/Android/data/com.example.apitic/files/Download/ManualH1.PDF");
                break;
            case "2":
                path = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/Android/data/com.example.apitic/files/Download/ManualH2.pdf");
                break;
            case "3":
                path = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/Android/data/com.example.apitic/files/Download/ManualH3.pdf");
                break;
        }

        if(path.exists()){
            PDFView pdfView = findViewById(R.id.pdfview);
            Toast.makeText(ManualHughesnet.this,path.toString(),Toast.LENGTH_SHORT).show();
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