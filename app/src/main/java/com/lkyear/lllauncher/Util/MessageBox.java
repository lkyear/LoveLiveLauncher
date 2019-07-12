package com.lkyear.lllauncher.Util;

import android.app.AlertDialog;
import android.content.Context;

public class MessageBox {

    Context context;

    public MessageBox(Context mContext) {
        context = mContext;
    }

    public void show(String content, String title, int sound) {
        new SoundKit(context).play(sound);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok, null);
    }

    public void show(String content, int sound) {
        new SoundKit(context).play(sound);
        new AlertDialog.Builder(context)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok, null);
    }

    public void show(String content) {
        new AlertDialog.Builder(context)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok, null);
    }

    public void show(String content, String title) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok, null);
    }

}
