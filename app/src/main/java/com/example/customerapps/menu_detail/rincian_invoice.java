package com.example.customerapps.menu_detail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.example.customerapps.pojos.cicilan_pojos;
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

public class rincian_invoice extends AppCompatActivity {

    TextView tanggal_co, no_CO, noInvoice, noPembayaran,
            sku, qty, noSO, noDO,
            dueDate, tagihan_totalinvoice, detail_sisatagihan, date_closed;
    Chip status;
    ListView list_tagihan;
    List<cicilan_pojos> cicilanPojosList = new ArrayList<>();

    ListViewAdapterTagihan adapter;

    RelativeLayout layoutDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_invoice);
        status = findViewById(R.id.status_tagihan);
        tanggal_co = findViewById(R.id.tanggal_co);
        no_CO = findViewById(R.id.no_CO_tagihan);
        noInvoice = findViewById(R.id.noInvoice);
        noPembayaran = findViewById(R.id.noPembayaran);
        sku = findViewById(R.id.sku_tagihan);
        qty = findViewById(R.id.qty_tagihan);
        date_closed = findViewById(R.id.date_closed);
        noSO = findViewById(R.id.noSO);
        noDO = findViewById(R.id.noDO);
        layoutDate = findViewById(R.id.layoutDate);
        dueDate = findViewById(R.id.dueDate);
        tagihan_totalinvoice = findViewById(R.id.tagihan_totalinvoice);
        list_tagihan = findViewById(R.id.list_tagihan);
        detail_sisatagihan = findViewById(R.id.detail_sisatagihan);

        if(menu_invoice.status.getText().toString().equals("Lunas")){
            status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
            status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
            status.setTextColor(Color.parseColor("#2ECC71"));
            date_closed.setText(convertFormatHour(getIntent().getStringExtra("dtmClosed")));
        } else {
            status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
            status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
            status.setTextColor(Color.parseColor("#FB4141"));
            layoutDate.setVisibility(View.GONE);
        }

        status.setText(menu_invoice.status.getText().toString());
        tanggal_co.setText(convertFormatHour(getIntent().getStringExtra("tgl_tagihan")));
        no_CO.setText(menu_invoice.detail_noco.getText().toString());
        noInvoice.setText(menu_invoice.detail_invoice.getText().toString());
        noPembayaran.setText(getIntent().getStringExtra("no_tagihan"));

        sku.setText(menu_invoice.detail_sku.getText().toString());
        qty.setText(menu_invoice.detail_qty.getText().toString());

        noSO.setText(getIntent().getStringExtra("noSO"));
        noDO.setText(getIntent().getStringExtra("noDO"));

        dueDate.setText(menu_invoice.detail_duedate.getText().toString());
        tagihan_totalinvoice.setText(menu_invoice.detail_totalinvoice.getText().toString());

        detail_sisatagihan.setText(menu_invoice.detail_sisapembayaran.getText().toString());

        getDetailPembayaran();

    }

    private void getDetailPembayaran() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_customer_apps/Customer_apps/index_DetailListInvoice?szDocId="+menu_invoice.detail_invoice.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);

                                final cicilan_pojos movieItem = new cicilan_pojos(
                                        movieObject.getString("tgl_payment"),
                                        movieObject.getString("payment"),
                                        movieObject.getString("no_payment"));

                                cicilanPojosList.add(movieItem);

                                adapter = new ListViewAdapterTagihan(cicilanPojosList, getApplicationContext());
                                list_tagihan.setAdapter(adapter);
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

    public static class ListViewAdapterTagihan extends ArrayAdapter<cicilan_pojos> {
        private List<cicilan_pojos> cicilan_pojoss;

        private Context context;

        public ListViewAdapterTagihan(List<cicilan_pojos> cicilan_pojoss, Context context) {
            super(context, R.layout.layout_list_tagihan, cicilan_pojoss);
            this.cicilan_pojoss = cicilan_pojoss;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.layout_list_tagihan, null, true);

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