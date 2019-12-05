package com.example.bluetoothex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaseInterface {

    Button button_start;
    Button button_stop;

    RecyclerView mRecyclerView;
    private ArrayList<Scan> mArrayList;
    String name, address, rssi_, uuid, UUID;
    List<ParcelUuid> uuids;
    private ScanAdapter mAdapter;

    //롤리팝미만
    BluetoothAdapter mBluetoothAdapter;
    //롤리팝이상
    BluetoothLeScanner mBluetoothLeScanner;

    private static int REQUEST_ACCESS_FINE_LOCATION = 1000;
    int REQUESTE_ENABLE = 0;
    private boolean isScanning = false;
    boolean gps = false;
    boolean bluetooth = false;

    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initItems();
        checkSDKVersion();
        initListener();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void initViews() {
        button_start = findViewById(R.id.button_start);
        button_stop = findViewById(R.id.button_stop);
        mRecyclerView = findViewById(R.id.recyclerview_main_list);
    }

    @Override
    public void initListener() {
        button_start.setOnClickListener(buttonStartListener);
        button_stop.setOnClickListener(buttonStopListener);
    }

    @Override
    public void initItems() {

        mRecyclerView = findViewById(R.id.recyclerview_main_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mArrayList = new ArrayList<>();
        mAdapter = new ScanAdapter(mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

    }

    @Override
    public void initProcess() {
        if (!isScanning) {
            startScan();
        }
    }


    private void startScan() {
        isScanning = true;
        //위치활성화 되어있는 경우


        //블루투스 활성화 되어있는 경우
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            builder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        }
        ScanSettings settings = builder.build();

        //롤리팝 이상 / 이하
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner.startScan(null, settings, mScanCallback);
        } else {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }


    private void stopScan() {
        isScanning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    //롤리팝 이상
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);

            try {

                final ScanRecord scanRecord = result.getScanRecord();
                Log.d("getTxPowerLevel()", scanRecord.getTxPowerLevel() + "");
                Log.d("onScanResult()", result.getDevice().getAddress() + "\n" + result.getRssi() + "\n" + result.getDevice().getName()
                        + "\n" + result.getDevice().getBondState() + "\n" + result.getDevice().getType());

                final ScanResult scanResult = result;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //UUID값 가져오기
                                BluetoothDevice device = result.getDevice();
                                if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                                    if (device.fetchUuidsWithSdp()) {
                                        System.out.println(device.getName());
                                        uuids = result.getScanRecord().getServiceUuids();
                                        System.out.println(uuids);
                                        System.out.println(result.getScanRecord().getBytes());
                                        System.out.println(result.getScanRecord().getBytes());

                                        if (uuids == null) {
                                            UUID = "NULL";
                                        } else {
                                            name = scanResult.getDevice().getName();
                                            address = scanResult.getDevice().getAddress();
                                            rssi_ = String.valueOf(result.getRssi());
                                            UUID = uuids.toString();

                                            if (name == null) {
                                                name = "N/A";
                                            }

                                            Scan data = new Scan(name, address, rssi_, UUID);
                                            mArrayList.add(data);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.e("OnBatchScanResults", results.size() + "");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("OnBatchScanResults", errorCode + "");
        }
    };

    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5) {
                if ((scanRecord[startByte + 2] & 0xff) == 0x02 &&
                        (scanRecord[startByte + 3] & 0xff) == 0x15) {
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                uuid = hexString.substring(0, 8) + "-" +
                        hexString.substring(8, 12) + "-" +
                        hexString.substring(12, 16) + "-" +
                        hexString.substring(16, 20) + "-" +
                        hexString.substring(20, 32);

                int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

                int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
            }

            name = device.getName();
            address = device.getAddress();
            UUID = uuid;
            rssi_ = String.valueOf(rssi);


            if (name == null && name.length() == 0) {
                name = "N/A";
            }

            Scan data = new Scan(name, address, rssi_, UUID);
            mArrayList.add(data);
            mAdapter.notifyDataSetChanged();
        }
    };

    //UUID 변환
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);

    }

    View.OnClickListener buttonStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            turnGPSOn();
            turnBluetoothOn();

            Log.e("gps", String.valueOf(gps));
            Log.e("bluetooth", String.valueOf(bluetooth));

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mBluetoothAdapter.isEnabled()) {
               startScan();
            } else {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    turnGPSOn();
                } else if (!mBluetoothAdapter.isEnabled()) {
                    turnBluetoothOn();
                }
            }
        }
    };

    View.OnClickListener buttonStopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopScan();
        }
    };


    private void checkSDKVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    private void turnGPSOn() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { //if gps is disabled
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
            gps = true;
        }
    }

    private void turnBluetoothOn() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUESTE_ENABLE);
            bluetooth = true;
        }
    }
}


