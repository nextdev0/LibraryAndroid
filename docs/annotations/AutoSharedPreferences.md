# AutoSharedPreferences

SharedPreferences 구현 또는 이용했을 때의 보일러 플레이트를 줄이기위한 어노테이션 프로세서.

## 명세서 작성 요구사항

- 라이브러리 1.5.0 버전 이상부터 사용가능.
- 어노테이션 1.3.0 버전 이상부터 사용가능.
- 명세서를 인터페이스로 작성해야함.
- `@AutoSharedPreferences`로 설정 이름을 지정함.
- `@AutoSharedPreferencesItem`로 각 항목을 지정함. (해당 어노테이션이 필드에 없으면 그 필드만 구현되지 않습니다.)

## 참고사항

- `@AutoSharedPreferences`, `@AutoSharedPreferencesItem`의 값이 없거나 비어있을 경우에는 필드 또는 클래스 이름으로 자동으로 지정됩니다.

## 사용 방법

내부적으로 값을 저장 또는 로딩을 위해서 데이터의 직렬화, 역직렬화를 합니다.  
아래와 같이 앱 시작점에 반드시 `AutoSharedPreferenceUtils.registerConverter(/* Converter 구현체 */);` 메소드를 쓰도록 합니다.

```java
public final class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AutoSharedPreferenceUtils.registerConverter(new AutoSharedPreferenceUtils.Converter() {
            final Gson gson = new Gson();

            @Override
            public String serialize(Object object) {
                return gson.toJson(object);
            }

            @Override
            public <T> T deserialize(String source, Class<T> klass) {
                return gson.fromJson(source, klass);
            }
        });
    }
}
```

### 명세서 작성 및 이용 예제

1. 명세서를 먼저 아래 코드와 같이 작성합니다.

    ```java
    @AutoSharedPreferences("test_settings")
    interface Test {
        @AutoSharedPreferencesItem("value_1")
        int value1 = 3;

        @AutoSharedPreferencesItem("value_2")
        int value2 = 0;

        // @AutoSharedPreferencesItem(저장될 키 이름)
        // int 변수명 = 기본값;
    }
    ```

2. 스튜디오 메뉴에서 `Build > Rebuild`를 실행하면 명세서에 따라 아래와 같은 사례로 코드가 생성됩니다.  
   생성된 클래스의 이름은 지정한 인터페이스의 이름에서 `SharedPreferences`가 붙인 이름입니다.

    ```java
    public final class TestSharedPreferences {
        private final SharedPreferences sharedPreferences;

        public TestSharedPreferences(Context context) {
            this.sharedPreferences = context.getSharedPreferences("test_settings", Context.MODE_PRIVATE);
        }

        public int getValue1() {
            AutoSharedPreferenceUtils.assertConverterIsNotNull();
            String source = this.sharedPreferences.getString("value_1", "");
            if (source.trim().isEmpty()) {
                return Test.value1;
            }
            try {
                return AutoSharedPreferenceUtils.deserialize(source, int.class);
            }
            catch (Throwable ignore) {
                return Test.value1;
            }
        }

        public void setValue1(int value1) {
            AutoSharedPreferenceUtils.assertConverterIsNotNull();
            String source = AutoSharedPreferenceUtils.serialize(value1);
            this.sharedPreferences
                .edit()
                .putString("value_1", source)
                .apply();
        }

        public int getValue2() {
            AutoSharedPreferenceUtils.assertConverterIsNotNull();
            String source = this.sharedPreferences.getString("value_2", "");
            if (source.trim().isEmpty()) {
                return Test.value2;
            }
            try {
                return AutoSharedPreferenceUtils.deserialize(source, int.class);
            }
            catch (Throwable ignore) {
                return Test.value2;
            }
        }

        public void setValue2(int value2) {
            AutoSharedPreferenceUtils.assertConverterIsNotNull();
            String source = AutoSharedPreferenceUtils.serialize(value2);
            this.sharedPreferences
                .edit()
                .putString("value_2", source)
                .apply();
        }
    }
    ```

3. 마지막으로 생성된 코드를 아래처럼 이용하면 됩니다.

    ```java
    TestSharedPreferences testSettings = new TestSharedPreferences(this /* Context */);
    int value = testSettings.getValue2();
    testSettings.setValue2(value + 1);
    ```
