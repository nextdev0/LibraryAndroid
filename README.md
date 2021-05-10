# 안드로이드 공용 라이브러리

프로젝트별 사용되는 공용 라이브러리 프로젝트입니다.

## 문서

### 어노테이션

- [AutoSharedPreferences](./docs/annotations/AutoSharedPreferences.md) : SharedPreference 코드 생성
- [ActivityIntentBuilder](./docs/annotations/ActivityIntentBuilder.md) : 액티비티 Intent 빌더 생성
- [FragmentArgumentsBuilder](./docs/annotations/FragmentArgumentsBuilder.md) : 프래그먼트 빌더 생성

### 메인

```text
내용 준비중
```

### libgdx

```text
내용 준비중
```

## 라이브러리 사용방법

### 저장소 구성

프로젝트 루트의 build.gradle에 아래 코드를 추가합니다.

```gradle
allprojects {
    repositories {

        // ...

        // 추가
        maven { url 'https://jitpack.io' }
        maven { url "https://oss.jfrog.org/libs-snapshot" }
        maven {
            url 'https://raw.githubusercontent.com/nextdev0/LibraryAndroid/main/repository'
            credentials(HttpHeaderCredentials) {
                name 'Authorization'
                value 'Bearer 5e1a054e517d2ea3c6055f7eaf92e8e66323abdc'
            }
            authentication {
                header(HttpHeaderAuthentication)
            }
        }
    }
}
```

### 라이브러리 의존성 구성

#### 메인 라이브러리

```gradle
dependencies {

    // ...

    // 추가
    implementation 'com.nextstory:library:1.5.1'

    // 추가, 아래 의존성은 내부적으로 사용되고 있습니다.
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'com.google.android.material:material:1.3.0'
    implementation 'com.google.code.gson:gson:2.8.6'
    implementation 'com.wdullaer:materialdatetimepicker:4.2.3'
    implementation 'com.github.esafirm.android-image-picker:imagepicker:2.4.5'
    implementation 'com.theartofdev.edmodo:android-image-cropper:2.8.0'
    implementation 'io.reactivex.rxjava3:rxjava:3.0.4'
    implementation 'io.reactivex.rxjava3:rxandroid:3.0.0'
    implementation 'net.danlew:android.joda:2.10.9'
}
```

#### 어노테이션 라이브러리

메인 라이브러리와 함께 `annotationProcessor 'com.nextstory:annotations'`를 추가합니다.

```gradle
dependencies {

    // ...

    // 추가
    implementation 'com.nextstory:library:1.5.1'
    annotationProcessor 'com.nextstory:annotations:1.3.0'

    // 추가, 아래 의존성은 내부적으로 사용되고 있습니다.
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'com.google.android.material:material:1.3.0'
    implementation 'com.google.code.gson:gson:2.8.6'
    implementation 'com.wdullaer:materialdatetimepicker:4.2.3'
    implementation 'com.github.esafirm.android-image-picker:imagepicker:2.4.5'
    implementation 'com.theartofdev.edmodo:android-image-cropper:2.8.0'
    implementation 'io.reactivex.rxjava3:rxjava:3.0.4'
    implementation 'io.reactivex.rxjava3:rxandroid:3.0.0'
    implementation 'net.danlew:android.joda:2.10.9'
}
```

#### libgdx 라이브러리

메인 라이브러리와 함께 `implementation 'com.nextstory:libgdx'`를 추가합니다.

```gradle
dependencies {

    // ...

    // 추가
    implementation 'com.nextstory:library:1.5.1'
    implementation 'com.nextstory:libgdx:1.0.0'

    // 추가, 아래 의존성은 내부적으로 사용되고 있습니다.
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'com.google.android.material:material:1.3.0'
    implementation 'com.google.code.gson:gson:2.8.6'
    implementation 'com.wdullaer:materialdatetimepicker:4.2.3'
    implementation 'com.github.esafirm.android-image-picker:imagepicker:2.4.5'
    implementation 'com.theartofdev.edmodo:android-image-cropper:2.8.0'
    implementation 'io.reactivex.rxjava3:rxjava:3.0.4'
    implementation 'io.reactivex.rxjava3:rxandroid:3.0.0'
    implementation 'net.danlew:android.joda:2.10.9'
}
```

## 라이브러리 배포 가이드

- 메인 라이브러리

1. 툴 우측 메뉴의 Gradle을 열어서 `library > Tasks > other > assembleRelease`를 실행.
2. 1번 과정을 한 번 더 실행.
3. 빌드가 완료될 경우 `library > Tasks > publishing > publish`를 실행하면 완료.

- 어노테이션 라이브러리

1. 툴 우측 메뉴의 Gradle을 열어서 `annotationProcessor > Tasks > build > jar`를 실행.
2. 빌드 후 다음으로 `annotationProcessor > Tasks > other > sourceJar`를 실행.
3. 1, 2번 과정을 한 번 더 실행.
4. 빌드가 완료될 경우 `annotationProcessor > Tasks > publishing > publish`를 실행하면 완료.

- libgdx 라이브러리

1. 툴 우측 메뉴의 Gradle을 열어서 `libgdx > Tasks > build > jar`를 실행.
2. 빌드 후 다음으로 `libgdx > Tasks > other > sourceJar`를 실행.
3. 1, 2번 과정을 한 번 더 실행.
4. 빌드가 완료될 경우 `libgdx > Tasks > publishing > publish`를 실행하면 완료.
