package com.lkyear.lllauncher.Launcher;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.AESCrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * 用户等级类
 * @author lkyear
 * @time 2017-07-11
 */

public class Level {

    /**
     * 声明上下文
     */
    private Context mContext;

    /**
     * 声明偏好
     */
    private SharedPreferences defaultSharedPreference;

    /**
     * 声明偏好编辑器
     */
    private SharedPreferences.Editor defaultSharedPreferenceEditor;

    /**
     * 声明等级数据AES加密密钥1
     */
    private static String DECRYPT_PWD = "replace_by_your_key" + Build.SERIAL;

    /**
     * 声明等级数据AES加密密钥2
     */
    private static String CRYPT_PWD = "replace_by_your_key";

    /**
     * 声明等级数据AES加密密钥3
     */
    private static String DECRYPT_PASSWORD_USING = "replace_by_your_key";

    /**
     * 声明等级数据AES加密密钥4
     */
    private static String BUILD_CRYPT_USING = "replace_by_your_key";

    /**
     * 声明当前等级点数最大值
     */
    public static int LEVEL_MAX_VALUE;

    /**
     * 声明用户点数
     */
    public static int LEVEL_USER_VALUE;

    /**
     * 声明用户等级名称
     */
    public static String LEVEL_USER_STRING;

    /**
     * 声明 初来乍到 成就标识
     */
    public static final int ACHIEVEMENT_BEGINNING = 0;

    /**
     * 声明 修罗场 成就标识
     */
    public static final int ACHIEVEMENT_SHURA_FIELD = 1;

    /**
     * 声明 超自然研究会 成就标识
     */
    public static final int ACHIEVEMENT_SUPERNATURAL = 2;

    /**
     * 声明 成为偶像 成就标识
     */
    public static final int ACHIEVEMENT_TOBE_IDOL = 3;

    /**
     * 声明 欢迎回来！成就标识
     */
    public static final int ACHIEVEMENT_WELCOME_BACK = 4;

    /**
     * 声明 技术宅拯救世界 成就标识
     */
    public static final int ACHIEVEMENT_USE_CONSOLE = 5;

    /**
     * 构造函数
     * @param context 上下文
     */
    public Level(Context context) {
        mContext = context;
        defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
        defaultSharedPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        setPublicInfo();
    }

    /**
     * 定义增加点数方法
     * 该方法会增加用户点数并且使用密钥加密保存值
     * @param value 点数
     */
    public void addPoint(int value) {
        int userVal = getUserValue() + value;
        if (userVal != 19200) {
            String prefUserVal = getEncryptValue(userVal);
            defaultSharedPreferenceEditor.putString("pref_level_point", prefUserVal).commit();
            setPublicInfo();
        }
    }

    /**
     * 定义设定点数方法
     * 该方法会覆盖用户点数并且使用密钥加密保存值
     * @param userVal 点数
     */
    public void setPoint(int userVal) {
        if (userVal != 19200) {
            String prefUserVal = getEncryptValue(userVal);
            defaultSharedPreferenceEditor.putString("pref_level_point", prefUserVal).commit();
            setPublicInfo();
        }
    }

