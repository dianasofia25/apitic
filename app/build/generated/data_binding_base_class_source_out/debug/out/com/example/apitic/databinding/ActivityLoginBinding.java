// Generated by view binder compiler. Do not edit!
package com.example.apitic.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.apitic.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityLoginBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button Login;

  @NonNull
  public final TextView Registrate;

  @NonNull
  public final TextView fPassword;

  @NonNull
  public final ImageView imageView3;

  @NonNull
  public final EditText lEmail;

  @NonNull
  public final EditText lPassword;

  @NonNull
  public final ProgressBar progressBar2;

  private ActivityLoginBinding(@NonNull ConstraintLayout rootView, @NonNull Button Login,
      @NonNull TextView Registrate, @NonNull TextView fPassword, @NonNull ImageView imageView3,
      @NonNull EditText lEmail, @NonNull EditText lPassword, @NonNull ProgressBar progressBar2) {
    this.rootView = rootView;
    this.Login = Login;
    this.Registrate = Registrate;
    this.fPassword = fPassword;
    this.imageView3 = imageView3;
    this.lEmail = lEmail;
    this.lPassword = lPassword;
    this.progressBar2 = progressBar2;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.Login;
      Button Login = ViewBindings.findChildViewById(rootView, id);
      if (Login == null) {
        break missingId;
      }

      id = R.id.Registrate;
      TextView Registrate = ViewBindings.findChildViewById(rootView, id);
      if (Registrate == null) {
        break missingId;
      }

      id = R.id.fPassword;
      TextView fPassword = ViewBindings.findChildViewById(rootView, id);
      if (fPassword == null) {
        break missingId;
      }

      id = R.id.imageView3;
      ImageView imageView3 = ViewBindings.findChildViewById(rootView, id);
      if (imageView3 == null) {
        break missingId;
      }

      id = R.id.lEmail;
      EditText lEmail = ViewBindings.findChildViewById(rootView, id);
      if (lEmail == null) {
        break missingId;
      }

      id = R.id.lPassword;
      EditText lPassword = ViewBindings.findChildViewById(rootView, id);
      if (lPassword == null) {
        break missingId;
      }

      id = R.id.progressBar2;
      ProgressBar progressBar2 = ViewBindings.findChildViewById(rootView, id);
      if (progressBar2 == null) {
        break missingId;
      }

      return new ActivityLoginBinding((ConstraintLayout) rootView, Login, Registrate, fPassword,
          imageView3, lEmail, lPassword, progressBar2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
