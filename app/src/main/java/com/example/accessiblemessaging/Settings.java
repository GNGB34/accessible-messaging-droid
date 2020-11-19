package com.example.accessiblemessaging;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class Settings extends AppCompatActivity {
    String[] languageOptions={"English","Spanish"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        Spinner languageSelect= (Spinner) findViewById(R.id.languageSpin);
        ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languageOptions);
        array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSelect.setAdapter(array_adapter);


    }
}