    /**
     * 定义获取用户点数方法
     * 获取偏好值并解码，然后返回真实点数，如果出错则返回-1
     * @return 用户点数
     */
    private int getUserValue() {
        String userValue = defaultSharedPreference.getString("pref_level_point", "");
        if (userValue.equals("")) {
            return 0;
        }
        try {
            return Integer.parseInt(AESCrypt.decrypt(DECRYPT_PWD, userValue));
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 定义加密方法
     * 如果出错则返回Error
     * @param value 需要加密的值
     * @return 经过密钥加密过后的值
     */
    private String getEncryptValue(int value) {
        String returnString = value + "";
        try {
            return AESCrypt.encrypt(DECRYPT_PWD, returnString);
        } catch (Exception e) {
            return "Error";
        }
    }

    /**
     * 定义设置公共用户等级信息方法
     */
    private void setPublicInfo() {
        LEVEL_USER_VALUE = getUserValue();
        if (LEVEL_USER_VALUE == -1) {
            LEVEL_MAX_VALUE = 299;
            LEVEL_USER_STRING = "Lv.-1 故障";
        } else if (LEVEL_USER_VALUE < 300) {
            LEVEL_MAX_VALUE = 299;
            LEVEL_USER_STRING = "Lv.0 无能力者";
        } else if (LEVEL_USER_VALUE < 600) {
            LEVEL_MAX_VALUE = 599;
            LEVEL_USER_STRING = "Lv.1 低能力者";
        } else if (LEVEL_USER_VALUE < 1200) {
            LEVEL_MAX_VALUE = 1199;
            LEVEL_USER_STRING = "Lv.2 异能力者";
        } else if (LEVEL_USER_VALUE < 2400) {
            LEVEL_MAX_VALUE = 2399;
            LEVEL_USER_STRING = "Lv.3 强能力者";
        } else if (LEVEL_USER_VALUE < 4800) {
            LEVEL_MAX_VALUE = 4799;
            LEVEL_USER_STRING = "Lv.4 大能力者";
        } else if (LEVEL_USER_VALUE < 9600) {
            LEVEL_MAX_VALUE = 9599;
            LEVEL_USER_STRING = "Lv.5 超能力者";
        } else if (LEVEL_USER_VALUE < 19200) {
            LEVEL_MAX_VALUE = 19199;
            LEVEL_USER_STRING = "Lv.6 绝对能力者";
        } else {
            LEVEL_MAX_VALUE = 19200;
            LEVEL_USER_STRING = "Lv.7 真正的Fans";
        }
    }

    /**
     * 定义设定成就方法
     * @param type 成就标识
     * @param add 是否解锁成就
     */
    public void setAchievement(int type, Boolean add) {
        switch (type) {
            case ACHIEVEMENT_BEGINNING:
                writeAchievement("初来乍到", "pref_achievement_begining", true, add);
                break;
            case ACHIEVEMENT_SHURA_FIELD:
                writeAchievement("修罗场", "pref_achievement_shura_field", true, add);
                break;
            case ACHIEVEMENT_SUPERNATURAL:
                writeAchievement("超自然研究会", "pref_achievement_supernatural", true, add);
                break;
            case ACHIEVEMENT_TOBE_IDOL:
                writeAchievement("成为偶像", "pref_achievement_tobe_idol", true, add);
                break;
            case ACHIEVEMENT_USE_CONSOLE:
                writeAchievement("技术宅拯救世界", "pref_achievement_use_console", true, add);
                break;
            case ACHIEVEMENT_WELCOME_BACK:
                writeAchievement("欢迎回来!", "pref_achievement_welcome_back", true, add);
                break;
        }
    }

    /**
     * 定义否认设定成就方法
     * @param type 成就标识
     * @param add 是否解锁成就
     */
    public void setExtendsAchievement(int type, Boolean add) {
        switch (type) {
            case ACHIEVEMENT_BEGINNING:
                writeExtendsAchievement("pref_achievement_begining", add);
                break;
            case ACHIEVEMENT_SHURA_FIELD:
                writeExtendsAchievement("pref_achievement_shura_field", add);
                break;
            case ACHIEVEMENT_SUPERNATURAL:
                writeExtendsAchievement("pref_achievement_supernatural", add);
                break;
            case ACHIEVEMENT_TOBE_IDOL:
                writeExtendsAchievement("pref_achievement_tobe_idol", add);
                break;
            case ACHIEVEMENT_USE_CONSOLE:
                writeExtendsAchievement("pref_achievement_use_console", add);
                break;
            case ACHIEVEMENT_WELCOME_BACK:
                writeExtendsAchievement("pref_achievement_welcome_back", add);
                break;
        }
    }

    /**
     * 定义写成就偏好方法
     * 自动判断成就是否已经解锁，如已解锁则自动跳过，反之自动增加点数并发送解锁成就通知
     * @param achievementName 成就名称
     * @param preferenceName 偏好名称
     * @param preferenceValue 偏好值
     */
    private void writeAchievement(String achievementName, String preferenceName, Boolean preferenceValue, Boolean addPoint) {
        if (!defaultSharedPreference.getBoolean(preferenceName, false)) {
            defaultSharedPreferenceEditor.putBoolean(preferenceName, preferenceValue).commit();
            if (addPoint) {
                addPoint(100);
                sendNotification("LoveLive桌面成就解锁！", "LoveLive桌面成就解锁", "完成 " + achievementName + " 成就，+100点！");
            }
        }
    }

    /**
     * 定义写成就偏好方法
     * @param preferenceName 偏好名称
     * @param preferenceValue 偏好值
     */
    private void writeExtendsAchievement(String preferenceName, Boolean preferenceValue) {
        defaultSharedPreferenceEditor.putBoolean(preferenceName, preferenceValue).commit();
    }


    /**
     * 定义获取成就方法
     * @param type 成就标识
     * @return 成就是否解锁
     */
    public Boolean getAchievement(int type) {
        switch (type) {
            case ACHIEVEMENT_BEGINNING:
                return defaultSharedPreference.getBoolean("pref_achievement_begining", false);
            case ACHIEVEMENT_SHURA_FIELD:
                return defaultSharedPreference.getBoolean("pref_achievement_shura_field", false);
            case ACHIEVEMENT_SUPERNATURAL:
                return defaultSharedPreference.getBoolean("pref_achievement_supernatural", false);
            case ACHIEVEMENT_TOBE_IDOL:
                return defaultSharedPreference.getBoolean("pref_achievement_tobe_idol", false);
            case ACHIEVEMENT_USE_CONSOLE:
                return defaultSharedPreference.getBoolean("pref_achievement_use_console", false);
            case ACHIEVEMENT_WELCOME_BACK:
                return defaultSharedPreference.getBoolean("pref_achievement_welcome_back", false);
            default:
                return false;
        }
    }

    /**
     * 定义获取继承码方法
     * @param extendPassword 集成密码
     * @return 继承码
     */
    public String getExtendCode(String extendPassword) {
        JSONObject rootObject = new JSONObject();
        try {
            rootObject.put("point", getUserValue());
            rootObject.put("sn", Build.SERIAL);
            rootObject.put("pwd", extendPassword);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            rootObject.put("exp", String.valueOf(calendar.get(Calendar.YEAR)) +
                    String.valueOf(calendar.get(Calendar.MONTH) + 1) +
                    String.valueOf(calendar.get(Calendar.DATE)));
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < 6; i++) {
                JSONObject arrayObject = new JSONObject();
                arrayObject.put("cd", i);
                arrayObject.put("val", getAchievement(i));
                jsonArray.put(arrayObject);
            }
            rootObject.put("am", jsonArray);
            String reVal = AESCrypt.encrypt(CRYPT_PWD, rootObject.toString());
            setPoint(0);
            for (int i = 0; i < 6; i++) {
                setExtendsAchievement(i, false);
            }
            return reVal;
        } catch (JSONException joe) {
            joe.printStackTrace();
            return "ERROR";
        } catch (GeneralSecurityException gse) {
            gse.printStackTrace();
            return "ERROR";
        }
    }

    /**
     * 定义获取继承码方法
     * @param extendPassword 继承密码
     * @return 继承码
     */
    public String getExtendCodeWithSn(String extendPassword, String snWords) {
        JSONObject rootObject = new JSONObject();
        try {
            rootObject.put("point", getUserValue());
            rootObject.put("sn", AESCrypt.decrypt(BUILD_CRYPT_USING, snWords));
            rootObject.put("pwd", extendPassword);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            rootObject.put("exp", String.valueOf(calendar.get(Calendar.YEAR)) +
                    String.valueOf(calendar.get(Calendar.MONTH) + 1) +
                    String.valueOf(calendar.get(Calendar.DATE)));
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < 6; i++) {
                JSONObject arrayObject = new JSONObject();
                arrayObject.put("cd", i);
                arrayObject.put("val", getAchievement(i));
                jsonArray.put(arrayObject);
            }
            rootObject.put("am", jsonArray);
            String reVal = AESCrypt.encrypt(CRYPT_PWD, rootObject.toString());
            setPoint(0);
            for (int i = 0; i < 6; i++) {
                setExtendsAchievement(i, false);
            }
            return reVal;
        } catch (JSONException joe) {
            joe.printStackTrace();
            return "ERROR";
        } catch (GeneralSecurityException gse) {
            gse.printStackTrace();
            return "ERROR";
        }
    }

