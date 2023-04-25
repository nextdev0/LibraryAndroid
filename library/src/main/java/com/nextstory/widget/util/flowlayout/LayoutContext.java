package com.nextstory.widget.util.flowlayout;

import androidx.annotation.RestrictTo;

/**
 * @author xhan (2016.4.11)
 * @see <a href="https://github.com/xiaofeng-han/AndroidLibs">원본 레포지토리</a>
 * @since 1.2
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LayoutContext {
  public FlowLayoutOptions layoutOptions;
  public int currentLineItemCount;

  public static LayoutContext clone(LayoutContext layoutContext) {
    LayoutContext resultContext = new LayoutContext();
    resultContext.currentLineItemCount = layoutContext.currentLineItemCount;
    resultContext.layoutOptions = FlowLayoutOptions.clone(layoutContext.layoutOptions);
    return resultContext;
  }

  public static LayoutContext fromLayoutOptions(FlowLayoutOptions layoutOptions) {
    LayoutContext layoutContext = new LayoutContext();
    layoutContext.layoutOptions = layoutOptions;
    return layoutContext;
  }
}
