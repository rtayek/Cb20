package acme.cb20;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.net.wifi.*;
import android.provider.*;
import android.support.v7.app.*;
import android.os.Bundle;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.net.*;
import java.util.*;

import p.*;
import p.Main.*;
import q.Colors;

import static java.lang.Math.*;
import static p.Main.*;
import static p.IO.*;
public class FullscreenActivity extends AppCompatActivity implements View.OnClickListener, Observer {
    final Colors colors=new Colors();
    final Et et=new Et();
    MediaPlayer mediaPlayer;
    Main main;
    int clicks;
    int width, depth, size;
    float fontSize;
    Button[] buttons;
    Integer specialButtonIndex=null;
    {
        p("started at: "+et.etms());
    }
    void setupAudio() {
        ((Audio.Factory.FactoryImpl.AndroidAudio)Audio.audio).setCallback(new Consumer<Audio.Sound>() {
            @Override
            public void accept(Audio.Sound sound) {
                Integer id=id(sound);
                if(id!=null) {
                    mediaPlayer=MediaPlayer.create(FullscreenActivity.this,id);
                    mediaPlayer.start();
                } else
                    l.warning("id for sound: "+sound+" is null!");
            }
            Integer id(Audio.Sound sound) {
                switch(sound) {
                    case electronic_chime_kevangc_495939803:
                        return R.raw.electronic_chime_kevangc_495939803;
                    case glass_ping_go445_1207030150:
                        return R.raw.glass_ping_go445_1207030150;
                    case store_door_chime_mike_koenig_570742973:
                        return R.raw.store_door_chime_mike_koenig_570742973;
                    default:
                        l.warning(""+" "+"default sound!");
                        return null;
                }
            }
        });
    }
    boolean menuItem(MenuItem item) {
        try {
            l.info("item: "+item);
            //item.isCheckable()
            int id=item.getItemId();
            if(Enums.MenuItem.isItem(id))
                if(Enums.MenuItem.item(id).equals(Enums.MenuItem.Quit)) {
                    l.warning("quitting.");
                    //areWeQuitting=true;
                } else {
                    if(Enums.MenuItem.values()[id].equals(Enums.MenuItem.Statistics))
                        alert("Statistics",main.statistics(),true);
                    else
                        Enums.MenuItem.doItem(id,main);
                    return true;
                }
            else if(Enums.LevelSubMenuItem.isItem(id-Enums.MenuItem.values().length)) {
                Enums.LevelSubMenuItem.doItem(id-Enums.MenuItem.values().length); // hack!
                return true;
            } else
                l.severe(item+" is not a tablet meun item!");
        } catch(Exception e) {
            l.severe("menut item: "+item+", caught: "+e);
        }
        return false;
        /*
        else if(id==Enums.MenuItem.values().length) { // some hack for restarting tablet?
            // wtf was i doing here?
            Intent i=mainActivity.getBaseContext().getPackageManager().getLaunchIntentForPackage(mainActivity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainActivity.startActivity(i);
        }
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        l.info("on create options menu");
        super.onCreateOptionsMenu(menu);
        for(Enums.MenuItem menuItem : Enums.MenuItem.values())
            if(menuItem!=Enums.MenuItem.Level)
                menu.add(Menu.NONE,menuItem.ordinal(),Menu.NONE,menuItem.name());
        menu.add(Menu.NONE,Enums.MenuItem.values().length,Menu.NONE,"Restart");
        SubMenu subMenu=menu.addSubMenu(Menu.NONE,99,Menu.NONE,"Level");
        for(Enums.LevelSubMenuItem levelSubMenuItem : Enums.LevelSubMenuItem.values())
            subMenu.add(Menu.NONE,Enums.MenuItem.values().length+levelSubMenuItem.ordinal()/*hack!*/,Menu.NONE,levelSubMenuItem.name());
        // add in new properties sub menu here
        //https://stackoverflow.com/questions/21282279/how-to-add-toggle-button-in-menu-item-in-android
        return true;
    }
    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.checkable_menu);
        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkable_menu:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                return true;
            default:
                return false;
        }
    }
    */
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        l.info("in options menu closed");
        super.onOptionsMenuClosed(menu);
        l.info("after super on options menu closed");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean rc=menuItem(item);
        if(!rc) {
            l.info("calling super on otions item selected");
            rc=super.onOptionsItemSelected(item);
        }
        return rc;
    }
    void alert(String title,String string,boolean cancelable) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(string);
        builder.setCancelable(cancelable);
        builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int whichButton) {
                //Your action here
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        int w=(int)round(width*.9), h=(int)round(depth*.9);
        alertDialog.getWindow().setLayout(w,h);
    }
    private Button getButton(int size,String string,float fontsize,int rows,int columns,int i,int x,int y) {
        return getButton(size,size,string,fontsize,rows,columns,i,x,y);
    }
    private Button getButton(int width,int depth,String string,float fontsize,int rows,int columns,int i,int x,int y) {
        Button button;
        RelativeLayout.LayoutParams params;
        button=new Button(this);
        //button.setId(model.buttons+i); // id is index!
        button.setTextSize(fontsize/4);
        button.setGravity(Gravity.CENTER);
        params=new RelativeLayout.LayoutParams(width,depth);
        params.leftMargin=x;
        params.topMargin=y;
        //p("other: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
        button.setLayoutParams(params);
        button.setText(string);
        //button.setBackgroundColor(colors.aColor(colors.whiteOn));
        button.setOnClickListener(this);
        return button;
    }
    View createButtons() {
        final int rows=colors.rows, columns=colors.columns, n=colors.n;
        RelativeLayout relativeLayout=new RelativeLayout(this);
        RelativeLayout.LayoutParams params=null;
        buttons=new Button[n+/*for ip address*/1]; // colors is intimatley tied to the mark 1 model!
        final int x0=size/4, y0=size/4;
        for(int i=0;i<rows*columns;i++) {
            Button button=new Button(this);
            button.setId(i+1);
            button.setTextSize(fontSize);
            button.setGravity(Gravity.CENTER);
            params=new RelativeLayout.LayoutParams(size,size);
            params.leftMargin=(int)(x0+i%columns*1.2*size);
            params.topMargin=(int)(y0+i/columns*size*1.2);
            //p("button: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
            button.setLayoutParams(params);
            if(i/columns%2==0)
                button.setText(""+(char)('0'+(i+1)));
            button.setBackgroundColor(colors.aColor(i,false));
            button.setOnClickListener(this);
            buttons[i]=button;
            relativeLayout.addView(button);
        }
        int i=rows*columns;
        { // reset
            Button button=new Button(this);
            button.setId(i+1);
            button.setTextSize(fontSize);
            button.setGravity(Gravity.CENTER);
            params=new RelativeLayout.LayoutParams(size,size);
            params.leftMargin=(int)(x0+i%columns*1.2*size);
            params.topMargin=(int)(y0+i/columns*size*1.2);
            //p("button: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
            button.setLayoutParams(params);
            button.setText("R");
            button.setBackgroundColor(colors.aColor(i,false));
            button.setOnClickListener(this);
            buttons[i]=button;
            relativeLayout.addView(button);
        }
        i++;
        { // extra buton for inet address
            specialButtonIndex=i;
            p("special button index: "+specialButtonIndex);
            Button button=new Button(this);
            button.setId(i+1);
            button.setTextSize((int)round(fontSize/4.));
            button.setGravity(Gravity.CENTER);
            params=new RelativeLayout.LayoutParams(size,size);
            params.leftMargin=(int)(x0+i%columns*1.2*size);
            params.topMargin=(int)(y0+i/columns*size*1.2);
            //p("button: "+i+", left margin="+params.leftMargin+", top margin="+params.topMargin);
            button.setLayoutParams(params);
            if(i/columns%2==0)
                button.setText(""+(char)('0'+(i+1)));
            button.setBackgroundColor(colors.aColor(i,false));
            button.setOnClickListener(this);
            buttons[i]=button;
            relativeLayout.addView(button);
            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[n].setText(main.myInetAddress!=null?main.myInetAddress.toString():"null");
                        }
                    });
                }
            },0,1_000);
        }
        relativeLayout.setBackgroundColor(Color.BLACK);
        return relativeLayout;
    }
    @Override
    public void onClick(final View v) {
        Integer index=v.getId()-1;
        p("click on: "+index);
        Integer id=index+1;
        p("id: "+id);
        if(1<=id&&id<=main.model.buttons) {
            p("it's a model button.");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    main.instance().click(v.getId());
                }
            },"click #"+clicks++).start();
        } else if(index.equals(specialButtonIndex)) {
            p("we clicked on on the special button.");
            //openOptionsMenu(); // we have menu button now
        } else {
            p("strange button index: "+index);
        }
    }
    public void update(Observable observable,Object hint) {
        p(observable+" "+hint);
        if(observable instanceof Model) {
            if(observable==main.model) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(Integer buttonId=1;buttonId<=main.model.buttons;buttonId++) {
                            //setButtonState(buttonId,main.model.state(buttonId));
                            buttons[buttonId-1].setBackgroundColor(colors.aColor(buttonId-1,main.model.state(buttonId)));
                        }
                    }
                });
            } else
                l.severe(observable+" is not our model!");
        } else
            l.severe(observable+" is not a model!");
    }
    void networkInterfaces() {
        try {
            Enumeration<NetworkInterface> netowrkInterfaces=NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface networkInterface : Collections.list(netowrkInterfaces))
                p("interface: "+networkInterface.getName()+" "+networkInterface.getInterfaceAddresses());
        } catch(SocketException e) {
            p("getNetworkInterfaces() caught: "+e);
            e.printStackTrace();
        }
    }
    void checkCaptivePortalDetectionEnabled() {
        String captivePortalDetectionEnabled="captive_portal_detection_enabled";
        try {
            int result=Settings.Global.getInt(getContentResolver(),captivePortalDetectionEnabled);
            p("captivePortalDetectionEnabled="+result);
        } catch(Exception e) {
            p("caught: "+e+" trying to get "+captivePortalDetectionEnabled);
        }
    }
    void getRouterFromWifiManager(Properties properties,WifiManager wifiManager) {
        String router=properties.getProperty("router","");
        if(router.equals("")) {
            p("we do not know the router");
            InetAddress inetAddress=MyBroadcastReceiver.getIpAddressFromWifiManager(wifiManager);
            if(inetAddress!=null) {
                p("our inet address is: "+inetAddress);
                byte[] bytes=inetAddress.getAddress();
                bytes[3]=1;
                InetAddress routersAddress=null;
                try {
                    routersAddress=InetAddress.getByAddress(bytes);
                    String string=routersAddress.getHostAddress();
                    p("inferred router is: "+routersAddress+" "+string);
                    properties.setProperty("router",string);
                } catch(UnknownHostException e) {
                    p("caught: "+e);
                }
            } else
                p("can not get inet address from wifi manager!");
        } else
            p("router is: "+router);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p("###################################################################################################");
        p("on create thread: "+Thread.currentThread().getName()+" after: "+et.etms());
        //printThreads();
        Main.propertiesFilename=new File(getExternalFilesDir(null),propertiesFilename).getPath();
        Properties properties=properties(new File(Main.propertiesFilename));
        p("properties: "+properties);
        logging();
        File logFileDirectory=new File(getExternalFilesDir(null),IO.logFileDirectory);
        String androidId=Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        p("androidId: "+androidId);
        // one id is" fa37f2329a84e09d
        addFileHandler(l,logFileDirectory,androidId);
        checkCaptivePortalDetectionEnabled();
        Context context=getApplicationContext();
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks=connectivityManager.getAllNetworks();
        for(Network network : networks)
            p("network: "+network);
        p("wifi");
        //properties.setProperty("ssid","\"Linksys48993\""); // hack for testing
        String ssid=properties.getProperty("ssid","");
        if(ssid.equals(""))
            p("we do not know our ssid");
        else
            p("target ssid: "+ssid);
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
            p("wifi manager is enabled.");
        else
            p("wifi manager is not enabled!");
        MyBroadcastReceiver.list(wifiManager);
        WifiConfiguration desired=MyBroadcastReceiver.findMyWifiConfiguration(wifiManager,ssid);
        if(desired!=null)
            p("found desired wifi configuration with ssid: "+desired.SSID+", status is: "+desired.status);
        if(!ssid.equals("")&&desired==null)
            p("can not find network id for: "+ssid);
        Integer networkId=desired!=null?desired.networkId:null;
        if(desired!=null)
            p("network id for desired wifi connection is: "+networkId);
        WifiConfiguration current=MyBroadcastReceiver.findCurrent(wifiManager);
        if(current!=null)
            p("current wifi configuration has ssid: "+current.SSID+" and network id: "+current.networkId);
        else
            p("no current wifi configuration!");
        //http://stackoverflow.com/questions/43342149/switching-between-2-wifi-networks-connecting-fails
        //http://stackoverflow.com/questions/8818290/how-do-i-connect-to-a-specific-wi-fi-network-in-android-programmatically
        if(!wifiManager.isWifiEnabled()) {
            alert("Wifi","Wifi manager is not enabled!",true);
        } else {
            if(!ssid.equals("")&&desired!=null&&current!=null&&!ssid.equals(current.SSID)) {
                p("wifi is connected to a wrong ssid: "+current.SSID+", disconnecting");
                boolean ok=wifiManager.disconnect();
                p("disconnect() returns: "+ok);
                if(ok) {
                    p("disconnected from: "+current.SSID+", and diabling it.");
                    ok=wifiManager.disableNetwork(current.networkId);
                    if(ok)
                        p("disabled: "+current.SSID);
                    else
                        p("diable failed!");
                } else
                    p("disconnect fails!");
                if(desired!=null/*&&desired.status!=WifiConfiguration.Status.ENABLED*/) {
                    p("enabling: "+desired.SSID);
                    ok=wifiManager.enableNetwork(desired.networkId,true);
                    p("enable returns: "+ok+", status is: "+desired.status);
                } else
                    p(desired.SSID+" already is enabled");
                if(ok) {
                    p(desired.SSID+" was or is enabled, trying to connect.");
                    ok=wifiManager.reconnect(); // was reassociate!
                    if(ok) {
                        p(desired.SSID+" says is connected.");
                        while((current=MyBroadcastReceiver.findCurrent(wifiManager))==null)
                            try {
                                p("sleep");
                                Thread.sleep(300);
                            } catch(InterruptedException e) {
                                e.printStackTrace();
                            }
                        if(current!=null)
                            p("now current is: "+current.SSID);
                        else
                            p("now current is null!");
                    } else
                        p(current.SSID+" says was not reconnected!");
                }
                current=MyBroadcastReceiver.findCurrent(wifiManager);
            }
            if(current!=null) {
                if(!ssid.equals(""))
                    if(ssid.equals(current.SSID)) {
                        p("wifi is connected to the correct ssid: "+current.SSID);
                        // maybe router does not always have to agree with wifi's ip address
                        getRouterFromWifiManager(properties,wifiManager);
                    } else
                        p("wrong ssid!");
                else
                    p("no ssid!");
            } else
                p("no current wifi connection!");
            // try to connect if we know our target ssid.
        }
        //networkInterfaces();
        //properties.setProperty("router","192.168.2.1"); // hack for testing
        findRouter(properties);
        getRouterFromWifiManager(properties,wifiManager);
        setupAudio();
        // nexus 5 wants to be in ptp mode - swipe down to get to option
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        p("properties before construcying main: "+properties);
        Integer first=new Integer(properties.getProperty("first"));
        Integer last=new Integer(properties.getProperty("last"));
        Group group=new Group(first,last,false);
        main=new Main(properties,group,Model.mark1) {
            @Override
            protected void loop() {
                // check wifi
                super.loop();
            }
        };
        Thread mainThread=new Thread(main,"rabbit2 main");
        mainThread.start();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fullscreen);
        Point point=new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        p("real window size is: "+point);
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        System.out.println(metrics);
        size=(int)round(metrics.heightPixels*.25); // size of a large square button
        p("size: "+size);
        fontSize=(int)round(metrics.heightPixels*.06);
        p("font size: "+fontSize);
        width=point.x;
        depth=point.y;
        View view=createButtons();
        setContentView(view);
        main.model.addObserver(this);
        p("exit on create after: "+et.etms());
        p("###################################################################################################");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putDouble(savedStateKey,et.etms());
        super.onSaveInstanceState(savedInstanceState);
    }
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //savedState=savedInstanceState.getDouble(savedStateKey);
    }
    @Override
    public void onPause() {
        l.config("paused");
        super.onPause();  // Always call the superclass method first
    }
    @Override
    public void onResume() {
        l.config("resumed");
        super.onResume();  // Always call the superclass method first
    }
    @Override
    protected void onStart() {
        super.onStart();
        l.config("started");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        l.config("restarted");
    }
    @Override
    protected void onStop() {
        super.onStop();
        l.config("stopped");
    }
    @Override
    protected void onDestroy() {
        l.config("destroyed");
        closeOptionsMenu();
        // stopTabletStuff();
        super.onDestroy();
        //System.runFinalizersOnExit(true);
        //System.exit(0);
    }
}
