package com.lkyear.lllauncher.Torch;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.lkyear.lllauncher.R;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private Boolean turnedOn = false;
    private ImageView SwitchButton, Menu;
    private int lastChar = 0, count = 0, first = 0;;
    private RelativeLayout mRelativeLayout;
    private ObjectAnimator objectAnimator;
    private int userBrightness;
    final int[] colors = new int[] { 0xFF000000, 0xFFFED2A3, 0xFFBFBDFE, 0xFFD8E0D5,
            0xFFC9F795, 0xFF96FAC8, 0xFFFEB6B9, 0xFFBBDDFF, 0xFFDAB9FE,
            0xFFFEB6DC };
    static Camera mCameraActivity;
    Camera.Parameters params;
    SurfaceView preview;
    SurfaceHolder mHolder;
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;
    private SharedPreferences defaultPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_torch_main);
        powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
        defaultPreference = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        init();
    }

    private void init() {
        preview = (SurfaceView) findViewById(R.id.preview);
        mHolder = preview.getHolder();
        try {
            setmCameraActivity(Camera.open());
        } catch (Exception e) {
            e.printStackTrace();
        }
        userBrightness = getScreenBrightness();
        SwitchButton = (ImageView) findViewById(R.id.imageView);
        Menu = (ImageView) findViewById(R.id.info);
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.launcher_menu_torch, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_use_screen_light:
                                if (turnedOn) {
                                    processOffClick();
                                    turnedOn = false;
                                    if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                                        cleanNotification();
                                    }
                                    if (!defaultPreference.getBoolean("pref_low_visual", false)){
                                        objectAnimator.cancel();
                                    }
                                    if (!defaultPreference.getBoolean("pref_torch_not_down_brightness", false)) {
                                        setBrightness(userBrightness);
                                    }
                                }
                                startActivity(new Intent(MainActivity.this, OnlyUseScreenLight.class));
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        SwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!turnedOn) {
                        if (first == 0) {
                            processOnClick();
                            turnedOn = true;
                            if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                                showNotification();
                            }
                            if (!defaultPreference.getBoolean("pref_low_visual", false)){
                                BackgroundColorTransformer();
                            }
                            first++;
                            if (!defaultPreference.getBoolean("pref_torch_not_down_brightness", false)) {
                                setBrightness(15);
                            }
                        } else {
                            if (!defaultPreference.getBoolean("pref_low_visual", false)) {
                                if (!objectAnimator.isRunning()) {
                                    processOnClick();
                                    turnedOn = true;
                                    BackgroundColorTransformer();
                                    if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                                        showNotification();
                                    }
                                    if (!defaultPreference.getBoolean("pref_torch_not_down_brightness", false)) {
                                        setBrightness(15);
                                    }
                                }
                            } else {
                                processOnClick();
                                turnedOn = true;
                                if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                                    showNotification();
                                }
                                if (!defaultPreference.getBoolean("pref_torch_not_down_brightness", false)) {
                                    setBrightness(15);
                                }
                            }
                        }
                    } else if (turnedOn) {
                        processOffClick();
                        turnedOn = false;
                        if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                            cleanNotification();
                        }
                        if (!defaultPreference.getBoolean("pref_low_visual", false)){
                            objectAnimator.cancel();
                        }
                        if (!defaultPreference.getBoolean("pref_torch_not_down_brightness", false)) {
                            setBrightness(userBrightness);
                        }
                    }
                } catch (Exception e) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("启动失败，因为" + e.toString())
                            .setNegativeButton(android.R.string.ok,null)
                            .show();
                }
            }
        });
        if (defaultPreference.getBoolean("pref_torch_default_open_light", false)) {
            if (!getIntent().getBooleanExtra("BackFromPreference", false)) {
                if (first == 0) {
                    processOnClick();
                    turnedOn = true;
                    if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                        showNotification();
                    }
                    if (!defaultPreference.getBoolean("pref_low_visual", false)){
                        BackgroundColorTransformer();
                    }
                    first++;
                    if (!defaultPreference.getBoolean("pref_torch_not_down_brightness", false)) {
                        setBrightness(15);
                    }
                } else {
                    if (!objectAnimator.isRunning()) {
                        processOnClick();
                        turnedOn = true;
                        if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                            showNotification();
                        }
                        if (!defaultPreference.getBoolean("pref_low_visual", false)){
                            BackgroundColorTransformer();
                        }
                        if (!defaultPreference.getBoolean("pref_torch_not_down_brightness", false)) {
                            setBrightness(15);
                        }
                    }
                }
            }
        }
    }

    private void showNotification() {
        if (isServiceWork(MainActivity.this, "com.lkyear.lllauncher.Torch.NotificationService")) {
            cleanNotification();
        }
        startService(new Intent(MainActivity.this, NotificationService.class));
    }

    private void cleanNotification() {
        stopService(new Intent(MainActivity.this, NotificationService.class));
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext 上下文
     * @param serviceName 包名+服务的类名
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    private void BackgroundColorTransformer() {
        if (turnedOn) {
            Random random = new Random();
            int charNum = random.nextInt(8) + 1;
            rotateyAnimRun(mRelativeLayout, colors[lastChar], colors[charNum]);
            lastChar = charNum;
        }
    }

    private void rotateyAnimRun(View view, int lastColor, int nextColor) {
        objectAnimator = ObjectAnimator.ofInt(view,"backgroundColor", lastColor, nextColor);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.setDuration(2000);
        objectAnimator.start();
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                BackgroundColorTransformer();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                rotateyAnimRun(mRelativeLayout, colors[lastChar], colors[0]);
                lastChar = 0;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void setBrightness(int brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        getWindow().setAttributes(lp);
    }

    private int getScreenBrightness(){
        int screenBrightness=255;
        try{
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Exception localException){

        }
        return screenBrightness;
    }

    // This is a design decision. When the user hits the home button, leave the
    // light on if it is on. We go through the processOffClick if the light is
    // off just to make sure everything is in sync.
    @Override
    protected void onPause() {
        super.onPause();
        if (!TorchGlobals.getIsFlashOn()) {
            if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                cleanNotification();
            }
            processOffClick();
        }
        wakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            processOffClick();
            turnedOn = false;
            if (defaultPreference.getBoolean("pref_torch_notification", false)) {
                cleanNotification();
            }
            if (!defaultPreference.getBoolean("pref_torch_not_down_brightness", false)) {
                setBrightness(userBrightness);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This is a design decision. When the user hits the back button, turn the
    // light off.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        processOffClick();
    }

    // Any time the widget or the button in the app is pressed to turn the LED
    // on we process this off click. On method that is deprecated is needed for
    // earlier than Android 3.0 devices.
    @SuppressWarnings("deprecation")
    private void processOnClick() {
        if (getmCameraActivity() == null) {
            try {
                mHolder.addCallback(this);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }
                setmCameraActivity(Camera.open());
                try {
                    if (mHolder != null) {
                        getmCameraActivity().setPreviewDisplay(mHolder);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getmCameraActivity() != null) {
            flashOnApp();
        }
    }

    // Turns the LED on when the button on the app is pressed.
    private void flashOnApp() {
        setParams(getmCameraActivity().getParameters());

        List<String> flashModes = getParams().getSupportedFlashModes();

        if (flashModes == null) {
            return;
        } else {
            if (count == 0) {
                getParams().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                getmCameraActivity().setParameters(getParams());
                preview = (SurfaceView) findViewById(R.id.preview);
                mHolder = preview.getHolder();
                mHolder.addCallback(this);

                try {
                    getmCameraActivity().startPreview();
                } catch (Exception e) {
                    e.printStackTrace();

                }

                TorchGlobals.setIsFlashOn(true);
            }

            String flashMode = getParams().getFlashMode();

            if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {

                if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    getParams().setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    getmCameraActivity().setParameters(getParams());
                } else {
                    getParams().setFlashMode(Camera.Parameters.FLASH_MODE_ON);

                    getmCameraActivity().setParameters(getParams());
                    try {
                        getmCameraActivity().autoFocus(new Camera.AutoFocusCallback() {
                            public void onAutoFocus(boolean success,
                                                    Camera camera) {
                                count = 1;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                TorchGlobals.setIsFlashOn(true);

            }
        }
    }

    // Any time the widget or the button in the app is pressed to turn the LED
    // off we process this off click.
    private void processOffClick() {
        if (defaultPreference.getBoolean("pref_torch_notification", false)) {
            cleanNotification();
        }
        if (getmCameraActivity() != null) {
            count = 0;
            flashOffApp();
        }
    }

    // Turns the LED off when the button on the app is pressed.
    private void flashOffApp() {
        getmCameraActivity().stopPreview();
        getmCameraActivity().release();
        setmCameraActivity(null);
        TorchGlobals.setIsFlashOn(false);
    }

    // The following three methods are needed to implement SurfaceView.Callback.
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        mHolder.addCallback(this);

        if (getmCameraActivity() != null) {

            try {
                getmCameraActivity().setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHolder = holder;
        mHolder.addCallback(this);
        if (getmCameraActivity() != null) {
            try {
                getmCameraActivity().setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
    }

    // Getters and setters for mCameraActivity.
    public static Camera getmCameraActivity() {
        return mCameraActivity;
    }

    public static void setmCameraActivity(Camera mCameraActivity) {
        MainActivity.mCameraActivity = mCameraActivity;
    }

    // Getters and setters for params.
    public Camera.Parameters getParams() {
        return params;
    }

    public void setParams(Camera.Parameters params) {
        this.params = params;
    }

}
