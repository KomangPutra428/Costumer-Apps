package com.example.customerapps.menu_profil;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.customerapps.R;
import com.example.customerapps.httpstrustmanager.HttpsTrustManager;
import com.example.customerapps.menu_login.login;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class menu_profile extends AppCompatActivity {
    TextView idpelanggan;
    TextView namaoutlet;
    TextView segment;
    TextView pemilik;
    TextView rtrw;
    static TextView alamatdiganti;
    SharedPreferences sharedPreferences;
    TextView kota;
    TextView kecamatan;
    TextView kelurahan;
    TextView kodepos;
    static TextView telepon;
    static TextView alamatlengkap;
    static TextView idktp, idnpwp;
    static TextView disarankan;
    static String alamat;
    static String longitude, latitude;
    SweetAlertDialog pDialog;
    MaterialCardView ktp, npwp, logout, detail;
    LinearLayout update;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_profile);
        HttpsTrustManager.allowAllSSL();
        idktp = findViewById(R.id.idktp);
        idnpwp = findViewById(R.id.idnpwp);
        disarankan = findViewById(R.id.disarankan);
        alamatdiganti = findViewById(R.id.alamatdiganti);

        ktp = findViewById(R.id.ktp);
        npwp = findViewById(R.id.npwp);
        logout = findViewById(R.id.logout);

        detail = findViewById(R.id.detail);

        idpelanggan = findViewById(R.id.idpelanggan);
        namaoutlet = findViewById(R.id.namaoutlet);
        segment = findViewById(R.id.segment);
        pemilik = findViewById(R.id.pemilik);

        kota = findViewById(R.id.kota);
        kecamatan = findViewById(R.id.kecamatan);
        kelurahan = findViewById(R.id.kelurahan);
        kodepos = findViewById(R.id.kodepos);
        telepon = findViewById(R.id.telepon);
        alamatlengkap = findViewById(R.id.alamatlengkap);
        rtrw = findViewById(R.id.rtrw);

        getDisarankan();

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        ;

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.bottom_nav_home){
                    finish();
                    return true;
                } else {
                    return true;
                }
            }
        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(menu_profile.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Apakah anda yakin?")
                        .setContentText("Anda akan keluar dari aplikasi ini")
                        .setConfirmText("Yes")
                        .setCancelText("Cancel")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent inetnt = new Intent(getBaseContext(), login.class);
                                inetnt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(inetnt);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
        });

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szId", null);
        String rest = nik_baru;
        String[] parts = rest.split("-");
        String restnomor = parts[0];
        String restnomorbaru = restnomor.replace(" ", "");

        pDialog = new SweetAlertDialog(menu_profile.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_login?szId=" + nik_baru + "&kode_dms=" + restnomorbaru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                JSONArray movieArray = obj.getJSONArray("data");
                                for (int i = 0; i < movieArray.length(); i++) {

                                    final JSONObject movieObject = movieArray.getJSONObject(i);
                                    idpelanggan.setText(movieObject.getString("szId"));
                                    namaoutlet.setText(movieObject.getString("szName"));
                                    if(movieObject.getString("szHierarchyId").equals("")){
                                        segment.setText("-");
                                    } else {
                                        segment.setText(movieObject.getString("szHierarchyId"));
                                    }

                                    if(movieObject.getString("szContactPerson1").equals("")){
                                        pemilik.setText("-");
                                    } else {
                                        pemilik.setText(movieObject.getString("szContactPerson1"));
                                    }
                                    kota.setText(movieObject.getString("szCity"));
                                    kecamatan.setText(movieObject.getString("szDistrict"));
                                    kelurahan.setText(movieObject.getString("szSubDistrict"));
                                    kodepos.setText(movieObject.getString("szZipCode"));
                                    telepon.setText(movieObject.getString("szPhone1"));

                                    longitude = movieObject.getString("szLongitude");
                                    latitude = movieObject.getString("szLatitude");

                                    String rest1 = movieObject.getString("szAddress").trim();
                                    String[] parts1 = rest1.split("RT");
                                    String restnomor1 = parts1[0];
                                    alamatlengkap.setText(restnomor1.trim());

                                    getNote();

                                    String rest = movieObject.getString("szAddress");
                                    String[] parts = rest.split("RT");
                                    int size=parts.length;

                                    pDialog.dismissWithAnimation();

                                    if(size == 1){
                                        rtrw.setText("-");
                                    } else {
                                        String restnomor = parts[1];
                                        rtrw.setText("RT "+restnomor);
                                    }
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
                        pDialog.dismissWithAnimation();
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
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(menu_profile.this);
        requestQueue.add(stringRequest);

    }

    private void getNote() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String szId = sharedPreferences.getString("szId", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_history?szId="+ szId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                alamatdiganti.setText(movieObject.getString("szAddress"));

                                alamat = movieObject.getString("szAddress");


                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        disarankan.setText("-");
                    }
                })

        {
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
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(menu_profile.this);
        requestQueue.add(stringRequest);
    }

    private void getDisarankan() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String szId = sharedPreferences.getString("szId", null);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/rest_server_survey/utilitas/Customer/index_segment?szId="+ szId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                if(movieObject.getString("jenis_outlet").equals("1")) {
                                    disarankan.setText("STAR OUTLET");
                                } else if(movieObject.getString("jenis_outlet").equals("2")) {
                                    disarankan.setText("WHOLESALER");
                                } else if(movieObject.getString("jenis_outlet").equals("3")){
                                    disarankan.setText("MANAGE OUTLET");
                                } else if(movieObject.getString("jenis_outlet").equals("4")){
                                    disarankan.setText("AHS");
                                }



                            }


                        } catch(JSONException e){
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        disarankan.setText("-");
                    }
                })

        {
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
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(menu_profile.this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_profil);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String nik_baru = sharedPreferences.getString("szId", null);
        StringRequest kota = new StringRequest(Request.Method.GET, "https://hrd.tvip.co.id/mobile_eis_2/file_perid.php?dokumen_npwp="+nik_baru,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (String.valueOf(jsonArray).equals("[]")){
                                idktp.setText("Tidak Ada Dokumen");
                                idnpwp.setText("Tidak Ada Dokumen");
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    idktp.setText(jsonObject1.getString("nik_ktp"));
                                    idnpwp.setText(jsonObject1.getString("npwp"));

                                    if(jsonObject1.getString("nik_ktp").equals("null")){
                                        idktp.setText("Tidak Ada Dokumen");
                                    } else {
                                        idktp.setText(jsonObject1.getString("nik_ktp"));
                                    }

                                    if(jsonObject1.getString("npwp").equals("null")){
                                        idnpwp.setText("Tidak Ada Dokumen");
                                    } else {
                                        idnpwp.setText(jsonObject1.getString("npwp"));
                                    }

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


                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        kota.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestkota = Volley.newRequestQueue(this);
        requestkota.add(kota);
    }

}