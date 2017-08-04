/**
 * <pre>
 * Title: 		ZipFileThread.java
 * Project: 	GugleFile
 * Type:		org.leoly.guglefile.threads.ZipFileThread
 * Author:		255507
 * Create:	 	2012-2-22 下午5:42:15
 * Copyright: 	Copyright (c) 2012
 * Company:		
 * <pre>
 */
package org.leoly.guglefile.threads;

import java.io.File;

import org.leoly.guglefile.utils.ZipTool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * <pre>
 * 压缩/解压缩线程
 * </pre>
 * @author 255507
 * @version 1.0, 2012-2-22
 */
public class ZipFileThread extends Thread
{
	// 后续处理对象
	private Handler handler;

	// 处理的文件
	private File file;
	
	// 处理的文件路径
	private String handlePath;

	// 处理的一堆 文件
	private File[] files;

	private boolean isZip;

	@Override
	public void run()
	{
		// 如果是压缩操作
		Message msg = null;
		int msgValue = -1;
		if (isZip)
		{
			msgValue = ZipTool.zip(handlePath + ".zip", new File[] { file });
		}
		else
		{
			String filePath = file.getAbsolutePath();
			msgValue = ZipTool.unzip(filePath, handlePath);
		}

		msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("OPTION_STATUS", msgValue);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public File[] getFiles()
	{
		return files;
	}

	public void setFiles(File[] files)
	{
		this.files = files;
	}

	public boolean isZip()
	{
		return isZip;
	}

	public void setZip(boolean isZip)
	{
		this.isZip = isZip;
	}

	public String getHandlePath()
	{
		return handlePath;
	}

	public void setHandlePath(String handlePath)
	{
		this.handlePath = handlePath;
	}

}
