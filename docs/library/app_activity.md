# AbstractBaseActivity, BaseActivity 클래스

기본 액티비티

## 1. AbstractBaseActivity

### 1.1. 설명

기본적인 기능이 포함된 액티비티

### 1.2. 사용방법

기존 `Activity`나 `AppCompatActivity` 등을 상속하여 사용하던걸 `AbstractBaseActivity`으로 변경하여 사용

```java
public class TestActivity extends AbstractBaseActivity {
}
```

## 2. BaseActivity

### 2.1. 설명

`AbstractBaseActivity`에서 데이터바인딩이 적용된 기본 액티비티

### 2.2. 사용방법

데이터바인딩 객체를 지정하여 `BaseActivity`을 상속합니다.  
`setContentView`를 호출할 필요없이 컨텐츠가 데이터바인딩 객체를 통하여 내부적으로 적용됩니다.

```java
public class TestActivity extends BaseActivity<ActivityTestBinding> {
    // getBinding() 메소드로 바인딩 객체를 쓸 수 있습니다.
}
```

## 3. 기능

### 3.1. 키보드 숨기기

#### 3.1.1. 설명

키보드가 표시되어 있을때 아무곳이나 터치 시 내리도록하는 기능

#### 3.1.2. 미리보기

![키보드 숨기기](./img/hide_keyboard.gif)

### 3.2. 로케일 관리

#### 3.2.1. 설명

앱 전반적인 로케일을 설정할 수 있습니다.

#### 3.2.2. 메소드 설명

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

### 3.3. 화면 스타일 변경

#### 3.3.1. 전체화면 지정

- 코드

```java
applyFullscreenTheme();
```

- 미리보기  
![미리보기](./img/app_fullscreen.png)

#### 3.3.2. 반투명 상태바 지정

- 코드

```java
applyTranslucentTheme();
```

- 미리보기  
![미리보기](./img/app_translucent.png)

#### 3.3.3. 투명 상태바 지정

- 코드

```java
applyTransparentTheme();
```

- 미리보기  
![미리보기](./img/app_transparent.png)

#### 3.3.4. 상태바 아이콘 색상 지정

- 코드

```java
applyLightStatusBar(true /* 상태바 밝은색 유무 */);
```

- 미리보기(순서대로 true, false)  
![미리보기](./img/app_light_statusbar.png)
![미리보기](./img/app_transparent.png)

### 3.4. 앱 테마 지정

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
