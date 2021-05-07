# ActivityIntentBuilder

액티비티를 시작할 때 전달할 intent extra를 빌더패턴 형태로 구성해주는 어노테이션

## 요구사항

- 라이브러리 1.2.10 버전 이상부터 사용가능.
- 어노테이션 1.0.0 버전 이상부터 사용가능.

## 사용방법

1. 아래와 같이 `@ActivityIntentBuilder`와 `@ActivityIntent` 어노테이션을 지정합니다.

    ```java

    @ActivityIntentBuilder
    public class TestActivity extends AppCompatActivity {
        @ActivityIntent
        int someValue = 4;

        // @ActivityIntent
        // {접근제한자} {기본형 또는 직렬화 가능한 타입} someValue2 = {기본값};
        //
        // 접근 제한자 : public 또는 default로 지정해야합니다.
        // 기본형 또는 직렬화 가능한 타입 : 기본 타입이나 Serializable을 구현한 모델 클래스만 가능합니다.
        // 기본값 : 빌더를 구성할 때 값을 정하지 않을 경우 기본으로 입력될 값.
    }

    ```

2. 스튜디오 메뉴에서 `Build > Rebuild`를 실행하면 아래와 같은 사례로 코드가 생성됩니다.

    ```java
    public final class TestActivityIntentBuilder {
        private final Intent intent;

        public TestActivityIntentBuilder(Context context) {
            this.intent = new Intent(context, TestActivity.class);
        }

        public static void inject(IntentBuilderTestActivity activity) {
            Intent intent = activity.getIntent();
            if (intent.hasExtra("extra_someValue")) {
                activity.someValue = (int) intent.getSerializableExtra("extra_someValue");
            }
            if (intent.hasExtra("extra_someValue2")) {
                activity.someValue2 = (int) intent.getSerializableExtra("extra_someValue2");
            }
        }

        public TestActivityIntentBuilder setSomeValue(int someValue) {
            intent.putExtra("extra_someValue", someValue);
            return this;
        }

        public TestActivityIntentBuilder setSomeValue2(int someValue2) {
            intent.putExtra("extra_someValue2", someValue2);
            return this;
        }

        public Intent create() {
            return this.intent;
        }

        public Intent build() {
            return this.intent;
        }
    }
    ```

3. 생성된 클래스로 액티비티를 실행합니다.

    ```java
    startActivity(new TestActivityIntentBuilder()
            .setSomeValue(2)
            .build());

    // 또는

    Intent intent = new TestActivityIntentBuilder()
            .setSomeValue(2)
            .build();
    startActivity(intent);
    ```

4. 이제 시작했을때 자동으로 `@ActivityIntent`를 지정한 필드에 주입이 됩니다. 전달된 값을 이용하면 됩니다.

    ```java
    @ActivityIntentBuilder
    public class TestActivity extends AppCompatActivity {
        @ActivityIntent
        int someValue = 4;

        @ActivityIntent
        int someValue2 = 2;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // super.onCreate(savedInstanceState); 이후로
            // 해당 지점에서 내부적으로 자동 주입됩니다.

            // "someValue=2"
            Log.d("test", "someValue=" + someValue);

            // "someValue2=2"
            Log.d("test", "someValue2=" + someValue2);
        }
    }
    ```
