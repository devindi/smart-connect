package com.devindi.connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class main extends Activity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    static final String POWER="POWER";
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        CheckBox power=(CheckBox)findViewById(R.id.power);
        power.setOnCheckedChangeListener(this);
        Button about = (Button)findViewById(R.id.info);
        about.setOnClickListener(this);
        settings.getBoolean(POWER, false);
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
        editor.putBoolean(POWER, value);
        editor.commit();
        if(value){
            startService(new Intent(this, SmartConnect.class));
        }else{
            stopService(new Intent(this, SmartConnect.class));
        }
    }

    public void onClick(View view) {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle("О программе")
                .setMessage("Приложение часовой сигнализирует(мелодия и/или вибрация) через заданный промежуток времени." +
                        "\n ©xotta6bl4 2012" +
                        "\n <a href=\"http://devindi.com\">devindi.com</a>")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                        //Stop the activity
                        // YourClass.this.finish();
                    }

                })
                .show();
    }
}