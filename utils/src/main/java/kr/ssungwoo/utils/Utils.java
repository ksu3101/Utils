package kr.ssungwoo.utils;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewConfigurationCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsSpinner;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class
 *
 * @author KangSung-Woo
 * @since 2015/08/25
 */
public class Utils {
  private static String TAG = Utils.class.getSimpleName();

  /**
   * 어떠한 시간과 현재의 시간을 비교해서 변화된 시간의 값에 따른 문자열을 얻는다.
   * 게시물의 리플, 알림의 내 소식에서 사용 된다.
   * <p/>
   * <li>1초 ~ 59초 이전 : 1초 전 ~ 59초 전 (초 단위)</li>
   * <li>1분 ~ 59분 이전 : 1분 전 ~ 59분 전 (분 단위)</li>
   * <li>1시간 ~ 23시간 59분전 : 1시간 전 ~ 23시간 전 (시간 단위)</li>
   * <li>24시간 이전 : yyyy.MM.dd (년.월.일)</li>
   * <p/>
   * (14/04/22 ksw : 현재 서버에서 처리 중)
   *
   * @param cal 시간 비교 대상의 Calendar object.
   * @return date string or EmptyString
   */
  @Deprecated
  public static String getDiffDateString(Calendar cal) {
    if (cal != null) {
      Calendar now = GregorianCalendar.getInstance();

      final long nowMillis = now.getTimeInMillis();
      final long curMillis = cal.getTimeInMillis();

      final long diff = nowMillis - curMillis;

      if (diff <= 0) {
        // 서버 시간과 로컬의 시간이 다른 경우 -> 그냥 방금 전으로 판단
        return "지금";
      }
      else {
        final long diffDays = diff / (24 * 60 * 60 * 1000);
        if (diffDays >= 1) {
          //Log.d(TAG, "// getDiffDateString() // diffDays = " + diffDays);

          // yyyy.mm.dd
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
          final String output = simpleDateFormat.format(cal.getTimeInMillis());
          //Log.d(TAG, "// getDiffDateString() // output string = " + (output != null ? output : "NULL"));
          return output;
        }
        else {
          final long diffHours = diff / (60 * 60 * 1000) % 24;
          if (diffHours >= 1) {
            //Log.d(TAG, "// getDiffDateString() // diffHours = " + diffHours);
            return String.valueOf(diffHours + "시간 전");
          }
          else {
            final long diffMins = diff / (60 * 1000) % 60;
            if (diffMins >= 1) {
              return String.valueOf(diffMins + "분 전");
            }
            else {
              final long diffSec = diff / 1000 % 60;
              return String.valueOf(diffSec + "초 전");
            }
          }
        }
      }
    }
    Log.w(TAG, "ERROR");
    return "";
  }

  /**
   * 어떠한 시간과 현재 시간을 비교하여, 하루가 지났는지를 판단해서 날짜를 다시 문자열로
   * 편집하여 반환한다.
   *
   * @param originalDate 비교할 시간 대상 ("yyyy-MM-dd hh:mm:ss")
   * @return 변환된 문자열 혹은 원본 문자열
   */

  public static String getDiffDayDateString(String originalDate) {
    if (originalDate != null) {
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
      try {
        cal.setTime(sdf.parse(originalDate));
      } catch (ParseException pe) {
        Log.e(TAG, pe.getMessage());
        return originalDate;
      }

      Calendar now = GregorianCalendar.getInstance();
      final int calDay = cal.get(Calendar.DAY_OF_YEAR);
      final int nowDay = now.get(Calendar.DAY_OF_YEAR);
      int dayDif = Math.abs(nowDay - calDay);
      if (dayDif >= 1) {
        // 하루 이상 지난 경우 / yyyy-mm-dd
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return String.valueOf(format.format(cal.getTime()));
      }
      else {
        // 오늘 업로드한 게시물 일 경우 / yyyy-mm-dd a hh:mm
        // -> 2016/01/18 수정 (24시간제로 바꿈)
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return String.valueOf(format.format(cal.getTime()));
      }
    }
    return "";
  }

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
   * 날짜 및 시간정보를 dateFormat에 맞추어서 얻는다.
   *
   * @param cal        날짜 및 시간의 Calendar 객체
   * @param dateFormat SimpleDateFormat을 참고 할 것
   * @return 시간 문자열
   */
  public static String getNow(Calendar cal, String dateFormat) {
    Date date = cal.getTime();
    return new SimpleDateFormat(dateFormat, Locale.getDefault()).format(date);
  }

