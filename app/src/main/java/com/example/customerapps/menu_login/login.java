package com.example.customerapps.menu_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.customerapps.menu_utama.MainActivity;
import com.example.customerapps.R;
import com.example.customerapps.httpstrustmanager.HttpsTrustManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class login extends AppCompatActivity {
    EditText no_pelanggan;
    MaterialButton login;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        HttpsTrustManager.allowAllSSL();
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        no_pelanggan = findViewById(R.id.no_pelanggan);
        login = findViewById(R.id.login);
        
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(no_pelanggan.getText().toString().length() == 0){
                    no_pelanggan.setError("Masukkan Nomor Pelanggan");
                } else {
                    doLogin();
                }
            }
        });
    }

    private void doLogin() {

        final SweetAlertDialog pDialog = new SweetAlertDialog(login.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_customer_apps/Customer_apps/index_toko?szId=" + no_pelanggan.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {

                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("szId", movieObject.getString("szId"));
                                    editor.putString("szName", movieObject.getString("szName"));
                                    editor.apply();

                                    new SweetAlertDialog(login.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setContentText("Selamat Datang")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    pDialog.dismissWithAnimation();
                                                    sDialog.dismissWithAnimation();

                                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(i);


                                                }
                                            })
                                            .show();




                                    pDialog.cancel();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.cancel();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(login.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
}