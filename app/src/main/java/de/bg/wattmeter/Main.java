package de.bg.wattmeter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Main extends ListActivity {
    ArrayAdapter<DeviceAide> aa;
    WattmeterModel wmm;
    int position;
    ArrayList<DeviceAide> darr;
    float cperkwh;
    private String activeDeviceId = "1";
    private String id;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(
                getString(R.string.preferences), 0);
        cperkwh = settings.getFloat("costaperkwh", 0f);

        wmm = new WattmeterModel(this);
        darr = wmm.getAllDevices();
        aa = new MyCustomAdapter(Main.this, R.layout.main, darr);
        setListAdapter(aa);
        registerForContextMenu(getListView());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        DeviceAide da = (DeviceAide) l.getItemAtPosition(position);
        activeDeviceId = da.getId();
        Intent myIntent = new Intent(Main.this, EditDevice.class);
        myIntent.putExtra("ID", da.getId());
        Main.this.startActivityForResult(myIntent, 1);
        this.position = position;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = new MenuInflater(getApplication());
        mi.inflate(R.menu.optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_device:
                Intent myIntent = new Intent(Main.this, EditDevice.class);
                myIntent.putExtra("ID", "");
                startActivityForResult(myIntent, 3);
                return true;
            case R.id.delete_device:
                wmm.delete(activeDeviceId);
                return true;
            case R.id.options:
                Intent myIntent1 = new Intent(Main.this, Options.class);
                startActivityForResult(myIntent1, 2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: // EditDevice
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String id = data.getExtras().getString("ID");
                        DeviceAide da = wmm.getDeviceById(id);
                        darr.set(position, da);
                        aa.notifyDataSetChanged();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case 3: // EditDevice new Device
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String id = data.getExtras().getString("ID");
                        DeviceAide da = wmm.getDeviceById(id);
                        darr.add(da);
                        aa.notifyDataSetChanged();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case 2: // Options
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        SharedPreferences settings = getSharedPreferences(
                                getString(R.string.preferences), 0);
                        cperkwh = settings.getFloat("costaperkwh", 0f);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case 4: // Camera Result
                Bitmap b = (Bitmap) data.getExtras().get("data");
                DeviceAide da = wmm.getDeviceById(this.id);
                da.setPicture(b);
                darr.set(this.position, da);
                aa.notifyDataSetChanged();
                wmm.update(da);
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_device:
                DeviceAide da = aa.getItem((int) info.id);
                wmm.delete(da.getId());
                darr.remove(da);
                aa.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void invokeCamera(int pos) {
        this.position = pos;
        DeviceAide da = aa.getItem((int) pos);
        this.id = da.getId();
        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 4);
    }

    // inner class für die Liste
    public class MyCustomAdapter extends ArrayAdapter<DeviceAide> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<DeviceAide> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.main, parent, false);

            DeviceAide da = getItem(position);

            TextView device = (TextView) row.findViewById(R.id.device);
            device.setText(getItem(position).getDevice());

            TextView usageperdayinours = (TextView) row
                    .findViewById(R.id.usageperdayinours);
            usageperdayinours.setText(new Integer(da.getAverageusage())
                    .toString() + " Ø Benutzung/Tag");

            TextView costsperour = (TextView) row
                    .findViewById(R.id.costsperour);
            costsperour.setText(da.getCostsPerOur(cperkwh) + " €/Stunde");

            TextView costsperday = (TextView) row
                    .findViewById(R.id.costsperday);
            costsperday.setText(da.getCostsPerDay(cperkwh) + " €/Tag");

            TextView costsperyear = (TextView) row
                    .findViewById(R.id.costsperyear);
            costsperyear.setText(da.getCostsPerYear(cperkwh) + " €/Jahr");

            TextView watt = (TextView) row.findViewById(R.id.watt);
            watt.setText(da.getPowerConsumptionStr());

            ImageView icon = (ImageView) row.findViewById(R.id.devicepicture);
            icon.setOnClickListener(new OnClickListener() {
                private int pos = position;

                @Override
                public void onClick(View v) {
                    invokeCamera(pos);
                }
            });

            Bitmap b = da.getPicture();
            if (b != null) {
                icon.setImageBitmap(b);
            } else {
                icon.setImageResource(R.drawable.icon);
            }

            return row;
        }
    }
}