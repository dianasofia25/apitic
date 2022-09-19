package com.example.apitic.ui.inicio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.apitic.MainActivity;
import com.example.apitic.databinding.FragmentInicioBinding;
import com.example.apitic.ui.auth.Login;
import com.example.apitic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.anstrontechnologies.corehelper.AnstronCoreHelper;

public class Inicio extends Fragment implements DatePickerDialog.OnDateSetListener{

    private FragmentInicioBinding binding;
    TextView WName, Username, WEmail,WTelephone, WBD;
    EditText UsernameE,BirthdateE,TelephoneNE;
    Button signOutButton;
    CircleImageView image;
    SwipeRefreshLayout swipeRefreshLayout;
    byte[] data_byte = null;
    boolean NameVisible, BirthdayVisible,TelephoneVisible;
    DatabaseReference dbTecnicos = FirebaseDatabase.getInstance().getReference("Tecnicos"); // Autorizacion firebase database
    FirebaseAuth fAuth = FirebaseAuth.getInstance(); // Autorizacion firebase
    FirebaseUser User = fAuth.getCurrentUser();
    String user;
    {
        assert User != null;
        user = User.getEmail();
    }
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        WName = (TextView) root.findViewById(R.id.welcome_name);
        Username = (TextView) root.findViewById(R.id.Username);
        UsernameE = (EditText) root.findViewById(R.id.UsernameE);
        WEmail = (TextView) root.findViewById(R.id.iEmail);
        WTelephone = (TextView) root.findViewById(R.id.TelephoneNumber);
        TelephoneNE = (EditText) root.findViewById(R.id.TelephoneNumberE);
        WBD = (TextView) root.findViewById(R.id.hBirthdate);
        BirthdateE = (EditText) root.findViewById(R.id.hBirthdateE);
        signOutButton = (Button) root.findViewById(R.id.logout);
        image = (CircleImageView)root.findViewById(R.id.imageView2);

        // SELECCIONAR DE LA CLASE Tecnicos UN DETERMINADO CHILDREN
        Query query = FirebaseDatabase.getInstance().getReference("Tecnicos")
                .orderByChild("temail").equalTo(user);

