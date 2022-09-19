// Generated by view binder compiler. Do not edit!
package com.example.apitic.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.apitic.R;
import com.github.mikephil.charting.charts.PieChart;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentResultadosBinding implements ViewBinding {
  @NonNull
  private final SwipeRefreshLayout rootView;

  @NonNull
  public final TextView CGuide1;

  @NonNull
  public final ConstraintLayout cronograma;

  @NonNull
  public final TextView dateFragmentR;

  @NonNull
  public final PieChart fragmentVerticalbarchartChart;

  @NonNull
  public final ImageView image;

  @NonNull
  public final ImageView imageResultados;

  @NonNull
  public final LinearLayout llayout;

  @NonNull
  public final NestedScrollView nscrollviewCrono;

  @NonNull
  public final RecyclerView resultadosList;

  @NonNull
  public final RelativeLayout rlayout;

  @NonNull
  public final SwipeRefreshLayout swipeRefreshLayoutR;

  @NonNull
  public final TextView textResultados;

  @NonNull
  public final TextView textResultados2;

  private FragmentResultadosBinding(@NonNull SwipeRefreshLayout rootView, @NonNull TextView CGuide1,
      @NonNull ConstraintLayout cronograma, @NonNull TextView dateFragmentR,
      @NonNull PieChart fragmentVerticalbarchartChart, @NonNull ImageView image,
      @NonNull ImageView imageResultados, @NonNull LinearLayout llayout,
      @NonNull NestedScrollView nscrollviewCrono, @NonNull RecyclerView resultadosList,
      @NonNull RelativeLayout rlayout, @NonNull SwipeRefreshLayout swipeRefreshLayoutR,
      @NonNull TextView textResultados, @NonNull TextView textResultados2) {
    this.rootView = rootView;
    this.CGuide1 = CGuide1;
    this.cronograma = cronograma;
    this.dateFragmentR = dateFragmentR;
    this.fragmentVerticalbarchartChart = fragmentVerticalbarchartChart;
    this.image = image;
    this.imageResultados = imageResultados;
    this.llayout = llayout;
    this.nscrollviewCrono = nscrollviewCrono;
    this.resultadosList = resultadosList;
    this.rlayout = rlayout;
    this.swipeRefreshLayoutR = swipeRefreshLayoutR;
    this.textResultados = textResultados;
    this.textResultados2 = textResultados2;
  }

  @Override
  @NonNull
  public SwipeRefreshLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentResultadosBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentResultadosBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_resultados, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentResultadosBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.CGuide1;
      TextView CGuide1 = ViewBindings.findChildViewById(rootView, id);
      if (CGuide1 == null) {
        break missingId;
      }

      id = R.id.cronograma;
      ConstraintLayout cronograma = ViewBindings.findChildViewById(rootView, id);
      if (cronograma == null) {
        break missingId;
      }

      id = R.id.dateFragmentR;
      TextView dateFragmentR = ViewBindings.findChildViewById(rootView, id);
      if (dateFragmentR == null) {
        break missingId;
      }

      id = R.id.fragment_verticalbarchart_chart;
      PieChart fragmentVerticalbarchartChart = ViewBindings.findChildViewById(rootView, id);
      if (fragmentVerticalbarchartChart == null) {
        break missingId;
      }

      id = R.id.image;
      ImageView image = ViewBindings.findChildViewById(rootView, id);
      if (image == null) {
        break missingId;
      }

      id = R.id.imageResultados;
      ImageView imageResultados = ViewBindings.findChildViewById(rootView, id);
      if (imageResultados == null) {
        break missingId;
      }

      id = R.id.llayout;
      LinearLayout llayout = ViewBindings.findChildViewById(rootView, id);
      if (llayout == null) {
        break missingId;
      }

      id = R.id.nscrollviewCrono;
      NestedScrollView nscrollviewCrono = ViewBindings.findChildViewById(rootView, id);
      if (nscrollviewCrono == null) {
        break missingId;
      }

      id = R.id.resultadosList;
      RecyclerView resultadosList = ViewBindings.findChildViewById(rootView, id);
      if (resultadosList == null) {
        break missingId;
      }

      id = R.id.rlayout;
      RelativeLayout rlayout = ViewBindings.findChildViewById(rootView, id);
      if (rlayout == null) {
        break missingId;
      }

      SwipeRefreshLayout swipeRefreshLayoutR = (SwipeRefreshLayout) rootView;

      id = R.id.textResultados;
      TextView textResultados = ViewBindings.findChildViewById(rootView, id);
      if (textResultados == null) {
        break missingId;
      }

      id = R.id.textResultados2;
      TextView textResultados2 = ViewBindings.findChildViewById(rootView, id);
      if (textResultados2 == null) {
        break missingId;
      }

      return new FragmentResultadosBinding((SwipeRefreshLayout) rootView, CGuide1, cronograma,
          dateFragmentR, fragmentVerticalbarchartChart, image, imageResultados, llayout,
          nscrollviewCrono, resultadosList, rlayout, swipeRefreshLayoutR, textResultados,
          textResultados2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}