package net.tatans.coeus.launcher.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

public class FileUtils {
	private String SDPath;

	public FileUtils() {
		// 得到当前外部存储设备的目录
		SDPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/";
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 */
	public File createSDFile(String fileName) {
		File file = new File(SDPath + fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File file = new File(SDPath + dirName);
		file.mkdir();
		return file;
	}
	/**
	 * * 在SD卡上创建多级目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDirs(String path, String dirName) {
		File file = new File(path + dirName);
		file.mkdir();
		return file;
	}

	/**
	 * 判断SD卡上文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPath + fileName);
		return file.exists();
	}

	/**
	 * 将一个inputStream里面的数据写到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param inputStream
	 * @return
	 */
	public File writeToSDfromInput(String path, String fileName,
			InputStream inputStream) {
		// createSDDir(path);
		File file = createSDFile(path + fileName);
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			while (inputStream.read(buffer) != -1) {
				outStream.write(buffer);
			}
			outStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
    /**
     * 取得文件夹大小
     * @param f
     *        文件
     * @return
     * @throws Exception
     */
    public long getFileSize(File f)throws Exception
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory())
            {
                size = size + getFileSize(flist[i]);
            } else
            {
                size = size + flist[i].length();
            }
        }
        return size;
    }
    /**
     * 删除创建时间最早的文件
     * @param path
     * @return 是否删除成功
     * @author luojianqin
     */
    public boolean deleteLastFromFloder(String path) {  
        boolean success = false;  
        try {  
            ArrayList<File> images = new ArrayList<File>();  
            getFiles(images, path);  
            File latestSavedImage = images.get(0);  
            if (latestSavedImage.exists()) {  
                for (int i = 1; i < images.size(); i++) {  
                    File nextFile = images.get(i);  
                    if (nextFile.lastModified() < latestSavedImage.lastModified()) {  
                        latestSavedImage = nextFile;  
                    }  
                }  
      
                Log.e("brady", "images = " + latestSavedImage.getAbsolutePath());  
                success = latestSavedImage.delete();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return success;  
    }  
      
    /**
     * 获取文件夹里面的文件
     * @param fileList
     * @param path
     * @author luojianqin
     */
    public void getFiles(ArrayList<File> fileList, String path) {  
        File[] allFiles = new File(path).listFiles();  
        for (int i = 0; i < allFiles.length; i++) {  
            File file = allFiles[i];  
            if (file.isFile()) {  
                fileList.add(file);  
            } else if (!file.getAbsolutePath().contains(".thumnail")) {  
                getFiles(fileList, file.getAbsolutePath());  
            }  
        }  
    }  
}