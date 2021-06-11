# 기본 다이얼로그

## 1. BaseDialog

### 1.1. 설명

기본 다이얼로그

### 1.2. 사용방법

데이터바인딩 객체를 지정하여 `BaseDialog`을 상속합니다.  
`setContentView`를 호출할 필요없이 컨텐츠가 데이터바인딩 객체를 통하여 내부적으로 적용됩니다.

- 클래스 작성

```java
public class TestDialog extends BaseDialog<DialogTestBinding> {
    // getBinding() 메소드로 바인딩 객체를 쓸 수 있습니다.
}
```

- 사용 예시

```java
Dialog dialog = new TestDialog(this /* Context */);
dialog.show();
```

## 2. BaseDialogFragment

### 2.1. 설명

기본 다이얼로그 프래그먼트

### 2.2. 사용방법

데이터바인딩 객체를 지정하여 `BaseDialogFragment`을 상속합니다.  
`setContentView`를 호출할 필요없이 컨텐츠가 데이터바인딩 객체를 통하여 내부적으로 적용됩니다.

- 클래스 작성

```java
public class TestDialog extends BaseDialogFragment<DialogTestBinding> {
    // getBinding() 메소드로 바인딩 객체를 쓸 수 있습니다.
}
```

- 사용 예시

```java
DialogFragment dialog = new BaseDialogFragment(this /* Context */);
dialog.show(/* todo FragmentManager 또는 FragmentTransaction */);
```

## 3. BaseBottomSheetDialog

### 3.1. 설명

기본 하단 다이얼로그

### 3.2. 사용방법

데이터바인딩 객체를 지정하여 `BaseBottomSheetDialog`을 상속합니다.  
`setContentView`를 호출할 필요없이 컨텐츠가 데이터바인딩 객체를 통하여 내부적으로 적용됩니다.

- 클래스 작성

```java
public class TestDialog extends BaseBottomSheetDialog<DialogTestBinding> {
    // getBinding() 메소드로 바인딩 객체를 쓸 수 있습니다.
}
```

- 사용 예시

```java
Dialog dialog = new TestDialog(this /* Context */);
dialog.show();
```

## 4. BaseDialogBottomSheetFragment

### 4.1. 설명

기본 하단 다이얼로그 프래그먼트

### 4.2. 사용방법

데이터바인딩 객체를 지정하여 `BaseDialogBottomSheetFragment`을 상속합니다.  
`setContentView`를 호출할 필요없이 컨텐츠가 데이터바인딩 객체를 통하여 내부적으로 적용됩니다.

- 클래스 작성

```java
public class TestDialog extends BaseDialogBottomSheetFragment<DialogTestBinding> {
    // getBinding() 메소드로 바인딩 객체를 쓸 수 있습니다.
}
```

- 사용 예시

```java
DialogFragment dialog = new BaseDialogFragment(this /* Context */);
dialog.show(/* todo FragmentManager 또는 FragmentTransaction */);
```

## 5. 기능

### 5.1. 로케일 관리

#### 5.1.1. 설명

앱 전반적인 로케일을 설정할 수 있습니다.

#### 5.1.2. 메소드 설명

- 현재 설정되어 있는 로케일 반환.  
  앱 상에서 로케일을 바꾸지 않았으면 시스템에서 설정되어있는 로케일을 반환함.

```java
getLocale();
```

- 로케일 지정

```java
applyLocale(Locale.KOREAN);
```

- 앱에서 지원되는 로케일 목록 지정

```java
registerSupportedLocales(Arrays.asList(
    Locale.KOREAN,
    Locale.ENGLISH
));
```

### 5.2. 화면 스타일 변경

#### 5.2.1. 전체화면 지정

- 코드

```java
applyFullscreenTheme();
```

- 미리보기  
![미리보기](../img/app_fullscreen.png)

#### 5.2.2. 투명 상태바 지정

- 코드

```java
applyTransparentTheme();
```

- 미리보기  
![미리보기](../img/app_transparent.png)

#### 5.2.3. 상태바 아이콘 색상 지정

- 코드

```java
applyLightStatusBar(true /* 상태바 밝은색 유무 */);
```

- 미리보기(순서대로 true, false)  
![미리보기](../img/app_light_statusbar.png)
![미리보기](../img/app_transparent.png)

### 5.3. 앱 테마 지정

- 현재 테마 타입 반환

```java
int type = getApplicationTheme();
```

- 테마 변경

```java
applyApplicationTheme(ThemeType.DARK /* ThemeType 참고 */);
```

- [테마 타입](../../library/src/main/java/com/nextstory/app/theme/ThemeType.java)

```java
// 밝은 테마
ThemeType.LIGHT;

// 어두운 테마
ThemeType.DARK;

// 시스템 설정에 따름
ThemeType.SYSTEM;
```
