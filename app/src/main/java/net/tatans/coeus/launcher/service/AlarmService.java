package net.tatans.coeus.launcher.service;

import java.util.Calendar;

import net.tatans.coeus.launcher.activities.LauncherApp;
import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;
/**
 * Created by chulu on 2015/3/25.
 */
@Deprecated
public class AlarmService extends IntentService {
    public AlarmService() {
		super("hello tatans");
	}

	//整点报时开始时间
    private static final int STARTCLOCK_TIME = 9;
    //整点报时结束时间
    private static final int ENDCLOCK_TIME = 20;
    
    @Override
    public void onCreate() {
        super.onCreate();
    }
   

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//     
//        if (hour >= STARTCLOCK_TIME && hour <= ENDCLOCK_TIME&&minute==0) {
//            if (hour <= 12) {
//                if(hour<=6){
//                	
//                	LauncherApp.getInstance().speech("现在时间凌晨" + hour + "点");
//                }else {
//                	if(hour==8){
//                		LauncherApp.getInstance().speech("现在时间上午" + hour + "点");
//                	}else{
//                		LauncherApp.getInstance().speech("现在时间上午" + hour + "点");
//                	}
//                }
//            } else {
//                hour = hour - 12;
//                if (hour < 6) {
//                	LauncherApp.getInstance().speech("现在时间下午" + hour + "点");
//                } else {
//                	LauncherApp.getInstance().speech("现在时间晚上" + hour + "点");
//                }
//            }
//        }
        return super.onStartCommand(intent, flags, startId);
    }
    
   

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(android.content.Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
