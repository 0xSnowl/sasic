package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_URL = "";
    private static final String TAG = "MainActivity";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);

        JSONObject jsonObject = buildJsonObject();

        if (jsonObject != null) {
            SendDataAsyncTask task = new SendDataAsyncTask(jsonObject, textView);
            task.execute();
        } else {
            textView.setText("Unable to build JSON object");
        }
    }

    private JSONObject buildJsonObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("ABI", Build.SUPPORTED_ABIS[0]);
            jsonObject.put("ABI2", Build.SUPPORTED_ABIS.length > 1 ? Build.SUPPORTED_ABIS[1] : "unknown");
            jsonObject.put("board", Build.BOARD);
            jsonObject.put("bootloader", Build.BOOTLOADER);
            jsonObject.put("brand", Build.BRAND);
            jsonObject.put("device", Build.DEVICE);
            jsonObject.put("display", Build.DISPLAY);
            jsonObject.put("fingerprint", Build.FINGERPRINT);
            jsonObject.put("hardware", Build.HARDWARE);
            jsonObject.put("host", Build.HOST);
            jsonObject.put("id", Build.ID);
            jsonObject.put("manufacturer", Build.MANUFACTURER);
            jsonObject.put("model", Build.MODEL);
            jsonObject.put("product", Build.PRODUCT);
            jsonObject.put("radio", Build.getRadioVersion());
            jsonObject.put("serial", Build.SERIAL);
            jsonObject.put("tags", Build.TAGS);
            jsonObject.put("time", Build.TIME);
            jsonObject.put("type", Build.TYPE);
            jsonObject.put("unknown", Build.UNKNOWN);
            jsonObject.put("user", Build.USER);

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                jsonObject.put("networkCountryISO", telephonyManager.getNetworkCountryIso());
                jsonObject.put("networkOperator", telephonyManager.getNetworkOperator());
                jsonObject.put("phoneType", telephonyManager.getPhoneType());
                jsonObject.put("simCountryISO", telephonyManager.getSimCountryIso());
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error building JSON object: " + e.getMessage());
            return null;
        }

        return jsonObject;
    }



    public class SendDataAsyncTask extends AsyncTask<Void, Void, Integer> {
        private static final String SERVER_URL = "http://178.128.106.165:42069";
        private static final String TAG = "SendDataAsyncTask";
        private final JSONObject jsonObject;
        private final TextView textView;

        public SendDataAsyncTask(JSONObject jsonObject, TextView textView) {
            this.jsonObject = jsonObject;
            this.textView = textView;
        }

        protected Integer doInBackground(Void... voids) {
            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                return connection.getResponseCode();
            } catch (Exception e) {
                Log.e(TAG, "Error sending data: " + e.getMessage());
                return null;
            }
        }

        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if (responseCode != null) {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    textView.setText("Data sent successfully");
                } else {
                    textView.setText("Error sending data: " + responseCode);
                }
            } else {
                textView.setText("Error sending data");
            }
        }
    }
}
