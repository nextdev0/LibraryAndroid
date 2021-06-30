# foreground

구버전 안드로이드에서 `foreground` 속성을 지원하기 위한 위젯

## 지원 위젯 목록

### 목록

- ForegroundTextView : 텍스트뷰
- ForegroundImageView : 이미지뷰
- ForegroundLinearLayout : 리니어 레이아웃
- ForegroundRelativeLayout : 상대 레이아웃
- ForegroundConstraintLayout : Constraint 레이아웃

### 기타

`FrameLayout`은 안드로이드 버전에 상관없이  
`android:foreground`이 지원되기 때문에 `ForegroundFrameLayout`은 따로 없습니다.

## 사용방법

`android:foreground` 대신에 `app:foreground`로 지정하여 사용합니다.

```xml
<!-- 구버전 안드로이드에서는 foreground가 표시되지 않습니다. -->
<TextView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clickable="true"
    android:focusable="true"
    android:background="@color/white"
    android:foreground="@drawable/bg_selector_dark" />

<!-- 
    ForegroundTextView를 사용하여
    app:foreground로 변경하면 재대로 표시가 됩니다.
-->
<com.nextstory.widget.ForegroundTextView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clickable="true"
    android:focusable="true"
    android:background="@color/white"
    app:foreground="@drawable/bg_selector_dark" />
```
