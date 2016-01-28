package net.tatans.coeus.launcher.view;

import java.util.List;

import android.content.pm.ResolveInfo;
 
/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.view.ILauncerView
 * 余亮 create at 2015/9/21 9:22
 */

public interface ILauncerAppView {
	void setLauncerAppBean(List<ResolveInfo> launcherBean);
}
