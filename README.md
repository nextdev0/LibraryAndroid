# 안드로이드 공용 라이브러리

프로젝트별 사용되는 공용 라이브러리 프로젝트입니다.

## 1. 문서

### 1.1. 어노테이션

- [AutoSharedPreferences](./docs/annotations/AutoSharedPreferences.md) : SharedPreference 코드 생성
- [ActivityIntentBuilder](./docs/annotations/ActivityIntentBuilder.md) : 액티비티 Intent 빌더 생성
- [FragmentArgumentsBuilder](./docs/annotations/FragmentArgumentsBuilder.md) : 프래그먼트 빌더 생성

### 1.2. library

- [app](./docs/library/app/app.md)
- [field](./docs/library/field.md)
- [widget](./docs/library/widget/widget.md)
- [util](./docs/library/util/util.md)
- [리소스](./docs/library/resources.md)

## 2. 사용방법

### 2.1. 저장소 구성

프로젝트 루트의 build.gradle에 아래 코드를 추가합니다.

```gradle
allprojects {
    repositories {

        // ...

        // 추가
        maven { url 'https://jitpack.io' }
        maven { url "https://oss.jfrog.org/libs-snapshot" }
        maven { url 'http://ec2-3-34-185-2.ap-northeast-2.compute.amazonaws.com:8889/troy/LibraryAndroid/-/raw/main/repository' }        
    }
}
```

### 2.2. 라이브러리 의존성 구성

```gradle
dependencies {

    // ...

    // 추가
    implementation 'com.nextstory:library:1.6.1'
    annotationProcessor 'com.nextstory:annotations:1.4.0'
}
```

## 3. 라이브러리 배포 가이드

1. 툴 우측 메뉴의 Gradle을 열어서 왼쪽 상단 `Execute Gradle Task` 실행.
2. `gradle publish`를 실행합니다.
3. 이제 `repository` 디렉토리에 배포용 바이너리가 생성됩니다.
