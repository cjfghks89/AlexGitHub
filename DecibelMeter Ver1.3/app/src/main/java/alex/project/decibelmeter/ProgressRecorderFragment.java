package alex.project.decibelmeter;


import android.annotation.SuppressLint;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static alex.project.decibelmeter.DynamicGraphFragment.audio;
import static alex.project.decibelmeter.DynamicGraphFragment.i;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
@SuppressLint("ValidFragment")
public class ProgressRecorderFragment extends android.support.v4.app.Fragment implements OnCompletionListener , View.OnClickListener{



    Context mContext;
    public ProgressRecorderFragment(Context context) {
        mContext = context;
    }


    private static final int REC_STOP = 0;
    private static final int RECORDING = 1;
    private static final int PLAY_STOP = 0;
    private static final int PLAYING = 1;
    private static final int PLAY_PAUSE = 2;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private int mRecState = REC_STOP;
    private int mPlayerState = PLAY_STOP;
    private SeekBar mRecProgressBar, mPlayProgressBar;
    private Button mBtnStartRec, mBtnStartPlay, mBtnStopPlay;
    private static String mFilePath, mFileName = null;
    private TextView mTvPlayMaxPoint, minform;


    private TextView testxt;

    private int mCurRecTimeMs = 0;
    private int mCurProgressTimeDisplay = 0;

    Handler mProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            mCurRecTimeMs = mCurRecTimeMs + 100;
            mCurProgressTimeDisplay = mCurProgressTimeDisplay + 100;

            if (mCurRecTimeMs < 0) {
            }

            else if (mCurRecTimeMs < 20000) {
                mRecProgressBar.setProgress(mCurProgressTimeDisplay);
                mProgressHandler.sendEmptyMessageDelayed(0, 100);
            }

