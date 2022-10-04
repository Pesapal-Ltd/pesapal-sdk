package com.pesapal.paygateway.activities.payment.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class TextDrawable
        extends Drawable
        implements TextWatcher
{
  private WeakReference<TextView> a;
  private String b;
  private final Paint c;
  private final Rect d;
  private boolean e = false;
  private final float f = 0.0F;
  private boolean g = false;
  private final boolean h = false;

  public TextDrawable(Paint paramPaint, String paramString)
  {
    this.b = paramString;
    this.c = new Paint(paramPaint);
    this.d = new Rect();
    a();
  }

  public TextDrawable(TextView paramTextView, String paramString)
  {
    this(paramTextView, paramString, false, false);
  }

  public TextDrawable(TextView paramTextView, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramTextView.getPaint(), paramString);
    this.a = new WeakReference(paramTextView);
    if ((paramBoolean1) || (paramBoolean2))
    {
      if (paramBoolean1) {
        paramTextView.addTextChangedListener(this);
      }
      this.e = paramBoolean2;
    }
  }

  private void a()
  {
    Rect localRect = getBounds();
    this.c.getTextBounds("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 0, 1, this.d);
    float f1 = this.c.measureText(this.b);
    localRect.top = this.d.top;
    localRect.bottom = this.d.bottom;
    localRect.right = ((int)f1);
    localRect.left = 0;
    setBounds(localRect);
  }

  private void b()
  {
    float f1 = this.a.get().getWidth() / this.c.measureText(this.b);
    this.c.setTextSize(f1 * this.c.getTextSize());
    this.g = false;
    a();
  }

  public void a(String paramString)
  {
    this.b = paramString;
    if ((this.h) && (!this.g)) {
      b();
    }
    for (;;)
    {
      invalidateSelf();
      return;
      //a();
    }
  }

  public void afterTextChanged(Editable paramEditable)
  {
    a(paramEditable.toString());
  }

  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}

  public void draw(Canvas paramCanvas)
  {
    if ((this.e) && (this.a.get() != null))
    {
      TextPaint localTextPaint = this.a.get().getPaint();
      paramCanvas.drawText(this.b, 0.0F, getBounds().height(), localTextPaint);
      return;
    }
    if (this.g) {
      b();
    }
    paramCanvas.drawText(this.b, 0.0F, getBounds().height(), this.c);
  }

  public int getOpacity()
  {
    int i = this.c.getAlpha();
    if (i == 0) {
      return PixelFormat.TRANSPARENT;
    }
    if (i == 255) {
      return PixelFormat.OPAQUE;// -1;
    }
    return PixelFormat.TRANSLUCENT;//-3;
  }

  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}

  public void setAlpha(int paramInt)
  {
    this.c.setAlpha(paramInt);
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.c.setColorFilter(paramColorFilter);
  }
}

