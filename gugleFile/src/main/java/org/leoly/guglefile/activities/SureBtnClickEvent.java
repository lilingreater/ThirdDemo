/**
 * <pre>
 * Title: 		SureBtnClickEvent.java
 * Project: 	GugleFile
 * Type:		org.leoly.guglefile.activities.SureBtnClickEvent
 * Author:		255507
 * Create:	 	2012-2-22 下午5:15:21
 * Copyright: 	Copyright (c) 2012
 * Company:		
 * <pre>
 */
package org.leoly.guglefile.activities;

import java.io.File;

import android.content.DialogInterface;

/**
 * <pre>
 * 弹出窗口的确定事件触发器
 * </pre>
 * @author 255507
 * @version 1.0, 2012-2-22
 */
public interface SureBtnClickEvent
{
	/**
	 * <pre>
	 * 执行触发事件的方法
	 * </pre>
	 * @param file
	 * @param dialog
	 * @param which
	 */
	void fireEvent(File file, DialogInterface dialog, int which);
}
