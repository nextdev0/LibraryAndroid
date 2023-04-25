package com.nextstory.widget.util.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.RestrictTo;

/**
 * @author troy
 * @since 1.4
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class BlurImpl implements Blur {
  private RenderScript mRenderScript;
  private ScriptIntrinsicBlur mBlurScript;
  private Allocation mBlurInput, mBlurOutput;

  @Override
  public boolean prepare(Context context, Bitmap buffer, float radius) {
    if (mRenderScript == null) {
      try {
        mRenderScript = RenderScript.create(context);
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript,
          Element.U8_4(mRenderScript));
      } catch (RSRuntimeException e) {
        release();
        return false;
      }
    }
    mBlurScript.setRadius(radius);
    mBlurInput = Allocation.createFromBitmap(
      mRenderScript,
      buffer,
      Allocation.MipmapControl.MIPMAP_NONE,
      Allocation.USAGE_SCRIPT);
    mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
    return true;
  }

  @Override
  public void release() {
    if (mBlurInput != null) {
      mBlurInput.destroy();
      mBlurInput = null;
    }
    if (mBlurOutput != null) {
      mBlurOutput.destroy();
      mBlurOutput = null;
    }
    if (mBlurScript != null) {
      mBlurScript.destroy();
      mBlurScript = null;
    }
    if (mRenderScript != null) {
      mRenderScript.destroy();
      mRenderScript = null;
    }
  }

  @Override
  public void blur(Bitmap input, Bitmap output) {
    mBlurInput.copyFrom(input);
    mBlurScript.setInput(mBlurInput);
    mBlurScript.forEach(mBlurOutput);
    mBlurOutput.copyTo(output);
  }
}
