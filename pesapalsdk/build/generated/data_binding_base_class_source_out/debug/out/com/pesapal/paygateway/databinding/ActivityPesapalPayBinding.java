// Generated by view binder compiler. Do not edit!
package com.pesapal.pesapalsdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.pesapal.pesapalsdk.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityPesapalPayBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView cancelPayment;

  @NonNull
  public final ConstraintLayout clToolbar;

  @NonNull
  public final FrameLayout fragControl;

  @NonNull
  public final ImageView tvClose;

  @NonNull
  public final TextView tvPesapal;

  private ActivityPesapalPayBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView cancelPayment, @NonNull ConstraintLayout clToolbar,
      @NonNull FrameLayout fragControl, @NonNull ImageView tvClose, @NonNull TextView tvPesapal) {
    this.rootView = rootView;
    this.cancelPayment = cancelPayment;
    this.clToolbar = clToolbar;
    this.fragControl = fragControl;
    this.tvClose = tvClose;
    this.tvPesapal = tvPesapal;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityPesapalPayBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityPesapalPayBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_pesapal_pay, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityPesapalPayBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cancelPayment;
      TextView cancelPayment = ViewBindings.findChildViewById(rootView, id);
      if (cancelPayment == null) {
        break missingId;
      }

      id = R.id.clToolbar;
      ConstraintLayout clToolbar = ViewBindings.findChildViewById(rootView, id);
      if (clToolbar == null) {
        break missingId;
      }

      id = R.id.frag_control;
      FrameLayout fragControl = ViewBindings.findChildViewById(rootView, id);
      if (fragControl == null) {
        break missingId;
      }

      id = R.id.tvClose;
      ImageView tvClose = ViewBindings.findChildViewById(rootView, id);
      if (tvClose == null) {
        break missingId;
      }

      id = R.id.tvPesapal;
      TextView tvPesapal = ViewBindings.findChildViewById(rootView, id);
      if (tvPesapal == null) {
        break missingId;
      }

      return new ActivityPesapalPayBinding((ConstraintLayout) rootView, cancelPayment, clToolbar,
          fragControl, tvClose, tvPesapal);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
