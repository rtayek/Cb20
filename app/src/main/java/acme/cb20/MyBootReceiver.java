package acme.cb20;
import android.content.*;
public class MyBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context,Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            if(startActivity) {
                Intent activityIntent=new Intent(context,FullscreenActivity.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
            }
    }
    boolean startActivity=true; // turn on when done with testing!
}
