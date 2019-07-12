package com.lkyear.lllauncher.Util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * ROOT权限工具类
 * @author lkyear
 *
 * Created by lkyear on 16/5/20.
 */

public class SuUtil {

    private static Process process;

    /**
     * 杀死进程暴露方法
     * @param packageName 包名
     */
    public static void kill(String packageName) {
        initProcess();
        killProcess(packageName);
        close();
    }

    /**
     * 清除数据暴露方法
     * @param packageName 包名
     */
    public static void clear(String packageName) {
        initProcess();
        cleanPackage(packageName);
        close();
    }

    /**
     * 初始化进程
     */
    private static void initProcess() {
        if (process == null)
            try {
                process = Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * 杀死进程方法
     * @param packageName 包名
     */
    private static void killProcess(String packageName) {
        OutputStream out = process.getOutputStream();
        String cmd = "am force-stop " + packageName + " \n";
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除应用数据方法
     * @param packageName 包名
     */
    private static void cleanPackage(String packageName) {
        OutputStream out = process.getOutputStream();
        String cmd = "pm clear " + packageName;
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭输出流
     */
    private static void close() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