  /**
   * 현재의 날짜 정보를 'yyyy. MM. dd.'형태로 얻는다.
   *
   * @return 'yyyy. MM. dd.'형태의 시간 문자열
   */
  public static String getNowDate() {
    return getNow(GregorianCalendar.getInstance(), "yyyy. MM. dd.");
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
   * @param dateStr
   * @param format
   * @return
   */
  public static String getDateString(String dateStr, String format) {
    if (!TextUtils.isEmpty(dateStr) && !TextUtils.isEmpty(format)) {
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
      try {
        cal.setTime(sdf.parse(dateStr));
        //return new SimpleDateFormat(format, Locale.getDefault()).format(dateStr);
        return String.valueOf(sdf.format(cal.getTime()));

      } catch (ParseException pe) {
        Log.e(TAG, pe.getMessage());
        return dateStr;
      }

    }
    return dateStr;
  }

  /**
   * dateStr 문자열이 date format형인지 여부를 판단 한다.
   *
   * @param dateStr 날짜 문자열
   * @param format  SimpleDateFormat의 format 형태 문자열
   * @return true or false
   */
  public static boolean validDateFormat(String dateStr, String format) {
    if (!TextUtils.isEmpty(dateStr) && !TextUtils.isEmpty(format)) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
      try {
        dateFormat.parse(dateStr);

      } catch (ParseException pe) {
        return false;
      }
      return true;
    }
    return false;
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
      Log.e(TAG, nnfe.getMessage());
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
      Log.e(TAG, se.getMessage());
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
    return status != null && status.equals(Environment.MEDIA_MOUNTED);
  }

