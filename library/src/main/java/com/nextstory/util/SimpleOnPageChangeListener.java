package com.nextstory.util;

import androidx.viewpager.widget.ViewPager;

/**
 * {@link ViewPager.OnPageChangeListener} 간소화 인터페이스
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public interface SimpleOnPageChangeListener extends ViewPager.OnPageChangeListener {
  @Override
  default void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    // no-op
  }

  @Override
  default void onPageSelected(int position) {
    // no-op
  }

  @Override
  default void onPageScrollStateChanged(int state) {
    // no-op
  }
}
