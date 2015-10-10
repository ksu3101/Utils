# Utils

## 설명
개인적으로 사용하기 위해 만든 Util메소드 패키지 입니다. 

저 또한 웹에서 수집하거나 제가 개인적으로 사용하기 위해 만든 클래스 및 메소드들이 있으므로 그냥 가져다가 사용 하셔도 무방 합니다. 

## 사용법
jcenter / build.gradle의 dependency에 아래 항목을 추가. 
```
dependencies {
    ...
    compile 'kr.ssungwoo.utils:utils:0.1.2'
    ...
}
```

Utils static class의 static메소드들을 상황에 맞추어 가져다가 쓰면 됩니다. 

## Utils 클래스 메소드 목록
```
String getCalendar(Calendar)                    : Calendar의 시간(yyyy-MM-dd'T'HH:mm:ssZ/ISO8601 규격)을 얻음
String getNow()                                 : 현재 시간(yyyy-MM-dd'T'HH:mm:ssZ/ISO8601 규격)을 얻음. 
String getApplicationName(Context)              : 앱의 이름을 얻는다. 
boolean isDatabasePresent(String, String)       : DB(두번째 변수)가 packageName(첫번째 변수)의 앱에 존재하는지 여부를 확인 한다. 
boolean isSDCardMounted()                       : SD카드가 마운트 되어 있는지 여부를 얻는다. 
String getPhoneNumber(Context)                  : 디바이스의 전화번호(MDN)을 얻는다. 
int getNetworkConnectionType(Context)           : 연결되어진 데이터 네트워크의 타입을 얻는다. (TYPE_MOBILE, TYPE_WIFI)
boolean isEmailString(String)                   : 주어진 문자열이 Email이 맞는지 valid 체크 한다. 
boolean isNumeric(String)                       : 주어진 문자열이 숫자로 이루어져 있는지 체크 한다. 
boolean isOnline(Context)                       : 디바이스의 네트워크 연결 여부를 확인 한다. 
String getApplicationVersionNumber(Context)     : 앱의 버전 네이밍을 얻는다. (예, 1.0)
int getApplicationVersionCode(Context)          : 앱의 버전 코드를 얻는다. 
String getOSVersion()                           : 안드로이드 운영체제의 버전 (2.3, 4.4)를 얻는다. 
boolean isServiceRunning(Context, String)       : 두번째 변수(서비스 이름 태그)의 서비스가 현재 실행중(Running)인지 여부를 확인한다. 
void setHighlightText(TextView, int, String, String) : 텍스트뷰의 일부 텍스트에 하이라이트 컬러를 설정 한다. 
void setHighlightText(TextView, String, String) : 텍스트뷰의 일부 텍스트에 기본 하이라이트 컬러(255, 0, 0 / RED)를 설정 한다. 
SpannableStringBuilder stringToBold(String)     : 텍스트에 bold를 설정한 SpannableStringBuilder를 얻는다. 
String getSizeFormat(long)                      : 데이터 사이즈에 따른 문자열 텍스트르 얻는다. (예, 10KB, 100MB)
boolean hasNavigationBar(Context)               : 디바이스에서 NavigationBar를 사용하는지 여부를 얻는다. 
boolean inViewBounds(View, int, int)            : 입력한 x(두번째 변수), y(세번째 변수)가 view(첫번째변수)의 영역 내(Bound)에 존재하는지 여부를 얻는다. 
boolean inViewBounds(View, Point)               : 입력한 Point(두번째 변수)의 x,y 좌표가 view의 영역 내(Bound)에 존재하는지 여부를 얻는다. 
void showToast(Context, String)                 : 간단한 토스트 메시지를 보여 준다. 
void showToast(Context, String, int)            : 간단한 토스트 메시지를 화면의 특정 위치에서 보여 준다. 
void showToastCenter(Context, String)           : 간단한 토스트 메시지를 화면의 가운데에 보여 준다. 
float convertPixelToDpi(Resources, int)         : Pixel단위 숫자를 DPI단위 float형태 숫자로 전환 한다. 
float convertDpiToPixel(Resources, int)         : DPI단위 숫자를 Pixel단위 float형태 숫자로 전환 한다. 
float convertPixelToDpi(Resources, float)       : Pixel단위 숫자를 DPI단위 float형태 숫자로 전환 한다. 
float convertDpiToPixel(Resources, float)       : DPI단위 숫자를 Pixel단위 float형태 숫자로 전환 한다. 
TransitionDrawable createTransitionDrawable(...) : drawable(1)과 drawable(2)간의 트랜지션 애니메이션을 적용한 TransitionDrawable을 만든다. 
TransitionDrawable createTransitionDrawable(Resources, int, int) : int(1)과 int(2) Drawable간의 트랜지션 에니메이션을 적용한 TransitionDrawable을 만든다. 
AlphaAnimation createFadeInAnimation(int)       : Fade in 애니메이션을 생성 한다. 
AlphaAnimation createFadeInAnimation(int, AnimationListener) : Fade in 애니메이션을 생성 한다. 
AlphaAnimation createFadeOutAnimation(int)      : Fade out 애니메이션을 생성 한다. 
AlphaAnimation createFadeOutAnimation(int, AnimationListener) : Fade out 애니메이션을 생성 한다. 
PorterDuffColorFilter applyBrightness(int)      : Drawable의 밝기를 조정하는 필터를 생성한다. (drawable.setColorFilter())
ColorMatrixColorFilter applyBrightnessByMatrixColorFilter : 이미지의 밝기를 조정할 수 있는 필터를 생성 한다. (drawable.setColorFilter())
Bitmap setImageBrightness(Bitmap, int)          : int값(-255 ~ 255)만큼 밝기가 조정된 Bitmap을 얻는다. 
Bitmap getResizeImg(Bitmap, int)                : int값만큼 사이즈가 조정된 Bitmap을 얻는다. 
Bitmap convertRGB565toARGB8888(Bitmap)          : RGB565 비트맵을 ARGB8888으로 변환 한다. 
boolean recyclerBitmap(ImageView)               : ImageView의 비트맵 이미지를 recycler하고 GC가 처리할 수 있게 null로 만든다. 
Bitmap drawableToBitmap(Drawable)               : Drawable을 Bitmap으로 변환한다. 
InputStream bitmapToInputStream(Bitmap)         : Bitmap을 InputStream으로 전환한다. 
Bitmap getBlurImage(Bitmap, int)                : int값(radius)만큼 적용된 blur bitmap를 얻는다. 
byte[] getBytesFromBitmap(Bitmap)               : Bitmap을 byte배열로 변환 한다. 
Bitmap getBlurImg_FastBlur_withResizing(...)    : 이미지를 리사이즈 하고 난뒤 blur적용 한다. 
Bitmap getBlurImg_FastBlur(Context, int, int)   : 두번째 변수 id를 가진의 이미지 자원에 blur를 적용하고 bitmap을 얻는다. 
Bitmap getViewCache(View)                       : View의 cache bitmap을 얻는다.
Bitmap getActivityViewCache(Activity)           : 액티비티의 view cache bitmap을 얻는다. OOM을 주의 할 것. 
Point getDeviceSize(Context)                    : 디바이스의 width, height를 얻는다. 
int getDeviceWidth(Context)                     : 디바이스의 width를 얻는다. 
int getDeviceHeight(Context)                    : 디바이스의 height를 얻는다. 
Point getDisplaySize(Context)                   : Display의 size를 얻는다. (상단 인디케이터 영역을 제외한 실제 표시 영역)
ValueAnimator createDefaultStartFloatValueAnimator(int, AnimatorUpdateListener) : 0.0f 에서 duration(첫번째 변수)동안 1.0f로 증가하는 ValueAnimator를 얻는다. 
ValueAnimator createDefaultEndFloatValueAnimator(int, AnimatorUpdateListener) : 1.0f 에서 duration(첫번째 변수)동안 0.0f로 감소하는 ValueAnimator를 얻는다. 
ValueAnimator createFloatValueAnimator(int, AnimatorUpdatelistener, AnimaotrListenerAdapter, float...)
float getDensityValue(Context)                  : 해상도에 따른 비율 배수를 얻는다. 
void setTileBackgorundToView(Context, int, int) : bgResId(세번째 변수)의 이미지를 targetViewResId(두번쨰 변수)에 타일 반복 형태의 배경으로 설정 한다. 
File getStorageDirectory(Context, String)       : 저장소의 갤러리에 만들어진(혹은 새로 만들고 난 뒤의) 폴더의 File인스턴스를 가져 온다. SD카드가 마운트 되어 있다면 먼저 SD카드부터 찾는다. 
File getFile(Context, String)                   : File을 가져 온다. 
void hideSoftKeyboard(Activity)                 : 포커싱 된 뷰로 인해 등장한 소프트 키보드를 감 춘다. 
void hideSoftKeyboard(Context, View...)         : 포커싱 된 뷰로 인해 등장한 소프트 키보드를 감 춘다. 
void showSoftKeyboard(Context, View)            : view에 포커스를 요청하고 키보드를 등장 하게 한다. 
```

## SWpreferences 클래스 메소드 목록
```
<T> boolean preferenceSave(Context, String, T)  : String 키를 가진 데이터 T를 저장 한다. (Integer, Long, Boolean, Float, String)
String preferencesLoad_String(Context, String)  : 저장된 값 중 문자열을 불러온다. 
int preferencesLoad_Int(Context, String)        : 저장된 값 중 Integer를 불러 온다. 
long preferencesLoad_Long(Context, String)      
float preferencesLoad_Float(Context, String)
boolean preferencesLoad_Boolean(Context, String)
boolean preferencesLoad_Boolean(Context, String, boolean defValue)
```

## SecurePreferences 클래스 메소드 목록
[Visit sveinung github](https://github.com/sveinungkb/encrypted-userprefs)



