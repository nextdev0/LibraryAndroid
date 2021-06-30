# intents

## email

이메일 앱 연결용 `Intent`를 생성함.

- 사용방법

```java
// 단일 이메일
Intent intent = Intents.email("앱 선택", "제목", "내용", /* 이메일 */);
startActivity(intent);

// 여러 이메일
Intent intent = Intents.email("앱 선택", "제목", "내용", /* 이메일 1 */, /* 이메일 2 ... */);
startActivity(intent);
```

## dial

전화 앱 연결용 `Intent`를 생성함.

- 사용방법

```java
// 하이폰(-) 포함
Intent intent = Intents.dial("010-1234-5678");
startActivity(intent);

// 미포함
Intent intent = Intents.dial("01012345678");
startActivity(intent);
```

## webBrowser

웹 브라우저 앱 연결용 `Intent`를 생성함.

- 사용방법

```java
Intent intent = Intents.webBrowser("https://www.test.com");
startActivity(intent);
```

## market

스토어 연결용 `Intent`를 생성함.

- 사용방법

```java
Intent intent = Intents.store(this /* Context */);
startActivity(intent);

// 또는

Intent intent = Intents.store("com.nextstory.test" /* 앱 패키지명 */);
startActivity(intent);
```

## appSettings

앱 설정 연결용 `Intent`를 생성함.

- 사용방법

```java
Intent intent = Intents.appSettings(this /* Context */);
startActivity(intent);
```

## locationSettings

위치 설정 연결용 `Intent`를 생성함.

- 사용방법

```java
Intent intent = Intents.locationSettings();
startActivity(intent);
```

## shareText

텍스트 공유 앱 `Intent`를 생성함.

- 사용방법

```java
Intent intent = Intents.shareText("앱 선택", "{공유할 내용}");
startActivity(intent);
```

## shareBitmap

비트맵 이미지 공유 앱 `Intent`를 생성함.

- 사용방법

```java
Intent intent = Intents.shareBitmap(this /* Context */, "앱 선택", /* 공유할 비트맵 */);
startActivity(intent);
```
