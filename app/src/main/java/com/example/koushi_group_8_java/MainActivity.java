package com.example.koushi_group_8_java;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    /*定義画面*/
    // ログ出力時のタグを定数定義
    private static final String LOG_TAG = "LogTraining";

    //センサーマネージャオブジェクト
    private SensorManager mSensorManager;
    //加速度センサーオブジェクト
    private Sensor mAccelerometerSensor;
    TextView xTextView;
    TextView yTextView;
    TextView zTextView;
    TextView sumTextView;
    TextView stepTextView;
    TextView upTextView;
    //歩数
    int stepcount = 0;
    //ローパスフィルタ用
    boolean first = true;
    boolean up = false;
    float d0, d= 0f;
    //フィルタリング係数
    float a = 0.65f;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //軸表示の管理
        xTextView = (TextView) findViewById(R.id.x_value);
        yTextView = (TextView) findViewById(R.id.y_value);
        zTextView = (TextView) findViewById(R.id.z_value);
        sumTextView = (TextView) findViewById(R.id.sum_value);
        stepTextView = (TextView) findViewById(R.id.counter);
        upTextView = (TextView) findViewById(R.id.up_boolean);



        //センサーマネージャの取得
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    //センサの値が更新された時
    @Override
    public void onSensorChanged (SensorEvent event) {
        // 加速度センサの場合、以下の処理を実行

            // 数値の単位はm/s^2
            // X軸
            float x = event.values[0];
            // Y軸
            float y = event.values[1];
            // Z軸
            float z = event.values[2];

            //リアルタイム表示
            xTextView.setText("X軸の加速度:" + String.valueOf(x));
            yTextView.setText("Y軸の加速度:" + String.valueOf(y));
            zTextView.setText("Z軸の加速度:" + String.valueOf(z));
            //sum = √x^2 + y^2 + z^2
            float sum = (float) Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
            sumTextView.setText("３軸ベクトルの長さ:" + String.valueOf(sum));
            upTextView.setText("up変数の値：" + String.valueOf(up));

            if(first) {
                first = false;
                up = true;
                d0 = a*sum;
            } else {
                //ローパスフィルタリング
                d = a*sum+(1-a)*d0;
                if(up&&d<d0){
                    up=false;
                    stepcount++;
                }else if(!up&& d>d0){
                    up=true;
                    //d0=d;
                }
                stepTextView.setText(String.valueOf(stepcount)+"歩");
            }

        }


    //センサの制度が変更された時
    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 非アクティブ時にSensorEventをとらないようにリスナの登録解除
        mSensorManager.unregisterListener(this);
    }
}



