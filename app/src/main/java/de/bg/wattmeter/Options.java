package de.bg.wattmeter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Options extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        TextView costsperkwh = (TextView) findViewById(R.id.options_costsperkwh);

        SharedPreferences settings = getSharedPreferences(
                getString(R.string.preferences), 0);
        float cperkwh = settings.getFloat("costaperkwh", 0f);
        costsperkwh.setText(new Float(cperkwh).toString());

        Button ok = (Button) findViewById(R.id.options_buttonok);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView costsperkwh = (TextView) findViewById(R.id.options_costsperkwh);
                SharedPreferences settings = getSharedPreferences(
                        getString(R.string.preferences), 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("costaperkwh",
                        new Float(costsperkwh.getText().toString()).floatValue());
                editor.commit();
                Bundle b = new Bundle();
                b.putBoolean("RET", true);
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button cancel = (Button) this.findViewById(R.id.options_buttoncancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });
    }
}