  /**
   * 디바이스의 전화번호(MDN)를 얻는다.
   *
   * @param context Context
   * @return 전화번호 문자열.
   */
  public static String getPhoneNumber(@NonNull Context context) {
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
  public static int getNetworkConnectionType(@NonNull Context context) {
    ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    if (mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
      return ConnectivityManager.TYPE_MOBILE;
    }
    else if (mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
      return ConnectivityManager.TYPE_WIFI;
    }
    else {
      return -1;
    }
  }

  /**
   * original 문자열에서 startIndex 에 위치한 글자를 target만큼(길이) 삭제 하고 replacement 문자열로
   * 치환 한다.
   *
   * @param original    원본 문자열
   * @param startIndex  replace를 시작할 위치 index
   * @param endWidth    원본 문자열의 길이
   * @param replacement 교체 할 문자열
   * @return 교체된 문자열. 오류시 원본 문자열을 반환
   */
  public static String replacOnIndex(String original, int startIndex, int endWidth, String replacement) {
    if (original == null) throw new NullPointerException("original 문자열은 null이 될 수 없습니다. ");
    if (replacement == null) throw new NullPointerException("replacement 문자열은 null이 될 수 없습니다.");

    StringBuilder b = new StringBuilder(original);
    try {
      b.delete(startIndex, endWidth);
      b.insert(startIndex, replacement);

    } catch (IndexOutOfBoundsException e) {
      Log.e(TAG, e.getMessage());
      return original;
    }

    return b.toString();
  }

  /**
   * 문자열 str이 URL형식을 갖추고 있는지 여부를 확인 한다.
   *
   * @param str url 형식을 체크할 문자열
   * @return true일 경우 url 형식
   */
  public static boolean isUrlString(String str) {
    if (!TextUtils.isEmpty(str)) {
      return Pattern.compile(ConstantParams.REGEX_VALIDATE_URL).matcher(str).matches();
      //return Patterns.WEB_URL.matcher(str).matches();
    }
    return false;
  }

  /**
   * 문자열 str이 이미지 경로 인지 여부를 확인 한다.
   *
   * @param str 이미지 경로인지 체크할 문자열
   * @return true일 경우 이미지 경로
   */
  public static boolean isImagePath(String str) {
    if (!TextUtils.isEmpty(str)) {
      return (str.endsWith(".png") || str.endsWith(".jpg") || str.endsWith(".jpeg") || str.endsWith(".webp") || str.endsWith(".gif"));
    }
    return false;
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
        Log.e(TAG, nfe.getMessage());
      }
    }
    return false;
  }

  /**
   * 문자열 str에 특수문자가 존재하는지 여부를 판단 한다.
   *
   * @param str 특수문자 존재 여부를 확인할 문자열.
   * @return true일 경우 특수문자가 존재. false일 경우 없음.
   */
  public static boolean hasSpecialCharacter(String str) {
    final String strU = str.toUpperCase();
    Pattern p = Pattern.compile(".*[^ㄱ-ㅎㅏ-ㅣ가-힣-a-zA-Z0-9].*");
    Matcher m = p.matcher(strU);
    return m.matches();
  }

  /**
   * 특정한 문자열 집합에서 정규식으로 표현된 문자열이 존재하는지 확인 하고 난 뒤
   * 해당하는 문자열들의 목록을 반환한다.
   *
   * @param regEx  찾을 대상의 정규 표현식
   * @param target 찾을 대상 문자열
   * @return 찾은 문자열 목록. 없을경우 비어있는 목록.
   */
  public static ArrayList<String> findPatternMatch(String regEx, String target) {
    ArrayList<String> result = new ArrayList<>();
    if (regEx != null && !TextUtils.isEmpty(target)) {
      Matcher m = Pattern.compile(regEx)
                         .matcher(target);
      while (m.find()) {
        result.add(m.group());
      }
    }
    return result;
  }

  /**
   * 특정한 문자열 집합에서 정규식으로 표현된 문자열이 존재하는지 확인 하고 난 뒤
   * 찾은 목록 Matcher object를 반환한다.
   *
   * @param regEx  찾을 대상의 정규 표현식
   * @param target 찾을 대상 문자열
   * @return Matcher object. 없을 경우 비어있는 목록.
   */
  public static Matcher findPatternMatcher(String regEx, String target) {
    Matcher m = null;
    if (regEx != null && !TextUtils.isEmpty(target)) {
      m = Pattern.compile(regEx)
                 .matcher(target);
    }
    return m;
  }

  public static Matcher findPatternMatcherUnicodes(String regEx, String target) {
    Matcher m = null;
    if (regEx != null && !TextUtils.isEmpty(target)) {
      m = Pattern.compile(regEx, Pattern.UNICODE_CASE)
                 .matcher(target);
    }
    return m;
  }

  /**
   * 특정 문자열집합 str에서 문자 c의 갯수를 반환한다. 없을 경우 0을 반환 한다.
   *
   * @param c   카운팅 할 찾을 문자
   * @param str 대상 문자열 집합
   * @return 찾은 갯수
   */
  public static int getCountCharactorInString(char c, String str) {
    int result = 0;
    if (!TextUtils.isEmpty(str)) {
      for (int i = 0; i < str.length(); i++) {
        if (str.charAt(i) == c) {
          result++;
        }
      }
    }
    return result;
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
   * @return empty or Version name string.
   */
  public static String getApplicationVersionNumber(Context context) {
    String versionNumber = "";
    try {
      versionNumber = context.getPackageManager()
                             .getPackageInfo(context.getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException nne) {
      Log.e(TAG, nne.getMessage());
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
      Log.e(TAG, nne.getMessage());
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
  public static boolean isServiceRunning(@NonNull Context context, String serviceNameTag) {
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
      Log.e(TAG, "Service is NULL...");
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
  public static void setHighlightText(TextView textView, @ColorInt int color, String highlightText, String original) {
    if (highlightText != null && textView != null) {
      String hexColor = String.format("#%06X", (0xFFFFFF & color));
      if (!TextUtils.isEmpty(hexColor)) {
        Log.d(TAG, "hexColor is " + hexColor);
        String result = original.replaceAll(highlightText, "<font color='" + hexColor + "'>" + highlightText + "</font>");
        textView.setText(Html.fromHtml(result));
      }
      else {
        Log.w(TAG, "hexColor is Empty.");
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
  public static void setHighlightText(TextView textView, String highlightText, @NonNull String original) {
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
      Log.e(TAG, "String is null.. ");
    }
    return null;
  }

  /**
   * 입력한 'original'변수 문자열에서 'findWord'변수로 받은 단어를 찾아서 설정한 color의 span을 설정 해 준다.
   *
   * @param original 원본 텍스트
   * @param findWord 찾을 단어 문자열
   * @param color    설정할 컬러 값
   * @return span이 설정된 객체나 혹은 null
   */
  public static SpannableStringBuilder findTextAndSetColor(String original, String findWord, @ColorInt int color) {
    if (!TextUtils.isEmpty(original)) {
      if (!TextUtils.isEmpty(findWord)) {
        final Pattern p = Pattern.compile(findWord);
        final Matcher m = p.matcher(original);

        SpannableStringBuilder sb = new SpannableStringBuilder(original);
        //ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        //SpannableString ss = new SpannableString(original);

        while (m.find()) {
          sb.setSpan(
              new ForegroundColorSpan(color), m.start(), m.end(), Spanned.SPAN_INCLUSIVE_INCLUSIVE
          );
          //sb.append(ss);
        }
        return sb;
      }
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
   * Pixel 단위 숫자를 DPI단위 Float형태의 숫자로 변환한다.
   *
   * @param res   Resources.
   * @param pixel 변환대상 Pixel 단위 숫자.
   * @return Float형태의 DPI.
   */
  public static float convertPixelToDpi(@NonNull Resources res, int pixel) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixel, res.getDisplayMetrics());
  }

  /**
   * DPI 단위 숫자를 Pixel 단위 Float형태의 숫자로 변환한다.
   *
   * @param res Resources.
   * @param dpi 변환대상 DPI단위의 숫자.
   * @return Float형태의 pixel 숫자.
   */
  public static float convertDpiToPixel(@NonNull Resources res, int dpi) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, res.getDisplayMetrics());
  }

  /**
   * Pixel 단위 숫자를 DPI단위 Float형태의 숫자로 변환한다.
   *
   * @param res   Resources.
   * @param pixel 변환대상 Pixel 단위 숫자.
   * @return Float형태의 DPI.
   */
  public static float convertPixelToDpi(@NonNull Resources res, float pixel) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixel, res.getDisplayMetrics());
  }

  /**
   * DPI 단위 숫자를 Pixel 단위 Float형태의 숫자로 변환한다.
   *
   * @param res Resources.
   * @param dpi 변환대상 DPI단위의 숫자.
   * @return Float형태의 pixel 숫자.
   */
  public static float convertDpiToPixel(@NonNull Resources res, float dpi) {
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
  public static TransitionDrawable createTransitionDrawable(@NonNull Drawable layer1, @NonNull Drawable layer2) {
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
  public static TransitionDrawable createTransitionDrawable(@NonNull Resources res,
                                                            @DrawableRes int layerResId1,
                                                            @DrawableRes int layerResId2) {
    TransitionDrawable td = new TransitionDrawable(
        new Drawable[]{
            res.getDrawable(layerResId1),
            res.getDrawable(layerResId2)
        });
    td.setCrossFadeEnabled(true);
    return td;
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
  public static PorterDuffColorFilter applyBrightness(@IntRange(from = -100, to = 100) int value) {
    if (value > 0) {
      int target = value * 255 / 100;
      return new PorterDuffColorFilter(Color.argb(target, 255, 255, 255), PorterDuff.Mode.SRC_OVER);
    }
    else {
      int target = value * -1 * 255 / 100;
      return new PorterDuffColorFilter(Color.argb(target, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
    }
  }

  /**
   * 이미지의 밝기를 조정 할 수 있는 Color Matrix Filter를 생성한다.
   * 사용법 : imageView.setColorFilter(brightIt(100));
   *
   * @param value Dark -100 ~ 100 Light, brightness value
   * @return ColorMatrixColorFilter
   * @see ColorMatrixColorFilter
   */
  public static ColorMatrixColorFilter applyBrightnessByMatrixColorFilter(@IntRange(from = -100, to = 100) int value) {
    ColorMatrix cMat = new ColorMatrix();
    cMat.set(new float[]{
        1, 0, 0, 0, value,
        0, 1, 0, 0, value,
        0, 0, 1, 0, value,
        0, 0, 0, 0, 1
    });
    ColorMatrix cMatSet = new ColorMatrix();
    cMatSet.set(cMat);
    return new ColorMatrixColorFilter(cMatSet);
  }

  /**
   * Bitmap 이미지에 밝기 조정을 한다.
   *
   * @param src   밝기조정을 할 원본 Bitmap 이미지.
   * @param value 밝기 조정 값. (-255 ~ 255 Integer)
   * @return 밝기값이 적용된 Bitmap 이미지.
   */
  public static Bitmap setImageBrightness(@NonNull Bitmap src, @IntRange(from = 0, to = 255) int value) {
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
  public static Bitmap convertRGB565toARGB8888(@NonNull Bitmap img) {
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
   * @param view
   * @param skipGarbageCollection
   */
  public static void unbindDrawables(@NonNull View view, boolean skipGarbageCollection) {
    if (view.getBackground() != null) {
      view.getBackground()
          .setCallback(null);
    }
    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        unbindDrawables(((ViewGroup) view).getChildAt(i), true);
      }
      if (!(view instanceof AbsSpinner) && !(view instanceof AbsListView)) {
        ((ViewGroup) view).removeAllViews();
      }
    }

    if (!skipGarbageCollection) System.gc();
  }

  /**
   * get image Uri from imageview drawable or exist cached file.
   *
   * @param iv ImageView instance
   * @return Uri instance
   */
  public static Uri getImageFromImageView(Context context, ImageView iv) {
    if (context == null) {
      throw new NullPointerException("Context is null..");
    }
    Uri imgUri = null;
    if (iv != null) {
      // extract bitmap from imageview drawable.
      Drawable drawable = iv.getDrawable();
      Bitmap bmp = null;
      if (drawable instanceof BitmapDrawable) {
        bmp = ((BitmapDrawable) drawable).getBitmap();
      }
      else {
        return null;
      }
      // store image to default external storage directory.
      try {
        File f = new File(context.getFilesDir(), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos = new FileOutputStream(f);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        Log.d(TAG, "// SHARE // saved temporary image file = " + f.getAbsolutePath());
        imgUri = Uri.fromFile(f);
      } catch (IOException ioe) {
        Log.e(TAG, ioe.getMessage());
      }
    }
    return imgUri;
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
            Log.i("Utils", "recycled bmp...");
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
      Log.e(TAG, "Drawable is NULL...");
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
      Log.e(TAG, "Bitmap is NULL...");
    }
    return is;
  }

  /**
   * 어떤 view를 사용할 수 있게 설정 하고 투명하지 않게 설정 한다.
   *
   * @param view 설정할 뷰
   */
  public static void setEnableView(View view) {
    if (view != null) {
      view.setEnabled(true);
      view.setAlpha(1.0f);
    }
  }

  /**
   * 어떤 view를 사용 할 수없게 설정 하고, 투명하게 설정 한다.
   *
   * @param view 설정 할 뷰
   */
  public static void setDisableView(View view) {
    if (view != null) {
      view.setEnabled(false);
      view.setAlpha(0.5f);
    }
  }

  /**
   * Bitmap이미지에 Blur를 radius만큼 적용한다.
   *
   * @param sentBitmap 원본 Bitmap 이미지
   * @param radius     blur radius value.
   * @return blur효과가 적용된 bitmap 객체.
   */
  public static Bitmap getBlurImage(Bitmap sentBitmap, @IntRange(from = 1) int radius) {
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

    /*
    if (startY >= 0 && startY < h) {
      y = startY;
    } */
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
  public static byte[] getBytesFromBitmap(@NonNull Bitmap bmp) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
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
  public static Bitmap getBlurImg_FastBlur_withRisizing(@NonNull Context context, @DrawableRes int imgResId, int maxResolution, int blurRadius) {
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
  public static Bitmap getBlurImg_FastBlur(@NonNull Context context, @DrawableRes int imgResId, int radius) {
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
  public static Point getDeviceSize(@NonNull Context context) {
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
  public static Point getDisplaySize(@NonNull Context context) {
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
   * 0.0f 에서 duration동안 1.0f로 증가 하는 ValueAnimator 를 만든다.
   *
   * @param duration       animation duration.
   * @param updateListener value animation이 진행 되는 동안의 해야 할 작업을 구현한 listner 인스턴스.
   * @return ValueAnimator
   * @see ValueAnimator
   */
  public static ValueAnimator createDefaultIntValueAnimator(
      int duration,
      ValueAnimator.AnimatorUpdateListener updateListener,
      int start, int end
  ) {
    return createIntValueAnimator(duration, updateListener, null, start, end);
  }

  /**
   * value(integer)값에 의해 start value부터 end value까지 duration동안 증가 혹은 감소하는 ValueAnimator를
   * 만든다.
   * return 받은 ValueAnimator객체의 start()메소드를 이용하여 시작 한다.
   *
   * @param duration         animation duration.
   * @param updateListener   value animation이 진행 되는 동안의 해야 할 작업을 구현한 listener 인스턴스.
   * @param animationAdapter value animation의 상황에 따라 불리어지는 콜백을 구현한 listener 인스턴스.
   * @param values           start value... end value.
   * @return ValueAnimator
   * @see ValueAnimator
   */
  public static ValueAnimator createIntValueAnimator(int duration,
                                                     ValueAnimator.AnimatorUpdateListener updateListener,
                                                     AnimatorListenerAdapter animationAdapter,
                                                     int... values) {
    ValueAnimator valueAnimator = ValueAnimator.ofInt(values);
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
  public static float getDensityValue(@NonNull Context context) {
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
  public static void setTileBackgroundToView(@NonNull Context context, @IdRes int targetViewResId, @DrawableRes int bgResId) {
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
      Log.e(TAG, e.getMessage());
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
  public static File getStorageDirectory(@NonNull Context context, String directoryName) {
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
  public static File getFile(@NonNull Context context, @NonNull String fileName) {
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
    if (views == null || context == null) return;
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
  public static void showSoftKeyboard(@NonNull Context context, View view) {
    if (view == null) return;
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    view.requestFocus();
    imm.showSoftInput(view, 0);
  }

  /**
   * 디바이스의 IP 주소를 얻는다.
   *
   * @return IP 주소 문자열.
   */
  public static String getLocalIPAddress_IPv6() {
    try {
      //Enumerate all the network interfaces
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
           en.hasMoreElements(); ) {
        NetworkInterface intf = en.nextElement();
        // Make a loop on the number of IP addresses related to each Network Interface
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
             enumIpAddr.hasMoreElements(); ) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          //Check if the IP address is not a loopback address, in that case it is
          //the IP address of your mobile device
          if (!inetAddress.isLoopbackAddress()) {
            return inetAddress.getHostAddress();
          }
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * 디바이스의 IP 주소를 얻는다.
   *
   * @return IP 주소 문자열.
   */
  public static String getLocalIPAddress_IPv4() {
    try {
      for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
        NetworkInterface intf = (NetworkInterface) en.nextElement();
        for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
          InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
            String ipAddress = inetAddress.getHostAddress()
                                          .toString();
            //            Log.d("' ~'", "IP address = " + ipAddress);
            return ipAddress;
          }
        }
      }
    } catch (SocketException ex) {
      Log.e("Socket exception", ex.toString());
    }
    return "";
  }

  /**
   * 비트맵 이미지 원본 src를 maxWidth, maxHeight를 감안하여 ratio를 유지한체 리사이징 한다.
   *
   * @param src      원본 비트맵 이미지
   * @param maxValue 원하는 너비
   * @return 리사이징된 비트맵 src 혹은 원본 src.
   */
  public static Bitmap createScaledBitmap(Bitmap src, int maxValue) {
    if (src != null) {
      Log.d(TAG, "// IMGFILE // createScaledBitmap // before resized Width = " + src.getWidth() + ", Height = " + src.getHeight());
      Point p = createScaledBitmapSize(src, maxValue);
      if (p != null) {
        Log.d(TAG, "// IMGFILE // createScaledBitmap // resized Width = " + p.x + ", Height = " + p.y);
        src = Bitmap.createScaledBitmap(src, p.x, p.y, true);
      }
      return src;
    }
    return src;
  }

  /**
   * get Aspaect ratio image resize point
   *
   * @param src      원본 비트맵 이미지
   * @param maxValue 원하는 값
   * @return 리사이징된 비트맵의 width, height가 설정된 Point객체 혹은 null
   */
  public static Point createScaledBitmapSize(Bitmap src, int maxValue) {
    Point p = null;
    if (src != null) {
      if (maxValue > 0) {
        int width = src.getWidth();
        int newWidth = width;
        int height = src.getHeight();
        int newHeight = height;
        float ratio = 0.0f;

        if (width > height) {
          if (maxValue < width) {
            ratio = maxValue / (float) width;
            newHeight = (int) (height * ratio);
            newWidth = maxValue;
          }
        }
        else {
          if (maxValue < height) {
            ratio = maxValue / (float) height;
            newWidth = (int) (width * ratio);
            newHeight = maxValue;
          }
        }
        p = new Point(newWidth, newHeight);
      }
    }
    return p;
  }

  /**
   * 사진 파일을 저장 한다.
   *
   * @param context Context instance.
   * @param bitmap  저장할 비트맵 이미지
   * @return 이미지를 성공적으로 저장했는지에 대한 여부.
   */
  public static boolean savePhoto(Context context, Bitmap bitmap) {
    boolean imageSaved = false;

    // 외부저장소에 쓰기가 가능 한지 체크
    if (!isExternalStorageWritable()) {
      Log.e(TAG, "External storage is not available for write.");
      return imageSaved;
    }

    if (context != null && bitmap != null && !bitmap.isRecycled()) {
      // resize bitmap.
      bitmap = createScaledBitmap(bitmap, 720);

      // 디렉터리 생성 및 체크
      File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/VIKL/");
      if (!dir.mkdir()) {
        Log.w(TAG, dir.getName() + " is not created. ");
      }
      if (dir.exists()) {
        Log.w(TAG, dir.getName() + " is exists. ");
      }

      String fileName = "VIKL_IMG_" + (new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()))
          .format(Calendar.getInstance()
                          .getTime());
      FileOutputStream fos = null;
      File imageFile = new File(dir, fileName + ".jpg");

      try {
        fos = new FileOutputStream(imageFile);
        imageSaved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        fos.flush();
        fos.close();

      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }

      if (imageSaved) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put("_data", imageFile.getAbsolutePath());

        context.getContentResolver()
               .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      }
    }
    return imageSaved;
  }

  /**
   * 사진 파일을 저장 한다.
   *
   * @param context Context instance.
   * @param bitmap  저장할 비트맵 이미지
   * @return 이미지를 성공적으로 저장했는지에 대한 여부.
   */
  @Nullable
  public static String savePhotoCache(@NonNull Context context, Bitmap bitmap, int maxValue, boolean isEnableResize) {
    boolean imageSaved = false;

    File imageFile = null;

    if (bitmap != null) {
      // resize bitmap.
      if (isEnableResize) {
        // resize Bitmap
        if (maxValue > 0) {
          bitmap = createScaledBitmap(bitmap, maxValue);
        }
      }

      // 디렉터리 생성 및 체크
      FileOutputStream fos = null;
      imageFile = new File(context.getCacheDir(), "IMG_CROPPED_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

      try {
        fos = new FileOutputStream(imageFile);
        imageSaved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        fos.flush();
        fos.close();

      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }
    }
    return imageSaved ? imageFile.getAbsolutePath() : null;
  }

  /**
   * 사진 파일을 저장 한다.
   *
   * @param context       Context instance.
   * @param imageFilePath photo image cache file path.
   */
  public static void savePhoto(Context context, String imageFilePath) {
    if (imageFilePath != null && context != null) {
      Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      File f = new File(imageFilePath);
      Uri contentUri = Uri.fromFile(f);
      mediaScanIntent.setData(contentUri);
      context.sendBroadcast(mediaScanIntent);
    }
  }

  /**
   * 외부저장소가 저장(Write) 가능 한 상태인지 여부를 확인한다.
   *
   * @return true 일 경우 저장가능.
   */
  public static boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state);
  }

  /**
   * Map(Key, Value pair set)으로 구성된 Request parameter 집합체를 Request post body에 실을 수
   * 있게 String 으로 만들어 준다.
   *
   * @param requestParams Map으로 구성된 Request parameters.
   * @return 만들어진 query String.
   */
  public static String createRequestQuery(@NonNull Map<String, String> requestParams) {
    StringBuilder sb = new StringBuilder();
    for (HashMap.Entry<String, String> e : requestParams.entrySet()) {
      if (sb.length() > 0) {
        sb.append('&');
      }
      try {
        sb.append(URLEncoder.encode(e.getKey(), "UTF-8"))
          .append('=')
          .append(URLEncoder.encode(e.getValue(), "UTF-8"));
      } catch (UnsupportedEncodingException uee) {
        Log.e(TAG, uee.getMessage());
      }
    }
    return sb.toString();
  }

  /**
   * 입력받은 key와 value를 바탕으로 Request post body에 실을 String request parameter를
   * 만들어 준다.
   *
   * @param key1   parameter key 1
   * @param value1 parameter value 1
   * @param key2   parameter key 2
   * @param value2 parameter value 2
   * @return query String.
   */
  public static String createRequestQuery(String key1, String value1, String key2, String value2) {
    return createRequestQuery(key1, value1) + "&" + createRequestQuery(key2, value2);
  }

  /**
   * 입력받은 key와 value를 바탕으로 Request post body에 실을 String request parameter를
   * 만들어 준다.
   *
   * @param key   Parameter key
   * @param value Parameter value
   * @return 만들어진 query String.
   */
  public static String createRequestQuery(@NonNull String key, String value) {
    return key + "=" + value;
  }

  /**
   * 입력받은 파일 경로인 path에 파일 이름을 얻어서 반환 한다.
   *
   * @param path 파일 이름을 얻을 대상의 path 문자열.
   * @return 파일 이름 문자열. 없거나 문자열이 형식에 맞지 않은 경우 empty string을 반환.
   */
  public static String getFileNameFromPath(String path) {
    if (path != null && !TextUtils.isEmpty(path) && path.contains("/")) {
      return path.substring(path.lastIndexOf("/") + 1);
    }
    return "";
  }

  /**
   * 입력한 두개의 문자열이 동일한 문자열인지 검사한다
   *
   * @param str  검사할 문자열 1
   * @param str2 검사할 문자열 2
   * @return 동일한 문자열 일 경우 true, 아니면 false
   */
  public static boolean isSameString(String str, String str2) {
    if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
      return str.equals(str2);
    }
    return false;
  }

  /**
   * str을 URLDecode 하고 공백문자를 일괄적으로 바꿔 준다.
   *
   * @param str 원본 문자열
   * @return URLDecode로 decode 한 문자열
   */
  public static String urlDecode(String str) {
    String buf = str;
    try {
      if (!TextUtils.isEmpty(str)) {
        Log.d(TAG, "urlDecode() // BEFORE // " + str);

        buf = URLDecoder.decode(str, ConstantParams.DEFAULT_CHAR_SET);
        buf = buf.replaceAll("\\+", "%20");   // to space character
        buf = buf.replaceAll("%", "\\\\%");

        Log.d(TAG, "urlDecode() // AFTER // \n" + buf);
      }
    } catch (UnsupportedEncodingException uee) {
      Log.e(TAG, uee.getMessage());
    }
    return buf;
  }

  /**
   * str을 URLEncode 한다
   *
   * @param str 원본 문자열
   * @return URNEncode로 encode 한 문자열
   */
  public static String urlEncode(String str) {
    String buf = str;
    try {
      if (!TextUtils.isEmpty(str)) {
        buf = URLEncoder.encode(str, ConstantParams.DEFAULT_CHAR_SET);
        //buf = buf.replaceAll("%", "%25");
      }
    } catch (UnsupportedEncodingException uee) {
      Log.e(TAG, uee.getMessage());
    }
    return buf;
  }

  /**
   * 레이아웃에서 EditText를 상속한 뷰를 제외한 모든 뷰에 키보드를 감추는 기능의 터치 이벤트를 넣는다.
   *
   * @param context Context instance
   * @param v       parent view group
   */
  public static void setOnHideKeyboardTouchUI(@NonNull final Context context, View v) {
    if (v != null) {
      if (!(v instanceof EditText)) {
        v.setOnTouchListener(
            new View.OnTouchListener() {
              @Override
              public boolean onTouch(View et, MotionEvent event) {
                hideSoftKeyboard(context, et);
                return false;
              }
            }
        );
      }
      if (v instanceof ViewGroup) {
        for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
          View childView = ((ViewGroup) v).getChildAt(i);
          setOnHideKeyboardTouchUI(context, childView);
        }
      }
    }
  }

  /**
   * EditText에서 현재 커서의 위치를 EditText 텍스트 상의 Line number를 반환 한다.
   *
   * @param et EditText instance
   * @return line integer number
   */
  public static int getCurrentCursorLineOfEditText(EditText et) {
    if (et != null) {
      final int selectionStart = Selection.getSelectionStart(et.getText());
      Layout layout = et.getLayout();
      if (!(selectionStart == -1)) {
        return layout.getLineForOffset(selectionStart);
      }
    }
    return 0;
  }

  public static File getOutputMediaFile(int type) {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.

    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), "vikl");
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.w("TEMPORARY_FILE", "failed to create directory");
        return null;
      }
    }

    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    File mediaFile;
    if (type == ConstantParams.MEDIA_TYPE_IMAGE) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }
    else if (type == ConstantParams.MEDIA_TYPE_VIDEO) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
    }
    else {
      return null;
    }

    return mediaFile;
  }

  /**
   * Uri로부터 file path를 String형태로 얻는다.
   *
   * @param context Context instance
   * @param uri     Uri object
   * @return String path
   */
  public static String getPathFromURI(Context context, Uri uri) {
    String path = null;
    if (context != null) {
      if (uri != null) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
          cursor.moveToNext();
          path = cursor.getString(cursor.getColumnIndex("_data"));
          cursor.close();
        }
        else {
          Log.e(TAG, "Cursor is Null.. ");
        }
      }
      else {
        Log.e(TAG, "Uri is Null..");
      }
    }
    else {
      Log.e(TAG, "Context is Null..");
    }
    return path;
  }

  public static Bitmap getImageExifOrientation(Bitmap source, String imageFilePath) {
    if (source != null && !TextUtils.isEmpty(imageFilePath)) {
      // Read EXIF Data
      ExifInterface exif = null;
      try {
        exif = new ExifInterface(imageFilePath);
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (exif != null) {
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        Log.d(TAG, "// EXIF-interface // orientString = " + orientString);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle);

        // Return result
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
      }
    }
    return null;
  }

  /**
   * 파일 하나를 삭제 한다.
   *
   * @param file 삭제할 파일의 경로 혹은 파일 객체
   * @param <T>  String or File instance
   * @return true일 경우 파일 삭제가 성공적으로 수행 됨.
   */
  public static <T> boolean fileDelete(T file) {
    if (file != null) {
      File targetFile = null;
      if (file instanceof String) {
        targetFile = new File((String) file);
      }
      else if (file instanceof File) {
        targetFile = (File) file;
      }
      else {
        Log.w(TAG, "File이나 String파일 절대 경로만 가능 합니다.");
        return false;
      }
      return targetFile.delete();
    }
    return false;
  }

  /**
   * 파일 하나를 이동 한다. AsyncTask등에 태워서 사용 할 것
   *
   * @param fromPath 이동할 파일의 절대 경로
   * @param target   이동할 경로 혹은 파일 객체
   * @param <T>      String or File instance
   * @return true일 경우 이동이 성공적으로 수행 됨.
   */
  public static <T> boolean fileMove(String fromPath, T target) {
    if (!TextUtils.isEmpty(fromPath)) {
      if (target != null) {
        File targetFile = null;
        if (target instanceof String) {
          targetFile = new File((String) target);
        }
        else if (target instanceof File) {
          targetFile = (File) target;
        }
        else {
          // Uri는 getPathFromUri()메소드를 활용 할 것.
          Log.w(TAG, "File이나 String파일 절대 경로만 가능 합니다.");
          return false;
        }

        File fromFile = new File(fromPath);
        if (fromFile.exists()) {
          if (!fromFile.renameTo(targetFile)) {
            // File의 renameTo는 파일을 정상적으로 이동시키지 못하는 경우가 있다.
            // 또한 이에 대한 예외조차 없다.
            try {
              // 기존 파일을 복사 하고 난 뒤 원본을 삭제 한다.
              FileInputStream fis = new FileInputStream(fromFile);
              FileOutputStream fos = new FileOutputStream(targetFile);

              byte[] buf = new byte[1024];
              int read = 0;
              while ((read = fis.read(buf, 0, buf.length)) != -1) {
                fos.write(buf, 0, read);
              }
              fis.close();
              fos.close();

              return fromFile.delete();

            } catch (FileNotFoundException fnfe) {
              Log.e(TAG, fnfe.getMessage());
              return false;
            } catch (IOException ioe) {
              Log.e(TAG, ioe.getMessage());
              return false;
            }
          }
          else {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * 파일 하나를 복사 한다. AsyncTask등에 태워서 처리 할 것
   *
   * @param from   복사할 파일의 절대 경로 혹은 객체
   * @param target 저장할 파일의 경로 혹은 객체
   * @param <T>    String or File instance
   * @return true일 경우 파일 복사가 성공적으로 수행 됨.
   */
  public static <T> boolean fileCopy(T from, T target) {
    if (from != null) {
      File fromFile = null;
      if (from instanceof String) {
        fromFile = new File((String) from);
      }
      else if (from instanceof File) {
        fromFile = (File) from;
      }
      else {
        Log.w(TAG, "File이나 String파일 절대 경로만 가능 합니다.");
        return false;
      }

      if (fromFile.exists()) {
        File targetFile = null;
        if (target != null) {
          if (target instanceof String) {
            targetFile = new File((String) target);
          }
          else if (target instanceof File) {
            targetFile = (File) target;
          }
          else {
            Log.w(TAG, "File이나 String파일 절대 경로만 가능 합니다.");
            return false;
          }

          try {
            FileInputStream fis = new FileInputStream(fromFile);
            FileOutputStream fos = new FileOutputStream(targetFile);
            int readCount = 0;
            byte[] buffer = new byte[1024];
            while ((readCount = fis.read(buffer, 0, 1024)) != -1) {
              fos.write(buffer, 0, readCount);
            }
            fos.close();
            fis.close();

          } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
            return false;
          }

          if (targetFile.length() > 0) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * image uri로부터 이미지의 orientation을 얻는다.
   */
  public static int getExifOrientation(@NonNull String path) {
    try {
      ExifInterface exifInterface = new ExifInterface(path);
      return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

    } catch (IOException ioe) {
      Log.e(TAG, ioe.getMessage());
    }
    return -1;
  }

  /**
   * GPS, Network Provider를 사용 할 수 있는지 여부를 얻는다.
   *
   * @param context Context instance
   * @return true일 경우 둘 중 하나 이상의 Provider를 사용 할 수 있다.
   */
  public static boolean isEnableLocationProviders(Context context) {
    if (context == null) throw new NullPointerException("Context is Null..");
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    if (locationManager != null) {
      if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
          || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        return true;
      }
    }
    return false;
  }

  public static String getKeyHash(@NonNull Context context) {
    String hashString = null;
    try {
      PackageInfo info = context.getPackageManager().getPackageInfo(
          context.getPackageName(),
          PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        hashString = Base64.encodeToString(md.digest(), Base64.DEFAULT);
        Log.d(TAG, "KeyHash : " + hashString);
      }
    } catch (PackageManager.NameNotFoundException e) {
      Log.e(TAG, e.getMessage());
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, e.getMessage());
    }
    return hashString;
  }

  /**
   * StatusBar의 높이를 얻는다.
   *
   * @param context Context instance
   * @return pixel size of Statusbar height or 0
   */
  public static int getStatusBarHeight(Context context) {
    if (context != null) {
      int statusBarHeightResourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (statusBarHeightResourceId > 0) {
        return context.getResources().getDimensionPixelSize(statusBarHeightResourceId);
      }
    }
    return 0;
  }

  /**
   * 하단 Navigation bar의 높이를 얻는다.
   *
   * @param context Context instance
   * @return pixel size of bottom of Navigation bar height or 0
   */
  public static int getNavigationBarHeight(Context context) {
    if (context != null) {
      int statusBarHeightResourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
      if (statusBarHeightResourceId > 0) {
        return context.getResources().getDimensionPixelSize(statusBarHeightResourceId);
      }
    }
    return 0;
  }

}
