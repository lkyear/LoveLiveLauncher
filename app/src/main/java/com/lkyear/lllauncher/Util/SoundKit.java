package com.lkyear.lllauncher.Util;

import android.content.Context;
import android.media.MediaPlayer;

import com.lkyear.lllauncher.R;

public class SoundKit {

    private SoundPoolPlayer soundPoolPlayer = null;

    public static final int DEFAULT = 0;

    public static final int NOTIFY = 1;

    public static final int TYPE = 2;

    Context context;

    public SoundKit(Context mContext) {
        context = mContext;
    }

    public void play(int type) {
        switch (type) {
            case DEFAULT:
                soundPoolPlayer = SoundPoolPlayer.create(context, R.raw.msgbox);
                break;
            case NOTIFY:
                soundPoolPlayer = SoundPoolPlayer.create(context, R.raw.notify);
                break;
            case TYPE:
                soundPoolPlayer = SoundPoolPlayer.create(context, R.raw.type);
                break;
        }
        soundPoolPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                soundPoolPlayer.release();
            }
        });
        soundPoolPlayer.play();
        /*SoundPool soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        switch (type) {
            case DEFAULT:
                soundPool.load(context, R.raw.msgbox, 1);
                break;
            case NOTIFY:
                soundPool.load(context, R.raw.notify, 1);
                break;
            case TYPE:
                soundPool.load(context, R.raw.type, 1);
                break;
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                soundPool.play(1, 0.6f, 0.6f, 1, 0, 1f);
                //soundPool.release();
            }
        });*/
    }

}
