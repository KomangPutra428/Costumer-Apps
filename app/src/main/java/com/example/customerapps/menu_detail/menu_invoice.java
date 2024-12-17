package com.example.customerapps.menu_detail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.example.customerapps.menu_utama.MainActivity;
import com.example.customerapps.pojos.cicilan_pojos;
import com.example.customerapps.pojos.co_pojos;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class menu_invoice extends AppCompatActivity {
    TextView tgl_invoice;
    static TextView detail_noco;
    static TextView detail_invoice;
    static TextView detail_sku;
    static TextView detail_qty;
    static TextView detail_duedate;
    static TextView detail_totalinvoice;
    TextView detail_totalpembayaran;
    static TextView detail_sisapembayaran;

    SweetAlertDialog pDialog;

    ListView list_cicilan;

    static Chip status;

    List<cicilan_pojos> cicilanPojosList = new ArrayList<>();

    ListViewAdapterCicilan adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_invoice);
        tgl_invoice = findViewById(R.id.tgl_invoice);
        detail_noco = findViewById(R.id.detail_noco);
        detail_invoice = findViewById(R.id.detail_invoice);
        detail_sku = findViewById(R.id.detail_sku);
        detail_qty = findViewById(R.id.detail_qty);
        detail_duedate = findViewById(R.id.detail_duedate);
        detail_totalinvoice = findViewById(R.id.detail_totalinvoice);
        detail_totalpembayaran = findViewById(R.id.detail_totalpembayaran);
        detail_sisapembayaran = findViewById(R.id.detail_sisapembayaran);
        status = findViewById(R.id.status);
        list_cicilan = findViewById(R.id.list_cicilan);

        tgl_invoice.setText(getIntent().getStringExtra("tgl_invoice"));
        detail_noco.setText(getIntent().getStringExtra("noCo"));
        detail_invoice.setText(getIntent().getStringExtra("noInvoice"));

        detail_sku.setText(getIntent().getStringExtra("sku"));
        detail_qty.setText(getIntent().getStringExtra("qty"));

        detail_duedate.setText(convertFormatHour(getIntent().getStringExtra("duedate")));
        
        getDetailPembayaran(getIntent().getStringExtra("noInvoice"));

        list_cicilan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tglPayment = ((cicilan_pojos) parent.getItemAtPosition(position)).getTgl_payment();
                String noPayment = ((cicilan_pojos) parent.getItemAtPosition(position)).getNo_payment();
                String payment = ((cicilan_pojos) parent.getItemAtPosition(position)).getPayment();

                Intent intent = new Intent(getApplicationContext(), rincian_invoice.class);

                intent.putExtra("tgl_tagihan", tglPayment);
                intent.putExtra("no_tagihan", noPayment);
                intent.putExtra("tagihan", payment);

                intent.putExtra("noSO", getIntent().getStringExtra("noso"));
                intent.putExtra("noDO", getIntent().getStringExtra("nodo"));
                intent.putExtra("dtmClosed", getIntent().getStringExtra("dtmClosed"));



                startActivity(intent);

            }
        });

    }

    private void getDetailPembayaran(String noInvoice) {
        pDialog = new SweetAlertDialog(menu_invoice.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_customer_apps/Customer_apps/index_DetailListInvoice?szDocId="+noInvoice,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);


                                String total_invoice = String.valueOf(formatRupiah(Integer.parseInt(movieObject.getString("total_invoice"))));
                                String sisa_invoice = String.valueOf(formatRupiah(Integer.parseInt(movieObject.getString("sisa_invoice"))));
                                String total_pembayaran = String.valueOf(formatRupiah(Integer.parseInt(movieObject.getString("total_pembayaran"))));

                                detail_totalinvoice.setText(total_invoice);
                                detail_totalpembayaran.setText(total_pembayaran);
                                detail_sisapembayaran.setText(sisa_invoice);

                                if(movieObject.getString("total_pembayaran").equals("0")){
                                    status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                    status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                    status.setTextColor(Color.parseColor("#FB4141"));
                                    status.setText("Belum Bayar");
                                } else if(!movieObject.getString("total_invoice").equals(movieObject.getString("total_pembayaran"))){
                                    status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                    status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                    status.setTextColor(Color.parseColor("#FB4141"));
                                    status.setText("Belum Lunas");
                                }  else if(movieObject.getString("total_invoice").equals(movieObject.getString("total_pembayaran"))){
                                    status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                    status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                    status.setTextColor(Color.parseColor("#2ECC71"));
                                    status.setText("Lunas");
                                }

                                final cicilan_pojos movieItem = new cicilan_pojos(
                                        movieObject.getString("tgl_payment"),
                                        movieObject.getString("payment"),
                                        movieObject.getString("no_payment"));

                                cicilanPojosList.add(movieItem);

                                adapter = new ListViewAdapterCicilan(cicilanPojosList, getApplicationContext());
                                list_cicilan.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                if(movieObject.getString("payment").equals("null")){
                                    cicilanPojosList.remove(movieItem);
                                    adapter.notifyDataSetChanged();
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
                        pDialog.dismissWithAnimation();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static class ListViewAdapterCicilan extends ArrayAdapter<cicilan_pojos> {
        private List<cicilan_pojos> cicilan_pojoss;

        private Context context;

        public ListViewAdapterCicilan(List<cicilan_pojos> cicilan_pojoss, Context context) {
            super(context, R.layout.layout_list_cicilan, cicilan_pojoss);
            this.cicilan_pojoss = cicilan_pojoss;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.layout_list_cicilan, null, true);

            TextView tanggal = listViewItem.findViewById(R.id.tanggal);
            TextView text_cicilan = listViewItem.findViewById(R.id.text_cicilan);

            cicilan_pojos data = cicilan_pojoss.get(position);

            tanggal.setText(convertFormatHour(data.getTgl_payment()));
            text_cicilan.setText("+ " + formatRupiah(Integer.parseInt(data.getPayment())));




            return listViewItem;
        }
    }

    public static String formatRupiah(int amount) {
        // Create a DecimalFormat with the appropriate pattern and locale
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        DecimalFormat df = new DecimalFormat("###,###,###,###", symbols);

        // Format the integer amount to a Rupiah string
        return "Rp " + df.format(amount);
    }

    public static String convertFormatHour(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return convetDateFormat.format(date);
    }
}