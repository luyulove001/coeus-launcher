package net.tatans.coeus.launcher.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;

public class MissSmsCallUtil {
	
	private  int mSmsCount , mMmsCount;
	private  Cursor cursor = null;

	/**
	 * @return 未处理的信息总数
	 * @author cly
	 */
	public  int getSmsCount(Context ctx) 
	{
		mMmsCount = 0;
		mSmsCount = 0;
		findNewMmsCount(ctx);
		findNewSmsCount(ctx);
		return mMmsCount + mSmsCount;
	}
	/**
	 * 
	 * @param context
	 * @return 未接电话数量
	 */
	public  int readMissCall(Context context) 
	{
		int result = 0;
		cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, new String[] { Calls.TYPE },
				" type=? and new>?",
				new String[] { Calls.MISSED_TYPE + "", "0" }, "date desc");
	
		if (cursor != null) 
		{
			result = cursor.getCount();
			cursor.close();
			cursor = null;
		}
		return result;
	}

	private  void findNewSmsCount(Context ctx) 
	{
		try 
		{
			cursor = ctx.getContentResolver().query(Uri.parse("content://sms"),
					null, "type = 1 and read = 0", null, null);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		if (cursor != null) 
		{
			mSmsCount = cursor.getCount();
			cursor.close();
			cursor = null;
		}
	}

	private  void findNewMmsCount(Context ctx) 
	{
		Cursor cursor = null;
		try 
		{
			cursor = ctx.getContentResolver().query(
					Uri.parse("content://mms/inbox"), null, "read = 0", null,
					null);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		if (cursor != null) 
		{
			mMmsCount = cursor.getCount();
			cursor.close();
			cursor = null;
		}
	}
}
