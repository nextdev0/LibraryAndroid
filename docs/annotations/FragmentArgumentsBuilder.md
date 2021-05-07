# FragmentArgumentsBuilder

프래그먼트를 시작할 때 전달할 arguments bundle를 빌더패턴 형태로 구성해주는 어노테이션

## 요구사항

- 라이브러리 1.4.0 버전 이상부터 사용가능.
- 어노테이션 1.2.0 버전 이상부터 사용가능.

## 사용방법

1. 아래와 같이 프래그먼트에 `@FragmentArgumentsBuilder`와 `@FragmentArgument` 어노테이션을 지정합니다.

    ```java

    @FragmentArgumentsBuilder
    public class TestFragment extends Fragment {
        @FragmentArgument
        int someValue = 4;

        // @FragmentArgument
        // {접근제한자} {기본형 또는 직렬화 가능한 타입} someValue2 = {기본값};
        //
        // 접근 제한자 : public 또는 default로 지정해야합니다.
        // 기본형 또는 직렬화 가능한 타입 : 기본 타입이나 Serializable을 구현한 모델 클래스만 가능합니다.
        // 기본값 : 빌더를 구성할 때 값을 정하지 않을 경우 기본으로 입력될 값.
    }

    ```

2. 스튜디오 메뉴에서 `Build > Rebuild`를 실행하면 아래와 같은 사례로 코드가 생성됩니다.

    ```java
    public final class TestFragmentArgumentsBuilder {
        private final TestFragment fragment;

        private final Bundle arguments;

        public TestFragmentArgumentsBuilder(Context context) {
            this.fragment = new TestFragment();
            this.arguments = new Bundle();
        }

        public static void inject(TestFragment fragment) {
            Bundle arguments = fragment.getArguments();
            if (arguments != null) {
                if (intent.hasExtra("argument_someValue")) {
                    fragment.someValue = (int) arguments.getSerializable("argument_someValue");
                }
                if (intent.hasExtra("argument_someValue2")) {
                    fragment.someValue2 = (int) arguments.getSerializable("argument_someValue2");
                }
            }
        }

        public TestFragmentArgumentsBuilder setSomeValue(int someValue) {
            arguments.putSerializable("argument_someValue", someValue);
            return this;
        }

        public TestFragmentArgumentsBuilder setSomeValue2(int someValue2) {
            arguments.putSerializable("argument_someValue2", someValue2);
            return this;
        }

        public Tes1Fragment create() {
            this.fragment.setArguments(this.arguments);
            return this.fragment;
        }

        public TestFragment build() {
            this.fragment.setArguments(this.arguments);
            return this.fragment;
        }
    }
    ```

3. 생성된 클래스로 프래그먼트를 실행합니다.

    ```java
    fragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, new Test1FragmentArgumentsBuilder()
                    .setSomeValue(2)
                    .build())
            .addToBackStack(null)
            .commit();
    ```

4. 마지막으로 시작했을때 자동으로 `@FragmentArgument`를 지정한 필드에 주입이 됩니다. 전달된 값을 이용하면 됩니다.

    ```java
    @FragmentArgumentsBuilder
    public final class Test1Fragment extends Fragment {
        @FragmentArgument
        int someValue = 4;

        @FragmentArgument
        int someValue2 = 2;

        @Override
        public void onAttachFragment(@NonNull Fragment childFragment) {
            super.onAttachFragment(childFragment);
            // super.onAttachFragment(childFragment); 이후로
            // 해당 지점에서 내부적으로 자동 주입됩니다.
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // "someValue=2"
            Log.d("test", "someValue=" + someValue);

            // "someValue2=2"
            Log.d("test", "someValue2=" + someValue2);
        }
    }
    ```
