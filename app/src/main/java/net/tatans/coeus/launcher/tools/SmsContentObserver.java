package net.tatans.coeus.launcher.tools;


import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import net.tatans.coeus.launcher.util.Const;

public class SmsContentObserver extends ContentObserver {

	private Handler handler;
	private Context context;


	public SmsContentObserver(Context context,Handler handler) {
		super(handler);
		this.handler = handler;
		this.context=context;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Message msg = handler.obtainMessage(Const.UPDATE_MESSAGE);
		handler.sendMessage(msg);
	}

}
