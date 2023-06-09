// Generated by view binder compiler. Do not edit!
package com.pesapal.pesapalsdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.pesapal.pesapalsdk.R;
import com.santalu.maskedittext.MaskEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentNewCardAddressBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final AppCompatButton acbNextStep;

  @NonNull
  public final CardView clMpesaMethod;

  @NonNull
  public final CountryCodePicker countryCodePicker;

  @NonNull
  public final TextInputEditText etAddress;

  @NonNull
  public final TextInputEditText etCity;

  @NonNull
  public final TextInputEditText etEmail;

  @NonNull
  public final TextInputEditText etFirstName;

  @NonNull
  public final MaskEditText etPhoneNumber;

  @NonNull
  public final TextInputEditText etPostal;

  @NonNull
  public final TextInputEditText etSurname;

  @NonNull
  public final LinearLayout llInputNewCard;

  @NonNull
  public final LinearLayout llTitleBarNewCard;

  @NonNull
  public final TextInputLayout postalCodeLayout;

  private FragmentNewCardAddressBinding(@NonNull ScrollView rootView,
      @NonNull AppCompatButton acbNextStep, @NonNull CardView clMpesaMethod,
      @NonNull CountryCodePicker countryCodePicker, @NonNull TextInputEditText etAddress,
      @NonNull TextInputEditText etCity, @NonNull TextInputEditText etEmail,
      @NonNull TextInputEditText etFirstName, @NonNull MaskEditText etPhoneNumber,
      @NonNull TextInputEditText etPostal, @NonNull TextInputEditText etSurname,
      @NonNull LinearLayout llInputNewCard, @NonNull LinearLayout llTitleBarNewCard,
      @NonNull TextInputLayout postalCodeLayout) {
    this.rootView = rootView;
    this.acbNextStep = acbNextStep;
    this.clMpesaMethod = clMpesaMethod;
    this.countryCodePicker = countryCodePicker;
    this.etAddress = etAddress;
    this.etCity = etCity;
    this.etEmail = etEmail;
    this.etFirstName = etFirstName;
    this.etPhoneNumber = etPhoneNumber;
    this.etPostal = etPostal;
    this.etSurname = etSurname;
    this.llInputNewCard = llInputNewCard;
    this.llTitleBarNewCard = llTitleBarNewCard;
    this.postalCodeLayout = postalCodeLayout;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentNewCardAddressBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentNewCardAddressBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_new_card_address, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentNewCardAddressBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.acbNextStep;
      AppCompatButton acbNextStep = ViewBindings.findChildViewById(rootView, id);
      if (acbNextStep == null) {
        break missingId;
      }

      id = R.id.clMpesaMethod;
      CardView clMpesaMethod = ViewBindings.findChildViewById(rootView, id);
      if (clMpesaMethod == null) {
        break missingId;
      }

      id = R.id.countryCode_picker;
      CountryCodePicker countryCodePicker = ViewBindings.findChildViewById(rootView, id);
      if (countryCodePicker == null) {
        break missingId;
      }

      id = R.id.et_address;
      TextInputEditText etAddress = ViewBindings.findChildViewById(rootView, id);
      if (etAddress == null) {
        break missingId;
      }

      id = R.id.et_city;
      TextInputEditText etCity = ViewBindings.findChildViewById(rootView, id);
      if (etCity == null) {
        break missingId;
      }

      id = R.id.et_email;
      TextInputEditText etEmail = ViewBindings.findChildViewById(rootView, id);
      if (etEmail == null) {
        break missingId;
      }

      id = R.id.et_first_name;
      TextInputEditText etFirstName = ViewBindings.findChildViewById(rootView, id);
      if (etFirstName == null) {
        break missingId;
      }

      id = R.id.et_phone_number;
      MaskEditText etPhoneNumber = ViewBindings.findChildViewById(rootView, id);
      if (etPhoneNumber == null) {
        break missingId;
      }

      id = R.id.et_postal;
      TextInputEditText etPostal = ViewBindings.findChildViewById(rootView, id);
      if (etPostal == null) {
        break missingId;
      }

      id = R.id.et_surname;
      TextInputEditText etSurname = ViewBindings.findChildViewById(rootView, id);
      if (etSurname == null) {
        break missingId;
      }

      id = R.id.llInputNewCard;
      LinearLayout llInputNewCard = ViewBindings.findChildViewById(rootView, id);
      if (llInputNewCard == null) {
        break missingId;
      }

      id = R.id.llTitleBarNewCard;
      LinearLayout llTitleBarNewCard = ViewBindings.findChildViewById(rootView, id);
      if (llTitleBarNewCard == null) {
        break missingId;
      }

      id = R.id.postal_code_layout;
      TextInputLayout postalCodeLayout = ViewBindings.findChildViewById(rootView, id);
      if (postalCodeLayout == null) {
        break missingId;
      }

      return new FragmentNewCardAddressBinding((ScrollView) rootView, acbNextStep, clMpesaMethod,
          countryCodePicker, etAddress, etCity, etEmail, etFirstName, etPhoneNumber, etPostal,
          etSurname, llInputNewCard, llTitleBarNewCard, postalCodeLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
