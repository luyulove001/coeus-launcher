package net.tatans.coeus.launcher.tools;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.util.Const;

/**
 * @author Lion
 *         Purpose:根据appName返回一键appIcon以及预装appIcon
 *         Create Time: 2016-1-18 下午3:09:58
 */
public class LauncherAppIcon {

    public static int getDrawableID(String appName) {
        int icon = R.mipmap.home;
        switch (appName) {
            /*case Const.LAUNCHER_NAME_0:
                icon = Const.LAUNCHER_ICON_0;
                break;
            case Const.OneKey_Name_0:
                icon = Const.OneKey_Icon_0;
                break;
            case Const.OneKey_Name_1:
                icon = Const.OneKey_Icon_1;
                break;
            case Const.OneKey_Name_2:
                icon = Const.OneKey_Icon_2;
                break;
            case Const.OneKey_Name_3:
                icon = Const.OneKey_Icon_3;
                break;
            case Const.OneKey_Name_4:
                icon = Const.OneKey_Icon_4;
                break;
            case Const.OneKey_Name_5:
                icon = Const.OneKey_Icon_5;
                break;
            case Const.OneKey_Name_6:
                icon = Const.OneKey_Icon_6;
                break;
            case Const.OneKey_Name_7:
                icon = Const.OneKey_Icon_7;
                break;
            case Const.OneKey_Name_8:
                icon = Const.OneKey_Icon_8;
                break;
            case Const.OneKey_Name_9:
                icon = Const.OneKey_Icon_9;
                break;
            case Const.OneKey_Name_10:
                icon = Const.OneKey_Icon_10;
                break;*/
            case Const.LAUNCHER_NAME_1:
                icon = Const.LAUNCHER_ICON_1;
                break;
            case Const.LAUNCHER_NAME_2:
                icon = Const.LAUNCHER_ICON_2;
                break;
            case Const.LAUNCHER_NAME_3:
                icon = Const.LAUNCHER_ICON_3;
                break;
            case Const.LAUNCHER_NAME_4:
                icon = Const.LAUNCHER_ICON_4;
                break;
            case Const.LAUNCHER_NAME_5:
                icon = Const.LAUNCHER_ICON_5;
                break;
            case Const.LAUNCHER_NAME_6:
                icon = Const.LAUNCHER_ICON_6;
                break;
            case Const.LAUNCHER_NAME_7:
                icon = Const.LAUNCHER_ICON_7;
                break;
            case Const.LAUNCHER_NAME_8:
                icon = Const.LAUNCHER_ICON_8;
                break;
            case Const.LAUNCHER_NAME_9:
                icon = Const.LAUNCHER_ICON_9;
                break;
            case Const.LAUNCHER_NAME_10:
				icon = Const.LAUNCHER_ICON_10;
				break;
			case Const.LAUNCHER_NAME_11:
				icon = Const.LAUNCHER_ICON_11;
				break;
			case Const.LAUNCHER_NAME_12:
				icon = Const.LAUNCHER_ICON_12;
				break;
            case Const.LAUNCHER_NAME_13:
                icon = Const.LAUNCHER_ICON_13;
                break;
            case Const.LAUNCHER_NAME_14:
                icon = Const.LAUNCHER_ICON_14;
                break;
            case Const.LAUNCHER_NAME_15:
                icon = Const.LAUNCHER_ICON_15;
                break;
            default:
                icon = R.mipmap.home;
                break;
        }
        return icon;
    }
}
