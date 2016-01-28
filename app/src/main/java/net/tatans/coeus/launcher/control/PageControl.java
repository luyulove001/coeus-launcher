package net.tatans.coeus.launcher.control;

import net.tatans.coeus.launcher.R;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * @author Yuliang
 * @time 2015/3/25
 */
public class PageControl {

	private LinearLayout layout;
	private TextView[] textViews;
	private TextView textView;
	private int pageSize;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	private int selectedImage = R.mipmap.radio_sel;
	private int unselectedImage = R.mipmap.radio;

	private int currentPage = 0;
	private Context mContext;

	public PageControl(Context context, LinearLayout layout, int pageSize) {
		this.mContext = context;
		this.pageSize = pageSize;
		this.layout = layout;
		//显示底部选择器
		initDots();
	}

	void initDots() {
		textViews = new TextView[pageSize];
		for (int i = 0; i < pageSize; i++) {

			textView = new TextView(mContext);
			textView.setLayoutParams(new LayoutParams(10, 10));
			textView.setPadding(2, 2, 2, 0);

			textViews[i] = textView;
			if (i == 0) {
				// 默认进入程序后第一张图片被选中;
				textViews[i].setBackgroundResource(R.mipmap.radio_sel);
			} else {
				textViews[i].setBackgroundResource(R.mipmap.radio);
			}
			layout.addView(textViews[i]);
		}
	}

	boolean isFirst() {
		return this.currentPage == 0;
	}

	boolean isLast() {
		return this.currentPage == pageSize;
	}

	public void selectPage(int current) {
		for (int i = 0; i < textViews.length; i++) {
			textViews[current].setBackgroundResource(R.mipmap.radio_sel);
			if (current != i) {
				textViews[i].setBackgroundResource(R.mipmap.radio);
			}
		}
	}

	void turnToNextPage() {
		if (!isLast()) {
			currentPage++;
			selectPage(currentPage);
		}
	}

	void turnToPrePage() {
		if (!isFirst()) {
			currentPage--;
			selectPage(currentPage);
		}
	}
}
