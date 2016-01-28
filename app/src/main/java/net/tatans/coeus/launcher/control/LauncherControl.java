package net.tatans.coeus.launcher.control;

import net.tatans.coeus.launcher.model.LauncherModel;
import net.tatans.coeus.launcher.model.imp.ILauncherModel;
import net.tatans.coeus.launcher.view.ILauncerView;

/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.control.LauncherControl
 * 余亮 create at 2015/9/21 10:57
 */

public class LauncherControl {
	private ILauncerView iLauncerView;
	private ILauncherModel iLauncherModel;
	public LauncherControl(ILauncerView iView){
		iLauncerView=iView;
		iLauncherModel = new LauncherModel() ;
	}
	public void loadLauncher(){
		iLauncerView.setLauncerBean(iLauncherModel.loadLauncher());
	}
}
