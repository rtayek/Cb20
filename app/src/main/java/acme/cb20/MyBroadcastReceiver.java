package acme.cb20;
import android.content.*;
import android.net.wifi.*;

import java.math.*;
import java.net.*;
import java.nio.*;
import java.util.*;

import static p.IO.*;
// http://www.grokkingandroid.com/android-getting-notified-of-connectivity-changes/
// maybe register these programmatically and make an abstract base class
// not sure we need this in cb2?
// but problems with rooted fire
public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver() {
        super();
    }
    /*
    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
for( WifiConfiguration i : list ) {
    if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
         wifiManager.disconnect();
         wifiManager.enableNetwork(i.networkId, true);
         wifiManager.reconnect();

         break;
    }
 }

     */
    @Override
    public void onReceive(Context context,Intent intent) {
        l.info("wifi receiver got action: "+intent.getAction());
        if(!isEnabled)
            return;
        mContext=context;
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(!isChecking) {
            isChecking=true;
            tryToConnectToWifi(wifiManager); // loops - take this out somewhere
            isChecking=false;
        }
        // or put it on a guard.
    }
    static void list(WifiManager wifiManager) {
        List<WifiConfiguration> wifiConfigurations=wifiManager.getConfiguredNetworks();
        for(WifiConfiguration wifiConfiguration : wifiConfigurations) {
            p("configured: "+wifiConfiguration.SSID+", status: "+wifiConfiguration.status+": "+WifiConfiguration.Status.strings[wifiConfiguration.status]+", networkId: "+wifiConfiguration.networkId);
        }
    }
    static WifiConfiguration findCurrent(WifiManager wifiManager) {
        List<WifiConfiguration> wifiConfigurations=wifiManager.getConfiguredNetworks();
        for(WifiConfiguration wifiConfiguration : wifiConfigurations)
            if(wifiConfiguration.status==WifiConfiguration.Status.CURRENT)
                return wifiConfiguration;
        return null;
    }
    static InetAddress getIpAddressFromWifiManager(WifiManager wifiManager) {
        p("isWifiEnabled() returns: "+wifiManager.isWifiEnabled());
        WifiInfo wifiInf=wifiManager.getConnectionInfo();
        p("wifi info: "+wifiInf);
        int ipAddress=wifiInf.getIpAddress();
        String ipAddressString=null;
        if(ipAddress!=0) {
            // if(ipAddress.isL)
            p("wifi ip address string: "+ipAddress);
            if(ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
                ipAddress=Integer.reverseBytes(ipAddress);
            p("ipAddress string: "+Integer.toHexString(ipAddress));
            byte[] ipByteArray=BigInteger.valueOf(ipAddress).toByteArray();
            try {
                ipAddressString=InetAddress.getByAddress(ipByteArray).getHostAddress();
            } catch(UnknownHostException ex) {
                l.warning("Unable to get host address for: "+Integer.toHexString(ipAddress));
            }
        }
        InetAddress inetAddress=null;
        try {
            inetAddress=InetAddress.getByName(ipAddressString);
        } catch(UnknownHostException e) {
        }
        return inetAddress;
    }
    static WifiConfiguration findMyWifiConfiguration(WifiManager wifiManager,String ssid) {
        if(!wifiManager.isWifiEnabled())
            return null;
        if(ssid.equals(""))
            return null;
        List<WifiConfiguration> list=wifiManager.getConfiguredNetworks();
        for(WifiConfiguration wifiConfiguration:list)
            if(wifiConfiguration.SSID.equals(ssid))
                return wifiConfiguration;
        return null;
    }
    void tryToConnectToWifi(WifiManager wifiManager) {
        final String tabletWifiSsid="\"tablets\"";
        boolean isWifiEnabled=wifiManager.isWifiEnabled();
        p("is wifi enabled: "+isWifiEnabled);
        List<WifiConfiguration> list=wifiManager.getConfiguredNetworks();
        if(list!=null&&list.size()>0)
            for(WifiConfiguration wifiConfiguration : list) {
                InetAddress inetAddress=null;
                p("configured: "+wifiConfiguration.SSID+", status: "+wifiConfiguration.status+": "+WifiConfiguration.Status.strings[wifiConfiguration.status]+", networkId: "+wifiConfiguration.networkId);
                if(!wifiConfiguration.SSID.equals(tabletWifiSsid))
                    p("not our network: "+wifiConfiguration.SSID);
                else
                    switch(wifiConfiguration.status) {
                        case WifiConfiguration.Status.CURRENT:
                            p("current connection is: "+tabletWifiSsid);
                            break;
                        case WifiConfiguration.Status.DISABLED:
                            p(wifiConfiguration.SSID+" is disabled, try to enable");
                            p(wifiConfiguration.SSID+" disconnecting first.");
                            boolean ok=wifiManager.disconnect();
                            if(ok) {
                                p("says we disconnected, try to enable.");
                                ok=wifiManager.enableNetwork(wifiConfiguration.networkId,true);
                                if(ok) {
                                    p(wifiConfiguration.SSID+" was enabled, trying to connect.");
                                    ok=wifiManager.reconnect();
                                    if(ok) {
                                        p(wifiConfiguration.SSID+" says is connected.");
                                    } else
                                        p(wifiConfiguration.SSID+" says was not connected!");
                                } else
                                    p(wifiConfiguration.SSID+" was not enabled!");
                            } else
                                p("says disconnect failed!");
                            break;
                        case WifiConfiguration.Status.ENABLED:
                            p(wifiConfiguration.SSID+" is enabled, trying to connect.");
                            p(wifiConfiguration.SSID+" disconnecting first.");
                            ok=wifiManager.disconnect();
                            if(ok) {
                                p("says we disconnected, try to reconnect.");
                                ok=wifiManager.reconnect();
                                if(ok) {
                                    p(wifiConfiguration.SSID+" says is connected.");
                                } else
                                    p(wifiConfiguration.SSID+" says was not connected!");
                            } else
                                p("says disconnect failed!");
                            break;
                        default:
                            p("unknown status: "+wifiConfiguration.status);
                    }
            }
        else
            p("no wifi networks are configured.");
    }
    private Context mContext;
    boolean isEnabled=false;
    boolean isChecking;
}