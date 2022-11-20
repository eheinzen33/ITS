package com.example.its_5;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import com.example.its_5.Adapters.RecyclerViewAdapter;
import com.example.its_5.DataClasses.TAG;
import com.example.its_5.uhf.UHFBaseActivity;
import com.example.its_5.uhf.auxFunction;
import com.pda.rfid.EPCModel;
import com.pda.rfid.IAsynchronousMessage;
import com.pda.rfid.uhf.UHFReader;
import com.port.Adapt;
import com.util.Helper.Helper_ThreadPool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author RFID_C Read Tag
 */
public class ReadEPCActivity extends UHFBaseActivity implements
		IAsynchronousMessage{
	private auxFunction ETL = new auxFunction();
	private AsyncTask<Void, Void, Void> a = new ClientProducts().execute();
	private List<Map<String, Object>> rt2 = new ArrayList<Map<String,Object>>();
	private List<Map<String, Object>> deletedItems = new ArrayList<Map<String,Object>>();
	private ArrayList<TAG> TagList = new ArrayList<>();
	private Boolean DeleteButtonAct = false;
	private ListView listView = null; // Data list object
	private SimpleAdapter sa = null;
	private Button btn_Read = null;
	private TextView tv_TitleTagID = null;
	private TextView tv_TitleTagCount = null;
	private TextView lb_ReadTime = null;
	private TextView lb_ReadSpeed = null;
	private TextView lb_TagCount = null;
	private Spinner sp_ReadType = null;
	private Spinner sp_Stage = null;
	private RecyclerView recyclerview;
	private RecyclerViewAdapter adapter;
	Button btnDelete;
	private static boolean isStartPingPong = false; //Whether to start reading tag
	private boolean isKeyDown = false; // Whether the board is pressed
	private boolean isLongKeyDown = false; // Whether the board is in long press state
	private int keyDownCount = 0; // Number of times the board is pressed

	private int readTime = 0;
	private int lastReadCount = 0;
	private int totalReadCount = 0; // Total number of reads
	private int speed = 0; // Read speed
	private static int _ReadType = 0; // 0:Read EPC，1:Read TID
	private HashMap<String, EPCModel> hmList = new HashMap<String, EPCModel>();
	private Object hmList_Lock = new Object();
	private boolean flag = true; //
	private Boolean IsFlushList = true; //Whether to refresh the list
	private Object beep_Lock = new Object();
	ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
			ToneGenerator.MAX_VOLUME);

	private static boolean isPowerLowShow = false;//Low or not

	private final int MSG_RESULT_READ = MSG_USER_BEG + 1; //constant
	private final int MSG_FLUSH_READTIME = MSG_USER_BEG + 2;
	private final int MSG_UHF_POWERLOW = MSG_USER_BEG + 3;

	private IAsynchronousMessage log = null;
	public ProgressBar progressBar;
	@Override
	protected void msgProcess(Message msg) {
		switch (msg.what) {
		case MSG_RESULT_READ:
			if (isKeyDown){TagList = GetData();}
			ShowList(TagList); // Show the list
			break;
		case MSG_FLUSH_READTIME:
			if (lb_ReadTime != null) { //Refresh read time
				readTime++;
				lb_ReadTime.setText("Time:" + readTime + "S");
			}
			break;
		case MSG_UHF_POWERLOW: //Low power
			//TODO:
			break;
		default:
			super.msgProcess(msg);
			break;
		}
	}
	@SuppressLint("SetTextI18n")
	public void LoadToDB(View v){
		Button btnLoadDB = (Button) v;
		if (btnLoadDB.getText().toString().equals("Loaded")){return;}
		btnLoadDB.setText("Uploading..");
		String controlText = btnLoadDB.getText().toString();
		System.out.println(controlText);
		ETL.toMySQL(TagList);
		btnLoadDB.setText(getString(R.string.btn_loadedDB));
		btnLoadDB.setEnabled(false);
	}

