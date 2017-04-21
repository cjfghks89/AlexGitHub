package alex.project.decibelmeter;

/**
 * Created by Alex on 2017-03-13.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.achartengine.GraphicalView;

import java.io.IOException;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.plus.model.people.Person;


@SuppressLint("ValidFragment")
public class DynamicGraphFragment extends android.support.v4.app.Fragment implements View.OnClickListener {


    Context mContext;

    public DynamicGraphFragment(Context context) {
        mContext = context;
    }


    private static GraphicalView view3;
    private LineGraph line3 = new LineGraph();
    private static Thread thread3;
    public LinearLayout graph_area3;


    private ImageButton btnMainRec;
    private static final int sampleRate = 8000;
    public static AudioRecord audio;
    private int bufferSize;
    private static double lastLevelpre = 0;
    private static double lastLevel = 39;
    private static Handler handler;
     private static int[] avgList;
    private static int j = 0;
    public static int i =0;


    private static final int REC_STOP = 0;
    private static final int RECORDING = 1;
    private static final int PLAY_STOP = 0;

    private int mRecState = REC_STOP;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View viewFrg = inflater.inflate(R.layout.fragment_graph, null);  //Fragment v4


        try {
            bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        } catch (Exception e) {
            android.util.Log.e("TrackingFlow", "Exception", e);
        }

        if(BuildConfig.DEBUG){
            Log.d("mode test", "Debug mode");
        }

        btnMainRec = (ImageButton) viewFrg.findViewById(R.id.btnMainStart);

        btnMainRec.setOnClickListener(this);

        AdView mAdView = (AdView) viewFrg.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        return viewFrg;
    }

    public void onResume() {
        super.onResume();

        view3 = line3.getView(getActivity());

        graph_area3 = (LinearLayout) getView().findViewById(R.id.FragSmall_layout);
        graph_area3.addView(view3);
        btnMainRec.setEnabled(true);

        audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);


        final TextView text = (TextView) getView().findViewById(R.id.comparelvl);
        final TextView inflvl = (TextView) getView().findViewById(R.id.influencelvl);
        final TextView stanlvl = (TextView) getView().findViewById(R.id.standardlvl);
        final TextView textlastlevel = (TextView) getView().findViewById(R.id.textFrgLastLevel);



        thread3 = new Thread() {
            public void run() {
                audio.startRecording();
                for (i = 0; i <= 240; i++) {
                    try {
                        line3.mRenderer.setXAxisMin(i - 30);
                        line3.mRenderer.setXAxisMax(i + 1);
                        Thread.sleep(250);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    readAudioBuffer();

                    handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int lstlvl = Integer.parseInt(String.valueOf(Math.round(lastLevel)));

                            String string = String.valueOf(lstlvl) ;
                            textlastlevel.setText(string);

                            if (lastLevel > 0 && lastLevel <= 10) {

                                text.setText("들을수 있는 가장 작은 소리");
                                stanlvl.setText("소음 인정 불가");

                            } else if (lastLevel > 10 && lastLevel <= 20) {

                                text.setText("나뭇잎 스치는 소리");
                                stanlvl.setText("소음 인정 불가");
                            } else if (lastLevel > 20 && lastLevel <= 30) {

                                text.setText("방송국 스튜디오");
                                stanlvl.setText("소음 인정 불가");

                            } else if (lastLevel > 30 && lastLevel <= 40) {

                                text.setText("밤중의 침실");
                                stanlvl.setText("소음 인정 불가");

                            } else if (lastLevel > 40 && lastLevel <= 50) {

                                text.setText("조용한 거실");
                                inflvl.setText("수면의 깊이 변화");
                                stanlvl.setText("소음 인정 불가");

                            } else if (lastLevel > 50 && lastLevel <= 51) {

                                stanlvl.setText("소음 인정 불가");

                            } else if (lastLevel >= 52 && lastLevel <= 56) {
                                text.setText("교무실, 사무실");
                                inflvl.setText("호흡, 맥박수 증가");
                                stanlvl.setText("야간(22시 이후) 소음 인정 ");

                            } else if (lastLevel > 57 && lastLevel <= 60) {

                                text.setText("교무실, 사무실");
                                inflvl.setText("호흡, 맥박수 증가");
                                stanlvl.setText("주,야간 소음 인정 ");

                            } else if (lastLevel > 60 && lastLevel <= 70) {

                                text.setText("1m 안, 보통 대화 소리");
                                inflvl.setText("수면 장애, 집중력 저하");
                                stanlvl.setText("주,야간 소음 인정 ");

                            } else if (lastLevel > 70 && lastLevel <= 80) {

                                text.setText("사무실 , 자동차 실내 소음");
                                inflvl.setText("말초혈관 수축, TV시청 방해 ");
                                stanlvl.setText("주,야간 소음 인정 ");

                            } else if (lastLevel > 80 && lastLevel <= 90) {

                                text.setText("전철 안 , 대도시 거리 소음");
                                inflvl.setText("청력 손실 시작");
                                stanlvl.setText("주,야간 소음 인정 ");

                            } else if (lastLevel > 90 && lastLevel <= 100) {

                                text.setText("대형 트럭, 굴착기");
                                inflvl.setText("난청 발생, 소변량 증가");
                                stanlvl.setText("주,야간 소음 인정 ");

                            } else if (lastLevel > 100 && lastLevel <= 110) {

                                text.setText("공장 내부, 철도 통과");
                                inflvl.setText("작업 능률 저하 ");
                                stanlvl.setText("주,야간 소음 인정 ");

                            } else if (lastLevel > 110 && lastLevel <= 120) {

                                text.setText("공사장 소음, 헤비메탈 공연장");
                                inflvl.setText("단시간 노출에도 일시적 청력 손실 ");
                                stanlvl.setText("주,야간 소음 인정 ");
                            } else if (lastLevel > 120 && lastLevel <= 130) {

                                text.setText("60m 앞 제트기 이륙소리");
                                inflvl.setText("심한 청력 손실 ");
                                stanlvl.setText("주,야간 소음 인정 ");

                            } else {
                                text.setText("측정 오류");

                            }
                        }
                    });
                    int lstlvl = Integer.parseInt(String.valueOf(Math.round(lastLevel)));
                    Point p = new Point(i, lstlvl);


                    line3.addNewPoints(p);
                    view3.repaint();
                }
            }
        };
    }

    public void onStart() {
        super.onStart();

    }


    private void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult = 1;

            if (audio != null) {


                bufferReadResult = audio.read(buffer, 0, bufferSize);
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++) {
                    sumLevel += buffer[i];
                }

                //still working on it to stablize the level
                lastLevel = Math.abs((sumLevel / bufferReadResult))+39;
                avgList[j] = Integer.parseInt(String.valueOf(Math.round(lastLevel)));
                j++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        static public int calAvg (int avgList[]){
             int result = 0;

            for (int i = 0; i < avgList.length; i++) {
                result += avgList[i];
            }
            return result / avgList.length;
        }
 /*
        public int getAvg(){
            return calAvg(avgList);
        }*/


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMainStart:
                BtnMainStartRecOnClick();

                TextView txtChecking = (TextView) getView().findViewById(R.id.textChecking);
                TextView txtDB = (TextView) getView().findViewById(R.id.textdB);

                txtChecking.setText("측정 중");
                txtDB.setText("dB");
                break;
            default:
                break;
        }
    }

    private void BtnMainStartRecOnClick() {
        if (mRecState == REC_STOP) {
            mRecState = RECORDING;

            startRecMain();
            updateUI();
            btnMainRec.setEnabled(false);

        }
    }


    private void startRecMain() {
        thread3.start();
    }


    /*
    private void stopRecMain() {
        //thread3.interrupt();

    }
*/

    private void updateUI() {

        if (mRecState == REC_STOP) {
          //btnMainRec.setText("1분간 측정");
            btnMainRec.setImageResource(R.drawable.button_checking_small);

        } else if (mRecState == RECORDING)
            btnMainRec.setImageResource(R.drawable.button_checking_small);
          //btnMainRec.setText("1분간 측정");

    }


/*
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.getLooper().quit();
        handler.removeMessages(0);

    }
    */
/*
    @Override
    public void onPause() {
        super.onPause();

    }
*/

}
