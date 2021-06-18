# field

## 클래스 목록

### LiveData

- [NonNullLiveData](../../library/src/main/java/com/nextstory/field/NonNullLiveData.java) : Null을 가질 수 없는 LiveData
- [ListLiveData](../../library/src/main/java/com/nextstory/field/ListLiveData.java) : 리스트 형식의 LiveData
- [SafeMutableLiveData](../../library/src/main/java/com/nextstory/field/SafeMutableLiveData.java) : 상태 복구가 가능한 MutableLiveData
- [SingleLiveEvent](../../library/src/main/java/com/nextstory/field/SingleLiveEvent.java) : 이벤트 전달용 LiveData
- [CompositeLiveData](../../library/src/main/java/com/nextstory/field/CompositeLiveData.java) : LiveData 컨테이너

### 기타

- [SafeData](../../library/src/main/java/com/nextstory/field/SafeData.java) : 상태 복구가 가능한 데이터 저장용 필드

## 사용 방법

### onSaveInstanceState

액티비티, 프래그먼트에서 화면 회전 등 `onSaveInstanceState`가 호출 후 데이터 복구되는것을 자동으로 처리하는 클래스입니다.

- NonNullLiveData

필드에 생성할 때 두번째 인자값에 true를 씁니다.

```java
LiveData<String> someLiveData = new NonNullLiveData<>("test", true /* SaveInstanceState 사용 유무 */);
```

- SafeMutableLiveData

사용하면 자동으로 처리됩니다.

```java
LiveData<String> someLiveData = new SafeMutableLiveData<>("test");
```

- SafeData

사용하면 자동으로 처리됩니다.

```java
SafeData<String> someData = new SafeData<>("test");
```

### CompositeLiveData

(1) 초기값을 먼저 지정합니다.

```java
LiveData<Boolean> enabled = new CompositeLiveData<>(true)
```

(2) 여러 LiveData와 반환 조건식을 추가합니다.

```java
LiveData<Boolean> enabled = new CompositeLiveData<>(true)
        .add(name, (isEnabled, data) -> isEnabled && !data.trim().isEmpty());
```

(3) 이제 여러 LiveData 데이터 변경때마다 해당 LiveData 값이 결정됩니다.

```java
LiveData<Boolean> enabled = new CompositeLiveData<>(true)
        .add(name, (isEnabled, data) -> isEnabled && !data.trim().isEmpty())
        .add(email, (isEnabled, data) -> isEnabled && !data.trim().isEmpty())
        .add(password, (isEnabled, data) -> isEnabled && !data.trim().isEmpty());
```
