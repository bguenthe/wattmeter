package de.bg.wattmeter;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

public class DeviceAide {
    private String id;
    private String device;
    private float powerconsumption;
    private int averageusage;
    private Bitmap picture;

    DecimalFormat df = new DecimalFormat("0.00");

    public DeviceAide() {
        id = "";
        device = "";
        powerconsumption = 0f;
        averageusage = 0;
        picture = null;
    }

    public DeviceAide(Cursor c) {
        id = c.getString(0);
        device = c.getString(1);
        powerconsumption = c.getFloat(2);
        averageusage = c.getInt(3);
        if (!c.isNull(4)) {
            byte[] ba = c.getBlob(4);
            picture = BitmapFactory.decodeByteArray(ba, 0, ba.length);
        } else {
            picture = null;
        }
    }

    public String getId() {
        return id;
    }

    public String getDevice() {
        return device;
    }

    public float getPowerConsumption() {
        return powerconsumption;
    }

    public int getAverageusage() {
        return averageusage;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setPowerConsumption(float powerconsumption) {
        this.powerconsumption = powerconsumption;
    }

    public void setAverageUsage(int averageusage) {
        this.averageusage = averageusage;
    }

    public void setId(long deviceId) {
        id = new Long(deviceId).toString();
    }

    public void setPicture(Bitmap b) {
        this.picture = b;
    }

    public Bitmap getPicture() {
        return this.picture;
    }

    public String getCostsPerOur(float cperkwh) {
        float costsperour = cperkwh * ((float) powerconsumption / 1000) / 100;

        return df.format(costsperour);
    }

    public String getCostsPerDay(float cperkwh) {
        float costsperday = cperkwh * ((float) powerconsumption / 1000) * 24 / 100;

        return df.format(costsperday);

    }

    public String getCostsPerYear(float cperkwh) {
        float costsperyear = cperkwh * ((float) powerconsumption / 1000) * 24 / 100
                * 365;

        return df.format(costsperyear);
    }

    public String getAverageCostsPerDay(float cperkwh) {
        float costsperday = cperkwh * ((float) powerconsumption / 1000) / 100
                * averageusage;

        return df.format(costsperday);
    }

    public String getAverageCostsPerYear(float cperkwh) {
        float costsperyear = cperkwh * ((float) powerconsumption / 1000) / 100
                * averageusage * 365;

        return df.format(costsperyear);
    }

    public String getPowerConsumptionStr() {
        DecimalFormat df = new DecimalFormat("0000.0");
        return df.format(powerconsumption);
    }

    public String getJSON() {
        JSONObject object = new JSONObject();

        try {
            object.put("id", id);
            object.put("device", device);
            object.put("powerconsumption", new Float(powerconsumption));
            object.put("averageusage", new Float(averageusage));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (picture != null) {
                picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

                object.put("picture", imageEncoded);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }
}