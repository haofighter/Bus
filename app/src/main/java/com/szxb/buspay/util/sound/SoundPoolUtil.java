package com.szxb.buspay.util.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.szxb.buspay.R;
import com.szxb.mlog.SLog;


/**
 * 作者: Tangren on 2017-09-05
 * 包名：szxb.com.commonbus.util.sound
 * 邮箱：996489865@qq.com
 * TODO:音源管理
 */

public class SoundPoolUtil {

    public static SoundPool mSoundPlayer = new SoundPool(1,
            AudioManager.STREAM_MUSIC, 5);
    public static SoundPoolUtil soundPlayUtils;
    private static MediaPlayer mediaPlayer;

    private static Context mContext;

    private static int sounds[] = new int[]{
            R.raw.scan_success,//扫码成功
            R.raw.ec_re_qr_code,//请刷新二维码
            R.raw.ec_fee,//超出最大金额
            R.raw.verify_fail,//验码失败
            R.raw.ec_balance,//余额不足
            R.raw.ic_base,//铛
            R.raw.ic_base2,//铛铛
            R.raw.ic_dis,//优惠卡
            R.raw.ic_emp,//员工卡
            R.raw.ic_blood,//无偿献血卡
            R.raw.ic_free,//免费卡
            R.raw.ic_honor,//荣军卡
            R.raw.ic_old,//老年卡
            R.raw.ic_student,//学生卡
            R.raw.ic_invalid,//卡失效
            R.raw.ic_love,//.爱心卡
            R.raw.ic_to_work,//上班
            R.raw.ic_off_work,//下班
            R.raw.ic_push_money,//请投币
            R.raw.ic_re,//重新刷卡
            R.raw.ic_yearly,//请年检
            R.raw.qr_error,//二维码有误
            R.raw.ic_recharge,//.请充值
            R.raw.ic_defect,//.优抚卡
            R.raw.ic_manager,//管理卡
            R.raw.ic_month,//月票卡
            R.raw.beep2,//.设置音
            R.raw.ic_lllegal_card,//.非法卡
            R.raw.ic_error,//.错误
            R.raw.ic_high_school_card,//高中卡
            R.raw.yinlianka,//银联卡
            R.raw.wangluoyichang,//银联卡
            R.raw.wuxiaoka,//无效卡
            R.raw.laorenka_t,//无效卡
            R.raw.junrenka_t,//军人卡
            R.raw.guanaika_t,//关爱卡
            R.raw.newfree,//免费卡

    };

    /**
     * 初始化
     *
     * @param context .
     */
    public static SoundPoolUtil init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPoolUtil();
        }
        mContext = context.getApplicationContext();
        for (int sound : sounds) {
            mSoundPlayer.load(mContext, sound, 1);
        }
        return soundPlayUtils;
    }


    /**
     * 播放声音
     *
     * @param soundID .
     */
    public synchronized static void play(int soundID) {
        int play = mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
        if (play == 0) {
            SLog.e("SoundPoolUtil(play.java:84)SoundPoolUtil播放失败>>音源ID=" + soundID);
            if (soundID > 0) {
                SLog.e("SoundPoolUtil(play.java:87)使用playMedia播放");
                playMedia(sounds[soundID - 1]);
            }
        }
    }

    private static void playMedia(int soundID) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(mContext, soundID);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            SLog.e("SoundPoolUtil(playMedia.java:106)playMedia播放异常>>>soundID=" + soundID + ">>>>>>>" + e.toString());
        }

    }

    public static void release() {
        if (mSoundPlayer != null)
            mSoundPlayer.release();
    }
}
