package net.tatans.coeus.launcher.control;

import net.tatans.coeus.launcher.model.LauncherAppModel;
import net.tatans.coeus.launcher.model.imp.ILauncherAppModel;
import net.tatans.coeus.launcher.view.ILauncerAppView;

/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.control.LauncherControl
 * 余亮 create at 2015/9/21 10:57
 */

public class LauncherAppControl {
	private ILauncerAppView iLauncerAppView;
	private ILauncherAppModel iLauncherAppModel;
	
	public LauncherAppControl(ILauncerAppView iView){
		iLauncerAppView=iView;
		iLauncherAppModel = new LauncherAppModel() ;
	}
	public void loadLauncherApp(){
		iLauncerAppView.setLauncerAppBean(iLauncherAppModel.loadLauncherApp());
	}
}
