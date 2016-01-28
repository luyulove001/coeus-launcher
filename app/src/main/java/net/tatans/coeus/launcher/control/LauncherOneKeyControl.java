package net.tatans.coeus.launcher.control;

import net.tatans.coeus.launcher.model.LauncherOneKeyModel;
import net.tatans.coeus.launcher.model.imp.ILauncherOneKeyModel;
import net.tatans.coeus.launcher.view.ILauncerOneKeyView;

/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.control.LauncherControl
 * 余亮 create at 2015/9/21 10:57
 */

public class LauncherOneKeyControl {
	private ILauncerOneKeyView iLauncerView;
	private ILauncherOneKeyModel iLauncherModel;
	public LauncherOneKeyControl(ILauncerOneKeyView iView){
		iLauncerView=iView;
		iLauncherModel = new LauncherOneKeyModel() ;
	}
	public void loadOneKeyApp(){
		iLauncerView.setOneKeyBean(iLauncherModel.loadLauncherOneKey());
	}
}