//	public void DeleteItems(View v) {
//		Button btnDeleteItems = (Button) v;
//		if (btnDeleteItems.getText().toString().equals(getString(R.string.btn_deleteItemConfirm))){
//			for (Map<String,Object> tag : deletedItems){
//				rt2.remove(tag);
//			}
//			isStartPingPong = true;
//			ShowList(TagList);
//			isStartPingPong = false;
//			System.out.println("OK");
//			Button Proceed = findViewById(R.id.btn_deleteItems);
//			Proceed.setBackground(getDrawable(R.drawable.button_blue));
//			Proceed.setText(getString(R.string.btn_deleteItems));
//			DeleteButtonAct = false;
//		} else {
//			btnDeleteItems.setBackgroundColor(Color.RED);
//			DeleteButtonAct = true;
//			btnDeleteItems.setText(getString(R.string.btn_deleteItemConfirm));
//		}

//	}

	// Read
	public void Read(View v) {
		Button btnRead = (Button) v;
		String controlText = btnRead.getText().toString();
		if (controlText.equals(getString(R.string.btn_read))) {
			PingPong_Read();
			btnRead.setText(getString(R.string.btn_read_stop));
			sp_ReadType.setEnabled(false);
			Button btn = findViewById(R.id.btn_loadToDB);
			btn.setEnabled(true);
			btn.setText(getString(R.string.btn_loadDB));
		} else {
			Pingpong_Stop();
			btnRead.setText(getString(R.string.btn_read));
			sp_ReadType.setEnabled(true);
		}
	}

	//Clear
	public void Clear(View v) {
		totalReadCount = 0;
		readTime = 0;
		hmList.clear();
		ShowList(TagList = GetData());
	}

	// Back
	public void Back(View v) {
		if (btn_Read.getText().toString()
				.equals(getString(R.string.btn_read_stop))) {
			showMsg(getString(R.string.uhf_please_stop), null);
			return;
		}
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//		intent.putExtra("username",String.valueOf(username));
//		intent.putStringArrayListExtra("epcList", epcList);
		ReadEPCActivity.this.startActivity(intent);
		this.finish();
	}

	//init
	protected void Init() {
		log = this;
		if (!UHF_Init(log)) { // 打开模块电源失败
			showMsg(getString(R.string.uhf_low_power_info),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							ReadEPCActivity.this.finish();
						}
					});
		} else {
			try {
				super.UHF_GetReaderProperty(); // 获得读写器的能力
				Thread.sleep(20);
				CLReader.Stop(); // 停止指令
				Thread.sleep(20);
				super.UHF_SetTagUpdateParam(); // 设置标签重复上传时间为20ms
			} catch (Exception ee) {
			}
			
			//刷新线程
			Refush();

			//listView = (ListView) this.findViewById(R.id.lv_Main);
			btnDelete = findViewById(R.id.btn_deleteItems);
			recyclerview = findViewById(R.id.recycler_view);
			recyclerview.setLayoutManager(new LinearLayoutManager(this));
			recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
			tv_TitleTagID = (TextView) findViewById(R.id.tv_TitleTagID);
			tv_TitleTagCount = (TextView) findViewById(R.id.tv_TitleTagCount);
			lb_ReadTime = (TextView) findViewById(R.id.lb_ReadTime);
			lb_ReadSpeed = (TextView) findViewById(R.id.lb_ReadSpeed);
			lb_TagCount = (TextView) findViewById(R.id.lb_TagCount);
			btn_Read = (Button) findViewById(R.id.btn_Read);
			btn_Read.setText(getString(R.string.btn_read));
			sp_ReadType = (Spinner) findViewById(R.id.sp_ReadType);
			sp_Stage = (Spinner) findViewById(R.id.sp_stage);
			progressBar = findViewById(R.id.progress_product_searchTAG);
			//

			sp_ReadType
					.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (isStartPingPong)
								return;
							int selectItem = sp_ReadType
									.getSelectedItemPosition();
							if (PublicData._IsCommand6Cor6B.equals("6C")) {// 读6C标签
								tv_TitleTagCount.setText("Count");
								if (selectItem == 0) {
									_ReadType = 0;
									tv_TitleTagID.setText("Product");
								} else if (selectItem == 1) {
									_ReadType = 1;
									tv_TitleTagID.setText("TID");
								} else if (selectItem == 2) {
									_ReadType = 2;
									tv_TitleTagID.setText("UserData");
								}
							} else {
								tv_TitleTagCount.setText("Count");
								if (selectItem == 0) {
									_ReadType = 0;
									tv_TitleTagID.setText("Product");
								} else if (selectItem == 1) {
									_ReadType = 1;
									tv_TitleTagID.setText("TID");
								} else if (selectItem == 2) {
									_ReadType = 2;
									tv_TitleTagID.setText("UserData");
								}
							}

						}
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

		}
		return;
	}

	//Refush
	protected void Refush() {
		IsFlushList = true;
		// thread
		Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() {
			@Override
			public void run() {
				while (IsFlushList) {
					try {
						sendMessage(MSG_RESULT_READ, null);
						Thread.sleep(1000); // Refresh every second
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

//		Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() { // The buzzer sounds
//					@Override
//					public void run() {
//						while (IsFlushList) {
//							synchronized (beep_Lock) {
//								try {
//									beep_Lock.wait();
//								} catch (InterruptedException e) {
//								}
//							}
//							if (IsFlushList) {
//								toneGenerator
//										.startTone(ToneGenerator.TONE_PROP_BEEP);
//							}
//
//						}
//					}
//				});
	}

	// Dispose
	protected void Dispose() {
		isStartPingPong = false;
		IsFlushList = false;
		synchronized (beep_Lock) {
			beep_Lock.notifyAll();
		}
		UHF_Dispose();
	}

	protected void ShowlistAux(){adapter = new RecyclerViewAdapter(this, TagList);
		recyclerview.setAdapter(adapter);
		progressBar.setVisibility(View.GONE);}
	//show the list
	protected void ShowList(ArrayList<TAG> EPCs) {

		if (!isStartPingPong)
			return;
		//rt2 = GetData();
		if (!(TagList.equals(EPCs))) {TagList = EPCs;}
		adapter = new RecyclerViewAdapter(this, TagList);
		recyclerview.setAdapter(adapter);
		progressBar.setVisibility(View.GONE);

		btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (adapter.getSeletected().size() > 0) {
					for (TAG tag : adapter.getSeletected()){
						TagList.remove(tag);
					}
//					adapter = new RecyclerViewAdapter(context, TagList);
//					recyclerview.setAdapter(adapter);
				}
				ShowlistAux();
//				isStartPingPong = true;
//				ShowList(TagList);
//				isStartPingPong = false;
			}
		});
		if (lb_ReadTime != null) { // 刷新读取时间
			readTime++;
			lb_ReadTime.setText("Time:" + readTime / 1 + "S");
		}
		if (lb_ReadSpeed != null) { // 刷新读取速度
			if (flag) {
				speed = totalReadCount - lastReadCount;
				if (speed < 0)
					speed = 0;
				lastReadCount = totalReadCount;
				if (lb_ReadSpeed != null) {
					lb_ReadSpeed.setText("SP:" + speed + "T/S");
				}
			}
		}
		if (lb_TagCount != null) { // 刷新标签总数
			lb_TagCount.setText("Total:" + hmList.size());
		}
	}

	// Get the update data source
	@SuppressWarnings({ "rawtypes", "unused" })
	protected ArrayList<TAG> GetData() {
		List<Map<String, Object>> rt = new ArrayList<Map<String, Object>>();

		synchronized (hmList_Lock) {
			Iterator iter = hmList.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				EPCModel val = (EPCModel) entry.getValue();
				Map<String, Object> map = new HashMap<String, Object>();
				if (_ReadType == 0) {
					map.put("Product",
							//val._EPC);
							ETL.hexToAscii(val._EPC));
				} else if (_ReadType == 1) {
					map.put("Product", val._TID);
				} else {
					map.put("Product", val._UserData);
				}
                map.put("ReadCount", val._TotalCount);
				rt.add(map);
			}
			TagList = ETL.get_dif_products(rt, sp_Stage.getSelectedItem().toString());
		}
		return TagList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 创建
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setContentView(R.layout.uhf_read);

		showCustomBar(getString(R.string.tv_Read_Title),
				getString(R.string.str_back), null,
				R.drawable.left, 0,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Back(v);
					}
				},
				null
		);
	}

	@Override
	protected void onDestroy() {
		//destroy
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Dispose();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Init();
	}

	@Override
	public void onBackPressed() {
		Back(null);
		//super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("PDADemo", "onKeyDown keyCode = " + keyCode);
		if ((Adapt.DEVICE_TYPE_HY820 == Adapt.getDeviceType() && (keyCode == KeyEvent.KEYCODE_F9 /* RFID扳机*/ || keyCode == 285  /* 左快捷*/ || keyCode == 286  /* 右快捷*/))
				|| ((Adapt.getSN().startsWith("K3")) && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5))
				|| ((Adapt.getSN().startsWith("K6")) && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5))) { // 按下扳机
			toneGenerator
					.startTone(ToneGenerator.TONE_PROP_BEEP);
			btn_Read.setText(getString(R.string.btn_read_stop));
			sp_ReadType.setEnabled(false);
			btn_Read.setClickable(false);
			if (!isKeyDown) {
				isKeyDown = true; //
				if (!isStartPingPong) {
					Clear(null);
					Pingpong_Stop(); // stop
					isStartPingPong = true;
					String rt;
					if (PublicData._IsCommand6Cor6B.equals("6C")) {// 6C
						progressBar.setVisibility(View.VISIBLE);
                        GetEPC_6C();
					} else {// 6B
						rt = CLReader.Get6B(_NowAntennaNo + "|1" + "|1" + "|"
								+ "1,000F");
					}
				}
			} else {
				if (keyDownCount < 10000)
					keyDownCount++;
			}
			if (keyDownCount > 100) {
				isLongKeyDown = true;
			}
			if (isLongKeyDown) { // long down

			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("PDADemo", "onKeyUp keyCode = " + keyCode);
		if ((Adapt.DEVICE_TYPE_HY820 == Adapt.getDeviceType() && (keyCode == KeyEvent.KEYCODE_F9 /* RFID扳机*/ || keyCode == 285  /* 左快捷*/ || keyCode == 286  /* 右快捷*/))
				|| ((Adapt.getSN().startsWith("K3")) && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5))
				|| ((Adapt.getSN().startsWith("K6")) && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5))) { // 放开扳机

			CLReader.Stop();
			isStartPingPong = false;
			keyDownCount = 0;
			isKeyDown = false;
			isLongKeyDown = false;
			btn_Read.setText(getString(R.string.btn_read));
			sp_ReadType.setEnabled(true);
			btn_Read.setClickable(true);
			Button btn = findViewById(R.id.btn_loadToDB);
			btn.setEnabled(true);
			btn.setText(getString(R.string.btn_loadDB));
			progressBar.setVisibility(View.GONE);
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void OutPutEPC(EPCModel model) {
		if (!isStartPingPong)
			return;
		try {
			synchronized (hmList_Lock) {
				if (hmList.containsKey(model._EPC + model._TID)) {
					EPCModel tModel = hmList.get(model._EPC + model._TID);
					tModel._TotalCount++;
					model._TotalCount = tModel._TotalCount;
					hmList.remove(model._EPC + model._TID);
					hmList.put(model._EPC + model._TID, model);
				} else {
					hmList.put(model._EPC + model._TID, model);
				}
			}
			synchronized (beep_Lock) {
				beep_Lock.notify();
			}
			totalReadCount++;
		} catch (Exception ex) {
			Log.d("Debug", "Tags output exceptions:" + ex.getMessage());
		}

	}

	// Start reading tag
	public void PingPong_Read() {
		if (isStartPingPong)
			return;
		isStartPingPong = true;
		Clear(null);
		Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() {
			@Override
			public void run() {
				try {
					if (!isPowerLowShow) {
						String rt;
						if (PublicData._IsCommand6Cor6B.equals("6C")) {// Read 6C
							GetEPC_6C();
						} else {// Read 6B
							rt = CLReader.Get6B(_NowAntennaNo + "|1" + "|1" + "|" + "1,000F");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Stop reading tag
	public void Pingpong_Stop() {
		isStartPingPong = false;
		CLReader.Stop();
	}

	//6C,Read tag
	private  int GetEPC_6C()
    {
        int ret=-1;
        String text = tv_TitleTagID.getText().toString();
        if(text.equals("Product")) {
            ret = UHFReader._Tag6C.GetEPC(_NowAntennaNo, 1);
        }
        else if(text.equals("TID"))
        {
            ret = UHFReader._Tag6C.GetEPC_TID(_NowAntennaNo, 1);
        }
        else if(text.equals("UserData"))
        {
            ret = UHFReader._Tag6C.GetEPC_TID_UserData(_NowAntennaNo, 1,0,6);
        }
        return  ret;
    }


	public class ClientProducts extends AsyncTask<Void, Void, Void> {
		String error = "";
		@Override
		protected Void doInBackground(Void... voids) {
//
			try {
				ETL.getProductsList();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {

		}
	}
}
