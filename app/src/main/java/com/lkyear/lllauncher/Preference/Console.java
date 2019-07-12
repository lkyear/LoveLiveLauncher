package com.lkyear.lllauncher.Preference;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lkyear.lllauncher.Launcher.Level;
import com.lkyear.lllauncher.R;
import com.lkyear.lllauncher.Util.AESCrypt;
import com.lkyear.lllauncher.Util.Utils;

/**
 * Created by lkyear on 2017/7/15.
 */

public class Console extends Activity {

    private TextView tvConsole;
    private EditText tvCommand;
    private Button btExecute;
    private ScrollView svScroll;

    private Boolean isAccredit = false;

    private SharedPreferences.Editor sharedPreferencesEditor;
    private SharedPreferences sharedPreferences;

    private Level userLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.launcher_console);
        userLevel = new Level(Console.this);
        tvConsole = (TextView) findViewById(R.id.launcher_console_console);
        tvCommand = (EditText) findViewById(R.id.launcher_console_command);
        btExecute = (Button) findViewById(R.id.launcher_console_execute);
        svScroll = (ScrollView) findViewById(R.id.launcher_console_sv);
        tvConsole.getPaint().setTypeface(Typeface.createFromAsset(getAssets(), "console_font.ttf"));
        btExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(tvCommand.getText().toString().trim())) {
                    printInfo(" " + tvCommand.getText().toString().trim(), false);
                    execCommand(tvCommand.getText().toString().trim());
                    tvCommand.setText("");
                }
            }
        });
    }

    private void execCommand(String command) {
        String[] cmdAry = command.split(" ");
        switch (cmdAry[0]) {
            case "ed":
                if (isAccredit) {
                    execCommand("cv adv");
                } else {
                    finish();
                }
                break;
            case "si":
                showInfo(cmdAry);
                break;
            case "cs":
                clearScreen();
                break;
            case "cu":
                convertUser(cmdAry);
                break;
            case "ld":
                loadData(cmdAry);
                break;
            case "cd":
                changeData(cmdAry);
                break;
            case "man":
                methodSearch(cmdAry);
                break;
            default:
                printInfo("NGSHELL : " + cmdAry[0] + " : Command not found", true);
                break;
        }
    }

    private void showInfo(String[] array) {
        try {
            if (array.length > 1) {
                switch (array[1]) {
                    case "vi":
                        printInfo(getPackageName() + "\n" + getVersionName() + " (" + getVersionCode() + ")", true);
                        userLevel.setAchievement(Level.ACHIEVEMENT_USE_CONSOLE, true);
                        break;
                    case "nsvi":
                        printInfo("NGShell V3\nDate : 2018 - 4 - 19 21:54 UTC+8\n" +
                                "Version : 3.0.0741" + "\nCommand : 7", true);
                        userLevel.setAchievement(Level.ACHIEVEMENT_USE_CONSOLE, true);
                        break;
                    case "conf":
                        if (isAccredit) {
                            if (sharedPreferences != null) {
                                switch (array[2]) {
                                    case "bl":
                                        if (sharedPreferences.getBoolean(array[3], false)) {
                                            printInfo("Config - " + array[3] + "\nValue : YES", true);
                                        } else {
                                            printInfo("Config - " + array[3] + "\nValue : NO", true);
                                        }
                                        break;
                                    case "it":
                                        printInfo("Config - " + array[3] + "\nValue : " + sharedPreferences.getInt(array[3], 0), true);
                                        break;
                                    case "st":
                                        printInfo("Config - " + array[3] + "\nValue : " + sharedPreferences.getString(array[3], "NIL"), true);
                                        break;
                                    case "ss":
                                        printInfo("si : No StringSet config.", true);
                                        break;
                                }
                            } else {
                                printInfo("si : Config file not loaded.", true);
                            }
                        } else {
                            printInfo("si : Permission denied.", true);
                        }
                        break;
                    default:
                        printInfo("si : illegal option -- " + array[1], true);
                        break;
                }
            } else {
                printInfo("si : Missing parameters.", true);
            }
        } catch (Exception e) {
            printInfo("si : An error occurred.", true);
        }
    }

    private void clearScreen() {
        if (isAccredit) {
            tvConsole.setText("#>");
        } else {
            tvConsole.setText("$>");
        }
    }

    private void convertUser(String[] array) {
        if (array.length > 1) {
            try {
                switch (array[1]) {
                    case "bas":
                        if (array.length < 3) {
                            printInfo("cu : missing option -- password", true);
                        } else if (array[2].equals("recent")) {
                            if (Utils.preferenceGetBoolean(Console.this, "pref_console_basic_activte", false)) {
                                isAccredit = true;
                                printEnd();
                            } else {
                                printInfo("Wrong password...", true);
                            }
                        } else if (array[2].equals(AESCrypt.encrypt("s520dh10fus", Build.SERIAL.toString()))) {
                            isAccredit = true;
                            Utils.preferencePutData(Console.this, "pref_console_basic_activte", true);
                            printEnd();
                        } else {
                            printInfo("Wrong password...", true);
                        }
                        break;
                    case "adv":
                        isAccredit = false;
                        printEnd();
                        break;
                    case "cp":
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("Launcher serial number",
                                "Your serial number is : " + Build.SERIAL.toString()
                                        + "\nCopied to your clipboard."));
                        printInfo("Your serial number is : " + Build.SERIAL.toString()
                                + "\nCopied to your clipboard.", true);
                        break;
                    default:
                        printInfo("cu : illegal option -- " + array[1], true);
                        break;
                }
            } catch (Exception e) {
                printInfo("cu : An error occurred.", true);
            }
        } else {
            printInfo("cu : Missing parameters.", true);
        }
    }

    private void loadData(String[] array) {
        try {
            if (array.length > 1) {
                if (isAccredit) {
                    switch (array[1]) {
                        case "conf":
                            if (array[2].equals("std")) {
                                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Console.this);
                                sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(Console.this).edit();
                                printInfo("Default config file loaded.", true);
                            } else {
                                sharedPreferences = getSharedPreferences(array[2], MODE_PRIVATE);
                                sharedPreferencesEditor = getSharedPreferences(array[2], MODE_PRIVATE).edit();
                                printInfo(array[2] + " config file loaded.", true);
                            }
                            break;
                        case "db":
                            printInfo("Cannot mount $PROJECT_LOCATION/database .", true);
                            break;
                    }
                } else {
                    printInfo("ld : Permission denied.", true);
                }
            } else {
                printInfo("ld : Missing parameters.", true);
            }
        } catch (Exception e) {
            printInfo("ld : An error occurred.", true);
        }
    }

    private void changeData(String[] array) {
        try {
            if (isAccredit) {
                if (array.length == 4) {
                    switch (array[1]) {
                        case "bl":
                            if (array[3].equals("YES")) {
                                sharedPreferencesEditor.putBoolean(array[2], true).commit();
                                printInfo("Config - " + array[2] + "\nValue : YES\nSAVED!", true);
                            } else if (array[3].equals("NO")) {
                                sharedPreferencesEditor.putBoolean(array[2], false).commit();
                                printInfo("Config - " + array[2] + "\nValue : NO\nSAVED!", true);
                            } else {
                                printInfo("Value is not a Boolean.", true);
                            }
                            break;
                        case "it":
                            if (array[3].matches("[0-9]*")) {
                                sharedPreferencesEditor.putInt(array[2], Integer.parseInt(array[3])).commit();
                                printInfo("Config - " + array[2] + "\nValue : " + Integer.parseInt(array[3]) + "\nSAVED!", true);
                            } else {
                                printInfo("Value is not an integer.", true);
                            }
                            break;
                        case "st":
                            String strVal = array[3];
                            strVal.replaceAll("&t", " ");
                            sharedPreferencesEditor.putString(array[2], strVal).commit();
                            printInfo("Config - " + array[2] + "\nValue : " + strVal + "\nSAVED!", true);
                            break;
                        case "ss":
                            printInfo("cd : No StringSet config.", true);
                            break;
                    }
                } else {
                    printInfo("cd : Missing parameters.", true);
                }
            } else {
                printInfo("cd : Permission denied.", true);
            }
        } catch (Exception e) {
            printInfo("cd : An error occurred.", true);
        }
    }

    private void methodSearch(String[] array) {
        if (array.length == 2) {
            switch (array[1]) {
                case "ed":
                    printInfo("\n" +
                            "Exit NGShell, if in basic access mode, exit basic mode first.\n" +
                            "\n" +
                            "Example:\n" +
                            "\n" +
                            "\ted", true);
                    break;
                case "si":
                    printInfo("\n" +
                            "Write information in console.\n\nUsage:\n\n" +
                            "\tsi [ vi | nsvi | conf ] [ bl | it | st | ss ] config_name\n" +
                            "\tvi : Package version info;\n" +
                            "\tnsvi : NGShell version info;\n" +
                            "\tconf : Show config value;\n" +
                            "\tbl : Boolean config;\n" +
                            "\tit : Integer config;\n" +
                            "\tst : String config;\n" +
                            "\tss : StringSet config;\n" +
                            "\tconfig_name : Config item name.\n\nExample:\n\n" +
                            "\tsi vi\n" +
                            "\tsi nsvi\n" +
                            "\tsi conf bl conf_item_name", true);
                    break;
                case "cd":
                    printInfo("\nSave data to config file.\n\nUsage:\n\n" +
                            "\tcd [ bl | it | st | ss ] config_name config_value\n" +
                            "\tbl : Boolean config(YES or NO);\n" +
                            "\tit : Integer config;\n" +
                            "\tst : String config(Use &t as space);\n" +
                            "\tss : StringSet config;\n" +
                            "\tconfig_name : Config item name;\n" +
                            "\tconfig_value : Config item value.\n\nExample:\n\n" +
                            "\tcd bl conf_item_name YES\n" +
                            "\tcd st conf_item_name Test&tfile", true);
                    break;
                case "cv":
                    printInfo("\nChange access mode.\n\nUsage:\n\n" +
                            "\tcv [ frank | adv ] password\n" +
                            "\tbas : Basic access mode;\n" +
                            "\tadv : Advanced access mode;\n" +
                            "\tpassword : basic access mode password.\n\nExample:\n\n" +
                            "\tsu frank password\n" +
                            "\tsu adv", true);
                    break;
                case "cs":
                    printInfo("\nClear screen.\n\nExample:\n\n\tcs", true);
                    break;
                case "ld":
                    printInfo("\nLoad data file.\n\nUsage:\n\n" +
                            "\tld [ conf | db ] [ std | file_name ]\n" +
                            "\tconf : Load Config file;\n" +
                            "\tdb : Load database file;\n" +
                            "\tstd : Default file in package;\n" +
                            "\tfile_name : Data file name.\n\nExample:\n\n" +
                            "\tld conf std\n" +
                            "\tld conf settings",true);
                    break;
                case "man":
                    printInfo("\nShow command help.\n\nUsgae:\n\n" +
                            "\tman command_name\n\n" +
                            "Example:\n\n" +
                            "\tman load\n" +
                            "\tman sm", true);
                    break;
                default:
                    printInfo("No help for " + array[1], true);
                    break;
            }
        } else {
            printInfo("man : Missing parameters.", true);
        }
    }

    private void printInfo(String output, Boolean end) {
        tvConsole.append(output + "\n");
        if (end) {
            printEnd();
        }
    }

    private void printEnd() {
        if (isAccredit) {
            tvConsole.append("\nREADY.\n\n#>");
        } else {
            tvConsole.append("\nREADY.\n\n$>");
        }
        svScroll.fullScroll(View.FOCUS_DOWN);
    }

    private int getVersionCode() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    private String getVersionName() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            return "NULL";
        }
    }

}
