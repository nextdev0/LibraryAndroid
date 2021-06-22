# convert

## boolean

- toBoolean(String) : `boolean` 형식으로 변환
- toBoolean(String, boolean) : `boolean` 형식으로 변환, 기본값을 줄 수 있음.

## 정수 변환

- toByte(String) : `byte` 형식으로 변환
- toByte(String, int) : `byte` 형식으로 변환, 기본값을 줄 수 있음.
- toHexByte(String) : 16진수로된 문자열을 `byte` 형식으로 변환
- toHexByte(String, int) : 16진수로된 문자열을 `byte` 형식으로 변환, 기본값을 줄 수 있음.
- toInt(String) : `int` 형식으로 변환
- toInt(String, int) : `int` 형식으로 변환, 기본값을 줄 수 있음.
- toHexInt(String) : 16진수로된 문자열을 `int` 형식으로 변환
- toHexInt(String, int) : 16진수로된 문자열을 `int` 형식으로 변환, 기본값을 줄 수 있음.
- toLong(String) : `long` 형식으로 변환
- toLong(String, long) : `long` 형식으로 변환, 기본값을 줄 수 있음.
- toHexLong(String) : 16진수로된 문자열을 `long` 형식으로 변환
- toHexLong(String, long) : 16진수로된 문자열을 `long` 형식으로 변환, 기본값을 줄 수 있음.

## 실수 변환

- toFloat(String) : `float` 형식으로 변환
- toFloat(String, float) : `float` 형식으로 변환, 기본값을 줄 수 있음.
- toDouble(String) : `double` 형식으로 변환
- toDouble(String, double) : `double` 형식으로 변환, 기본값을 줄 수 있음.

## 색상 변환

- toColorInt(String) : 컬러값 정수로 변환, 기본값은 투명색임.
- toColorInt(String, int) : 컬러값 정수로 변환, 기본값을 줄 수 있음.

## 변환 가능 체크

- isBoolean(String) : 문자열을 `boolean`으로 변환가능한지 체크
- isNumeric(String) : 문자열을 숫자로 변환가능한지 체크
- isHexadecimal(String) : 문자열이 16진수 형태인지 체크