            else {
                mBtnStartRecOnClick();
            }
        }
    };

    Handler mProgressHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            if (mPlayer == null) return;

            try {
                if (mPlayer.isPlaying()) {
                    mPlayProgressBar.setProgress(mPlayer.getCurrentPosition());
                    mProgressHandler2.sendEmptyMessageDelayed(0, 100);
                }
            } catch (IllegalStateException e) {
            } catch (Exception e) {
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View viewFrgRec = inflater.inflate(R.layout.fragment_recorder, null);

        mFilePath = "/sdcard/Download/";


        final SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");


        mFileName = "/decibel"
                + timeStampFormat.format(new Date()).toString()
                + "Rec.mp4";

        mBtnStartRec = (Button) viewFrgRec.findViewById(R.id.btnStartRec);
        mBtnStartPlay = (Button) viewFrgRec.findViewById(R.id.btnStartPlay);
        mBtnStopPlay = (Button) viewFrgRec.findViewById(R.id.btnStopPlay);
        mRecProgressBar = (SeekBar) viewFrgRec.findViewById(R.id.recProgressBar);
        mPlayProgressBar = (SeekBar) viewFrgRec.findViewById(R.id.playProgressBar);
        mTvPlayMaxPoint = (TextView) viewFrgRec.findViewById(R.id.tvPlayMaxPoint);

        minform = (TextView) viewFrgRec.findViewById(R.id.inform_date);

        testxt = (TextView)viewFrgRec.findViewById(R.id.testtxt);

        mBtnStartRec.setOnClickListener(this);
        mBtnStartPlay.setOnClickListener(this);
        mBtnStopPlay.setOnClickListener(this);


        AdView mAdView = (AdView) viewFrgRec.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        return viewFrgRec;
    }




    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartRec:
                i = 250;
                mBtnStartRecOnClick();
                break;
            case R.id.btnStartPlay:
                mBtnStartPlayOnClick();
                break;
            case R.id.btnStopPlay:
                mBtnStopPlayOnClick();
                break;
            default:
                break;
        }
    }

    private void mBtnStartRecOnClick() {
        if (mRecState == REC_STOP) {
            mRecState = RECORDING;
            audio.stop();
            startRec();
            updateUI();
        } else if (mRecState == RECORDING) {
            mRecState = REC_STOP;
            audio.startRecording();
            stopRec();
            testxt.setText("녹음이 완료 되었습니다.");
            minform.setText("녹음 파일명 : "+mFileName);
            updateUI();
        }
    }


    private void startRec() {
        mCurRecTimeMs = 0;
        mCurProgressTimeDisplay = 0;

        mProgressHandler.sendEmptyMessageDelayed(0, 100);

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.reset();
        } else {
            mRecorder.reset();
        }

        try {

            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(mFilePath + mFileName);
            mRecorder.prepare();
            mRecorder.start();
        } catch (IllegalStateException e) {

            Toast.makeText(mContext, "IllegalStateException." , Toast.LENGTH_LONG).show();

        } catch (IOException e) {

            Toast.makeText(mContext, "IOException." , Toast.LENGTH_LONG).show();
        }
    }

    private void stopRec() {
        try {
            mRecorder.stop();
        } catch (Exception e) {

        } finally {
            mRecorder.release();
            mRecorder = null;
        }

        mCurRecTimeMs = -999;
        mProgressHandler.sendEmptyMessageDelayed(0, 0);
    }

    private void mBtnStartPlayOnClick() {
        if (mPlayerState == PLAY_STOP) {
            mPlayerState = PLAYING;
            initMediaPlayer();
            startPlay();
            updateUI();
        } else if (mPlayerState == PLAYING) {
            mPlayerState = PLAY_PAUSE;
            pausePlay();
            updateUI();
        } else if (mPlayerState == PLAY_PAUSE) {
            mPlayerState = PLAYING;
            startPlay();
            updateUI();
        }
    }

    private void mBtnStopPlayOnClick() {
        if (mPlayerState == PLAYING || mPlayerState == PLAY_PAUSE) {
            mPlayerState = PLAY_STOP;
            stopPlay();
            releaseMediaPlayer();
            updateUI();
        }
    }

    private void initMediaPlayer() {
        if (mPlayer == null)
            mPlayer = new MediaPlayer();
        else
            mPlayer.reset();

        mPlayer.setOnCompletionListener(this);
        String fullFilePath = mFilePath + mFileName;

        try {
            mPlayer.setDataSource(fullFilePath);
            mPlayer.prepare();
            int point = mPlayer.getDuration();
            mPlayProgressBar.setMax(point);

            int maxMinPoint = point / 1000 / 60;
            int maxSecPoint = (point / 1000) % 60;
            String maxMinPointStr = "";
            String maxSecPointStr = "";

            if (maxMinPoint < 10)
                maxMinPointStr = "0" + maxMinPoint + ":";
            else
                maxMinPointStr = maxMinPoint + ":";

            if (maxSecPoint < 10)
                maxSecPointStr = "0" + maxSecPoint;
            else
                maxSecPointStr = String.valueOf(maxSecPoint);

            mTvPlayMaxPoint.setText(maxMinPointStr + maxSecPointStr);

            mPlayProgressBar.setProgress(0);
        } catch (Exception e) {
            Log.v("ProgressRecorder", "미디어 플레이어 Prepare Error ==========> " + e);
        }
    }

    private void startPlay() {
        Log.v("ProgressRecorder", "startPlay().....");

        try {
            mPlayer.start();


            mProgressHandler2.sendEmptyMessageDelayed(0, 100);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void pausePlay() {
       Log.v("ProgressRecorder", "pausePlay().....");
        mPlayer.pause();
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);
    }

    private void stopPlay() {
        mPlayer.stop();
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);
    }

    private void releaseMediaPlayer() {
        mPlayer.release();
        mPlayer = null;
        mPlayProgressBar.setProgress(0);
    }

    public void onCompletion(MediaPlayer mp) {
        mPlayerState = PLAY_STOP;
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);
        updateUI();
    }

    private void updateUI() {
        if (mRecState == REC_STOP) {
            mBtnStartRec.setText("Rec");
            mRecProgressBar.setProgress(0);
        } else if (mRecState == RECORDING)
            mBtnStartRec.setText("Stop");


        if (mPlayerState == PLAY_STOP) {
            mBtnStartPlay.setText("Play");
            mPlayProgressBar.setProgress(0);
        } else if (mPlayerState == PLAYING)
            mBtnStartPlay.setText("Pause");
        else if (mPlayerState == PLAY_PAUSE)
            mBtnStartPlay.setText("Start");
    }
}
