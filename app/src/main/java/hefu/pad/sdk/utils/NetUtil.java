package hefu.pad.sdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import hefu.pad.sdk.JyhApp;

/**
 * Created by rs on 17-9-13.
 */

public class NetUtil {
    private String TAG = "NetUtil";
    private String localIpAddress;//本地ip地址 如 192.168.0.1
    public List<IP_MAC> devices = new ArrayList<>();

    public class IP_MAC {
        private String ip;
        private String mac;

        public IP_MAC(String ip, String mac) {
            this.ip = ip;
            this.mac = mac;
        }

        public String getIp() {
            return ip;
        }

        public String getMac() {
            return mac;
        }
    }

    //定义WifiManager对象
    private WifiManager mWifiManager;
    //定义WifiInfo对象
    private WifiInfo mWifiInfo;


    public NetUtil() {

//        //取得WifiManager对象
//        mWifiManager = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        //取得WifiInfo对象
//        mWifiInfo = mWifiManager.getConnectionInfo();

        //设置本地ip
        String ip = getLocalIpAddress();
        localIpAddress = ip.substring(0, ip.length());
        Log.e(TAG, "readArp: ip= " + ip);

    }


//    /**
//     * 获取本地ip
//     */
//    public InetAddress getLocalIpAddress() {
//        int hostAddress = mWifiInfo.getIpAddress();
//        byte[] addressBytes = {(byte) (0xff & hostAddress),
//                (byte) (0xff & (hostAddress >> 8)),
//                (byte) (0xff & (hostAddress >> 16)),
//                (byte) (0xff & (hostAddress >> 24))};
//
//        try {
//            return InetAddress.getByAddress(addressBytes);
//        } catch (UnknownHostException e) {
//            throw new AssertionError();
//        }
//    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public String getLocalIpAddress() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }


    // 根据ip 网段去 发送arp 请求
    private void discover(String ip) {
        ExecutorService myExecutorService;
        myExecutorService = Executors.newCachedThreadPool();
        String newip = "";
        if (!ip.equals("")) {
            String ipseg = ip.substring(0, ip.lastIndexOf(".") + 1);
            for (int i = 2; i < 255; i++) {
                newip = ipseg + String.valueOf(i);
                if (newip.equals(ip)) continue;
                myExecutorService.execute(new UDPThread(newip));
            }
        }
        myExecutorService.shutdown();
        try {
            myExecutorService.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        myExecutorService.shutdownNow();
    }

    // UDPThread
    public class UDPThread extends Thread {
        private String target_ip = "";

        public final byte[] NBREQ = {(byte) 0x82, (byte) 0x28, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x1,
                (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x20, (byte) 0x43, (byte) 0x4B,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x0, (byte) 0x0, (byte) 0x21, (byte) 0x0, (byte) 0x1};

        public static final short NBUDPP = 137;

        public UDPThread(String target_ip) {
            this.target_ip = target_ip;
        }

        @Override
        public synchronized void run() {
            if (target_ip == null || target_ip.equals("")) return;
            DatagramSocket socket = null;
            InetAddress address = null;
            DatagramPacket packet = null;
            try {
                address = InetAddress.getByName(target_ip);
                packet = new DatagramPacket(NBREQ, NBREQ.length, address, NBUDPP);
                socket = new DatagramSocket();
                socket.setSoTimeout(200);
                socket.send(packet);
                socket.close();
            } catch (SocketException se) {
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }

    private void readArp() {
        devices.clear();
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";

            while ((line = br.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.length() < 63) continue;
                    if (line.toUpperCase(Locale.US).contains("IP")) continue;
                    ip = line.substring(0, 17).trim();
                    flag = line.substring(29, 32).trim();
                    mac = line.substring(41, 63).trim();
                    if (mac.contains("00:00:00:00:00:00")) continue;
                    Log.e(TAG, "readArp: mac= " + mac + " ; ip= " + ip + " ;flag= " + flag);
                    IP_MAC ip_mac = new IP_MAC(ip, mac);
                    devices.add(ip_mac);
                } catch (Exception e) {
                }
            }
            br.close();

        } catch (Exception e) {
        }
    }

    public List<IP_MAC> getLocalDevices() {
        discover(localIpAddress);
        readArp();
        return devices;
    }

    public static boolean isNetworkConnected() {
        if ( JyhApp.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) JyhApp.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected() {
        if ( JyhApp.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)  JyhApp.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isMobileConnected() {
        if ( JyhApp.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)  JyhApp.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType() {
        if ( JyhApp.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)   JyhApp.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
}
