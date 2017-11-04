package com.celestine.exchangerrate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.celestine.exchangerrate.app.AppConfig;
import com.celestine.exchangerrate.app.AppController;
import com.celestine.exchangerrate.app.CurrencyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String[] str = {"BTC", "USD", "EUR", "NGN", "AFN","EGP","DZD","AOA","ARS","EUR","CNY","BDT","GBP","BRL","JPY","KHR","INR","XAF","ETB","ETH","HKD"};
    private RecyclerView mRecyclerView;
    private List<CurrencyModel> notificationList = new ArrayList<>();
    private MyAdapter mAdapter;
    public String baseCurrency="BTC";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView=findViewById(R.id.recycleView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Exchange Rate");

        mRecyclerView.setHasFixedSize(false);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(notificationList,baseCurrency);
        mRecyclerView.setAdapter(mAdapter);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }

    private void form_url(String currnt){
        String my_api="https://min-api.cryptocompare.com/data/price?fsym="+currnt+"&tsyms=";
        for(int i=0; i<str.length;i++){
            my_api+=str[i]+",";
        }
        getAccessToken(my_api,baseCurrency);
    }

    private void getAccessToken(final String url,final String baseCurrency) {
        notificationList.clear();
        pDialog.setMessage("Please wait ...");
        showDialog();
        String tag_string_req = "req_fetch_result";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    hideDialog();
                    JSONObject jObj = new JSONObject(response);
                    for(int i=0; i<str.length;i++){
                        Double rate = jObj.getDouble(str[i]);
                        CurrencyModel product = new CurrencyModel(str[i],rate);
                        notificationList.add(product);
                    }
                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(), "Network Error or Something wrong with api", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                hideDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
        private List<CurrencyModel> myStoreModelList;
        private CurrencyModel myOffer;
        private String baseCurrency;

        public MyAdapter(List<CurrencyModel> mList,String baseCurrency) {
            this.myStoreModelList = mList;
            this.baseCurrency=baseCurrency;
        }


        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)) {
                return TYPE_HEADER;

            }

            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            if (viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items, viewGroup, false);
                return new ItemViewHolder(view);

            } else if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header, viewGroup, false);
                return new HeaderViewHolder(view);

            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ItemViewHolder) {
                myOffer = myStoreModelList.get(position-1);
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.code1.setText(baseCurrency);
                itemViewHolder.code2.setText(myOffer.getCurrency());
                itemViewHolder.price1.setText("1");
                itemViewHolder.price2.setText(String.valueOf(myOffer.getRate()));

            }


        }


        @Override
        public int getItemCount() {
            // Add two more counts to accomodate header and footer
            return this.myStoreModelList.size() + 1;
        }


        // The ViewHolders for Header, Item and Footer
        class HeaderViewHolder extends RecyclerView.ViewHolder{
            public View View;
            public Spinner spinner;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                View = itemView;
                spinner=View.findViewById(R.id.spinner);
                ArrayAdapter<String> adp1 = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item, str);
                adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adp1);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        baseCurrency = parent.getItemAtPosition(position).toString();
                        form_url(baseCurrency);
                    }
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });

            }
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public View View;
            public TextView code1,code2,price1,price2;
            public ItemViewHolder(View v) {
                super(v);
                View = v;
                v.setOnClickListener(this);
                code1= (TextView) View.findViewById(R.id.code1);
                code2= (TextView) View.findViewById(R.id.code2);
                price2= (TextView) View.findViewById(R.id.price2);
                price1= (TextView) View.findViewById(R.id.price1);
            }

            @Override
            public void onClick(View view) {
                myOffer = myStoreModelList.get(getAdapterPosition()-1);
                Intent showPhotoIntent = new Intent(MainActivity.this, ExchangeActivity.class);
                showPhotoIntent.putExtra("base_code", baseCurrency);
                showPhotoIntent.putExtra("code", myOffer.getCurrency());
                showPhotoIntent.putExtra("price", String.valueOf(myOffer.getRate()));
                startActivity(showPhotoIntent);

            }
        }

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
