package com.example.apitic.ui.cronograma;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.apitic.ui.clases.Actividad;
import com.example.apitic.R;
import com.example.apitic.databinding.FragmentCronogramaBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CronogramaFragment extends Fragment {

    private FragmentCronogramaBinding binding;
    TextView actualDate;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    String userEmail;
    {
        assert user != null;
        userEmail = user.getEmail();
    }
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter<Actividad,ActividadViewHolder> actividadAdapter;
    RecyclerView dateLists;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textView;
    ImageView imageView;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCronogramaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Calendar currentTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E,dd LLL yyyy");
        final String dateTime = simpleDateFormat.format(currentTime.getTime());
        actualDate = (TextView) root.findViewById(R.id.dateFragment);
        actualDate.setText(dateTime);

        dateLists = (RecyclerView) root.findViewById(R.id.dateList);
        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.floatB);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),addCrono.class));
                getActivity(); // terminar un fragment (no poderse devolver)
        }
        });

        imageView = (ImageView) root.findViewById(R.id.imageCrono);
        textView = (TextView) root.findViewById(R.id.textCrono);

        if(isInternetAvailable()){
            Query query = fstore.collection("Cronograma").document(user.getUid())
                    .collection("miCrono")
                    .orderBy("initialDate",Query.Direction.DESCENDING).limit(30);

            getData(query);

        }else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_wifi_off_24);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Sin conexión a internet");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dateLists.setVisibility(View.GONE);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Sin conexión a internet\nIntentelo mas tarde...", Toast.LENGTH_SHORT).show();
                }
            });

        }

        swipeRefreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isInternetAvailable()){
                    Query query = fstore.collection("Cronograma").document(user.getUid())
                            .collection("miCrono")
                            .orderBy("initialDate",Query.Direction.DESCENDING).limit(30);

                    getData(query);

                }else {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_wifi_off_24);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Sin conexión a internet");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    dateLists.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    public void getData(Query query){

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_error_outline);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Cronograma vacío\n\nAgrega una actividad...");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            }
        });

        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        FirestoreRecyclerOptions<Actividad> actividades = new FirestoreRecyclerOptions.Builder<Actividad>()
                .setLifecycleOwner(getViewLifecycleOwner())
                .setQuery(query,Actividad.class).build();

        ////// RECYCLER ADAPTER = actividadAdapter ////////////
        actividadAdapter = new FirestoreRecyclerAdapter<Actividad, ActividadViewHolder>(actividades) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull ActividadViewHolder actividadViewHolder, int i,
                                            @NonNull Actividad actividad) {
                ///////// PASAR DE TIMESTAMP EN FIRESTORE A SIMPLEDATEFORMAT ///////
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
                String dateTimeI = simpleDateFormat.format(actividad.getInitialDate().getTime());

                actividadViewHolder.ActivityTitle.setText(actividad.getTitle());
                actividadViewHolder.ActivityID.setText("ID actividad: "+actividad.getId());
                actividadViewHolder.ActivityDateI.setText("Fecha inicio: \n"+dateTimeI);
                actividadViewHolder.ActivityDateF.setText("Ultima actualizacion: \n"+actividad.getFinalDate());
                actividadViewHolder.ActivityState.setText("Estado actividad: \n"+actividad.getState());
                actividadViewHolder.ActivityNote.setText("Notas: \n"+actividad.getNote());
                String actividadId = actividadAdapter.getSnapshots().getSnapshot(i).getId(); /// guardar el id de la actividad

                /////////////// CLIC EN LA UNA ACTIVIDAD PARA VER DATOS ///////////////
                actividadViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), detallesCrono.class);
                        i.putExtra("titulo",actividad.getTitle());
                        i.putExtra("id",actividad.getId());
                        i.putExtra("dateI",dateTimeI);
                        i.putExtra("dateF",actividad.getFinalDate());
                        i.putExtra("state",actividad.getState());
                        i.putExtra("note",actividad.getNote());
                        i.putExtra("cronoId",actividadId);
                        v.getContext().startActivity(i);
                    }
                });

                ////// CLIC EN LOS TRES PUNTOS PARA EDITAR O ELIMINAR NOTA /////////
                ImageView settingsIcon = actividadViewHolder.view.findViewById(R.id.settingsIcon);
                settingsIcon.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Editar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(v.getContext(), editCrono.class);
                                i.putExtra("titulo",actividad.getTitle());
                                i.putExtra("id",actividad.getId());
                                i.putExtra("dateI",dateTimeI);
                                i.putExtra("dateF",actividad.getFinalDate());
                                i.putExtra("state",actividad.getState());
                                i.putExtra("note",actividad.getNote());
                                i.putExtra("cronoId",actividadId);
                                v.getContext().startActivity(i);
                                return false;
                            }
                        });

                        menu.getMenu().add("Eliminar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReference = fstore.collection("Cronograma")
                                        .document(user.getUid()).collection("miCrono")
                                        .document(actividadId); //// REFERENCIA DEL DOCUMENTO

                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Actividad eliminada", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });

            }

            @NonNull
            @Override
            public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_view_layout,
                        parent, false);
                return new ActividadViewHolder(view);
            }
        };

