package in.tagglabs.sensors;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private static final String TAG = "--MainActivity--";
    TextView x, y, z;
    float mapX, mapY, mapZ;
    Button connect, gyro;
    float[] values;
    JSONObject data = new JSONObject();
    EditText ip;
    String url;
    SensorManager sensorManager = null;
    List list;
    private Socket socketClient;
    { try

    {
        socketClient = IO.socket("http://192.168.0.110:3000/androidClient");
        socketClient.connect();
        Log.d(TAG, "http://" + url + "/androidClient");
    } catch(
    URISyntaxException e)

    {
        e.printStackTrace();
        Toast.makeText(getApplicationContext(), "Not connect", Toast.LENGTH_SHORT).show();
    }

}

    Boolean isConnected=false;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {
                String msg = "message";
                //Toast.makeText(getApplicationContext(),""+msg,Toast.LENGTH_SHORT).show();
            } catch (Exception exp) {
                return;
            }
        }
    };
    SensorEventListener sel = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            values = event.values;
            mapX=((values[0]/9)*100);
            mapY=(((values[1])/9)*100);
            x.setText("x: " + values[0]+" mapX: "+mapX);
            y.setText("y: " + values[1]+ "map: "+mapY);
            z.setText("z: " + values[2]);

            try{
                data.put("x",values[0]);
                data.put("y",values[1]);
                data.put("z",values[2]);
                data.put("mapX",mapX);
                data.put("mapY",mapY);
//                sendData();
                if(isConnected==true){
                    socketClient.emit("sensor",data);
                }else {
                    Log.e("--Else","False");
                }
            }catch (JSONException e){
                Log.d(TAG,"JSONException :",e);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        z = findViewById(R.id.z);
        ip=findViewById(R.id.url);
        socketClient.connect();
        connect=findViewById(R.id.connect);
        gyro=findViewById(R.id.gyro);
        gyro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),gyro.class);
                startActivity(intent);
                finish();
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url=ip.getText().toString();
                try {
                    socketClient = IO.socket("http://"+ url+"/androidClient");
                    socketClient.connect();
                    Log.d(TAG,"http://"+ url+"/androidClient");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Not connect", Toast.LENGTH_SHORT).show();
                }
                if(!socketClient.connected()){
                    try{
                        Emitter.Listener onConnect = new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.d(TAG, "connected " + socketClient.connected());
                            }
                        };
                        Emitter.Listener onConnectError = new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.d(TAG, "error " + args[0].toString());
                            }
                        };
                        socketClient.disconnect();
                        socketClient.connect();
                        socketClient.on(Socket.EVENT_CONNECT_ERROR,onConnectError);
                        socketClient.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                        socketClient.on(Socket.EVENT_CONNECT, onConnect);
                        socketClient.on(Socket.EVENT_RECONNECT, onConnect);
                        socketClient.on(Socket.EVENT_DISCONNECT, onConnectError);
                        Log.d(TAG,"Socket Connected"+socketClient);
                        socketClient.emit("connect","Ready");
                        isConnected=true;

                    }catch (Exception e){
                        Log.d(TAG,"Exception :",e);
                    }
                    if(data!=null){
//                        final Handler h = new Handler();
//                        h.postDelayed(new Runnable()
//                        {
//                            private long time = 0;
//
//                            @Override
//                            public void run()
//                            {
//                                // do stuff then
//                                // can call h again after work!
////                                socketClient.emit("sensor",data);
//                                time += 1000;
//                                Log.d("TimerExample", "Going for... " + time);
//                                h.postDelayed(this, 1000);
//                            }
//                        }, 100); // 1 second delay (takes millis)

                    }else {
                        Log.d(TAG,"Data is null");
                    }
                }else if(socketClient.connected()) {
                    Log.e("--Socket--","is connected");
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        list=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        Log.e("-----", String.valueOf(sensorManager.getDefaultSensor(1)));
        Log.e("--list--", String.valueOf(list));
        if(list.size()>0){
        sensorManager.registerListener(sel,(Sensor)list.get(0),SensorManager.SENSOR_DELAY_NORMAL);
        }else {
            Toast.makeText(getBaseContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }
    }
    public void sendData(){
        url=ip.getText().toString();
        if(!socketClient.connected()){
            try{
                try {
                    socketClient = IO.socket("http://"+ url+"/androidClient");
                    Log.d(TAG,"http://"+ url+"/androidClient");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Not connect", Toast.LENGTH_SHORT).show();
                }
                socketClient.connect();
                Log.d(TAG,"Socket Connected");
            }catch (Exception e){
                Log.d(TAG,"Exception :",e);
            }
            if(data!=null){
                socketClient.emit("sensor",data);
            }else {
                Log.d(TAG,"Data is null");
            }
        }
    }
//    @Override
//    protected void onStop() {
//        if(list.size()>0){
//            sensorManager.unregisterListener(sel);
//        }
//        super.onStop();
//    }
@Override
public void onBackPressed()
{
    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
    {
        super.onBackPressed();
        return;
    }
    else {
        Intent intent=new Intent(getApplication(),sensor.class);
//        startActivities(intent);
        startService(intent);
        Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

    mBackPressed = System.currentTimeMillis();
}
}
