package rtl.com.beaconpoc;

import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "TefyMainActivity";
    private BeaconManager beaconManager;
    private ArrayAdapter<String> mBeaconsAdapter;
    private Context self = this;
    //private List<String> id1Beacons = new ArrayList<>();
    List<String> id1Beacons;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) this.findViewById(R.id.listview_beacon);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:8-9=0215,i:10-13,i:14-15,i:16-17,p:18-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-12"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setBackgroundScanPeriod(10000l);
        beaconManager.setBackgroundBetweenScanPeriod(180000l);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "onBeaconServiceConnect");
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didExitRegion(Region region) {
            }

            @Override
            public void didEnterRegion(Region region) {
                try {
                    beaconManager.startRangingBeaconsInRegion(new Region("beaconsNelli", null, null, null));
                    beaconManager.addRangeNotifier(new RangeNotifier() {
                        @Override
                        public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                            for (Beacon beacon : collection) {
                                if (beacon.getDistance() < 1.0) {
                                    Toast.makeText(self, "beacon is not in range 1m", Toast.LENGTH_LONG).show();
                                }
                                Log.i(TAG, "Beacon info: " + beacon.getId1().toString() + ". About " + beacon.getDistance() + " meters away.");
                                id1Beacons = new ArrayList<>();
                                id1Beacons.add(beacon.getId1().toString() + "\n" + beacon.getDistance() + " meters away");
                                mBeaconsAdapter = new ArrayAdapter<String>(
                                        self,
                                        R.layout.list_item_beacon,
                                        R.id.list_item_beacon_textview,
                                        id1Beacons
                                );
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listView.setAdapter(mBeaconsAdapter);
                                }
                            });
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i(TAG, "mahita, tsy mahita...");
            }
        });
        /*beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                for (Beacon beacon : collection) {
                    if (beacon.getDistance() < 1.0) {
                        MainActivity.showAlert(self, "I just saw a beacon", "Beacon detection");
                    }
                    Log.i(TAG, "Beacon info: " + beacon.getId1().toString() + ". About " + beacon.getDistance() + " meters away.");
                    id1Beacons = new ArrayList<>();
                    id1Beacons.add(beacon.getId1().toString() + "\n" + beacon.getDistance() + " meters away");
                    mBeaconsAdapter = new ArrayAdapter<String>(
                            self,
                            R.layout.list_item_beacon,
                            R.id.list_item_beacon_textview,
                            id1Beacons
                    );
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(mBeaconsAdapter);
                    }
                });
            }
        });*/
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("beaconsNelli", null, null, null));
            //beaconManager.startRangingBeaconsInRegion(new Region("beaconsNelli", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void showAlert(Context mContext, String message, String title) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}
