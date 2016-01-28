package net.tatans.coeus.launcher.tools;

import android.util.Log;

/**
 * @author 余亮 <br/>
 * Log统一管理类 
 *  
 */
public class LauncherLog  
{  

  public  boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化  
  private String TAG = "tatans";  
	// 下面四个是默认tag的函数   
  public LauncherLog(String str)  
  {   TAG= str;
      /* cannot be instantiated */
  }  


  public void d(String msg)  
  {  
      if (isDebug)  
          Log.d(TAG, msg);  
  }  



}

