package com.lkyear.lllauncher.Launcher;

/**
 * Created by lkyear on 2018/2/14.
 */

public class Shelves {

    private int Id;

    private String Title;

    private String Packages;

    public void setId(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitle() {
        return Title;
    }

    public void setPackages(String packages) {
        Packages = packages;
    }

    public String getPackages() {
        return Packages;
    }
}