    /**
     * 定义设定继承码方法
     * @param extendCode 继承码
     */
    public Boolean setExtend(String extendCode, String pwd) {
        try {
            String json = AESCrypt.decrypt(CRYPT_PWD, extendCode);
            final JSONObject extObject = new JSONObject(json);
            final String extendPassword = extObject.getString("pwd");
            URL url=new URL("http://www.baidu.com");
            URLConnection uc=url.openConnection();
            uc.connect();
            long ld = uc.getDate();
            Date datee =new Date(ld);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(datee);
            int date = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)) +
                    String.valueOf(calendar.get(Calendar.MONTH) + 1) +
                    String.valueOf(calendar.get(Calendar.DATE)));
            int expDate = Integer.parseInt(extObject.getString("exp"));
            if (date > expDate) {
                return false;
            }
            String snVal = extObject.getString("sn");
            if (!Build.SERIAL.equals(snVal)) {
                return false;
            }
            if (!pwd.equals(extendPassword)) {
                return false;
            } else {
                try {
                    setPoint(extObject.getInt("point"));
                    JSONArray jsonArray = extObject.getJSONArray("am");
                    for (int i = 0; i < 6; i++) {
                        setExtendsAchievement(jsonArray.getJSONObject(i).getInt("cd"), jsonArray.getJSONObject(i).getBoolean("val"));
                    }
                    return true;
                } catch (JSONException joe) {
                    joe.printStackTrace();
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getThisDeviceBuild() {
        try {
            return AESCrypt.encrypt(BUILD_CRYPT_USING, Build.SERIAL.toString());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    /***
     * 定义发送通知方法
     * @param ticker 通知栏初始提示信息
     * @param title 标题
     * @param content 内容
     */
    private void sendNotification(String ticker, String title, String content) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        Notification.Builder nBuilder = new Notification.Builder(mContext);
        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setTicker(ticker);
        nBuilder.setContentTitle(title);
        nBuilder.setContentText(content);
        nBuilder.setSmallIcon(R.drawable.res_about_icon);
        Notification notification = nBuilder.build();
        notificationManager.notify(11,notification);
    }

}
