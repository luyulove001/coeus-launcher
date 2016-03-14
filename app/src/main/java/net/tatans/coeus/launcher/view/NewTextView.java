package net.tatans.coeus.launcher.view;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import net.tatans.coeus.launcher.model.imp.ISendChar;


/**
 * Created by SiLiPing on 2015/12/23.
 */
public class NewTextView extends TextView {

    private String TAG = "NewTextView";
    private String text;
    private Context context;
    ISendChar iSendChar;

    public NewTextView(Context context, ISendChar iSendChar) {
        super(context);
        this.context = context;
        this.iSendChar = iSendChar;
    }

    public NewTextView(Context context) {
        super(context);
        this.context = context;
    }
    /**
     * (API级别4)当用户在一个视图操作时调用此方法。事件是按照用户操作类型分类,如TYPE_VIEW_CLICKED。
     * 你通常不需要实现该方法,除非你是创建一个自定义视图。
     * @param eventType
     */
    @Override
    public void sendAccessibilityEvent(int eventType) {
        super.sendAccessibilityEvent(eventType);
        if(eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED){
            iSendChar.onSendChar(this.getText().toString());
        }
    }
}
