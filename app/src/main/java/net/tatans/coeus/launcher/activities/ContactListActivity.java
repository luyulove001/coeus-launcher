package net.tatans.coeus.launcher.activities;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.adapter.ContactListAdapter;
import net.tatans.coeus.launcher.bean.ContactsUsersBean;
import net.tatans.coeus.launcher.model.imp.ISendChar;
import net.tatans.coeus.launcher.model.imp.ITatansItemClick;
import net.tatans.coeus.launcher.util.ContactsUsersUtils;
import net.tatans.coeus.launcher.util.FavoriteComparator;
import net.tatans.coeus.launcher.util.Person;
import net.tatans.coeus.launcher.util.PinyinComparator;
import net.tatans.coeus.launcher.util.StringHelper;
import net.tatans.coeus.launcher.view.NewTextView;
import net.tatans.coeus.network.tools.TatansActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by SiLiPing on 2015/12/23.
 * 联系人列表的UI
 */
public class ContactListActivity extends TatansActivity implements ITatansItemClick,ISendChar {

    private LinearLayout layoutIndex;
    private ListView listView;
    private TextView tv_show;
    private EditText edt_serch;
    private ContactListAdapter adapter;
    private String[] indexStr = { "★", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z","#" } ;
    private static int HEIGHT; /**字体高度*/
    private boolean flag ; /**是否显示搜索栏*/

    private static List<Person> newPersons ;
    private static List<Person> mSortList ;

    private Handler handler = new Handler();
    public  postHandler postHandler = new postHandler();
    public String cur_text ="";

    List<String> list = new ArrayList<String>();
    List<ContactsUsersBean> contactsDtos;
    /**
     * 延时机制
     */
    private class postHandler implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            onChangeText(cur_text);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sort_activity);
        initData();
        initViewEvent();
        setListData(list);
    }
    /**
     * 初始化布局数据
     */
    private void initData(){
        newPersons = new ArrayList<Person>();
        mSortList = new ArrayList<Person>();
    }
    /**
     * 初始化布局数据
     */
    private void initViewEvent() {
        layoutIndex = (LinearLayout) this.findViewById(R.id.layout);
        listView = (ListView) findViewById(R.id.listView);
        tv_show = (TextView) findViewById(R.id.tv);
        tv_show.setVisibility(View.GONE);

        //更具输入值 查询listview并刷新
        edt_serch = (EditText) findViewById(R.id.edt_search);
        edt_serch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length()==1){
                    onChangeText(StringHelper.getPinYinHeadChar(text));
                }else if(text.length() > 1){
                    onChangeText(StringHelper.getPinYinHeadChar(text.substring(0, 1)));
                }
            }
        });
    }



    /**
     * 设置setTitle
     * @param title
     */
    public void setTitleName(String title){
        this.setTitle(title);
    }

    /**
     * edt_输入框是否隐藏(使用)
     * @param flag
     */
    public void setVisibility(boolean flag){
        if(flag){
            edt_serch.setVisibility(View.GONE);
        }else{
            edt_serch.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 填充数据并加载adapter
     * @param listData
     */
    public void setListData(List<String> listData){
        contactsDtos = ContactsUsersUtils.getFavoriteContacts(getApplicationContext());
        newPersons.clear();
        newPersons = setFilledData(listData);
        // 根据a-z进行排序源数据
        Collections.sort(newPersons, new PinyinComparator());
        Collections.sort(newPersons, new FavoriteComparator());
        adapter = new ContactListAdapter(this, newPersons,this);
        listView.setAdapter(adapter);
    }

    /**
     * 为Person填充数据
     * @param date
     * @return
     */
    private List<Person> setFilledData(List<String> date){
        for(int i=0; i<date.size(); i++){
            Person sortModel = new Person();
            sortModel.setName(date.get(i).toString());
            // 汉字转换成拼音
            // 正则表达式，判断首字母是否是英文字母
            if (checkFavorite(date.get(i).toString())) {
                sortModel.setPinYinName("★");
            }else{
                //汉字转换成拼音
                String sortString = StringHelper.getPinYinHeadChar(date.get(i).toString().substring(0, 1));
                // 正则表达式，判断首字母是否是英文字母
                if(sortString.matches("[a-zA-Z]")){
                    sortModel.setPinYinName(sortString.toUpperCase());
                }else{
                    sortModel.setPinYinName("#");
                }
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }


    /**
     * 根据屏幕高度绘制索引
     * 在onCreate里面得到的getHeight=0，So，在onWindowFocusChanged得到getHeight
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!flag) {
            HEIGHT = (layoutIndex.getMeasuredHeight()-40) / indexStr.length;
            setIndexSideView();
            flag = true;
        }
    }

    /**
     * 绘制侧栏索引列表
     */
    private void setIndexSideView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, HEIGHT);
        for (int i = 0; i < indexStr.length; i++) {
            final NewTextView tv_label = new NewTextView(this,this);
            if (i == 0) tv_label.setContentDescription("收藏。单指快速左右滑动来滚动翻页");
            else if(i == indexStr.length-1) tv_label.setContentDescription("其他。单指快速左右滑动来滚动翻页");
            else tv_label.setContentDescription(" "+indexStr[i]+"。单指快速左右滑动来滚动翻页");
            tv_label.setGravity(Gravity.CENTER);
            tv_label.setLayoutParams(params);
            tv_label.setText(indexStr[i]);
            tv_label.setTextColor(getResources().getColor(android.R.color.white));
            tv_label.setPadding(15, 0, 15, 0);
            layoutIndex.addView(tv_label);
            tv_label.setTextSize(11.5f);
            /** 该方法只有在所有的无障碍服务都关闭才有效 */
            if (tv_label.isOk()){
                //侧边栏的OnTouchListener
                layoutIndex.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        float y = event.getY();
                        int index = (int) (y / HEIGHT);
                        if (index > -1 && index < indexStr.length) {// 防止越界
                            String key = indexStr[index];
                            for (int i = 0; i < mSortList.size(); i++) {
                                if (mSortList.get(i).getPinYinName().equals(key)) {
                                    if (listView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。
                                        listView.setSelectionFromTop(i + listView.getHeaderViewsCount(), 0);
                                    } else {
                                        listView.setSelectionFromTop(i, 0);// 滑动到第一项
                                    }
                                    tv_show.setVisibility(View.VISIBLE);
                                    tv_show.setText(indexStr[index]);
                                    break;/**执行一次就好*/
                                }
                            }
                        }
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                layoutIndex.setBackgroundColor(Color.parseColor("#151b21"));
                                break;

                            case MotionEvent.ACTION_MOVE:

                                break;
                            case MotionEvent.ACTION_UP:
                                layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));
                                tv_show.setVisibility(View.GONE);
                                break;
                        }
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void OnTatansItemClick(int code, String name) {
    }

    @Override
    public void OnTatansItemLongClick(int code, String name) {

    }

    @Override
    public void onSendChar(String sChar) {
        onChangeText(sChar);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 根据text(ABCDE...)刷新listview列表
     * @param text
     */
    private void onChangeText(String text){
        //该字母首次出现的位置
        int position = adapter.getPositionForSection(text.charAt(0));
        if(position != -1){
            listView.setSelection(position);
        }
    }


    /**
     * 检测该联系人是否已经被收藏
     *
     * @return
     */
    private boolean checkFavorite(String str) {
        for (int i = 0; i < contactsDtos.size(); i++) {
            if (contactsDtos.get(i).getDISPLAY_NAME().equals(str)) {
                return true;
            }
        }
        return false;
    }

}
