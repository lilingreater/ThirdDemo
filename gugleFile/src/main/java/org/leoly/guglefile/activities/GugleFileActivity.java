package org.leoly.guglefile.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.leoly.guglefile.R;
import org.leoly.guglefile.threads.ManageFileThread;
import org.leoly.guglefile.threads.ZipFileThread;
import org.leoly.guglefile.utils.GugleConstants;
import org.leoly.guglefile.utils.GugleUtils;
import org.leoly.guglefile.utils.ZipTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * <pre>
 * </pre>
 * @author 255507
 * @version 1.0, 2012-2-22
 */
public class GugleFileActivity extends Activity
{
	// 列表数据集合 27
	private ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

	// 列表界面视图
	private ListView listView;

	// 列表数据与视图的适配器
	private SimpleAdapter adapter;

	// 目录层级的堆栈
	private Stack<String> folderStack = new Stack<String>();

	// 当前的目录
	private String nowFolder;

	// 返回次数，当为2时，退出软件
	private int backIndex = 1;

	// 是否是升序
	private boolean isAsc = true;

	// 共享属性的名称
	private final String GUGLE_FILE_SET = "GUGLE_FILE";

	private final String GF_SET_LIST_TYPE = "LIST_TYPE";

	private SharedPreferences setting = null;

	private ProgressDialog progressDialog;

	private String fileManagePath;

	private int fileOption = -1;

	private ImageButton paseBtn;

	private final int LIST_BY = 0;

	private final int ALL_DELETE = 1;

	private final int ALL_COPY = 2;

	private final int ALL_MOVE = 3;

	private final int ABOUT_APP = 4;

	private final int CLOSE_APP = 5;

	private final Animation animation = new AlphaAnimation(1, 0);

	private final String OPEN_TAG = "OPEN_FILE";

	private String TEMP_BASE = null;

