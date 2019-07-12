package com.lkyear.lllauncher.Preference;

/**
 * Created by lkyear on 17/5/30.
 */

public class Role {

    private int Id;
    private String title;
    private String path;
    private int verCode;
    private String verName;

    public static final int LOGO_DEFAULT = 0;

    public static final int LOGO_HONOKA = 1;

    public static final int LOGO_KOTORI = 2;

    public static final int LOGO_UMI = 3;

    public static final int LOGO_KKE = 4;

    public static final int LOGO_NOZOMI = 5;

    public static final int LOGO_MAKI = 6;

    public static final int LOGO_RIN = 7;

    public static final int LOGO_HANAYO = 8;

    public static final int LOGO_NICO = 9;

    public static final int LOGO_YUTONG = 10;

    public void setId(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public void setTitle(String t) {
        title = t;
    }

    public String getTitle() {
        return title;
    }

    public void setPath(String p) {
        path = p;
    }

    public String getPath() {
        return path;
    }

    public void setVerCode(int i) {
        verCode = i;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerName(String s) {
        verName = s;
    }

    public String getVerName() {
        return verName;
    }

}
