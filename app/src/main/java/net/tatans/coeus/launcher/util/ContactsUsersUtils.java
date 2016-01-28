package net.tatans.coeus.launcher.util;

import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.bean.ContactsUsersBean;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
/**
 * @author SiLiPing
 * Purpose: 获取本地联系人信息
 * Create Time: 2015-11-4 下午4:17:59
 */
public class ContactsUsersUtils {
	
    public static List<ContactsUsersBean> getContactsList(Context ctx) {
    	List<ContactsUsersBean> list = new ArrayList<ContactsUsersBean>();
        ContentResolver cr = ctx.getContentResolver();  
        String str[] = { Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID };  
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, str, null, null, null);  
        if (cur != null) {  
            while (cur.moveToNext()) {  
            	ContactsUsersBean cub = new ContactsUsersBean();
            	cub.setNUMBER(cur.getString(cur.getColumnIndex(Phone.NUMBER)));// 得到手机号码  
            	cub.setDISPLAY_NAME(cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME)));  
                long contactid = cur.getLong(cur.getColumnIndex(Phone.CONTACT_ID));
                long photoid = cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID));
                System.out.println("___联系人:" + cub.getDISPLAY_NAME()+"："+cub.getNUMBER());
                list.add(cub);
            }  
            cur.close();  
        }  
        return list;
    }  
}