        emailVerified();
        getUserDatafromFirebase(query);
        editImage();
        editEmail();
        editName(query);
        editBirthdate(query);
        editTelephoneNumber(query);
        signOut();

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayoutInicio);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                emailVerified();
                getUserDatafromFirebase(query);
                editImage();
                editEmail();
                editName(query);
                editBirthdate(query);
                editTelephoneNumber(query);
                signOut();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    public void getUserDatafromFirebase(Query query){

        query.addValueEventListener(dbTecnicos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot tSnapshot : snapshot.getChildren()){
                        String nameM = tSnapshot.child("tname").getValue().toString();
//                                .toUpperCase(Locale.ROOT); //toUpperCase ayuda a poner el text en MAYUS
                        String name = tSnapshot.child("tname").getValue().toString();
                        String email = tSnapshot.child("temail").getValue().toString();
                        String telephone = tSnapshot.child("tphonenumber").getValue().toString();
                        String BD = tSnapshot.child("tbirthDate").getValue().toString();
                        assert user != null;
                        if (user.equals(email)){
                            WName.setText(nameM);
                            Username.setText("Nombre usuario:\n"+name);
                            WEmail.setText("Correo electrónico:\n"+email);
                            WTelephone.setText("Telefono: \n"+telephone);
                            WBD.setText("Fecha de nacimiento:\n"+BD);

                            StorageReference ref = storageReference.child("images/"+User.getUid());

                            try {
                                File lfile = File.createTempFile("Template","png");
                                ref.getFile(lfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        if(binding != null){
                                            Bitmap bitmap = BitmapFactory.decodeFile(lfile.getAbsolutePath());
                                            binding.imageView2.setImageBitmap(bitmap);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
    }

    //////////////// ACCESO SOLO A LOS USUARIOS CON CORREOS VERIFICADOS ////////////////////
    public void emailVerified(){
        if (!User.isEmailVerified()){ // Si el email NO esta verificado
            final CharSequence[] opciones = {"Enviar correo de verificacion", "Cancelar"};
            AlertDialog.Builder sendEmail = new AlertDialog.Builder(requireContext());
            sendEmail.setTitle("Acceso restringido. Verifique su correo para poder ingresar.");
            sendEmail.setItems(opciones, (dialog, item) -> {
                if (opciones[item].equals("Enviar correo de verificacion")) {
                    User.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) { // Si se envia el correo de verificacion
                            Toast.makeText(getActivity(), "Link de verificación enviado.\nRevise el SPAM de su correo.",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() { // si no se envia por error
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error! No se pudo enviar el link de verificacion.",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dialog.dismiss();
                    fAuth.signOut();
                    startActivity(new Intent(getActivity(),Login.class));
                    requireActivity().finish(); // terminar un fragment (no poderse devolver)
                }
            });
            sendEmail.create().show(); // mostrar el dialogo
            Toast.makeText(getActivity(), "CORREO NO VERIFICADO",Toast.LENGTH_SHORT).show();
        }
    }

    public void editEmail(){
        /////// EDITAR CORREO /////
        WEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "No se puede editar el correo.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editImage(){
        ////////// EDITAR LA IMAGEN /////////////
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] opciones = {"Eligir imagen existente", "Cancelar"};
                AlertDialog.Builder ChangeImage = new AlertDialog.Builder(view.getContext());
                ChangeImage.setTitle("Desea cambiar la imagen del perfil?");
                ChangeImage.setItems(opciones, (dialog, item) -> {
                    if (opciones[item].equals("Eligir imagen existente")) {
                        Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,1);
                    } else {
                        dialog.dismiss();
                    }
                });
                ChangeImage.create().show(); // mostrar el dialogo
            }
        });
    }

    public void editName(Query query){
        ///////// EDITAR EL NOMBRE //////////////
        Username.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                Username.setVisibility(View.INVISIBLE);
                UsernameE.setVisibility(View.VISIBLE);

                UsernameE.requestFocus();
                InputMethodManager imm = (InputMethodManager) requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                UsernameE.setSelection(0);

                UsernameE.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int Right = 2;
                        if (event.getAction()== MotionEvent.ACTION_UP){
                            if( event.getRawX()>=UsernameE.getRight()-UsernameE.getCompoundDrawables()[Right].getBounds().width()){
                                int selection = UsernameE.getSelectionEnd();
                                if (NameVisible){
                                    // EDITAR
                                    NameVisible = false;
                                } else {
                                    // TERMINAR DE EDITAR
                                    UsernameE.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.done_24,0);
                                    String name = UsernameE.getText().toString().trim();
                                    if (!TextUtils.isEmpty(name)){ // Si el campo de email no esta vacio
                                        Username.setText("Nombre usuario:\n"+name);
                                        query.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                dbTecnicos.child(snapshot.getKey()).child("tname").setValue(name);
                                                UsernameE.setText(null);

                                                imm.hideSoftInputFromWindow(UsernameE.getWindowToken(), 0);
                                            }
                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                            @Override
                                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                                            @Override
                                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {}
                                        });
                                    } else {
                                        UsernameE.setText(null);
                                        imm.hideSoftInputFromWindow(TelephoneNE.getWindowToken(), 0);
                                    }
                                    Username.setVisibility(View.VISIBLE);
                                    UsernameE.setVisibility(View.INVISIBLE);
                                    NameVisible =true;
                                }
                                UsernameE.setSelection(selection);
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        });
    }

    public void editBirthdate(Query query){
        ///////// EDITAR FECHA DE NACIMIENTO /////////
        WBD.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                WBD.setVisibility(View.INVISIBLE);
                BirthdateE.setVisibility(View.VISIBLE);
                DialogFragment datePicker = new DatePickerFragmentF(); // Mostrar el DatePicker en un dialog
                datePicker.setTargetFragment(Inicio.this, 0); /// Mostrar resultado en el fragment
                datePicker.show(getFragmentManager(), "datePicker");
                BirthdateE.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int Right = 2;
                        if (event.getAction()== MotionEvent.ACTION_UP){
                            if( event.getRawX()>=BirthdateE.getRight()-BirthdateE.getCompoundDrawables()[Right].getBounds().width()){
                                int selection = BirthdateE.getSelectionEnd();
                                if (BirthdayVisible){
                                    // EDITAR
                                    BirthdayVisible = false;
                                } else {
                                    // TERMINAR DE EDITAR
                                    BirthdateE.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.done_24,0);
                                    String BD = BirthdateE.getText().toString().trim();
                                    if (!TextUtils.isEmpty(BD)){ // Si el campo de email no esta vacio
                                        WBD.setText("Fecha de nacimiento:\n"+BD);
                                        query.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                dbTecnicos.child(snapshot.getKey()).child("tbirthDate").setValue(BD);
                                            }
                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                            @Override
                                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                                            @Override
                                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {}
                                        });
                                    } else {
                                        BirthdateE.setText(null);
                                    }
                                    WBD.setVisibility(View.VISIBLE);
                                    BirthdateE.setVisibility(View.INVISIBLE);
                                    BirthdayVisible =true;
                                }
                                BirthdateE.setSelection(selection);
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        });
    }

    public void editTelephoneNumber(Query query){
        //////// EDITAR NUMERO TELEFONICO ///////////
        WTelephone.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                WTelephone.setVisibility(View.INVISIBLE);
                TelephoneNE.setVisibility(View.VISIBLE);

                TelephoneNE.requestFocus();
                InputMethodManager imm = (InputMethodManager) requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                TelephoneNE.setSelection(0);

                TelephoneNE.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int Right = 2;
                        if (event.getAction()== MotionEvent.ACTION_UP){
                            if( event.getRawX()>=TelephoneNE.getRight()-TelephoneNE.getCompoundDrawables()[Right].getBounds().width()){
                                int selection = TelephoneNE.getSelectionEnd();
                                if (TelephoneVisible){
                                    // EDITAR
                                    TelephoneVisible = false;
                                } else {
                                    // TERMINAR DE EDITAR
                                    TelephoneNE.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.done_24,0);
                                    String TN = TelephoneNE.getText().toString().trim();
                                    if (!TextUtils.isEmpty(TN)){ // Si el campo de email no esta vacio
                                        if (TN.length() == 10){ // Si el numero telefonico tiene una longitud de 10 digitos
                                            WTelephone.setText("Número telefónico:\n"+TN);
                                            query.addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                    dbTecnicos.child(snapshot.getKey()).child("tphonenumber").setValue(TN);

                                                    imm.hideSoftInputFromWindow(TelephoneNE.getWindowToken(), 0);
                                                }
                                                @Override
                                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                                @Override
                                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                                                @Override
                                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {}
                                            });
                                        } else {
                                            Toast.makeText(getActivity(), "Número telefónico invalido.",Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        TelephoneNE.setText(null);
                                    }
                                    WTelephone.setVisibility(View.VISIBLE);
                                    TelephoneNE.setVisibility(View.INVISIBLE);
                                    TelephoneVisible =true;
                                }
                                TelephoneNE.setSelection(selection);
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        });
    }

    public void signOut(){
        // BOTON LOUGOUT
        signOutButton.setOnClickListener(v -> {
            fAuth.signOut();
            startActivity(new Intent(getActivity(),Login.class));
            getActivity().finish(); // terminar un fragment (no poderse devolver)
        });
    }

    ////////// EDITAR LA IMAGEN PARTE 2 /////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null){
            CircleImageView i = image.findViewById(R.id.imageView2);
            Uri selectImage = data.getData(); // obtener la ubicacion de la imagen
            i.setImageURI(selectImage); // Poner en el circle Image View la imagen de la ubicacion

            if (selectImage != null) {
                // Cuadro de dialogo para mostrar el progreso
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Cambiando foto...");
                progressDialog.show();

                // Ruta en la cual se va a guardar la imagen
                StorageReference ref = storageReference.child("images/" + User.getUid());

                ////////// COMPRIMIR IMAGEN CON BITMAP ANTES DE SUBIRLA A FIREBASE /////////////
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.requireActivity().getContentResolver(), selectImage);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,bytes);
                    data_byte = bytes.toByteArray();
                } catch (IOException e) { e.printStackTrace(); }

                ref.putBytes(data_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Imagen subida exitosamente
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Imagen cargada!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e){
                        // La imagen no se pudo subir
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    // Porcentaje de progreso
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Cargando " + (int)progress + "%");
                    }
                });


            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //////////// MOSTRAR FECHA DE NACIMIENTO EN UN DIALOG PARTE 2////////////
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.YEAR,year);
        calendario.set(Calendar.MONTH,month);
        calendario.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String Birthdate = DateFormat.getDateInstance().format(calendario.getTime()); // Fecha elegida
        BirthdateE.setText(Birthdate);
    }
}

