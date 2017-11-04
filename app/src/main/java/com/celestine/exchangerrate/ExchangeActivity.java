package com.celestine.exchangerrate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ExchangeActivity extends AppCompatActivity {
    private TextView code1,code2,rate;
    private EditText editText;
    private String base_code,code,price;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        code1=findViewById(R.id.code1);
        code2=findViewById(R.id.code2);
        rate=findViewById(R.id.rate);
        editText=findViewById(R.id.editText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setTitle("Currency Converter");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        extras=getIntent().getExtras();
        base_code=extras.getString("base_code");
        code=extras.getString("code");
        price=extras.getString("price");

        code1.setText(code+" : ");
        code2.setText(base_code+" : ");
        rate.setText(price);
        editText.setText("1");

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    String check =editText.getText().toString().trim();
                    if(check.isEmpty()){
                        rate.setText("0");
                    }
                    else {
                        Double amount = Double.valueOf(check);
                        Double cal = amount*Double.valueOf(price);
                        rate.setText(String.valueOf(cal));
                    }

                }
                else {
                    rate.setText("0");
                }
            }
        });

    }
}
