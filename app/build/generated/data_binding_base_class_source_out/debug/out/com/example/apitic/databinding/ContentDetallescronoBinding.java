// Generated by view binder compiler. Do not edit!
package com.example.apitic.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.apitic.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ContentDetallescronoBinding implements ViewBinding {
  @NonNull
  private final NestedScrollView rootView;

  @NonNull
  public final ConstraintLayout detallescrono;

  @NonNull
  public final TextView fCrono;

  @NonNull
  public final TextView iCrono;

  @NonNull
  public final TextView idCrono;

  @NonNull
  public final TextView nCrono;

  @NonNull
  public final TextView sCrono;

  private ContentDetallescronoBinding(@NonNull NestedScrollView rootView,
      @NonNull ConstraintLayout detallescrono, @NonNull TextView fCrono, @NonNull TextView iCrono,
      @NonNull TextView idCrono, @NonNull TextView nCrono, @NonNull TextView sCrono) {
    this.rootView = rootView;
    this.detallescrono = detallescrono;
    this.fCrono = fCrono;
    this.iCrono = iCrono;
    this.idCrono = idCrono;
    this.nCrono = nCrono;
    this.sCrono = sCrono;
  }

  @Override
  @NonNull
  public NestedScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ContentDetallescronoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ContentDetallescronoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.content_detallescrono, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ContentDetallescronoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.detallescrono;
      ConstraintLayout detallescrono = ViewBindings.findChildViewById(rootView, id);
      if (detallescrono == null) {
        break missingId;
      }

      id = R.id.fCrono;
      TextView fCrono = ViewBindings.findChildViewById(rootView, id);
      if (fCrono == null) {
        break missingId;
      }

      id = R.id.iCrono;
      TextView iCrono = ViewBindings.findChildViewById(rootView, id);
      if (iCrono == null) {
        break missingId;
      }

      id = R.id.idCrono;
      TextView idCrono = ViewBindings.findChildViewById(rootView, id);
      if (idCrono == null) {
        break missingId;
      }

      id = R.id.nCrono;
      TextView nCrono = ViewBindings.findChildViewById(rootView, id);
      if (nCrono == null) {
        break missingId;
      }

      id = R.id.sCrono;
      TextView sCrono = ViewBindings.findChildViewById(rootView, id);
      if (sCrono == null) {
        break missingId;
      }

      return new ContentDetallescronoBinding((NestedScrollView) rootView, detallescrono, fCrono,
          iCrono, idCrono, nCrono, sCrono);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}