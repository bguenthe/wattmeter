package de.bg.wattmeter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EditDevice extends Activity {
    WattmeterModel wmm;
    DeviceAide da;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editdevice);
        Bundle b = getIntent().getExtras();
        String id = b.getString("ID");
        wmm = new WattmeterModel(this);

        TextView device = (TextView) this.findViewById(R.id.editdevice_device);
        TextView watt = (TextView) this.findViewById(R.id.editdevice_watt);
        TextView averageusage = (TextView) findViewById(R.id.editdevice_averageusage);

        if (id.equals("")) { // Neues device
            device.setText("");
            watt.setText("");
            averageusage.setText("");
            da = new DeviceAide();
        } else {
            da = wmm.getDeviceById(id);
            device.setText(da.getDevice());
            watt.setText(new Float(da.getPowerConsumption()).toString());
            averageusage.setText(new Integer(da.getAverageusage()).toString());
        }

        Button save = (Button) findViewById(R.id.buttonsave);
        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView device = (TextView) findViewById(R.id.editdevice_device);
                TextView watt = (TextView) findViewById(R.id.editdevice_watt);
                TextView averageusage = (TextView) findViewById(R.id.editdevice_averageusage);

                da.setDevice(device.getText().toString());
                da.setPowerConsumption(new Float(watt.getText().toString()).floatValue());
                da.setAverageUsage(new Integer(averageusage.getText().toString()).intValue());
                da.setPicture(null);
                if (da.getId().equals("")) {
                    wmm.insert(da);
                } else {
                    wmm.update(da);
                }
                Bundle b = new Bundle();
                b.putCharSequence("ID", da.getId());
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button cancel = (Button) this.findViewById(R.id.buttoncancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });
    }
}