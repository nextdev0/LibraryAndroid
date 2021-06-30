# BaseApplication 클래스

기본 애플리케이션

## 1. 사용방법

`BaseApplication`을 상속합니다.

```java
public class TestApplication extends BaseApplication {
}
```

## 2. 기능

### 2.1. 로케일 관리

#### 2.1.1. 설명

앱 전반적인 로케일을 설정할 수 있습니다.

#### 2.1.2. 메소드 설명

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

### 2.2. 앱 테마 지정

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

## 3. 주의사항

`getResources()` 메소드로 받은 리소스 객체는 로케일 설정이 적용되지 않습니다.  
`getLocaleResources()` 메소드를 이용해야합니다.
