package kr.ssungwoo.utils;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.ViewConfigurationCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Utility class
 *
 * @author SungWoo Kang
 * @since 2015/08/25
 */
public class Utils {
  private static String  TAG   = Utils.class.getSimpleName();
  public static  boolean DEBUG = false;


  /**
   * Calendar를 ISO8601 규격에 맞춘 시간 문자열로 얻는다.
   *
   * @param cal Calendar instance.
   * @return ISO8601 규격에 맞춘 시간 문자열.
   */
  public static String getCalendar(Calendar cal) {
    Date date = cal.getTime();
    String format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).format(date);
    return format.substring(0, 22) + ":" + format.substring(22);
  }

  /**
   * 현재 시간의 문자열을 얻는다.
   *
   * @return 현재 시간 문자열
   */
  public static String getNow() {
    return getCalendar(GregorianCalendar.getInstance());
  }

  /**
   * 앱의 이름을 얻는다. 앱을 찾을 수 없는 경우 "UNKNOWN"을 반혼 한다.
   *
   * @param context Context
   * @return App name or UNKNOWN.
   */
  public static String getApplicationName(Context context) {
    PackageManager mgr = context.getPackageManager();
    ApplicationInfo appInfo = null;
    try {
      appInfo = mgr.getApplicationInfo(context.getPackageName(), PackageManager.SIGNATURE_MATCH);
    } catch (PackageManager.NameNotFoundException nnfe) {
      if (DEBUG) Log.e(TAG, "//// ERROR //// " + nnfe.getMessage());
    }
    return (String) (appInfo != null ? mgr.getApplicationLabel(appInfo) : "UNKNOWN");
  }

  /**
   * DB가 packageName의 앱에 존재하는지 여부를 확인 한다.
   *
   * @param packageName DB존재 여부를 확인할 앱
   * @param dbName      DB 이름.
   * @return true or false.
   */
  public static boolean isDatabasePresent(String packageName, String dbName) {
    SQLiteDatabase checkDb = null;
    try {
      checkDb = SQLiteDatabase.openDatabase("/data/data/" + packageName + "/databases/" + dbName, null, SQLiteDatabase.OPEN_READONLY);
      checkDb.close();
    } catch (SQLiteException se) {
      if (DEBUG) Log.e(TAG, "//// ERROR //// " + se.getMessage());
    }
    boolean isDBPresent = checkDb != null ? true : false;
    return isDBPresent;
  }

  /**
   * SD카드가 마운트 되어 있는지 여부를 얻는다.
   *
   * @return true일 경우 SdCard가 마운트 되어 있음.
   */
  public static boolean isSDCardMounted() {
    String status = Environment.getExternalStorageState();
    if (status != null && status.equals(Environment.MEDIA_MOUNTED)) {
      return true;
    }
    return false;
  }

  /**
   * 디바이스의 전화번호(MDN)를 얻는다.
   *
   * @param context Context
   * @return 전화번호 문자열.
   */
  public static String getPhoneNumber(Context context) {
    TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tMgr.getLine1Number();
  }

  /**
   * 연결되어진 데이터 네트워크의 타입을 얻는다.
   *
   * @param context Context
   * @return TYPE_MOBILE, TYPE_WIFI or -1
   * @see ConnectivityManager
   */
  public static int getNetworkConnectionType(Context context) {
    ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (mgr != null && mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                          .isConnected()) {
      if (mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
             .isConnected()) {
        return ConnectivityManager.TYPE_MOBILE;
      }
      else if (mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                  .isConnected()) {
        return ConnectivityManager.TYPE_WIFI;
      }
      else {
        return -1;
      }
    }
    return -1;
  }

  /**
   * 문자열 str이 Email형식을 갖추고 있는지 여부를 확인한다.
   *
   * @param str Email형식을 체크할 문자열.
   * @return true일 경우 Email형식.
   */
  public static boolean isEmailString(String str) {
    if (str != null) {
      return Patterns.EMAIL_ADDRESS.matcher(str)
                                   .matches();
    }
    return false;
  }

  /**
   * 문자열 str이 숫자로 되어 있는지 체크 한다.
   *
   * @param str 순자인지 확인 할 문자열.
   * @return true일 경우 숫자. false일 경우 숫자가 아니다.
   */
  public static boolean isNumeric(String str) {
    if (str != null) {
      try {
        Double.parseDouble(str);
        return true;
      } catch (NumberFormatException nfe) {
        if (DEBUG) {
          Log.e(TAG, nfe.getMessage());
        }
      }
    }
    return false;
  }

  /**
   * 디바이스의 네트워크 연결 여부를 확인 한다.
   *
   * @param context Context
   * @return true일 경우 온라인 상태. false 일 경우 오프라인 상태.
   */
  public static boolean isOnline(Context context) {
    if (context != null) {
      ConnectivityManager connMgr =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
      return (networkInfo != null && networkInfo.isConnected());
    }
    return false;
  }

  /**
   * 앱의 버전 네이밍을 얻는다. (예를 들어 1.0)
   *
   * @param context Context
   * @return null or Version name string.
   */
  public static String getApplicationVersionNumber(Context context) {
    String versionNumber = null;
    try {
      versionNumber = context.getPackageManager()
                             .getPackageInfo(context.getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException nne) {
      if (DEBUG) Log.e(TAG, "//// ERROR //// " + nne.getMessage());
    }
    return versionNumber;
  }

  /**
   * 앱의 버전 코드를 얻는다.
   *
   * @param context Context
   * @return 0 or Version code integer value.
   */
  public static int getApplicationVersionCode(Context context) {
    int versionCode = 0;
    try {
      versionCode = context.getPackageManager()
                           .getPackageInfo(context.getPackageName(), 0).versionCode;
    } catch (PackageManager.NameNotFoundException nne) {
      if (DEBUG) Log.e(TAG, "//// ERROR //// " + nne.getMessage());
    }
    return versionCode;
  }

  /**
   * 안드로이드 운영체제의 버전(2.3, 4.4)를 얻는다.
   *
   * @return 안드로이드 운영체제 버전 문자열.
   */
  public static String getOSVersion() {
    return Build.VERSION.RELEASE;
  }

  /**
   * serviceNameTag의 서비스가 현재 실행중(Running)인지 여부를 얻는다.
   *
   * @param context        Context
   * @param serviceNameTag 실행중 여부를 확인 할 서비스의 태그 문자열.
   * @return true or false
   */
  public static boolean isServiceRunning(Context context, String serviceNameTag) {
    if (serviceNameTag != null) {
      ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
        if (service.service.getClassName()
                           .equals(serviceNameTag)) {
          return true;
        }
      }
    }
    else {
      if (DEBUG) Log.e(TAG, "//// ERROR //// Service is NULL...");
    }
    return false;
  }

  /**
   * 하이라이팅 할 텍스트뷰의 텍스트를 설정 한다. (기본 red컬러)
   *
   * @param textView      대상 텍스트 뷰.
   * @param color         하이라이트 할 텍스트의 컬러. 기본값은 red.
   * @param highlightText 하이리이팅 할 텍스트.
   * @param original      원본 텍스트.
   */
  public static void setHighlightText(TextView textView, int color, String highlightText, String original) {
    if (highlightText != null && textView != null) {
      String hexColor = String.format("#%06X", (0xFFFFFF & color));
      if (!TextUtils.isEmpty(hexColor)) {
        if (DEBUG) Log.d(TAG, "//// DEBUG //// hexColor is " + hexColor);
        String result = original.replaceAll(highlightText, "<font color='" + hexColor + "'>" + highlightText + "</font>");
        textView.setText(Html.fromHtml(result));
      }
      else {
        if (DEBUG) Log.w(TAG, "//// WARNING //// hexColor is Empty.");
      }
    }
  }

  /**
   * 하이라이팅 할 텍스트뷰의 텍스트를 설정한다. (기본 하이라이팅 컬러 / RED)
   *
   * @param textView      대상 텍스트뷰
   * @param highlightText 하이라이팅 할 텍스트.
   * @param original      원본 텍스트.
   */
  public static void setHighlightText(TextView textView, String highlightText, String original) {
    if (highlightText != null && textView != null) {
      String result = original.replaceAll(highlightText, "<font color='red'>" + highlightText + "</font>");
      textView.setText(Html.fromHtml(result));
    }
  }

  /**
   * 텍스트에 bold를 설정한 SpannableStringBuilder를 얻는다.
   *
   * @param original 굵음 효과를 설정할 문자열
   * @return 굶음 효과가 설정 된 SpannableStringBuilder instance or null.
   */
  public static SpannableStringBuilder stringToBold(String original) {
    if (original != null) {
      SpannableStringBuilder sb = new SpannableStringBuilder(original);
      StyleSpan style = new StyleSpan(Typeface.BOLD);
      sb.setSpan(style, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      return sb;
    }
    else {
      if (DEBUG) Log.e(TAG, "//// ERROR //// String is null.. ");
    }
    return null;
  }

  /**
   * 데이터 사이즈에 따른 문자열 텍스트를 얻는다. (예, 10 KB)
   *
   * @param size 사이즈를 알아낼 데이터 사이즈 long value.
   * @return "0" or size text.
   */
  public static String getSizeFormat(long size) {
    if (size <= 0) return "0";
    final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
  }

  /**
   * 디바이스에서 NavigationBar를 사용하는지 여부를 얻는다.
   *
   * @param context Context object.
   * @return true일 경우 NavigationBar를 사용 중.
   */
  public static boolean hasNavigationBar(Context context) {
    if (context == null) return false;
    boolean hasMenuKey = ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(context));
    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    return (!hasMenuKey && !hasBackKey);
  }

  /**
   * 입력한 x, y좌표가 View의 영역 내(Bound)에 존재하는지 여부를 얻는다.
   *
   * @param view  조사할 View의 instance.
   * @param point x, y좌표의 객체.
   * @return true일 경우 x, y좌표가 View내에 존재 한다.
   */
  public static boolean inViewBounds(View view, Point point) {
    if (view != null && point != null) {
      return inViewBounds(view, point.x, point.y);
    }
    return false;
  }

  /**
   * 입력한 x, y좌표가 View의 영역 내(Bound)에 존재하는지 여부를 얻는다.
   *
   * @param view 조사할 View의 instance.
   * @param x    x 좌표.
   * @param y    y 좌표.
   * @return true 일 경우 x, y 좌표가 View내에 존재 한다.
   */
  public static boolean inViewBounds(View view, int x, int y) {
    if (view != null) {
      final Rect outRect = new Rect();
      int[] location = new int[2];
      view.getDrawingRect(outRect);
      view.getLocationOnScreen(location);
      outRect.offset(location[0], location[1]);
      return outRect.contains(x, y);
    }
    return false;
  }

  /**
   * 간단한 토스트 메시지를 보여 준다.
   *
   * @param context Context object.
   * @param message 토스트를 통해서 보여줄 메시지.
   */
  public static void showToast(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT)
         .show();
  }

  /**
   * 간단한 토스트 메시지를 화면의 특정 위치에서 보여 준다.
   *
   * @param context      Context object.
   * @param message      토스트를 통해서 보여줄 메시지
   * @param toastGravity 토스트의 위치.
   * @see android.view.Gravity
   */
  public static void showToast(Context context, String message, int toastGravity) {
    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    toast.setGravity(toastGravity, 0, 0);
    toast.show();
  }

  /**
   * 간단한 토스트 메세지를 화면의 중간에 보여 준다.
   *
   * @param context Context object.
   * @param message 토르스틀 통해서 보여줄 메시지.
   */
  public static void showToastCenter(Context context, String message) {
    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    toast.show();
  }

  /**
   * Pixel 단위 숫자를 DPI단위 Float형태의 숫자로 변환한다.
   *
   * @param res   Resources.
   * @param pixel 변환대상 Pixel 단위 숫자.
   * @return Float형태의 DPI.
   */
  public static float convertPixelToDpi(Resources res, int pixel) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixel, res.getDisplayMetrics());
  }

  /**
   * DPI 단위 숫자를 Pixel 단위 Float형태의 숫자로 변환한다.
   *
   * @param res Resources.
   * @param dpi 변환대상 DPI단위의 숫자.
   * @return Float형태의 pixel 숫자.
   */
  public static float convertDpiToPixel(Resources res, int dpi) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, res.getDisplayMetrics());
  }

  /**
   * Pixel 단위 숫자를 DPI단위 Float형태의 숫자로 변환한다.
   *
   * @param res   Resources.
   * @param pixel 변환대상 Pixel 단위 숫자.
   * @return Float형태의 DPI.
   */
  public static float convertPixelToDpi(Resources res, float pixel) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixel, res.getDisplayMetrics());
  }

  /**
   * DPI 단위 숫자를 Pixel 단위 Float형태의 숫자로 변환한다.
   *
   * @param res Resources.
   * @param dpi 변환대상 DPI단위의 숫자.
   * @return Float형태의 pixel 숫자.
   */
  public static float convertDpiToPixel(Resources res, float dpi) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, res.getDisplayMetrics());
  }

  /**
   * Drawable1과 Drawable2간의 트랜지션 에니메이션을 적용한 TransitionDrawable을 만든다.
   * 만들어진 TransitionDrawable의 객채의 startTransition(durationMillis)메소드를 이용하여
   * 애니메이션을 시작 한다.
   *
   * @param layer1 before Drawable.
   * @param layer2 after Drawable.
   * @return {@link TransitionDrawable}
   */
  public static TransitionDrawable createTransitionDrawable(Drawable layer1, Drawable layer2) {
    TransitionDrawable td = new TransitionDrawable(new Drawable[]{layer1, layer2});
    td.setCrossFadeEnabled(true);
    return td;
  }

  /**
   * Drawable1(resId1) 과 Drawable2(resId2)간의 트랜지션 애니메이션을 적용한 TransitionDrwable을 만든다.  만들어진
   * TransitionDrwable 객체의 startTransition(durationMillis)를 이용하여 애니메이션을 시작한다.
   *
   * @param res         Resources (from Context)
   * @param layerResId1 before Drawable Resource ID.
   * @param layerResId2 after Drawable Resource ID.
   * @return {@link TransitionDrawable}
   */
  public static TransitionDrawable createTransitionDrawable(Resources res,
                                                            int layerResId1,
                                                            int layerResId2) {
    if (res != null) {
      TransitionDrawable td = new TransitionDrawable(new Drawable[]{res.getDrawable(layerResId1),
          res.getDrawable(layerResId2)});
      td.setCrossFadeEnabled(true);
      return td;
    }
    return null;
  }

  /**
   * Fade In 애니메이션을 생성한다.
   *
   * @param duration 애니메이션이 진행될 ms시간.
   * @return {@link AlphaAnimation}
   */
  public static AlphaAnimation createFadeInAnimation(int duration) {
    AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
    fadeIn.setDuration(duration);
    fadeIn.setFillAfter(true);
    fadeIn.setInterpolator(new AccelerateInterpolator());
    return fadeIn;
  }

  /**
   * Fade In 애니메이션을 생성한다.
   *
   * @param duration 애니메이션이 진행될 ms시간.
   * @param listener 애니메이션 콜백 리스너.
   * @return {@link AlphaAnimation}
   */
  public static AlphaAnimation createFadeInAnimation(int duration, Animation.AnimationListener listener) {
    AlphaAnimation fadeIn = createFadeInAnimation(duration);
    if (listener != null) {
      fadeIn.setAnimationListener(listener);
    }
    return fadeIn;
  }

  /**
   * Fade Out 애니메이션을 생성한다.
   *
   * @param duration 애니메이션이 진행될 ms시간.
   * @return {@link AlphaAnimation}
   */
  public static AlphaAnimation createFadeOutAnimation(int duration) {
    AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
    fadeOut.setDuration(duration);
    fadeOut.setFillAfter(false);
    fadeOut.setInterpolator(new DecelerateInterpolator());
    return fadeOut;
  }

  /**
   * Fade Out 애니메이션을 생성한다.
   *
   * @param duration 애니메이션이 진행될 ms시간.
   * @param listener 애니메이션 콜백 리스너.
   * @return {@link AlphaAnimation}
   */
  public static AlphaAnimation createFadeOutAnimation(int duration, Animation.AnimationListener listener) {
    AlphaAnimation fadeOut = createFadeOutAnimation(duration);
    if (listener != null) {
      fadeOut.setAnimationListener(listener);
    }
    return fadeOut;
  }

  /**
   * 이미지Drawable 의 밝기를 조정하는 PorterDuffColorFilter를 생성한다.
   * 사용법 : drawable.setColorFilter(applyBrightness(-30));
   *
   * @param value -100(더 어둡게) 부터 100(더 밝게) 까지.
   * @return PorterDuffColorFilter instance.
   * @see PorterDuffColorFilter
   */
  public static PorterDuffColorFilter applyBrightness(int value) {
    if (value > 0) {
      int target = (int) value * 255 / 100;
      return new PorterDuffColorFilter(Color.argb(target, 255, 255, 255), PorterDuff.Mode.SRC_OVER);
    }
    else {
      int target = (int) (value * -1) * 255 / 100;
      return new PorterDuffColorFilter(Color.argb(target, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
    }
  }

  /**
   * 이미지의 밝기를 조정 할 수 있는 Color Matrix Filter를 생성한다.
   * 사용법 : imageView.setColorFilter(brightIt(100));
   *
   * @param value ?
   * @return ColorMatrixColorFilter
   * @see ColorMatrixColorFilter
   */
  public static ColorMatrixColorFilter applyBrightnessByMatrixColorFilter(int value) {
    ColorMatrix cMat = new ColorMatrix();
    cMat.set(new float[]{
        1, 0, 0, 0, value,
        0, 1, 0, 0, value,
        0, 0, 1, 0, value,
        0, 0, 0, 0, 1
    });
    ColorMatrix cMatSet = new ColorMatrix();
    cMatSet.set(cMat);
    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cMatSet);
    return filter;
  }

  /**
   * Bitmap 이미지에 밝기 조정을 한다.
   *
   * @param src   밝기조정을 할 원본 Bitmap 이미지.
   * @param value 밝기 조정 값. (-255 ~ 255 Integer)
   * @return 밝기값이 적용된 Bitmap 이미지.
   */
  public static Bitmap setImageBrightness(Bitmap src, int value) {
    if (value < -255 && value > 255) {
      return null;
    }

    // image size
    int width = src.getWidth();
    int height = src.getHeight();

    // create output bitmap
    Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

    // color information
    int A, R, G, B;
    int pixel;

    // scan through all pixels
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        // get pixel color
        pixel = src.getPixel(x, y);
        A = Color.alpha(pixel);
        R = Color.red(pixel);
        G = Color.green(pixel);
        B = Color.blue(pixel);

        // increase/decrease each channel
        R += value;
        if (R > 255) {
          R = 255;
        }
        else if (R < 0) {
          R = 0;
        }

        G += value;
        if (G > 255) {
          G = 255;
        }
        else if (G < 0) {
          G = 0;
        }

        B += value;
        if (B > 255) {
          B = 255;
        }
        else if (B < 0) {
          B = 0;
        }

        // apply new pixel color to output bitmap
        bmOut.setPixel(x, y, Color.argb(A, R, G, B));
      }
    }

    // return final image
    return bmOut;
  }

  /**
   * Bitmap이미지를 리사이즈 한다.
   *
   * @param originalImg   리사이즈 대상 원본 Bitmap 이미지.
   * @param maxResolution width, height 대상 중 최대 감안 크기.
   * @return 리사이징 된 Bitmap 이미지.
   */
  public static Bitmap getResizeImg(Bitmap originalImg, int maxResolution) {
    if (originalImg == null) return null;
    final int width = originalImg.getWidth();
    final int height = originalImg.getHeight();
    int newWidth = width;
    int newHeight = height;
    float rate = 0.0f;

    if (width > height) {
      if (maxResolution < width) {
        rate = maxResolution / (float) width;
        newHeight = (int) (height * rate);
        newWidth = maxResolution;
      }
    }
    else {
      if (maxResolution < height) {
        rate = maxResolution / (float) height;
        newWidth = (int) (width * rate);
        newHeight = maxResolution;
      }
    }
    return Bitmap.createScaledBitmap(originalImg, newWidth, newHeight, true);
  }

  /**
   * RGB565의 비트맵 이미지를 ARGB888으로 전환 한다.
   *
   * @param img ARGB8888으로 전환할 원본 이미지.
   * @return ARGB8888으로 전환된 비트맵 객체.
   */
  public static Bitmap convertRGB565toARGB8888(Bitmap img) {
    int numPixels = img.getWidth() * img.getHeight();
    int[] pixels = new int[numPixels];

    //Get JPEG pixels.  Each int is the color values for one pixel.
    img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

    //Create a Bitmap of the appropriate format.
    Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

    //Set RGB pixels.
    result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
    return result;
  }

  /**
   * ImageView의 Bitmap을 recycle하고 GC가 처리 할 수 있게 null로 만든다.
   *
   * @param iv 더이상 사용하지 않아 Bitmap을 recycle할 대상 ImageView.
   * @return true일 경우 성공적으로 Recycle 성공.
   */
  public static boolean recycleBitmap(ImageView iv) {
    if (iv != null) {
      Drawable d = iv.getDrawable();
      if (d != null) {
        if (d instanceof BitmapDrawable) {
          Bitmap bmp = ((BitmapDrawable) d).getBitmap();
          if (bmp != null) {
            bmp.recycle();
            bmp = null;
            if (DEBUG) Log.i("Utils", ">>> recycleBitmap() / recycled bmp...");
          }
        }
        d.setCallback(null);
        return true;
      }
    }
    return false;
  }

  /**
   * Drawable 을 Bitmap으로 전환 한다.
   *
   * @param drawable Bitmap으로 전환할 Drawable instance.
   * @return null or Bitmap instance. (OOM을 조심 할 것)
   */
  public static Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable != null) {
      if (drawable instanceof BitmapDrawable) {
        return ((BitmapDrawable) drawable).getBitmap();
      }
      if (drawable.getIntrinsicWidth() <= 0 && drawable.getIntrinsicHeight() <= 0) {
        return null;
      }
      Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bmp);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
      return bmp;
    }
    else {
      if (DEBUG) Log.e(TAG, "//// ERROR //// Drawable is NULL...");
    }
    return null;
  }

  /**
   * Bitmap을 InputStream으로 전환 한다.
   *
   * @param bmp InputStream으로 전환할 Bitmap instance.
   * @return null or InputStream instance.
   */
  public static InputStream bitmapToInputStream(Bitmap bmp) {
    InputStream is = null;
    if (bmp != null) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
      is = new ByteArrayInputStream(baos.toByteArray());
      return is;
    }
    else {
      if (DEBUG) Log.e(TAG, "//// ERROR //// Bitmap is NULL...");
    }
    return is;
  }

  /**
   * Bitmap이미지에 Blur를 radius만큼 적용한다.
   *
   * @param sentBitmap 원본 Bitmap 이미지
   * @param radius     blur radius value.
   * @return Blur효과가 적용된 Bitmap 객체.
   */
  public static Bitmap getBlurImage(Bitmap sentBitmap, int radius) {
    // Stack Blur v1.0 from
    // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
    //
    // Java Author: Mario Klingemann <mario at quasimondo.com>
    // http://incubator.quasimondo.com
    // created Feburary 29, 2004
    // Android port : Yahel Bouaziz <yahel at kayenko.com>
    // http://www.kayenko.com
    // ported april 5th, 2012

    // This is a compromise between Gaussian Blur and Box blur
    // It creates much better looking blurs than Box Blur, but is
    // 7x faster than my Gaussian Blur implementation.
    //
    // I called it Stack Blur because this describes best how this
    // filter works internally: it creates a kind of moving stack
    // of colors whilst scanning through the image. Thereby it
    // just has to add one new block of color to the right side
    // of the stack and remove the leftmost color. The remaining
    // colors on the topmost layer of the stack are either added on
    // or reduced by one, depending on if they are on the right or
    // on the left side of the stack.
    //
    // If you are using this algorithm in your code please add
    // the following line:
    //
    // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

    Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

    if (radius < 1) {
      return (null);
    }

    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    int[] pix = new int[w * h];
    bitmap.getPixels(pix, 0, w, 0, 0, w, h);

    int wm = w - 1;
    int hm = h - 1;
    int wh = w * h;
    int div = radius + radius + 1;

    int r[] = new int[wh];
    int g[] = new int[wh];
    int b[] = new int[wh];
    int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
    int vmin[] = new int[Math.max(w, h)];

    int divsum = (div + 1) >> 1;
    divsum *= divsum;
    int dv[] = new int[256 * divsum];
    for (i = 0; i < 256 * divsum; i++) {
      dv[i] = (i / divsum);
    }

    yw = yi = 0;

    int[][] stack = new int[div][3];
    int stackpointer;
    int stackstart;
    int[] sir;
    int rbs;
    int r1 = radius + 1;
    int routsum, goutsum, boutsum;
    int rinsum, ginsum, binsum;

    for (y = 0; y < h; y++) {
      rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
      for (i = -radius; i <= radius; i++) {
        p = pix[yi + Math.min(wm, Math.max(i, 0))];
        sir = stack[i + radius];
        sir[0] = (p & 0xff0000) >> 16;
        sir[1] = (p & 0x00ff00) >> 8;
        sir[2] = (p & 0x0000ff);
        rbs = r1 - Math.abs(i);
        rsum += sir[0] * rbs;
        gsum += sir[1] * rbs;
        bsum += sir[2] * rbs;
        if (i > 0) {
          rinsum += sir[0];
          ginsum += sir[1];
          binsum += sir[2];
        }
        else {
          routsum += sir[0];
          goutsum += sir[1];
          boutsum += sir[2];
        }
      }
      stackpointer = radius;

      for (x = 0; x < w; x++) {

        r[yi] = dv[rsum];
        g[yi] = dv[gsum];
        b[yi] = dv[bsum];

        rsum -= routsum;
        gsum -= goutsum;
        bsum -= boutsum;

        stackstart = stackpointer - radius + div;
        sir = stack[stackstart % div];

        routsum -= sir[0];
        goutsum -= sir[1];
        boutsum -= sir[2];

        if (y == 0) {
          vmin[x] = Math.min(x + radius + 1, wm);
        }
        p = pix[yw + vmin[x]];

        sir[0] = (p & 0xff0000) >> 16;
        sir[1] = (p & 0x00ff00) >> 8;
        sir[2] = (p & 0x0000ff);

        rinsum += sir[0];
        ginsum += sir[1];
        binsum += sir[2];

        rsum += rinsum;
        gsum += ginsum;
        bsum += binsum;

        stackpointer = (stackpointer + 1) % div;
        sir = stack[(stackpointer) % div];

        routsum += sir[0];
        goutsum += sir[1];
        boutsum += sir[2];

        rinsum -= sir[0];
        ginsum -= sir[1];
        binsum -= sir[2];

        yi++;
      }
      yw += w;
    }
    for (x = 0; x < w; x++) {
      rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
      yp = -radius * w;
      for (i = -radius; i <= radius; i++) {
        yi = Math.max(0, yp) + x;

        sir = stack[i + radius];

        sir[0] = r[yi];
        sir[1] = g[yi];
        sir[2] = b[yi];

        rbs = r1 - Math.abs(i);

        rsum += r[yi] * rbs;
        gsum += g[yi] * rbs;
        bsum += b[yi] * rbs;

        if (i > 0) {
          rinsum += sir[0];
          ginsum += sir[1];
          binsum += sir[2];
        }
        else {
          routsum += sir[0];
          goutsum += sir[1];
          boutsum += sir[2];
        }

        if (i < hm) {
          yp += w;
        }
      }
      yi = x;
      stackpointer = radius;
      for (y = 0; y < h; y++) {
        // Preserve alpha channel: ( 0xff000000 & pix[yi] )
        pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

        rsum -= routsum;
        gsum -= goutsum;
        bsum -= boutsum;

        stackstart = stackpointer - radius + div;
        sir = stack[stackstart % div];

        routsum -= sir[0];
        goutsum -= sir[1];
        boutsum -= sir[2];

        if (x == 0) {
          vmin[y] = Math.min(y + r1, hm) * w;
        }
        p = x + vmin[y];

        sir[0] = r[p];
        sir[1] = g[p];
        sir[2] = b[p];

        rinsum += sir[0];
        ginsum += sir[1];
        binsum += sir[2];

        rsum += rinsum;
        gsum += ginsum;
        bsum += binsum;

        stackpointer = (stackpointer + 1) % div;
        sir = stack[stackpointer];

        routsum += sir[0];
        goutsum += sir[1];
        boutsum += sir[2];

        rinsum -= sir[0];
        ginsum -= sir[1];
        binsum -= sir[2];

        yi += w;
      }
    }
    bitmap.setPixels(pix, 0, w, 0, 0, w, h);
    return (bitmap);
  }

  /**
   * Bitmap을 Byte배열로 전환 한다.
   *
   * @param bmp 비트맵 이미지
   * @return byte array.
   */
  public static byte[] getBytesFromBitmap(Bitmap bmp) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
    return os.toByteArray();
  }

  /**
   * Blur가 적용된 이미지를 리사이증 후 얻는다.
   *
   * @param context       Context
   * @param imgResId      Blur효과를 적용할 이미지의 리소스 ID.
   * @param maxResolution width, height 대상 중 최대 감안 크기.
   * @param blurRadius    blur 가 적용될 value.
   * @return Blur및 리사이징 된 Bitmap이미지.
   */
  public static Bitmap getBlurImg_FastBlur_withRisizing(Context context, int imgResId, int maxResolution, int blurRadius) {
    Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), imgResId);
    if (bmp != null) {
      Bitmap bmpResized = getResizeImg(bmp, maxResolution);
      return getBlurImage(bmpResized, blurRadius);
    }
    return bmp;
  }

  /**
   * 이미지 리소스에 Blur 효과를 적용하는 메소드.
   *
   * @param context  Context.
   * @param imgResId Blur효과를 적용할 이미지의 리소스 ID.
   * @param radius   흐림의 정도 값.
   * @return Blur적용된 Bitmap 이미지.
   */
  public static Bitmap getBlurImg_FastBlur(Context context, int imgResId, int radius) {
    Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), imgResId);
    return getBlurImage(bmp, radius);
  }

  /**
   * 어떤 View의 view cache bitmap 인스턴스를 어든ㄴ다.
   *
   * @param view View cache를 얻을 대상의 view.
   * @return null or Bitmap object.
   */
  public static Bitmap getViewCache(View view) {
    if (view == null) return null;
    Bitmap bmp = null;
    view.setDrawingCacheEnabled(true);
    ;
    bmp = view.getDrawingCache();
    return bmp;
  }

  /**
   * 현재 액티비티의 View Cache를 Bitmap 인스턴스 로 얻는다. OOM에 유의 할 것.
   *
   * @param activity View Cache를 얻을 대상 Activity.
   * @return null or Bitmap object.
   */
  public static Bitmap getActivityViewCache(Activity activity) {
    if (activity == null) return null;
    Bitmap bmp = null;
    View view = activity.findViewById(android.R.id.content);
    if (view != null) {
      view.setDrawingCacheEnabled(true);
      bmp = view.getDrawingCache();
    }
    return bmp;
  }

  /**
   * 디바이스의 width, height를 구한다.
   *
   * @param context Context
   * @return Point object.
   */
  public static Point getDeviceSize(Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    if (Build.VERSION.SDK_INT > 13) {
      display.getSize(size);
    }
    else {
      size.x = display.getWidth();
      size.y = display.getHeight();
    }
    return size;
  }

  /**
   * 디바이스의 Width값을 구한다.
   *
   * @param context Context
   * @return device width.
   */
  public static int getDeviceWidth(Context context) {
    return getDeviceSize(context).x;
  }

  /**
   * 디바이스의 Height값을 구한다.
   *
   * @param context Context
   * @return device height.
   */
  public static int getDeviceHeight(Context context) {
    return getDeviceSize(context).y;
  }

  /**
   * Display의 Size를 구한다. (상단 인디케이터 영역을 제외한 실제 표시 영역)
   *
   * @param context Context
   * @return device size Point object.
   */
  public static Point getDisplaySize(Context context) {
    DisplayMetrics metrics = context.getResources()
                                    .getDisplayMetrics();

    Point size = new Point();
    size.x = metrics.widthPixels;
    size.y = (int) (metrics.heightPixels - 25 * metrics.density);

    return size;
  }

  /**
   * 0.0f 에서 duration동안 1.0f로 증가 하는 ValueAnimator 를 만든다.
   *
   * @param duration       animation duration.
   * @param updateListener value animation이 진행 되는 동안의 해야 할 작업을 구현한 listner 인스턴스.
   * @return ValueAnimator
   * @see ValueAnimator
   */
  public static ValueAnimator createDefaultStartFloatValueAnimator(int duration, ValueAnimator.AnimatorUpdateListener updateListener) {
    return createFloatValueAnimator(duration, updateListener, null, 0.0f, 1.0f);
  }

  /**
   * 1.0f 에서 duration동안 0.0f로 감소 하는 ValueAnimator 를 만든다.
   *
   * @param duration       animation duration.
   * @param updateListener value animation이 진행 되는 동안의 해야 할 작업을 구현한 listner 인스턴스.
   * @return ValueAnimator
   * @see ValueAnimator
   */
  public static ValueAnimator createDefaultEndFloatValueAnimator(int duration, ValueAnimator.AnimatorUpdateListener updateListener) {
    return createFloatValueAnimator(duration, updateListener, null, 1.0f, 0.0f);
  }

  /**
   * values(float)값에 의해 start value부터 end value까지 duration동안 증가 혹은 감소하는 ValueAnimator를 만든다.
   * return 받은 ValueAnimator의 객체의 start()메소드를 이용하여 시작 하면 된다.
   *
   * @param duration         animation duration.
   * @param updateListener   value animation이 진행 되는 동안의 해야 할 작업을 구현한 listener 인스턴스.
   * @param animationAdapter value animation의 상황에 따라 불리어지는 콜백을 구현한 listener 인스턴스.
   * @param values           start value... end value.
   * @return ValueAnimator
   * @see ValueAnimator
   */
  public static ValueAnimator createFloatValueAnimator(int duration,
                                                       ValueAnimator.AnimatorUpdateListener updateListener,
                                                       AnimatorListenerAdapter animationAdapter,
                                                       float... values) {
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(values);
    valueAnimator.setDuration(duration);
    if (updateListener != null) valueAnimator.addUpdateListener(updateListener);
    if (animationAdapter != null) valueAnimator.addListener(animationAdapter);
    return valueAnimator;
  }

  /**
   * 해상도에 따른 비율 배수를 얻는다.
   *
   * @param context Context.
   * @return Density multiplier value.
   */
  public static float getDensityValue(Context context) {
    return context.getResources()
                  .getDisplayMetrics().density;
  }

  /**
   * bgResId의 이미지를 targetViewResId에 타일반복 형태의 배경으로 설정 한다.
   *
   * @param context         Context
   * @param targetViewResId 배경을 설정할 View의 resource id.
   * @param bgResId         타일 배경으로 설정될 Drawable의 resource id.
   */
  public static void setTileBackgroundToView(Context context, int targetViewResId, int bgResId) {
    try {
      Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bgResId);
      BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bmp);
      bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
      View view = ((Activity) context).findViewById(targetViewResId);
      if (view != null) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
          view.setBackgroundDrawable(bitmapDrawable);
        }
        else {
          view.setBackground(bitmapDrawable);
        }
      }
    } catch (Exception e) {
      if (DEBUG) Log.e(TAG, "//// ERROR //// " + e.getMessage());
    }
  }

  /**
   * 데이터 저장소의 갤러리에 만들어진(혹은 새로 만들고 난 뒤의) 폴더의 File 인스턴스를 가져 온다.
   * 만약 SdCard같은 외부 저장소가 마운트 되어 있다면 우선 외부 저장소를 체크(새로) 한다.
   *
   * @param context       Context
   * @param directoryName 체크 혹은 새로 만들 폴더의 이름.
   * @return File instance or Null
   */
  public static File getStorageDirectory(Context context, String directoryName) {
    if (directoryName != null) {
      File f = null;
      String state = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED.equals(state)) {
        f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + directoryName);
      }
      else {
        f = new File(context.getCacheDir() + "/" + directoryName);
      }
      if (!f.exists()) f.mkdir();
      return f;
    }
    return null;
  }

  /**
   * File을 가져 온다.
   *
   * @param context  Context
   * @param fileName File의 이름
   * @return File instance or null
   */
  public static File getFile(Context context, String fileName) {
    File dir = getStorageDirectory(context, null);
    if (dir != null) {
      File f = new File(dir, fileName);
      return f;
    }
    return null;
  }

  /**
   * 포커싱 된 뷰로 인해 등장한 소프트 키보드를 감 춘다.
   *
   * @param activity 키보드가 보여지고 있는 포커싱 된 뷰가 존재하는 액티비티
   */
  public static void hideSoftKeyboard(Activity activity) {
    hideSoftKeyboard(activity, activity.getCurrentFocus());
  }

  /**
   * 포커싱 된 뷰로 인해서 등장한 소프트 키보드를 감춘다.
   *
   * @param context Context
   * @param views   포커싱 된 뷰들
   */
  public static void hideSoftKeyboard(Context context, View... views) {
    if (views == null) return;
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    for (View currentView : views) {
      if (currentView == null) continue;
      imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
      currentView.clearFocus();
    }
  }

  /**
   * view에 포커스를 요청하고 키보드를 등장 시키게 한다.
   *
   * @param context Context
   * @param view    포커스받고 입력 받을 Input View.
   */
  public static void showSoftKeyboard(Context context, View view) {
    if (view == null) return;
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    view.requestFocus();
    imm.showSoftInput(view, 0);
  }

}
