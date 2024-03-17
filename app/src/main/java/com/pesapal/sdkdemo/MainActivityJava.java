package com.pesapal.sdkdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pesapal.sdk.activity.PesapalSdkActivity;
import com.pesapal.sdk.model.card.CustomerData;
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse;
import com.pesapal.sdk.utils.PESAPALAPI3SDK;
import com.pesapal.sdk.utils.PESAPALAPI3SDK.COUNTRIES_ENUM;
import com.pesapal.sdkdemo.adapter.DemoCartAdapter;
import com.pesapal.sdkdemo.databinding.ActivityMainBinding;
import com.pesapal.sdkdemo.model.CatalogueModel;
import com.pesapal.sdkdemo.model.UserModel;
import com.pesapal.sdkdemo.profile.ProfileActivity;
import com.pesapal.sdkdemo.utils.PrefManager;
import com.pesapal.sdkdemo.utils.PrefUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {1, 7, 0},
        k = 1,
        d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\nH\u0016J\b\u0010\u001b\u001a\u00020\fH\u0002J\u0010\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\fH\u0002J\b\u0010\u001e\u001a\u00020\u0017H\u0002J\u0010\u0010\u001f\u001a\u00020\u00172\u0006\u0010 \u001a\u00020!H\u0002J\u0010\u0010\"\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\fH\u0002J\u0010\u0010#\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\fH\u0002J\b\u0010$\u001a\u00020\u0017H\u0002J\b\u0010%\u001a\u00020\u0017H\u0002J\b\u0010&\u001a\u00020\u0017H\u0002J\b\u0010'\u001a\u00020\u0017H\u0002J\b\u0010(\u001a\u00020\u0017H\u0002J\"\u0010)\u001a\u00020\u00172\u0006\u0010*\u001a\u00020\u00052\u0006\u0010+\u001a\u00020\u00052\b\u0010,\u001a\u0004\u0018\u00010-H\u0014J\u0012\u0010.\u001a\u00020\u00172\b\u0010/\u001a\u0004\u0018\u000100H\u0014J\b\u00101\u001a\u00020\u0017H\u0014J\u0010\u00102\u001a\u00020\u00172\u0006\u00103\u001a\u00020\fH\u0002J\u0010\u00104\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\fH\u0002J\b\u00105\u001a\u00020\u0017H\u0002J\b\u00106\u001a\u00020\u0017H\u0002J\b\u00107\u001a\u000208H\u0002J\b\u00109\u001a\u00020\u0017H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n \u0013*\u0004\u0018\u00010\u00120\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082.¢\u0006\u0002\n\u0000¨\u0006:"},
        d2 = {"Lcom/pesapal/sdkdemo/MainActivityJava;", "Landroidx/appcompat/app/AppCompatActivity;", "Lcom/pesapal/sdkdemo/adapter/DemoCartAdapter$clickedListener;", "()V", "PAYMENT_REQUEST", "", "binding", "Lcom/pesapal/sdkdemo/databinding/ActivityMainBinding;", "catalogueModelList", "", "Lcom/pesapal/sdkdemo/model/CatalogueModel$ProductsBean;", "currency", "", "demoCartAdapter", "Lcom/pesapal/sdkdemo/adapter/DemoCartAdapter;", "itemModelList", "orderId", "total", "Ljava/math/BigDecimal;", "kotlin.jvm.PlatformType", "userModel", "Lcom/pesapal/sdkdemo/model/UserModel;", "Clicked", "", "isAdd", "", "catalogueModel", "createTransactionID", "handleCancelledTxn", "message", "handleClicks", "handleCompletedTxn", "transactionStatusResponse", "Lcom/pesapal/sdk/model/txn_status/TransactionStatusResponse;", "handleDefaultError", "handleFailedTxn", "hardCodedInfo", "initData", "initPayment", "initRecyclerData", "initSdk", "onActivityResult", "requestCode", "resultCode", "data", "Landroid/content/Intent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "setImage", "photoUrl", "showMessage", "startPayment", "startProfile", "translateCountryToEnum", "Lcom/pesapal/sdk/utils/PESAPALAPI3SDK$COUNTRIES_ENUM;", "updateBasketList", "app_debug"}
)
public final class MainActivityJava extends AppCompatActivity implements DemoCartAdapter.clickedListener {
    private ActivityMainBinding binding;
    private String currency = "";
    private BigDecimal total;
    private DemoCartAdapter demoCartAdapter;
    private List catalogueModelList;
    private List itemModelList;
    private String orderId;
    private UserModel userModel;
    private int PAYMENT_REQUEST;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding var10001 = ActivityMainBinding.inflate(this.getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(var10001, "ActivityMainBinding.inflate(layoutInflater)");
        this.binding = var10001;
        var10001 = this.binding;
        if (var10001 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        this.setContentView((View)var10001.getRoot());
    }

    private final void initSdk() {
        if (Intrinsics.areEqual(PrefManager.getConsumerKey(), "")) {
            PrefUtil.INSTANCE.setData(0);
        }

        Context var10000 = (Context)this;
        String var10001 = PrefManager.getConsumerKey();
        String var10002 = PrefManager.getConsumerSecret();
        String var10003 = PrefManager.getAccount();
        String var10004 = PrefManager.getCallBackUrl();
        Boolean var10006 = PrefManager.getIsProduction();
        Intrinsics.checkNotNullExpressionValue(var10006, "PrefManager.getIsProduction()");
        PESAPALAPI3SDK.init(var10000, var10001, var10002, var10003, var10004, "https://test.dev", var10006);
    }

    private final void initData() {
        this.initRecyclerData();
        this.handleClicks();
    }

    private final void initRecyclerData() {
        this.catalogueModelList = (List)(new ArrayList());
        this.itemModelList = (List)(new ArrayList());
        this.orderId = this.createTransactionID();
        List var10000 = this.catalogueModelList;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("catalogueModelList");
        }

        CatalogueModel.ProductsBean[] var10001 = new CatalogueModel.ProductsBean[3];
        BigDecimal var10008 = (new BigDecimal(1)).setScale(2);
        Intrinsics.checkNotNullExpressionValue(var10008, "BigDecimal(1).setScale(2)");
        var10001[0] = new CatalogueModel.ProductsBean("Chips", 700000, var10008);
        var10008 = (new BigDecimal(5)).setScale(2);
        Intrinsics.checkNotNullExpressionValue(var10008, "BigDecimal(5).setScale(2)");
        var10001[1] = new CatalogueModel.ProductsBean("Burgers", 700013, var10008);
        var10008 = (new BigDecimal(500)).setScale(2);
        Intrinsics.checkNotNullExpressionValue(var10008, "BigDecimal(500).setScale(2)");
        var10001[2] = new CatalogueModel.ProductsBean("Milkshakes", 700013, var10008);
        var10000.addAll((Collection)CollectionsKt.listOf(var10001));
        this.demoCartAdapter = new DemoCartAdapter((DemoCartAdapter.clickedListener)this);
        ActivityMainBinding var1 = this.binding;
        if (var1 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        RecyclerView var2 = var1.rv;
        Intrinsics.checkNotNullExpressionValue(var2, "binding.rv");
        var2.setLayoutManager((RecyclerView.LayoutManager)(new LinearLayoutManager((Context)this)));
        var1 = this.binding;
        if (var1 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        var2 = var1.rv;
        Intrinsics.checkNotNullExpressionValue(var2, "binding.rv");
        DemoCartAdapter var3 = this.demoCartAdapter;
        if (var3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("demoCartAdapter");
        }

        var2.setAdapter((RecyclerView.Adapter)var3);
        DemoCartAdapter var4 = this.demoCartAdapter;
        if (var4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("demoCartAdapter");
        }

        List var5 = this.catalogueModelList;
        if (var5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("catalogueModelList");
        }

        var4.setData(var5);
    }

    private final void handleClicks() {
        ActivityMainBinding var10000 = this.binding;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        var10000.btnCheckOut.setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                MainActivityJava.this.startPayment();
            }
        }));
        var10000 = this.binding;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        var10000.civProfile.setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                MainActivityJava.this.startProfile();
            }
        }));
    }

    private final void startProfile() {
        this.startActivity(new Intent((Context)this, ProfileActivity.class));
    }

    private final String createTransactionID() {
        String var10000 = UUID.randomUUID().toString();
        Intrinsics.checkNotNullExpressionValue(var10000, "UUID.randomUUID().toString()");
        String var1 = var10000;
        var10000 = var1.toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String).toUpperCase(Locale.ROOT)");
        var1 = var10000;
        byte var2 = 0;
        byte var3 = 8;
        var10000 = var1.substring(var2, var3);
        Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String…ing(startIndex, endIndex)");
        return var10000;
    }

    private final void updateBasketList() {
        this.total = BigDecimal.ZERO;
        List var10000 = this.itemModelList;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("itemModelList");
        }

        BigDecimal var10001;
        for(Iterator var2 = var10000.iterator(); var2.hasNext(); this.total = var10001) {
            CatalogueModel.ProductsBean catelog = (CatalogueModel.ProductsBean)var2.next();
            BigDecimal var3 = this.total;
            BigDecimal var4 = catelog.getPrice();
            var10001 = var3.add(var4);
            Intrinsics.checkNotNullExpressionValue(var10001, "this.add(other)");
        }

        ActivityMainBinding var5 = this.binding;
        if (var5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        TextView var6 = var5.totalPrice;
        Intrinsics.checkNotNullExpressionValue(var6, "binding.totalPrice");
        var6.setText((CharSequence)(this.currency + ' ' + this.total.setScale(2)));
        var5 = this.binding;
        if (var5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        var6 = var5.tvOrderId;
        Intrinsics.checkNotNullExpressionValue(var6, "binding.tvOrderId");
        var6.setText((CharSequence)this.orderId);
    }

    private final void showMessage(String message) {
        Log.e(" error ", " message " + message);
        Toast.makeText((Context)this, (CharSequence)message, Toast.LENGTH_LONG).show();
    }

    protected void onResume() {
        super.onResume();
        String var10001 = PrefManager.getCurrency();
        Intrinsics.checkNotNullExpressionValue(var10001, "PrefManager.getCurrency()");
        this.currency = var10001;
        this.initData();
        this.initSdk();
        this.updateBasketList();
    }

    private final void setImage(String photoUrl) {
        RequestCreator var10000 = Picasso.get().load(photoUrl);
        ActivityMainBinding var10001 = this.binding;
        if (var10001 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        var10000.into((ImageView)var10001.civProfile);
    }

    private final void startPayment() {
        this.hardCodedInfo();
    }

    private final void hardCodedInfo() {
        String displayName = "";
        String firstName = PrefManager.getString("PREF_FIRST_NAME", "");
        String lastName = PrefManager.getString("PREF_LAST_NAME", "");
        String email = PrefManager.getString("PREF_EMAIL", "");
        String phone = PrefManager.getString("PREF_PHONE", "");
        String photoUrl = null;
        String time = null;
        this.userModel = new UserModel(displayName, firstName, lastName, email, (String)photoUrl, (String)time, phone);
        this.initPayment();
    }

    private final void initPayment() {
        String line = "a";
        String countryCode = "b";
        String line2 = "c";
        String emailAddress = "d";
        String city = "e";
        String lastName = "d";
        String phoneNumber = "703318241";
        String state = "d";
        String middleName = "d";
        String postalCode = "00111";
        String firstName = "dd";
        String zipCode = "00100";
        CustomerData customerData = new CustomerData(line, countryCode, line2, emailAddress, city, lastName, phoneNumber, state, middleName, postalCode, firstName, zipCode);
        Intent myIntent = new Intent((Context)this, PesapalSdkActivity.class);
        myIntent.putExtra(PESAPALAPI3SDK.AMOUNT, this.total.toString());
        myIntent.putExtra(PESAPALAPI3SDK.ORDER_ID, this.orderId);
        myIntent.putExtra(PESAPALAPI3SDK.CURRENCY, this.currency);
        myIntent.putExtra(PESAPALAPI3SDK.COUNTRY, (Serializable)this.translateCountryToEnum());
        myIntent.putExtra(PESAPALAPI3SDK.USER_DATA, (Serializable)customerData);
        this.startActivityForResult(myIntent, this.PAYMENT_REQUEST);
    }

    private final PESAPALAPI3SDK.COUNTRIES_ENUM translateCountryToEnum() {
        String var10000 = PrefManager.getCountry();
        PESAPALAPI3SDK.COUNTRIES_ENUM var2;
        if (var10000 != null) {
            switch ("var1") {
                case "Uganda":
                    var2 = COUNTRIES_ENUM.COUNTRY_UG;
                    return var2;
                case "Tanzania":
                    var2 = COUNTRIES_ENUM.COUNTRY_TZ;
                    return var2;
            }
        }

        var2 = COUNTRIES_ENUM.COUNTRY_KE;
        return var2;
    }

    public void Clicked(boolean isAdd, @NotNull CatalogueModel.ProductsBean catalogueModel) {
        Intrinsics.checkNotNullParameter(catalogueModel, "catalogueModel");
        List var10000;
        if (isAdd) {
            var10000 = this.itemModelList;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("itemModelList");
            }

            var10000.add(catalogueModel);
            this.updateBasketList();
        } else {
            var10000 = this.itemModelList;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("itemModelList");
            }

            var10000.remove(catalogueModel);
            this.updateBasketList();
        }

    }

//    protected void onActivityResult(int var1, int var2, @Nullable Intent var3) {
//        // $FF: Couldn't be decompiled
//    }

    private final void handleCompletedTxn(TransactionStatusResponse transactionStatusResponse) {
        List var10000 = this.itemModelList;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("itemModelList");
        }

        var10000.clear();
        DemoCartAdapter var2 = this.demoCartAdapter;
        if (var2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("demoCartAdapter");
        }

        var2.notifyDataSetChanged();
        String var10001;
        if (transactionStatusResponse.getDescription() != null) {
            var10001 = transactionStatusResponse.getDescription();
            Intrinsics.checkNotNull(var10001);
            this.showMessage(var10001);
        } else {
            var10001 = transactionStatusResponse.getMessage();
            Intrinsics.checkNotNull(var10001);
            this.showMessage(var10001);
        }

    }

    private final void handleFailedTxn(String message) {
        this.showMessage(message);
    }

    private final void handleCancelledTxn(String message) {
        this.showMessage(message);
    }

    private final void handleDefaultError(String message) {
        this.showMessage(message);
    }

    public MainActivityJava() {
        this.total = BigDecimal.ZERO;
        this.orderId = "";
        this.PAYMENT_REQUEST = 100001;
    }
}
