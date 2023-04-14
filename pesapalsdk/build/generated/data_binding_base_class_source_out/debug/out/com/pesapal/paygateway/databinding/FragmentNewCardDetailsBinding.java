// Generated by view binder compiler. Do not edit!
package com.pesapal.pesapalsdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pesapal.pesapalsdk.R;
import com.santalu.maskedittext.MaskEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentNewCardDetailsBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final AppCompatButton acbCreateCard;

  @NonNull
  public final AppCompatImageView cardLogoMastercardImg;

  @NonNull
  public final AppCompatImageView cardLogoUnknownImg;

  @NonNull
  public final AppCompatImageView cardLogoVisaImg;

  @NonNull
  public final CardView clMpesaMethod;

  @NonNull
  public final ConstraintLayout constraintLayout;

  @NonNull
  public final ImageView cvvInfoIcon;

  @NonNull
  public final TextInputEditText etCvv;

  @NonNull
  public final TextInputLayout etCvvLayout;

  @NonNull
  public final MaskEditText etNumberCard;

  @NonNull
  public final TextInputLayout expiryMonthContainer;

  @NonNull
  public final TextInputLayout expiryYearContainer;

  @NonNull
  public final LinearLayout linearLayout2;

  @NonNull
  public final TextInputEditText monthField;

  @NonNull
  public final TextInputEditText yearField;

  private FragmentNewCardDetailsBinding(@NonNull ScrollView rootView,
      @NonNull AppCompatButton acbCreateCard, @NonNull AppCompatImageView cardLogoMastercardImg,
      @NonNull AppCompatImageView cardLogoUnknownImg, @NonNull AppCompatImageView cardLogoVisaImg,
      @NonNull CardView clMpesaMethod, @NonNull ConstraintLayout constraintLayout,
      @NonNull ImageView cvvInfoIcon, @NonNull TextInputEditText etCvv,
      @NonNull TextInputLayout etCvvLayout, @NonNull MaskEditText etNumberCard,
      @NonNull TextInputLayout expiryMonthContainer, @NonNull TextInputLayout expiryYearContainer,
      @NonNull LinearLayout linearLayout2, @NonNull TextInputEditText monthField,
      @NonNull TextInputEditText yearField) {
    this.rootView = rootView;
    this.acbCreateCard = acbCreateCard;
    this.cardLogoMastercardImg = cardLogoMastercardImg;
    this.cardLogoUnknownImg = cardLogoUnknownImg;
    this.cardLogoVisaImg = cardLogoVisaImg;
    this.clMpesaMethod = clMpesaMethod;
    this.constraintLayout = constraintLayout;
    this.cvvInfoIcon = cvvInfoIcon;
    this.etCvv = etCvv;
    this.etCvvLayout = etCvvLayout;
    this.etNumberCard = etNumberCard;
    this.expiryMonthContainer = expiryMonthContainer;
    this.expiryYearContainer = expiryYearContainer;
    this.linearLayout2 = linearLayout2;
    this.monthField = monthField;
    this.yearField = yearField;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentNewCardDetailsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentNewCardDetailsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_new_card_details, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentNewCardDetailsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.acbCreateCard;
      AppCompatButton acbCreateCard = ViewBindings.findChildViewById(rootView, id);
      if (acbCreateCard == null) {
        break missingId;
      }

      id = R.id.card_logo_mastercard_img;
      AppCompatImageView cardLogoMastercardImg = ViewBindings.findChildViewById(rootView, id);
      if (cardLogoMastercardImg == null) {
        break missingId;
      }

      id = R.id.card_logo_unknown_img;
      AppCompatImageView cardLogoUnknownImg = ViewBindings.findChildViewById(rootView, id);
      if (cardLogoUnknownImg == null) {
        break missingId;
      }

      id = R.id.card_logo_visa_img;
      AppCompatImageView cardLogoVisaImg = ViewBindings.findChildViewById(rootView, id);
      if (cardLogoVisaImg == null) {
        break missingId;
      }

      id = R.id.clMpesaMethod;
      CardView clMpesaMethod = ViewBindings.findChildViewById(rootView, id);
      if (clMpesaMethod == null) {
        break missingId;
      }

      id = R.id.constraintLayout;
      ConstraintLayout constraintLayout = ViewBindings.findChildViewById(rootView, id);
      if (constraintLayout == null) {
        break missingId;
      }

      id = R.id.cvv_info_icon;
      ImageView cvvInfoIcon = ViewBindings.findChildViewById(rootView, id);
      if (cvvInfoIcon == null) {
        break missingId;
      }

      id = R.id.et_cvv;
      TextInputEditText etCvv = ViewBindings.findChildViewById(rootView, id);
      if (etCvv == null) {
        break missingId;
      }

      id = R.id.et_cvv_layout;
      TextInputLayout etCvvLayout = ViewBindings.findChildViewById(rootView, id);
      if (etCvvLayout == null) {
        break missingId;
      }

      id = R.id.et_number_card;
      MaskEditText etNumberCard = ViewBindings.findChildViewById(rootView, id);
      if (etNumberCard == null) {
        break missingId;
      }

      id = R.id.expiry_month_container;
      TextInputLayout expiryMonthContainer = ViewBindings.findChildViewById(rootView, id);
      if (expiryMonthContainer == null) {
        break missingId;
      }

      id = R.id.expiry_year_container;
      TextInputLayout expiryYearContainer = ViewBindings.findChildViewById(rootView, id);
      if (expiryYearContainer == null) {
        break missingId;
      }

      id = R.id.linearLayout2;
      LinearLayout linearLayout2 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout2 == null) {
        break missingId;
      }

      id = R.id.month_field;
      TextInputEditText monthField = ViewBindings.findChildViewById(rootView, id);
      if (monthField == null) {
        break missingId;
      }

      id = R.id.year_field;
      TextInputEditText yearField = ViewBindings.findChildViewById(rootView, id);
      if (yearField == null) {
        break missingId;
      }

      return new FragmentNewCardDetailsBinding((ScrollView) rootView, acbCreateCard,
          cardLogoMastercardImg, cardLogoUnknownImg, cardLogoVisaImg, clMpesaMethod,
          constraintLayout, cvvInfoIcon, etCvv, etCvvLayout, etNumberCard, expiryMonthContainer,
          expiryYearContainer, linearLayout2, monthField, yearField);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