//        dateLists.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        dateLists.setLayoutManager(new LinearLayoutManager(getActivity()));
        dateLists.setAdapter(actividadAdapter);
        dateLists.setItemAnimator(null);
    }

    public class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView ActivityTitle,ActivityID,ActivityDateI,ActivityDateF,ActivityState,ActivityNote;
        View view;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            ActivityTitle = itemView.findViewById(R.id.titles);
            ActivityID = itemView.findViewById(R.id.content1);
            ActivityDateI = itemView.findViewById(R.id.content2);
            ActivityDateF = itemView.findViewById(R.id.content3);
            ActivityState = itemView.findViewById(R.id.content4);
            ActivityNote = itemView.findViewById(R.id.content5);
            view = itemView;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_crono,menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Buscar por ID...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                if(isInternetAvailable()){
                    searchData(query);
                }else {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_wifi_off_24);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Sin conexión a internet");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    dateLists.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Calendar currentDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(currentDate.getTime());

        File csv = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/cronograma-"+date+".csv");

        MenuItem item2 = menu.findItem(R.id.action_downloadCrono);
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Query query = fstore.collection("Cronograma").document(user.getUid())
                        .collection("miCrono");

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");

                            downloadData(csv, task, simpleDateFormat);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchData(String s){
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Buscando...");
        progressDialog.show();

        Query query = fstore.collection("Cronograma").document(user.getUid())
                .collection("miCrono").whereEqualTo("id", s);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();
                if(task.getResult().isEmpty()){
                    Toast.makeText(getActivity(), "Actividad no encontrada.", Toast.LENGTH_SHORT).show();
                } else{
                    getData(query);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void downloadData(File csv, Task<QuerySnapshot> task, SimpleDateFormat simpleDateFormat){
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv));
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[]{"Activity_Title", "Activity_Id", "Activity_initialDate", "Activity_finalDate","Activity_State","Activity_Notes"});

            for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                String title = queryDocumentSnapshot.getString("title");
                String id = queryDocumentSnapshot.getString("id");
                String initialDate = simpleDateFormat.format(queryDocumentSnapshot.getDate("initialDate").getTime());
                String finalDate = queryDocumentSnapshot.getString("finalDate");
                String state = queryDocumentSnapshot.getString("state");
                String note = queryDocumentSnapshot.getString("note");

                data.add(new String[]{title,id,initialDate,finalDate,state,note});
            }
            writer.writeAll(data);
            writer.close();
            if(csv.exists()){
                Toast.makeText(getActivity(), "Cronograma descargado." + csv, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "Hubo un error, intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /////////////// CONEXION A INTERNET /////////////
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}