package com.example.apitic.ui.resultados;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apitic.ui.clases.Actividad;
import com.example.apitic.R;
import com.example.apitic.ui.clases.Resultado;
import com.example.apitic.databinding.FragmentResultadosBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ResultadosFragment extends Fragment {

    private FragmentResultadosBinding binding;
    private PieChart pieChart;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    String userEmail;
    {
        assert user != null;
        userEmail = user.getEmail();
    }

    public static final ArrayList<String> dataList = new ArrayList<>();
    public static final ArrayList<String> dataListP = new ArrayList<>();
    public static final ArrayList<String> dataListF = new ArrayList<>();
    public static final ArrayList<String> dataListE = new ArrayList<>();

    SwipeRefreshLayout swipeRefreshLayout;
    private TextView actualMonth;

    RecyclerView recyclerView;
    ResultadosAdapter resultadosAdapter;
    TextView textView,textView2;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResultadosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pieChart = (PieChart) root.findViewById(R.id.fragment_verticalbarchart_chart);
        recyclerView = (RecyclerView) root.findViewById(R.id.resultadosList);
        imageView = (ImageView) root.findViewById(R.id.imageResultados);
        textView = (TextView) root.findViewById(R.id.textResultados);
        textView2 = (TextView) root.findViewById(R.id.textResultados2);

        final Calendar currentTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLLL yyyy");
        final String dateTime = simpleDateFormat.format(currentTime.getTime());
        actualMonth = (TextView) root.findViewById(R.id.dateFragmentR);
        actualMonth.setText("Mes de "+dateTime);

        if(isInternetAvailable()){
            getData();
            setupPieChart();
            setRecyclerView();
        }else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_wifi_off_24);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Sin conexión a internet");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            recyclerView.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
        }

        swipeRefreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.swipeRefreshLayoutR);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isInternetAvailable()){
                    getData();
                    setupPieChart();
                    setRecyclerView();
                }else {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_wifi_off_24);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Sin conexión a internet");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    recyclerView.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                    pieChart.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    private void getData(){
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.MONTH);
        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

        Query queryE = fstore.collection("Cronograma").document(user.getUid())
                .collection("miCrono").orderBy("state");

        queryE.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot actividades: queryDocumentSnapshots) {
                        Actividad actividad = actividades.toObject(Actividad.class);

                        ///////// PASAR DE TIMESTAMP EN FIRESTORE A SIMPLEDATEFORMAT ///////
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
                        String dateTimeI = simpleDateFormat.format(actividad.getInitialDate().getTime());

                        String[] month = dateTimeI.split("/");
                        String monthF = month[1].replace("0", "");
                        String thisMonthS = String.valueOf(thisMonth+1);

                        if(monthF.equals(thisMonthS)) {
                            dataList.add(actividad.getState());

                            dataListE.add(actividad.getState());
                            dataListE.remove("Parada de reloj");
                            dataListE.remove("Finalizado");

                            dataListP.add(actividad.getState());
                            dataListP.remove("Ejecutandose");
                            dataListP.remove("Finalizado");

                            dataListF.add(actividad.getState());
                            dataListF.remove("Ejecutandose");
                            dataListF.remove("Parada de reloj");

                            float stateE = (dataListE.size()*100)/ dataList.size();
                            float stateP = (dataListP.size()*100)/ dataList.size();
                            float stateF = (dataListF.size()*100)/ dataList.size();

                            imageView.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            pieChart.setVisibility(View.VISIBLE);
                            setupPieChart();
                            loadDataPieChart(stateE,stateP,stateF);
                        }
                    }
                } else{
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_error_outline);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("No hay actividades cargadas \nen el cronograma");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    pieChart.setVisibility(View.GONE);
                }


                if(thisMonth == 0 || thisMonth == 2 || thisMonth == 4 || thisMonth == 6
                        || thisMonth == 7 || thisMonth == 9 || thisMonth == 11){
                    if(thisDay == 31){
                        setResultados(dataListE.size(),dataListP.size(),dataListF.size());
                        dataListE.clear();
                        dataListP.clear();
                        dataListF.clear();
                        System.out.println("HOLA MES CON 31 DIAS: "+dataListE.size()+" "+dataListP.size()+" "+dataListF.size());
                    }
                } else if (thisMonth == 3 || thisMonth == 5 || thisMonth == 8 || thisMonth == 10){
                    if(thisDay == 30 || thisDay == 31){
                        setResultados(dataListE.size(),dataListP.size(),dataListF.size());
                        dataListE.clear();
                        dataListP.clear();
                        dataListF.clear();
                        System.out.println("HOLA MES CON 30 DIAS: "+dataListE.size()+" "+dataListP.size()+" "+dataListF.size());
                    }
                } else if (thisMonth == 1){
                    if(thisDay == 28){
                        setResultados(dataListE.size(),dataListP.size(),dataListF.size());
                        dataListE.clear();
                        dataListP.clear();
                        dataListF.clear();
                        System.out.println("HOLA FEBRERO: "+dataListE.size()+" "+dataListP.size()+" "+dataListF.size());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Error! "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupPieChart(){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(14);
        pieChart.setCenterText("Actividades");
        pieChart.setCenterTextSize(22);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadDataPieChart(float e, float p, float f){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float) e,"Ejecutandose"));
        pieEntries.add(new PieEntry((float) p,"Parada de reloj"));
        pieEntries.add(new PieEntry((float) f,"Finalizado"));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FFC107"));
        colors.add(Color.parseColor("#FF7800"));
        colors.add(Color.parseColor("#3A46BD"));

        PieDataSet dataSet = new PieDataSet(pieEntries,"Estado actividad");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutBounce);

    }

    private void setResultados(int sizeListE, int sizeListP, int sizeListF){
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.MONTH);
        int thisYear = calendar.get(Calendar.YEAR);

        DocumentReference documentReference = fstore.collection("Cronograma")
                .document(user.getUid()).collection("misResultados").document();

        Query queryE = fstore.collection("Cronograma").document(user.getUid())
                .collection("misResultados")
                .whereEqualTo("month",String.valueOf(thisMonth))
                .whereEqualTo("year",String.valueOf(thisYear));

        queryE.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty()){
                    Map<String,Object> resultados = new HashMap<>();
                    resultados.put("stateE",String.valueOf(sizeListE));
                    resultados.put("stateP",String.valueOf(sizeListP));
                    resultados.put("stateF",String.valueOf(sizeListF));
                    resultados.put("month",String.valueOf(thisMonth));
                    resultados.put("year",String.valueOf(thisYear));

                    documentReference.set(resultados);

                    dataListE.clear();
                    dataListP.clear();
                    dataListF.clear();
                }
            }
        });

    }

    private void setRecyclerView(){

        ArrayList<Resultado> resultadosList = new ArrayList<>();

        Query queryE = fstore.collection("Cronograma").document(user.getUid())
                .collection("misResultados").orderBy("month", Query.Direction.DESCENDING);

        queryE.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot resultados: queryDocumentSnapshots) {
                        Resultado resultado = resultados.toObject(Resultado.class);
                        resultadosList.add(resultado);

                        textView2.setVisibility(View.GONE);

                    }
                } else{
                    textView2.setVisibility(View.VISIBLE);
                    textView2.setText("Abrir la aplicación el último día del mes para generar " +
                            "tabla con los resultados finales");
                    textView2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                resultadosAdapter = new ResultadosAdapter(getContext(),resultadosList);
                recyclerView.setAdapter(resultadosAdapter);
                recyclerView.setItemAnimator(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Error! "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
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