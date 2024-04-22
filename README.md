# 안드로이드 공용 라이브러리

프로젝트별 사용되는 공용 라이브러리 프로젝트입니다.

## 사용방법

### maven 저장소 구성

프로젝트 루트의 `settings.gradle`에 아래 코드를 추가합니다.

```gradle
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()

    // ...

    // 추가
    maven { url 'https://github.com/nextdev0/LibraryAndroid/raw/main/repository' }
  }
}
```

### 라이브러리 의존성 추가

```gradle
dependencies {
  // ...

  // 추가
  implementation 'com.nextstory:library:3.0.0'
}
```

## 배포 방법

1. 툴 우측 메뉴의 Gradle을 열어서 왼쪽 상단 `Execute Gradle Task` 실행.
2. `gradle publish`를 실행합니다.
3. 이제 `repository` 디렉토리에 배포용 바이너리가 생성됩니다.
