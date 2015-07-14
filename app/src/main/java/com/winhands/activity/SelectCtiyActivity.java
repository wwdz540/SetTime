package com.winhands.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winhands.adapter.CityAdapter;
import com.winhands.adapter.SearchCityAdapter;
import com.winhands.bean.City;
import com.winhands.extraview.BladeView;
import com.winhands.extraview.BladeView.OnItemClickListener;
import com.winhands.extraview.PinnedHeaderListView;
import com.winhands.settime.R;
import com.winhands.util.L;
import com.winhands.util.SharePreferenceUtil;
import com.winhands.util.SharePreferenceUtils;

public class SelectCtiyActivity extends Activity implements TextWatcher,
		OnClickListener{
	private EditText mSearchEditText;
	private ImageButton mClearSearchBtn;
	private View mCityContainer;
	private View mSearchContainer;
	private PinnedHeaderListView mCityListView;
	private BladeView mLetter;
	private ListView mSearchListView;
	private List<City> mCities;
	private SharePreferenceUtil mSpUtil;
	private SearchCityAdapter mSearchCityAdapter;
	private CityAdapter mCityAdapter;
	private RelativeLayout select_city_background;
	private SharedPreferences sp;
	// 首字母集
	private List<String> mSections;
	// 根据首字母存放数据
	private Map<String, List<City>> mMap;
	// 首字母位置集
	private List<Integer> mPositions;
	// 首字母对应的位置
	private Map<String, Integer> mIndexer;
	private BaseApplication mApplication;
	private InputMethodManager mInputMethodManager;
	
	private TextView mTitleTextView;
	private ImageView mBackBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.biz_plugin_weather_select_city);
		initView();
		initData();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView() {
		sp = new SharePreferenceUtils(this).getSP();
		mTitleTextView = (TextView) findViewById(R.id.title_name);
		mBackBtn = (ImageView) findViewById(R.id.title_back);
		mBackBtn.setOnClickListener(this);
		mTitleTextView.setText(BaseApplication.getInstance()
				.getSharePreferenceUtil().getCity());
		select_city_background = (RelativeLayout) findViewById(R.id.select_city_background);
		mSearchEditText = (EditText) findViewById(R.id.search_edit);
		mSearchEditText.addTextChangedListener(this);
		mClearSearchBtn = (ImageButton) findViewById(R.id.ib_clear_text);
		mClearSearchBtn.setOnClickListener(this);

		mCityContainer = findViewById(R.id.city_content_container);
		mSearchContainer = findViewById(R.id.search_content_container);
		mCityListView = (PinnedHeaderListView) findViewById(R.id.citys_list);
		mCityListView.setEmptyView(findViewById(R.id.citys_list_empty));
		mLetter = (BladeView) findViewById(R.id.citys_bladeview);
		select_city_background.setBackgroundColor(getColor());
		mLetter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(String s) {
				if (mIndexer.get(s) != null) {
					mCityListView.setSelection(mIndexer.get(s));
				}
			}
		});
		mLetter.setVisibility(View.GONE);
		mSearchListView = (ListView) findViewById(R.id.search_list);
		mSearchListView.setEmptyView(findViewById(R.id.search_empty));
		mSearchContainer.setVisibility(View.GONE);
		mSearchListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mInputMethodManager.hideSoftInputFromWindow(
						mSearchEditText.getWindowToken(), 0);
				return false;
			}
		});
		mCityListView
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						L.i(mCityAdapter.getItem(position).toString());
						System.out.println(mCityAdapter.getItem(position).getName());
						System.out.println(mCityAdapter.getItem(position).getPinyin());
						ocnc.setCityName(mCityAdapter.getItem(position));
						//
						mSpUtil.setCity(mCityAdapter.getItem(position).getName());
						mSpUtil.setPinyin(mCityAdapter.getItem(position).getPinyin());
						startActivity(mCityAdapter.getItem(position));
					}
				});

		mSearchListView
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						L.i(mSearchCityAdapter.getItem(position).toString());
						startActivity(mSearchCityAdapter.getItem(position));
					}
				});
	}

	private void startActivity(City city) {
		Intent i = new Intent();
		i.putExtra("city", city);
		setResult(RESULT_OK, i);
		finish();
	}
	private int getColor() {
		switch (sp.getInt("background", R.drawable.bg_blue)) {
		case R.drawable.bg_blue:
			return Color.parseColor("#196EB8");
		case R.drawable.bg_green:
			return Color.parseColor("#90B348");
		case R.drawable.bg_purple:
			return Color.parseColor("#664498");
		case R.drawable.bg_qing:
			return Color.parseColor("#1F9699");
		case R.drawable.bg_red:
			return Color.parseColor("#B50929");
		case R.drawable.bg_yellow:
			return Color.parseColor("#F7AD1D");

		default:
			return 0;
		}
	}
	private void initData() {
		
		mApplication = BaseApplication.getInstance();
		mSpUtil = BaseApplication.getInstance().getSharePreferenceUtil();
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		mCities = mApplication.getCityList();
		mSections = mApplication.getSections();
		mMap = mApplication.getMap();
		mPositions = mApplication.getPositions();
		mIndexer = mApplication.getIndexer();

		mCityAdapter = new CityAdapter(SelectCtiyActivity.this, mCities, mMap,
				mSections, mPositions);
		mCityListView.setAdapter(mCityAdapter);
		mCityListView.setOnScrollListener(mCityAdapter);
		mCityListView.setPinnedHeaderView(LayoutInflater.from(
				SelectCtiyActivity.this).inflate(
				R.layout.biz_plugin_weather_list_group_item, mCityListView,
				false));
		mLetter.setVisibility(View.VISIBLE);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// do nothing
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mSearchCityAdapter = new SearchCityAdapter(SelectCtiyActivity.this,
				mCities);
		mSearchListView.setAdapter(mSearchCityAdapter);
		mSearchListView.setTextFilterEnabled(true);
		if (mCities.size() < 1 || TextUtils.isEmpty(s)) {
			mCityContainer.setVisibility(View.VISIBLE);
			mSearchContainer.setVisibility(View.INVISIBLE);
			mClearSearchBtn.setVisibility(View.GONE);
		} else {
			mClearSearchBtn.setVisibility(View.VISIBLE);
			mCityContainer.setVisibility(View.INVISIBLE);
			mSearchContainer.setVisibility(View.VISIBLE);
			mSearchCityAdapter.getFilter().filter(s);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// 如何搜索字符串长度为0，是否隐藏输入法
		// if(TextUtils.isEmpty(s)){
		// mInputMethodManager.hideSoftInputFromWindow(
		// mSearchEditText.getWindowToken(), 0);
		// }

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_clear_text:
			if (!TextUtils.isEmpty(mSearchEditText.getText().toString())) {
				mSearchEditText.setText("");
				mInputMethodManager.hideSoftInputFromWindow(
						mSearchEditText.getWindowToken(), 0);
			}
			break;
		case R.id.title_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	public static onCityNameChanged ocnc;
	public interface onCityNameChanged{
		public void setCityName(City city);
	}

}
