# 안드로이드 공용 라이브러리

프로젝트별 사용되는 공용 라이브러리 프로젝트입니다.

## 사용방법

1. 프로젝트 루트의 build.gradle에 아래 코드를 추가합니다. 

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

2. 앱 프로젝트의 build.gradle에 아래 코드를 추가합니다.

    ```gradle
    dependencies {

        // ...

        // 추가
        implementation 'com.nextstory:library:1.1.0'

        // 추가, 아래 의존성은 내부적으로 사용되고 있습니다.
        implementation 'androidx.appcompat:appcompat:1.2.0'
        implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
        implementation 'com.google.android.material:material:1.3.0'
        implementation 'com.google.code.gson:gson:2.8.6'
        implementation 'com.wdullaer:materialdatetimepicker:4.2.3'
        implementation 'com.github.esafirm.android-image-picker:imagepicker:2.4.1'
        implementation 'com.theartofdev.edmodo:android-image-cropper:2.8.0'
        implementation 'io.reactivex.rxjava3:rxjava:3.0.4'
        implementation 'io.reactivex.rxjava3:rxandroid:3.0.0'
        implementation 'net.danlew:android.joda:2.10.9'
    }
    ```
