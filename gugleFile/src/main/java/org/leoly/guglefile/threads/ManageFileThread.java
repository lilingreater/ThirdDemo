/**
 * 
 */
package org.leoly.guglefile.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.leoly.guglefile.utils.GugleUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 文件相关操作线程,包括删除,移动,拷贝,使用Linux本身的命令实现
 * @author Leoly
 */
public class ManageFileThread extends Thread
{
	// 需要操作的命令后缀
	private String command;

	// 后续处理对象
	private Handler handler;

	// 操作类型
	private int manageType;

	// 操作类型:拷贝
	public static final int COPY = 2;

	// 操作类型:移动
	public static final int MOVE = 1;

	// 操作类型:删除
	public static final int REMOVE = 0;

	// 操作类型:统计
	public static final int COUNT = 3;

	@Override
	public void run()
	{
		Message msg = null;
		Bundle bundle = new Bundle();
		try
		{
			// 执行手机的Shell命令
			Process process = Runtime.getRuntime().exec("sh");
			OutputStream os = process.getOutputStream();
			// 获得组装后需要执行的命令,并执行
			String rmCmd = getExecCmd();
			os.write(rmCmd.getBytes());
			os.flush();
			os.close();
			try
			{
				// 如果是统计操作,需要命令返回值
				if (getManageType() == COUNT)
				{
					InputStream in = process.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String tempStr = reader.readLine();
					if (!GugleUtils.isEmpty(tempStr))
					{
						tempStr = tempStr.substring(0, tempStr.lastIndexOf("	"));
						bundle.putString("FILE_SIZE", tempStr);
					}

					in.close();
				}

				// 等待命令执行完成,并把执行码保存.执行码为0时证明命令执行成功,非零时失败
				bundle.putInt("EXIT_TYPE", process.waitFor());
				msg = new Message();
				msg.setData(bundle);
				// 返回到主界面进行后续处理
				handler.sendMessage(msg);
			}
			catch (InterruptedException e)
			{
				Log.e("Execute Command Error:", "Process exit!");
			}
		}
		catch (IOException e)
		{
			Log.e("Execute Command Error:", "Can not execute command!");
		}
		finally
		{
			if (null == msg)
			{
				msg = new Message();
				bundle.putInt("EXIT_TYPE", -1);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		}
	}

	// 组装命令
	private String getExecCmd()
	{
		String result = null;
		switch (manageType)
		{
			case REMOVE:
				result = "rm -r ";
				break;
			case MOVE:
				result = "mv -f ";
				break;
			case COPY:
				result = "cp -rf ";
				break;
			case COUNT:
				result = "du -sh ";
			default:
				break;
		}

		return result + getCommand();
	}

	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public int getManageType()
	{
		return manageType;
	}

	public void setManageType(int manageType)
	{
		this.manageType = manageType;
	}

}
