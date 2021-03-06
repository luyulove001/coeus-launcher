package net.tatans.coeus.launcher.util;

import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.bean.ContactsUsersBean;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

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



    /**
     * 获取联系人列表(联系人姓名)
     * @return
     */
    public static List<String> getContactsListStr(Context mContext, List<String> mList) {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "sort_key COLLATE LOCALIZED ASC");
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            if (TextUtils.isEmpty(id))
                continue;
            String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
            if (TextUtils.isEmpty(displayName))
                continue;
            mList.add(displayName);
        }
        return mList;
    }


    /**
     * 获得收藏夹的联系人
     */
    public static List<ContactsUsersBean> getFavoriteContacts(Context context) {
        List<ContactsUsersBean> contactsName = new ArrayList<ContactsUsersBean>();
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null,
                ContactsContract.Contacts.STARRED + " =  1 ", null,
                "sort_key COLLATE LOCALIZED ASC");
        int num = cursor.getCount();
        System.out.println(num + "");
        while (cursor.moveToNext()) {
            ContactsUsersBean temp = new ContactsUsersBean();
            String name = cursor.getString(cursor
                    .getColumnIndex("display_name"));
            if (TextUtils.isEmpty(name))
                continue;
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            temp.setDISPLAY_NAME(name);
            temp.setCONTACT_ID(id + "");
            contactsName.add(temp);
        }
        cursor.close();
        return contactsName;
    }
}