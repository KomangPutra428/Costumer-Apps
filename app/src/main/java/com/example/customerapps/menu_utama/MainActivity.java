package com.example.customerapps.menu_utama;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.customerapps.menu_detail.menu_invoice;
import com.example.customerapps.menu_profil.menu_profile;
import com.example.customerapps.pojos.co_pojos;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    TextView text_nama;
    TextView text_id;

    EditText pencarian;

    TextView totalcicilan;

    TextView tanggal;

    String first_date;
    String last_date;
    static String bClosed;

    SharedPreferences sharedPreferences;

    ListView list_co;

    ListViewAdapterCO adapter;

    TabLayout tablayout;

    SweetAlertDialog pDialog;

    BottomNavigationView bottomNavigationView;

    MaterialButton buttonFilter;

    List<co_pojos> coPojosList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpsTrustManager.allowAllSSL();
        text_nama = findViewById(R.id.text_nama);
        text_id = findViewById(R.id.text_id);
        tanggal = findViewById(R.id.tanggal);
        list_co = findViewById(R.id.list_co);
        tablayout = findViewById(R.id.tablayout);
        totalcicilan = findViewById(R.id.totalcicilan);
        buttonFilter = findViewById(R.id.buttonFilter);
        pencarian = findViewById(R.id.pencarian);

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");
        String currentDateandTime2 = sdf2.format(new Date());
        tanggal.setText(currentDateandTime2 + " - " + currentDateandTime2);





        sharedPreferences = getSharedPreferences("user_details", 0);

        String szId = sharedPreferences.getString("szId", (String) null);
        String nama = sharedPreferences.getString("szName", (String) null);

        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime3 = sdf3.format(new Date());

        first_date = currentDateandTime3;
        last_date = currentDateandTime3;

        bClosed = "0";
        getCO(szId, first_date, last_date, bClosed);

        text_nama.setText(szId);
        text_id.setText(nama);

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFilter.setEnabled(false);
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.filter_co);

                Window window = dialog.getWindow();
                window.setLayout(MATCH_PARENT, WRAP_CONTENT);


                RadioGroup filterbulantanggal = dialog.findViewById(R.id.filterbulantanggal);
                EditText tanggalawal = dialog.findViewById(R.id.tanggalawal);
                EditText tanggalakhir = dialog.findViewById(R.id.tanggalakhir);

                MaterialButton lihat = dialog.findViewById(R.id.lihat);
                MaterialButton batal = dialog.findViewById(R.id.batal);

                RadioButton bulanini = dialog.findViewById(R.id.bulanini);
                RadioButton bulanlalu = dialog.findViewById(R.id.bulanlalu);
                RadioButton pilihtanggal = dialog.findViewById(R.id.pilihtanggal);



                tanggalakhir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();

                        // on below line we are getting
                        // our day, month and year.
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int day = c.get(Calendar.DAY_OF_MONTH);

                        // on below line we are creating a variable for date picker dialog.
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                // on below line we are passing context.
                                MainActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        // on below line we are setting date to our edit text.
                                        tanggalakhir.setText(convertFormatTanggal(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));

                                    }
                                },
                                // on below line we are passing year,
                                // month and day for selected date in our date picker.
                                year, month, day);
                        // at last we are calling show to
                        // display our date picker dialog.
                        datePickerDialog.show();
                    }
                });

                tanggalawal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();

                        // on below line we are getting
                        // our day, month and year.
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int day = c.get(Calendar.DAY_OF_MONTH);

                        // on below line we are creating a variable for date picker dialog.
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                // on below line we are passing context.
                                MainActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        // on below line we are setting date to our edit text.
                                        tanggalawal.setText(convertFormatTanggal(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));

                                    }
                                },
                                // on below line we are passing year,
                                // month and day for selected date in our date picker.
                                year, month, day);
                        // at last we are calling show to
                        // display our date picker dialog.
                        datePickerDialog.show();
                    }
                });

                LinearLayout layouttanggal = dialog.findViewById(R.id.layouttanggal);
                layouttanggal.setVisibility(View.GONE);

                filterbulantanggal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        if(checkedId==R.id.bulanini) {
                            layouttanggal.setVisibility(View.GONE);
                        } else if(checkedId==R.id.bulanlalu) {
                            layouttanggal.setVisibility(View.GONE);
                        } else if(checkedId==R.id.pilihtanggal) {
                            layouttanggal.setVisibility(View.VISIBLE);
                        }


                    }
                });

                lihat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bulanini.isChecked()){
                            dialog.dismiss();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            Date firstDate = calendar.getTime();

                            calendar.add(Calendar.MONTH, 1);
                            calendar.add(Calendar.DATE, -1);
                            Date lastDate = calendar.getTime();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Customize the date format as needed
                            String formattedFirstDate = sdf.format(firstDate);
                            String formattedLastDate = sdf.format(lastDate);

                            first_date = formattedFirstDate;
                            last_date = formattedLastDate;

                            tanggal.setText(bulanIni(formattedFirstDate) + " - " + bulanIni(formattedLastDate));
                            buttonFilter.setEnabled(true);
                            getCO(szId, first_date, last_date, bClosed);
                        } else if(bulanlalu.isChecked()){
                            dialog.dismiss();
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.MONTH, -1);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            Date firstDate = calendar.getTime();

                            calendar.add(Calendar.MONTH, 1);
                            calendar.add(Calendar.DATE, -1);
                            Date lastDate = calendar.getTime();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Customize the date format as needed
                            String formattedFirstDate = sdf.format(firstDate);
                            String formattedLastDate = sdf.format(lastDate);

                            first_date = formattedFirstDate;
                            last_date = formattedLastDate;
                            buttonFilter.setEnabled(true);
                            tanggal.setText(bulanIni(formattedFirstDate) + " - " + bulanIni(formattedLastDate));

                            getCO(szId, first_date, last_date, bClosed);
                        } else if (pilihtanggal.isChecked()){
                            tanggalawal.setError(null);
                            tanggalakhir.setError(null);
                            if(tanggalawal.getText().toString().length() == 0){
                                tanggalawal.setError("Pilih Tanggal Awal");
                            } else if(tanggalakhir.getText().toString().length() == 0){
                                tanggalakhir.setError("Pilih Tanggal Akhir");
                            } else {
                                String dateBeforeStr = tanggalawal.getText().toString();
                                String dateAfterStr = tanggalakhir.getText().toString();

                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                                try {
                                    Date dateBefore = sdf.parse(dateBeforeStr);
                                    Date dateAfter = sdf.parse(dateAfterStr);

                                    if (dateBefore.before(dateAfter)) {
                                        buttonFilter.setEnabled(true);
                                        first_date = pilihTanggal(tanggalawal.getText().toString());
                                        last_date = pilihTanggal(tanggalakhir.getText().toString());
                                        tanggal.setText(bulanIniReverse(tanggalawal.getText().toString()) + " - " + bulanIniReverse(tanggalakhir.getText().toString()));
                                        dialog.dismiss();
                                        getCO(szId, first_date, last_date, bClosed);
                                    } else if (dateBefore.after(dateAfter)) {
                                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Tanggal Awal Tidak Boleh Lebih Dari Tanggal Akhir")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        buttonFilter.setEnabled(true);
                                        first_date = pilihTanggal(tanggalawal.getText().toString());
                                        last_date = pilihTanggal(tanggalakhir.getText().toString());
                                        tanggal.setText(bulanIniReverse(tanggalawal.getText().toString()) + " - " + bulanIniReverse(tanggalakhir.getText().toString()));
                                        dialog.dismiss();
                                        getCO(szId, first_date, last_date, bClosed);
                                    }
                                } catch (ParseException e) {
                                    // Handle parsing errors
                                }


                            }
                        } else {
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Pilih Filter Tanggal")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        }
                    }
                });

                batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        buttonFilter.setEnabled(true);
                    }
                });


                dialog.show();
            }
        });

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.bottom_nav_home){
                    return true;
                } else {
                    startActivity(new Intent(getApplicationContext(), menu_profile.class));
                    overridePendingTransition(0,0);
                    return true;
                }
            }
        });

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if(position == 0){
                    bClosed = "0";
                } else if(position == 1) {
                    bClosed = "1";
                }
                getCO(szId, first_date, last_date, bClosed);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    bClosed = "0";
                } else if(position == 1) {
                    bClosed = "1";
                }
                getCO(szId, first_date, last_date, bClosed);
            }
        });

        list_co.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tglInvoice = ((co_pojos) parent.getItemAtPosition(position)).getTanggal_co();
                String noCo = ((co_pojos) parent.getItemAtPosition(position)).getNo_co();
                String noInvoice = ((co_pojos) parent.getItemAtPosition(position)).getNo_invoice();
                String sku = ((co_pojos) parent.getItemAtPosition(position)).getSzName();
                String qty = ((co_pojos) parent.getItemAtPosition(position)).getDecQty();
                String duedate = ((co_pojos) parent.getItemAtPosition(position)).getDtmDue();

                String no_so = ((co_pojos) parent.getItemAtPosition(position)).getNo_so();
                String no_do = ((co_pojos) parent.getItemAtPosition(position)).getNo_do();
                String dtmClosed = ((co_pojos) parent.getItemAtPosition(position)).getDtmClosed();

                Intent intent = new Intent(getApplicationContext(), menu_invoice.class);
                intent.putExtra("tgl_invoice", tglInvoice);
                intent.putExtra("noCo", noCo);
                intent.putExtra("noInvoice", noInvoice);

                intent.putExtra("sku", sku);
                intent.putExtra("qty", qty);


                intent.putExtra("duedate", duedate);

                intent.putExtra("noso", no_so);
                intent.putExtra("nodo", no_do);
                intent.putExtra("dtmClosed", dtmClosed);


                startActivity(intent);

            }
        });




    }

    private void getCO(String szId, String date1, String date2, String bClosed) {
        list_co.setVisibility(View.GONE);
        adapter = new ListViewAdapterCO(coPojosList, getApplicationContext());
        adapter.clear();
        totalcicilan.setText("Rp. 0");

        System.out.println("Link CO = " + "https://apisec.tvip.co.id/rest_server_customer_apps/Customer_apps/index_CO?szCustomerId="+szId+"&date1="+first_date+"&date2="+last_date);
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_customer_apps/Customer_apps/index_CO?szCustomerId="+szId+"&date1="+first_date+"&date2="+last_date,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        list_co.setVisibility(View.VISIBLE);
                        try {
                            int number = 0;
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");
                            for (int i = 0; i < movieArray.length(); i++) {
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                final co_pojos movieItem = new co_pojos(
                                        movieObject.getString("no_co"),
                                        movieObject.getString("no_invoice"),
                                        movieObject.getString("szName"),
                                        movieObject.getString("decQty"),
                                        movieObject.getString("dtmDue"),
                                        movieObject.getString("nom_inv"),
                                        convertFormat(movieObject.getString("tanggal_co")),
                                        movieObject.getString("no_so"),
                                        movieObject.getString("no_do"),
                                        movieObject.getString("dtmClosed"));

                                coPojosList.add(movieItem);


                                list_co.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                if(!movieObject.getString("bClosed").equals(bClosed)){
                                    coPojosList.remove(movieItem);
                                    adapter.notifyDataSetChanged();
                                }

                                pencarian.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        adapter.getFilter().filter(charSequence);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                    }
                                });




                            }
                            int total = 0;
                            int totalvalue;
                            for(int i = 0; i < coPojosList.size();i++){
                                String szId = adapter.getItem(i).getNom_inv();
                                String[] parts = szId.split("\\.");
                                String szIdSlice = parts[0];

                                totalvalue = Integer.parseInt(szIdSlice);
                                total+=totalvalue;
                                totalcicilan.setText(String.valueOf(formatRupiah(total)));

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

    public static class ListViewAdapterCO extends ArrayAdapter<co_pojos> {
        private List<co_pojos> co_pojoss;

        private Context context;

        public ListViewAdapterCO(List<co_pojos> co_pojoss, Context context) {
            super(context, R.layout.layout_list_co, co_pojoss);
            this.co_pojoss = co_pojoss;
            this.context = context;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);

            View listViewItem = inflater.inflate(R.layout.layout_list_co, null, true);

            TextView tanggal_co = listViewItem.findViewById(R.id.tanggal_co);
            TextView no_co = listViewItem.findViewById(R.id.no_co);
            TextView sku = listViewItem.findViewById(R.id.sku);
            TextView qty = listViewItem.findViewById(R.id.qty);
            Chip duedate = listViewItem.findViewById(R.id.duedate);
            TextView cicilan = listViewItem.findViewById(R.id.cicilan);


            co_pojos data = co_pojoss.get(position);

            tanggal_co.setText(data.getTanggal_co());
            no_co.setText(data.getNo_co());
            sku.setText("SKU : " +data.getSzName());
            qty.setText("QTY : " + data.getDecQty());

            if(bClosed.equals("0")){
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                String before = sdf.format(new Date());

                String dateBeforeStr = before; // Replace with your date string
                String dateAfterStr = convertFormatHour(data.getDtmDue());  // Replace with your date string

                try {
                    Date dateBefore = sdf.parse(dateBeforeStr);
                    Date dateAfter = sdf.parse(dateAfterStr);

                    long timeDifferenceMillis = dateAfter.getTime() - dateBefore.getTime();
                    long daysDifference = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);



                    // Compare the two dates
                    if (dateBefore.after(dateAfter)) {
                        duedate.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                        duedate.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                        duedate.setTextColor(Color.parseColor("#FB4141"));
                        duedate.setText("(" + "H" + String.valueOf(daysDifference) + ")" +convertFormatHour(data.getDtmDue()));
                    } else if (dateBefore.before(dateAfter)) {
                        duedate.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#AFC3D5")));
                        duedate.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#EDF7FF")));
                        duedate.setTextColor(Color.parseColor("#0F4C81"));
                        duedate.setText(convertFormatHour(data.getDtmDue()));
                    } else {
                        duedate.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#AFC3D5")));
                        duedate.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#EDF7FF")));
                        duedate.setTextColor(Color.parseColor("#0F4C81"));
                        duedate.setText(convertFormatHour(data.getDtmDue()));

                    }
                } catch (ParseException e) {
                    System.err.println("Date parsing error: " + e.getMessage());
                }


            } else {
                duedate.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                duedate.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                duedate.setTextColor(Color.parseColor("#2ECC71"));
                duedate.setText(convertFormatHour(data.getDtmDue()));

            }

            String szId = data.getNom_inv();
            String[] parts = szId.split("\\.");
            String szIdSlice = parts[0];
            cicilan.setText(String.valueOf(formatRupiah(Integer.parseInt(szIdSlice))));


            return listViewItem;
        }
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

    public static String convertFormat(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    public static String bulanIni(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("dd MMM yy");
        return convetDateFormat.format(date);
    }

    public static String bulanIniReverse(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("dd MMM yy");
        return convetDateFormat.format(date);
    }

    public static String pilihTanggal(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return convetDateFormat.format(date);
    }


    public static String convertFormatTanggal(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
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

    public static String formatRupiah(int amount) {
        // Create a DecimalFormat with the appropriate pattern and locale
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        DecimalFormat df = new DecimalFormat("###,###,###,###", symbols);

        // Format the integer amount to a Rupiah string
        return "Rp " + df.format(amount);
    }

    @Override
    public void onResume(){
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);

    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Apakah anda yakin?")
                .setContentText("Anda akan keluar dari aplikasi ini")
                .setConfirmText("Yes")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        finishAffinity();
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

}