	// 处理文件转移数据
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Bundle bundle = msg.getData();
			int exitType = bundle.getInt("EXIT_TYPE");
			if (exitType == 0)
			{
				GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.optionSuccess));
			}
			else
			{
				GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.optionFail));
			}

			freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
			paseBtn.setVisibility(ImageButton.INVISIBLE);
			setFileOption(-1);
			progressDialog.dismiss();
		}
	};

	// 处理压缩返回信息
	private Handler zipHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Bundle bundle = msg.getData();
			int exitType = bundle.getInt("OPTION_STATUS");
			switch (exitType)
			{
				case ZipTool.EXIST_UNZIPFILE:
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.existunzipfile));
					break;
				case ZipTool.EXIST_ZIPFILE:
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.existzipfile));
					break;
				case ZipTool.NOTEXIST_ZIPFILE:
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.notexistzipfile));
					break;
				case ZipTool.NULL_ZIPPATH:
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.nullinputzipfile));
					break;
				case ZipTool.ZIPOPTION_FAIL:
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.optionFail));
					break;
				default:
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.optionSuccess));
					break;
			}

			freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
			progressDialog.dismiss();
		}
	};

	// 处理文件信息数据
	private Handler attrHandler = new Handler()
	{
		@Override
		public void handleMessage(final Message msg)
		{
			Bundle bundle = msg.getData();
			int exitType = bundle.getInt("EXIT_TYPE");
			if (exitType == 0)
			{
				// 彈出輸入框
				AlertDialog.Builder builder = new AlertDialog.Builder(GugleFileActivity.this);
				builder.setIcon(android.R.drawable.btn_star_big_on).setTitle(R.string.fileinfo);
				LayoutInflater flater = LayoutInflater.from(GugleFileActivity.this);
				LinearLayout builderLayout = (LinearLayout) flater.inflate(R.layout.fileinfo, null);
				File file = new File(getFileManagePath());
				TextView filePathView = (TextView) builderLayout.findViewById(R.id.filePathText);
				String filePath = file.getAbsolutePath();
				filePathView.setText(filePath);
				TextView fileNameView = (TextView) builderLayout.findViewById(R.id.fileNameText);
				fileNameView.setText(file.getName());
				TextView fileSizeView = (TextView) builderLayout.findViewById(R.id.fileSizeText);
				TextView fileModifyView = (TextView) builderLayout.findViewById(R.id.fileModifyText);
				fileModifyView.setText(GugleUtils.formatDate(file.lastModified()));
				TextView fileCountView = (TextView) builderLayout.findViewById(R.id.fileCountText);
				if (file.isDirectory())
				{
					String[] files = file.list();
					fileCountView.setText(String.valueOf(files.length));
					fileSizeView.setText(bundle.getString("FILE_SIZE"));
				}
				else
				{
					fileCountView.setText(String.valueOf(1));
					fileSizeView.setText(GugleUtils.getFileSize(file.length()));
				}
				builder.setView(builderLayout);
				builder.setNegativeButton(R.string.close_rss_menu, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
						dialog.dismiss();
					}
				});

				builder.create();
				builder.show();
				setFileManagePath(null);
			}
			else
			{
				GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.optionFail));
			}

			progressDialog.dismiss();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent intent = getIntent();
		String openPath = intent.getStringExtra(OPEN_TAG);
		File sdFile = null;
		if (GugleUtils.isEmpty(openPath))
		{
			// 取得SD卡要目录的文件
			sdFile = GugleUtils.getSDFile();
			if (null == sdFile)
			{
				GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.no_sdcard));
				return;
			}
		}
		else
		{
			sdFile = new File(openPath);
			if (!sdFile.exists())
			{
				GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.nofile));
				return;
			}
		}

		String basePath = sdFile.getAbsolutePath();
		TEMP_BASE = sdFile.getAbsolutePath();
		// 初始化配置對象
		initSetting(basePath);

		folderStack.setSize(25);
		folderStack.clear();
		pushData(basePath);
		setNowFolder(basePath);

		if (null == listView)
		{
			listView = (ListView) findViewById(R.id.fileList);
		}

		listView.setCacheColorHint(0);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> thisAdapter, View view, int index, long arg3)
			{
				fileClick(thisAdapter, index);
			}
		});

		// 长按事件
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> thisAdapter, View arg1, int index, long arg3)
			{
				return fileLongClick(thisAdapter, index);
			}
		});

		// 视图与列表数据的适配器
		adapter = new SimpleAdapter(this, listItem, R.layout.file_item, new String[] { "fileTypeImg", "fileName",
				"lastModify" }, new int[] { R.id.fileTypeImage, R.id.fileName, R.id.fileLastModify, 101010, 202020 });
		listView.setAdapter(adapter);
		// 刷新列表
		freshList(sdFile.getAbsolutePath(), setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
		setTitle(nowFolder);

		// 排序按鈕
		final ImageButton listBtn = (ImageButton) findViewById(R.id.listBtn);
		listBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				orderList(listBtn);
			}
		});

		// 主目录按鈕
		final ImageButton goHomeBtn = (ImageButton) findViewById(R.id.gohome);
		goHomeBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				goHome();
			}
		});

		// 创建文件夹按钮
		final ImageButton newFolderBtn = (ImageButton) findViewById(R.id.newFolderBtn);
		newFolderBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				createNewFolder();
			}
		});

		// 放置文件按钮
		animation.setDuration(500);
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);
		paseBtn = (ImageButton) findViewById(R.id.paseBtn);
		paseBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				paseBtn.clearAnimation();
				paseFile();
			}
		});

		// 上一层按钮
		final ImageButton goBackBtn = (ImageButton) findViewById(R.id.goback);
		goBackBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				onKeyDown(KeyEvent.KEYCODE_BACK, null);
			}
		});
	}

	/**
	 * <pre>
	 * 放置需要复制或者移动的文件(夹)
	 * </pre>
	 */
	private void paseFile()
	{
		if (GugleUtils.isEmpty(getFileManagePath()))
		{
			GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.nofileDo));
			paseBtn.setVisibility(ImageButton.INVISIBLE);
			setFileOption(-1);
			return;
		}

		progressDialog = ProgressDialog.show(GugleFileActivity.this, getString(R.string.alert_msg),
				getString((fileOption == ManageFileThread.COPY) ? R.string.copying : R.string.moving), true, false);
		ManageFileThread thread = new ManageFileThread();
		// 把移到到此文件夹的路径给补上
		thread.setCommand(getFileManagePath() + " " + nowFolder);
		thread.setHandler(handler);
		thread.setManageType(fileOption);
		thread.start();
	}

	/**
	 * 文件长按事件处理
	 * @param view
	 */
	private boolean fileLongClick(final AdapterView<?> view, final int index)
	{
		// 彈出輸入框
		AlertDialog.Builder builder = new AlertDialog.Builder(GugleFileActivity.this);
		builder.setTitle(R.string.optionSelect);
		HashMap<String, Object> map = (HashMap<String, Object>) view.getAdapter().getItem(index);
		String absolutePath = String.valueOf(map.get("absolutePath"));
		map.clear();
		final File file = new File(absolutePath);
		String[] options = null;
		if (file.isDirectory())
		{
			options = new String[] { getString(R.string.optionCopy), getString(R.string.optionMove),
					getString(R.string.optionDelete), getString(R.string.renameFile), getString(R.string.zipfile),
					getString(R.string.unzipfile), getString(R.string.attribute), getString(R.string.fastWidget) };
		}
		else
		{
			options = new String[] { getString(R.string.optionCopy), getString(R.string.optionMove),
					getString(R.string.optionDelete), getString(R.string.renameFile), getString(R.string.zipfile),
					getString(R.string.unzipfile), getString(R.string.attribute) };
		}
		builder.setItems(options, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				String path = file.getAbsolutePath();
				boolean isUnZip = false;
				if (path.lastIndexOf(".") > 0)
				{
					String subfix = path.substring(path.lastIndexOf(".") + 1);
					if (subfix.equalsIgnoreCase("zip"))
					{
						isUnZip = true;
					}
				}
				switch (which)
				{
					case 0:
						copyItemFile(GugleUtils.formatBlank(file.getAbsolutePath()), ManageFileThread.COPY);
						break;
					case 1:
						copyItemFile(GugleUtils.formatBlank(file.getAbsolutePath()), ManageFileThread.MOVE);
						break;
					case 2:
						deleteItemFile(file);
						break;
					case 3:
						renameFile(file);
						break;
					case 4:
						zipFile(file);
						break;
					case 5:
						Log.e("TAG", "isunzip="+isUnZip);
						if (isUnZip)
						{
							unzipFile(file);
						}
						else
						{
							GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.cannotunzip));
						}
						break;
					case 6:
						fileAttr(file);
						break;
					case 7:
						fastWidget(file);
						break;
					default:
						break;
				}
			}
		});
		builder.create();
		builder.show();
		return true;
	}

	/**
	 * <pre>
	 * 压缩文件
	 * </pre>
	 * @param file
	 */
	private void zipFile(File file)
	{
		popupInputBox(file, R.string.alert_msg, R.string.inputzipfilename, new SureBtnClickEvent()
		{
			public void fireEvent(File file, DialogInterface dialog, int which)
			{
				AlertDialog ad = (AlertDialog) dialog;
				EditText newTextView = (EditText) ad.findViewById(100100100);
				String zipFileName = newTextView.getText().toString();
				if (GugleUtils.isEmpty(zipFileName))
				{
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.inputzipfilename));
					return;
				}

				progressDialog = ProgressDialog.show(GugleFileActivity.this, getString(R.string.alert_msg),
						getString(R.string.zippingnow), true, false);
				ZipFileThread zft = new ZipFileThread();
				zft.setFile(file);
				zft.setHandlePath(getNowFolder() + File.separator + zipFileName);
				zft.setHandler(zipHandler);
				zft.setZip(true);
				zft.start();
			}
		});
	}

	/**
	 * <pre>
	 * 解压缩文件
	 * </pre>
	 * @param file
	 */
	private void unzipFile(File file)
	{
		popupInputBox(file, R.string.alert_msg, R.string.inputzipfilename, new SureBtnClickEvent()
		{
			public void fireEvent(File file, DialogInterface dialog, int which)
			{
				AlertDialog ad = (AlertDialog) dialog;
				EditText newTextView = (EditText) ad.findViewById(100100100);
				String zipFileName = newTextView.getText().toString();
				if (GugleUtils.isEmpty(zipFileName))
				{
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.inputzipfilename));
					return;
				}

				progressDialog = ProgressDialog.show(GugleFileActivity.this, getString(R.string.alert_msg),
						getString(R.string.zippingnow), true, false);
				ZipFileThread zft = new ZipFileThread();
				zft.setFile(file);
				zft.setHandlePath(getNowFolder() + File.separator + zipFileName);
				zft.setHandler(zipHandler);
				zft.setZip(false);
				zft.start();
			}
		});
	}

	/**
	 * <pre>
	 * 重命名文件(夹)
	 * </pre>
	 * @param view
	 * @param index
	 * @param manageType
	 */
	private void renameFile(final File file)
	{
		popupInputBox(file, R.string.alert_msg, R.string.inputfilename, new SureBtnClickEvent()
		{
			public void fireEvent(File file, DialogInterface dialog, int which)
			{
				AlertDialog ad = (AlertDialog) dialog;
				EditText newTextView = (EditText) ad.findViewById(100100100);
				String newFolderName = newTextView.getText().toString();
				if (GugleUtils.isEmpty(newFolderName))
				{
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.inputFolderName));
					return;
				}

				String newFolderPath = nowFolder + File.separatorChar + newFolderName;
				// 檢查此文件夾是否已經存在
				File newFile = new File(newFolderPath);
				if (newFile.exists())
				{
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.folderExist));
					return;
				}

				// 重命名文件
				file.renameTo(newFile);
				freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
			}
		});
	}

	/**
	 * <pre>
	 * 创建桌面快捷方式 
	 * </pre>
	 * @param view
	 * @param index
	 * @param manageType
	 */
	private void fastWidget(File file)
	{
		if (file.isDirectory())
		{
			Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, file.getName());
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(GugleFileActivity.this, R.drawable.shorttcut));
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(this.getPackageName(), ".activities.GugleFileActivity"));
			Bundle bundle = new Bundle();
			bundle.putString(OPEN_TAG, file.getAbsolutePath());
			intent.putExtras(bundle);
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			sendBroadcast(shortcutIntent);
		}
	}

	/**
	 * <pre>
	 * 文件(夹)属性
	 * </pre>
	 * @param view
	 * @param index
	 * @param manageType
	 */
	private void fileAttr(final File file)
	{
		progressDialog = ProgressDialog.show(GugleFileActivity.this, getString(R.string.alert_msg),
				getString(R.string.counting), true, false);
		ManageFileThread thread = new ManageFileThread();
		thread.setCommand(GugleUtils.formatBlank(file.getAbsolutePath()));
		thread.setHandler(attrHandler);
		thread.setManageType(ManageFileThread.COUNT);
		thread.start();
		setFileManagePath(file.getAbsolutePath());
	}

	/**
	 * <pre>
	 * 复制文件(夹),保存需要复制的文件夹路径,高亮显示粘贴按钮
	 * </pre>
	 * @param view
	 * @param index
	 */
	private void copyItemFile(String absolutePath, int manageType)
	{
		setFileManagePath(absolutePath);
		setFileOption(manageType);
		paseBtn.setVisibility(ImageButton.VISIBLE);
		paseBtn.startAnimation(animation);
	}

	/**
	 * 删除文件和文件夹
	 * @param view
	 * @param index
	 */
	private void deleteItemFile(final File file)
	{
		// 彈出輸入框
		AlertDialog.Builder builder = new AlertDialog.Builder(GugleFileActivity.this);
		builder.setIcon(android.R.drawable.btn_star_big_on).setTitle(R.string.alert_msg);
		builder.setMessage(R.string.deleteChecked);
		builder.setNegativeButton(R.string.cancel_menu, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		builder.setPositiveButton(R.string.sure_menu, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				String absolutePath = file.getAbsolutePath();
				progressDialog = ProgressDialog.show(GugleFileActivity.this, getString(R.string.alert_msg),
						getString(R.string.deleting), true, false);
				ManageFileThread delThread = new ManageFileThread();
				delThread.setCommand(GugleUtils.formatBlank(absolutePath));
				delThread.setHandler(handler);
				delThread.setManageType(ManageFileThread.REMOVE);
				delThread.start();
			}
		});

		builder.create();
		builder.show();
	}

	/**
	 * 点击文件时触发的事件处理
	 * @param thisAdapter
	 * @param index
	 */
	private void fileClick(AdapterView<?> thisAdapter, int index)
	{
		HashMap<String, Object> map = (HashMap<String, Object>) (thisAdapter.getAdapter().getItem(index));
		boolean isFolder = Boolean.parseBoolean(String.valueOf(map.get("isFolder")));
		String absolutePath = String.valueOf(map.get("absolutePath"));
		File file = new File(absolutePath);
		if (isFolder)
		{
			if (file.exists())
			{
				pushData(getNowFolder());
				rollbackIndex();
				setNowFolder(file.getAbsolutePath());
				freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
				setTitle(nowFolder);
			}
		}
		else
		{
			String fileName = file.getName();
			if (fileName.lastIndexOf(".") > 0)
			{
				int fileType = GugleUtils.getFileType(false, fileName);
				if (R.drawable.text == fileType)
				{
					Intent intent = new Intent();
					intent.setClass(GugleFileActivity.this, NotePadActivity.class);
					intent.putExtra("FILE_NAME", getNowFolder() + File.separator + fileName);
					startActivity(intent);
				}
				else
				{
					String subfix = fileName.substring(fileName.lastIndexOf(".") + 1);
					String mineType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(subfix);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), mineType);
					startActivity(Intent.createChooser(intent, getString(R.string.selectChooser)));
				}
			}
			else
			{
				Intent intent = new Intent();
				intent.setDataAndType(Uri.fromFile(file), null);
				startActivity(Intent.createChooser(intent, getString(R.string.selectChooser)));
			}
		}
	}

	/**
	 * 排序列表
	 * @param listBtn
	 */
	private void orderList(ImageButton listBtn)
	{
		isAsc = !isAsc;
		// 改变图形
		Drawable drawable = getResources().getDrawable(isAsc ? R.drawable.ascending : R.drawable.descending);
		listBtn.setImageDrawable(drawable);
		// 排序
		int sortType = setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD);
		freshList(nowFolder, sortType);
	}

	/**
	 * 转到主目录
	 */
	private void goHome()
	{
		if (nowFolder.equalsIgnoreCase(GugleUtils.getSDFile().getAbsolutePath()))
		{
			return;
		}
		// 清空堆疊，再壓入要目錄元素
		folderStack.clear();
		String path = GugleUtils.getSDFile().getAbsolutePath();
		pushData(path);
		setNowFolder(path);
		// 刷新列表
		freshList(path, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
	}

	/**
	 * 创建文件夹
	 */
	private void createNewFolder()
	{
		popupInputBox(null, R.string.alert_msg, R.string.inputFolderName, new SureBtnClickEvent()
		{
			public void fireEvent(File file, DialogInterface dialog, int which)
			{
				AlertDialog ad = (AlertDialog) dialog;
				EditText newTextView = (EditText) ad.findViewById(100100100);
				String newFolderName = newTextView.getText().toString();
				if (GugleUtils.isEmpty(newFolderName))
				{
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.inputFolderName));
					return;
				}

				String newFolderPath = nowFolder + File.separatorChar + newFolderName;
				// 檢查此文件夾是否已經存在
				File newFile = new File(newFolderPath);
				if (newFile.exists())
				{
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.folderExist));
					return;
				}

				// 在此目錄中新建文件夾
				if (newFile.mkdirs())
				{
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.optionSuccess));
					freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
					dialog.dismiss();
				}
				else
				{
					GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.optionFail));
					return;
				}
			}
		});
	}

	/**
	 * 压入元素，如果元素个数超过了规定的数量，则把最底层的元素弹出，再压入
	 * @param data
	 */
	private void pushData(String data)
	{
		folderStack.push(data);
	}

	/**
	 * 取得堆栈元素，当取到最后一层时，直接返回根目录
	 * @return
	 */
	private String popData()
	{
		if (folderStack.isEmpty())
		{
			return null;
		}

		return folderStack.pop();
	}

	/**
	 * <pre>
	 * 
	 * </pre>
	 * @param fileList
	 * @return
	 */
	private void freshList(String path, int sortType)
	{
		List<File> fileList = GugleUtils.getSortedFiles(new File(nowFolder), sortType, isAsc);
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = null;
		for (File file : fileList)
		{
			map = new HashMap<String, Object>();
			map.put("fileTypeImg", GugleUtils.getFileType(file.isDirectory(), file.getName()));
			map.put("fileName", file.getName());
			map.put("lastModify", GugleUtils.formatDate(file.lastModified()));
			map.put("absolutePath", file.getAbsolutePath());
			map.put("isFolder", file.isDirectory());
			result.add(map);
		}

		if (GugleUtils.isEmpty(result))
		{
			GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.nofile));
		}

		listItem.clear();
		listItem.addAll(result);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		// 返回键按下时，返回刚才操作的目录，如果已經到了最後一層，則按再次退出
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (backIndex == 2 || GugleUtils.isEmpty(nowFolder))
			{
				finish();
				return true;
			}

			// 已經退到了根節點了
			if (nowFolder.equalsIgnoreCase(TEMP_BASE))
			{
				GugleUtils.showMessage(GugleFileActivity.this, getString(R.string.finishapp));
				freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
				pushData(nowFolder);
				backIndex++;
				setTitle(nowFolder);
				return true;
			}

			String popStr = popData();
			if (GugleUtils.isEmpty(popStr))
			{
				finish();
				return true;
			}
			setNowFolder(popStr);
			freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
			setTitle(nowFolder);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * <pre>
	 * 初始化程序配置
	 * </pre>
	 * @param basePath
	 */
	private void initSetting(String basePath)
	{
		if (null == setting)
		{
			setting = getSharedPreferences(GUGLE_FILE_SET, MODE_PRIVATE);
		}

		if (-1 == setting.getInt(GF_SET_LIST_TYPE, -1))
		{
			SharedPreferences.Editor editor = setting.edit();
			editor.putInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD);
			editor.commit();
		}
	}

	@Override
	protected void onDestroy()
	{
		listItem.clear();
		listItem = null;
		folderStack.clear();
		folderStack = null;
		setting = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, LIST_BY, LIST_BY, getString(R.string.listBy));
		menu.add(0, ALL_DELETE, ALL_DELETE, getString(R.string.optionDelete));
		menu.add(0, ALL_COPY, ALL_COPY, getString(R.string.optionCopy));
		menu.add(0, ALL_MOVE, ALL_MOVE, getString(R.string.optionMove));
		menu.add(0, ABOUT_APP, ABOUT_APP, getString(R.string.about_rss));
		menu.add(0, CLOSE_APP, CLOSE_APP, getString(R.string.close_rss_menu));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch (item.getItemId())
		{
			case LIST_BY:
				listBy();
				break;
			case ALL_DELETE:
				allOperation(ManageFileThread.REMOVE);
				break;
			case ALL_COPY:
				allOperation(ManageFileThread.COPY);
				break;
			case ALL_MOVE:
				allOperation(ManageFileThread.MOVE);
				break;
			case ABOUT_APP:
				aboutApp();
				break;
			case CLOSE_APP:
				finish();
				break;
			default:
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * <pre>
	 * 多個刪除
	 * </pre>
	 */
	private void allOperation(final int dealType)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(GugleFileActivity.this);
		int titleTag = R.string.selectDelFile;
		switch (dealType)
		{
			case ManageFileThread.COPY:
				titleTag = R.string.selectCopyFile;
				break;
			case ManageFileThread.MOVE:
				titleTag = R.string.selectMoveFile;
				break;
			case ManageFileThread.REMOVE:
				titleTag = R.string.selectDelFile;
				break;
			default:
				break;
		}
		builder.setIcon(android.R.drawable.btn_star_big_on).setTitle(titleTag);
		File file = new File(nowFolder);
		final String[] fileNames = file.list();
		final boolean[] checkeds = GugleUtils.getAllChecked(fileNames.length);
		builder.setMultiChoiceItems(fileNames, checkeds, new OnMultiChoiceClickListener()
		{
			public void onClick(DialogInterface dialoginterface, int i, boolean flag)
			{
				checkeds[i] = flag;
			}
		});
		builder.setPositiveButton(R.string.sure_menu, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialoginterface, int i)
			{
				StringBuilder sb = new StringBuilder();
				for (int index = 0; index < checkeds.length; index++)
				{
					if (checkeds[index])
					{
						sb.append(GugleUtils.formatBlank(nowFolder));
						sb.append(File.separator);
						sb.append(GugleUtils.formatBlank(fileNames[index]));
						sb.append(' ');
					}
				}

				switch (dealType)
				{
					case ManageFileThread.COPY:
						copyItemFile(sb.toString(), ManageFileThread.COPY);
						break;
					case ManageFileThread.MOVE:
						copyItemFile(sb.toString(), ManageFileThread.MOVE);
						break;
					case ManageFileThread.REMOVE:
						progressDialog = ProgressDialog.show(GugleFileActivity.this, getString(R.string.alert_msg),
								getString(R.string.alert_msg), true, false);
						ManageFileThread thread = new ManageFileThread();
						thread.setCommand(sb.toString());
						thread.setHandler(handler);
						thread.setManageType(ManageFileThread.REMOVE);
						thread.start();
						break;
					default:
						break;
				}
			}
		});
		builder.create();
		builder.show();
	}

	/**
	 * <pre>
	 * 关于程序
	 * </pre>
	 */
	private void aboutApp()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(GugleFileActivity.this);
		builder.setIcon(android.R.drawable.btn_star_big_on).setTitle(R.string.about_rss);
		builder.setMessage(R.string.about_msg);
		builder.setPositiveButton(R.string.sure_menu, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialoginterface, int i)
			{
				dialoginterface.dismiss();
			}
		});
		builder.create();
		builder.show();
	}

	/**
	 * <pre>
	 * 選擇排序方式 
	 * </pre>
	 */
	private void listBy()
	{
		// 彈出輸入框
		AlertDialog.Builder builder = new AlertDialog.Builder(GugleFileActivity.this);
		builder.setIcon(android.R.drawable.btn_star_big_on).setTitle(R.string.listStype);
		builder.setItems(new String[] { getString(R.string.sortbyname), getString(R.string.sortbytime) },
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialoginterface, int index)
					{
						SharedPreferences.Editor editor = setting.edit();
						switch (index)
						{
							case 0:
								editor.putInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD);
								break;
							case 1:
								editor.putInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_TIME);
								break;
							default:
								break;
						}

						editor.commit();
						dialoginterface.dismiss();
						freshList(nowFolder, setting.getInt(GF_SET_LIST_TYPE, GugleConstants.SORT_BY_WORD));
					}
				});
		builder.create();
		builder.show();
	}

	/**
	 * <pre>
	 * 弹出输入窗口
	 * </pre>
	 * @param file
	 */
	private void popupInputBox(final File file, int titleRs, int textRs, final SureBtnClickEvent clickEvent)
	{
		// 彈出輸入框
		AlertDialog.Builder builder = new AlertDialog.Builder(GugleFileActivity.this);
		builder.setIcon(android.R.drawable.btn_star_big_on).setTitle(titleRs);
		LinearLayout builderLayout = new LinearLayout(GugleFileActivity.this);
		builderLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		builderLayout.setOrientation(LinearLayout.VERTICAL);
		TextView inputView = new TextView(GugleFileActivity.this);
		inputView.setText(textRs);
		inputView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		EditText newTextView = new EditText(GugleFileActivity.this);
		newTextView.setId(100100100);
		newTextView.setText(file.getName());
		newTextView.selectAll();
		newTextView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		builderLayout.addView(inputView);
		builderLayout.addView(newTextView);
		builder.setView(builderLayout);
		builder.setNegativeButton(R.string.cancel_menu, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		builder.setPositiveButton(R.string.sure_menu, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				clickEvent.fireEvent(file, dialog, which);
			}
		});

		builder.create();
		builder.show();
	}

	public String getNowFolder()
	{
		return nowFolder;
	}

	public void setNowFolder(String nowFolder)
	{
		this.nowFolder = nowFolder;
	}

	private void rollbackIndex()
	{
		backIndex = 1;
	}

	public String getFileManagePath()
	{
		return fileManagePath;
	}

	public void setFileManagePath(String fileManagePath)
	{
		this.fileManagePath = fileManagePath;
	}

	public int getFileOption()
	{
		return fileOption;
	}

	public void setFileOption(int fileOption)
	{
		this.fileOption = fileOption;
	}
}