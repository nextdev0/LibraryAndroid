package com.nextstory.widget.util.flowlayout;

import androidx.annotation.RestrictTo;

/**
 * @author xhan (2016.4.11)
 * @see <a href="https://github.com/xiaofeng-han/AndroidLibs">원본 레포지토리</a>
 * @since 1.2
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class FlowLayoutOptions {
  public static final int ITEM_PER_LINE_NO_LIMIT = 0;
  public Alignment alignment = Alignment.LEFT;
  public int itemsPerLine = ITEM_PER_LINE_NO_LIMIT;

  public static FlowLayoutOptions clone(FlowLayoutOptions layoutOptions) {
    FlowLayoutOptions result = new FlowLayoutOptions();
    result.alignment = layoutOptions.alignment;
    result.itemsPerLine = layoutOptions.itemsPerLine;
    return result;
  }
}
