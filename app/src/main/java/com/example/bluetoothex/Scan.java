package com.example.bluetoothex;

public class Scan {

    private String name;
    private String address;
    private String rssi;
    private String uuid;

    public Scan(String name, String address, String rssi, String uuid) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
       this.address = address;
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
