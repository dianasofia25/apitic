// Generated by view binder compiler. Do not edit!
package com.example.apitic.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.apitic.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DateViewLayoutBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CardView cardView;

  @NonNull
  public final TextView content1;

  @NonNull
  public final TextView content2;

  @NonNull
  public final TextView content3;

  @NonNull
  public final TextView content4;

  @NonNull
  public final TextView content5;

  @NonNull
  public final CardView noteCard;

  @NonNull
  public final ImageView settingsIcon;

  @NonNull
  public final TextView titles;

  private DateViewLayoutBinding(@NonNull ConstraintLayout rootView, @NonNull CardView cardView,
      @NonNull TextView content1, @NonNull TextView content2, @NonNull TextView content3,
      @NonNull TextView content4, @NonNull TextView content5, @NonNull CardView noteCard,
      @NonNull ImageView settingsIcon, @NonNull TextView titles) {
    this.rootView = rootView;
    this.cardView = cardView;
    this.content1 = content1;
    this.content2 = content2;
    this.content3 = content3;
    this.content4 = content4;
    this.content5 = content5;
    this.noteCard = noteCard;
    this.settingsIcon = settingsIcon;
    this.titles = titles;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DateViewLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DateViewLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.date_view_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DateViewLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardView;
      CardView cardView = ViewBindings.findChildViewById(rootView, id);
      if (cardView == null) {
        break missingId;
      }

      id = R.id.content1;
      TextView content1 = ViewBindings.findChildViewById(rootView, id);
      if (content1 == null) {
        break missingId;
      }

      id = R.id.content2;
      TextView content2 = ViewBindings.findChildViewById(rootView, id);
      if (content2 == null) {
        break missingId;
      }

      id = R.id.content3;
      TextView content3 = ViewBindings.findChildViewById(rootView, id);
      if (content3 == null) {
        break missingId;
      }

      id = R.id.content4;
      TextView content4 = ViewBindings.findChildViewById(rootView, id);
      if (content4 == null) {
        break missingId;
      }

      id = R.id.content5;
      TextView content5 = ViewBindings.findChildViewById(rootView, id);
      if (content5 == null) {
        break missingId;
      }

      id = R.id.noteCard;
      CardView noteCard = ViewBindings.findChildViewById(rootView, id);
      if (noteCard == null) {
        break missingId;
      }

      id = R.id.settingsIcon;
      ImageView settingsIcon = ViewBindings.findChildViewById(rootView, id);
      if (settingsIcon == null) {
        break missingId;
      }

      id = R.id.titles;
      TextView titles = ViewBindings.findChildViewById(rootView, id);
      if (titles == null) {
        break missingId;
      }

      return new DateViewLayoutBinding((ConstraintLayout) rootView, cardView, content1, content2,
          content3, content4, content5, noteCard, settingsIcon, titles);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
