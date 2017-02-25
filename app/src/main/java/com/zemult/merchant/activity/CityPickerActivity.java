package com.zemult.merchant.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.city.adapter.CityListAdapter;
import com.zemult.merchant.activity.city.adapter.ResultListAdapter;
import com.zemult.merchant.activity.city.db.DBManager;
import com.zemult.merchant.activity.city.entity.City;
import com.zemult.merchant.activity.city.entity.LocateState;
import com.zemult.merchant.activity.city.utils.StringUtils;
import com.zemult.merchant.activity.city.view.SideLetterBar;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.SPUtils;

import java.util.List;

import cn.trinea.android.common.util.ToastUtils;

public class CityPickerActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_PICKED_CITY = "picked_city";
    RelativeLayout relativeLayoutHead;
    Button btnBack, btnRight;
    TextView tvTitle;
    private ListView mListView;
    private ListView mResultListView;
    private SideLetterBar mLetterBar;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private CityListAdapter mCityAdapter;
    private ResultListAdapter mResultAdapter;
    private List<City> mAllCities, mRecentCities;
    private DBManager dbManager;
    private AMapLocationClient mLocationClient;
    private LinearLayout llBack;
    private Context mContext;
    private City city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        initData();
        initView();
        initLocation();
    }

    private void initLocation() {
        mLocationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        String location = aMapLocation.getCity();
                        String district = aMapLocation.getDistrict();
                        String name = StringUtils.extractLocation(location, district);
                        String no = aMapLocation.getCityCode();
                        city = new City(name, "", no);
                        Log.e("onLocationChanged", "city: " + city);
                        Log.e("onLocationChanged", "district: " + district);

                        // 存储到SP中
//                        SPUtils.put(CityPickerActivity.this, Constants.SP_CITY, city.getNo());
                        SPUtils.put(CityPickerActivity.this, Constants.SP_CENTER, aMapLocation.getLongitude() + "," + aMapLocation.getLatitude());

                        Constants.CENTER = (String) SPUtils.get(mContext, Constants.SP_CENTER, "119.971736,31.829737");

                        mCityAdapter.updateLocateState(LocateState.SUCCESS, city);
                    } else {
                        //定位失败
                        mCityAdapter.updateLocateState(LocateState.FAILED, null);
                    }
                }
            }
        });
        mLocationClient.startLocation();
    }

    private void initData() {
        mContext = this;
        dbManager = new DBManager(this);
        dbManager.copyDBFile();
        mAllCities = dbManager.getAllCities();
        mRecentCities = dbManager.getRecentCities();
        mCityAdapter = new CityListAdapter(this, mAllCities, mRecentCities);
        mCityAdapter.setOnCityClickListener(new CityListAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(City city) {
                back(city);
                dbManager.insertCity(city);
            }

            @Override
            public void onLocateClick() {
                requestLocationPermission();
            }
        });

        mResultAdapter = new ResultListAdapter(this, null);
    }

    private void initView() {
        relativeLayoutHead = (RelativeLayout) findViewById(R.id.include_layout_head);
        btnBack = (Button) relativeLayoutHead.findViewById(R.id.lh_btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);

        llBack = (LinearLayout) relativeLayoutHead.findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);

        btnRight = (Button) relativeLayoutHead.findViewById(R.id.lh_btn_right);
        btnRight.setText(getResources().getString(R.string.btn_register));
        btnRight.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnRight.setTextColor(getResources().getColor(R.color.white));
        tvTitle = (TextView) relativeLayoutHead.findViewById(R.id.lh_tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("选择城市");
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        tvTitle.setTextSize(18);

        mListView = (ListView) findViewById(R.id.listview_all_city);
        mListView.setAdapter(mCityAdapter);

        TextView overlay = (TextView) findViewById(R.id.tv_letter_overlay);
        mLetterBar = (SideLetterBar) findViewById(R.id.side_letter_bar);
        mLetterBar.setOverlay(overlay);
        mLetterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityAdapter.getLetterPosition(letter);
                mListView.setSelection(position);
            }
        });

        searchBox = (EditText) findViewById(R.id.et_search);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    clearBtn.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    mResultListView.setVisibility(View.GONE);
                } else {
                    clearBtn.setVisibility(View.VISIBLE);
                    mResultListView.setVisibility(View.VISIBLE);
                    List<City> result = dbManager.searchCity(keyword);
                    if (result == null || result.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        mResultAdapter.changeData(result);
                    }
                }
            }
        });

        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                back(mResultAdapter.getItem(position));
            }
        });

        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);

        clearBtn.setOnClickListener(this);
    }

    private void back(City city) {
        // 存储到SP中
        SPUtils.put(CityPickerActivity.this, Constants.SP_CITY, city.getNo());
        Constants.CITYID = (String) SPUtils.get(mContext, Constants.SP_CITY, "0519");

        ToastUtils.show(this, "选择的城市：" + city.getName() + "-" + city.getNo());
        Intent data = new Intent();
        data.putExtra("city_name", city.getName());
        data.putExtra("city_no", city.getNo());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_clear:
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                mResultListView.setVisibility(View.GONE);
                break;
            case R.id.ll_back:
            case R.id.lh_btn_back:
                if (city == null) {
                    back(new City("常州", "changzhou", "0519"));
                } else {
                    if(getIntent().getStringExtra("cityName").contains("定位"))
                        back(city);
                    else
                        finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
    }

    private void requestLocationPermission() {
        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .rationale(rationaleListener)
                .send();
    }

    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            new AlertDialog.Builder(CityPickerActivity.this)
                    .setTitle("友好提醒")
                    .setMessage("您已拒绝过定位权限，没有定位权限无法提供更好服务, 请授予定位权限")
                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.resume();
                        }
                    })
                    .setNegativeButton("仍然拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.cancel();
                        }
                    }).show();
        }
    };

    @PermissionYes(100)
    private void getLocationYes() {
        Log.e("onLocateClick", "重新定位...");
        mCityAdapter.updateLocateState(LocateState.LOCATING, null);
        mLocationClient.startLocation();
    }

    @PermissionNo(100)
    private void getLocationNo() {
        Toast.makeText(this, "获取定位权限失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
