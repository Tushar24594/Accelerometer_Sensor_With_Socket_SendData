package in.tagglabs.sensors;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class sensor extends Service {
    SensorManager sensorManager = null;
    List list;
    SensorEventListener sel = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
//            x.setText("x: " + values[0]);
//            y.setText("y: " + values[1]);
//            z.setText("z: " + values[2]);
//            Toast.makeText(sensor.this, "x: " + values[0]+"y: " + values[1]+"z: " + values[2], Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
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

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public sensor() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
