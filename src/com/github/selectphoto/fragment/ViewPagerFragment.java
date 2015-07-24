package com.github.selectphoto.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.github.selectphoto.R;
import com.github.selectphoto.SelectPhotoActivity;
import com.github.selectphoto.adapter.ViewPagerAdapter;
import com.github.selectphoto.constants.Constants;
import com.github.selectphoto.entity.AlbumInfo;
import com.github.selectphoto.entity.PhotoInfo;
import com.github.selectphoto.ui.CustomImageView;
import com.github.selectphoto.ui.ViewPagerFixed;

/**
 * 展示图片大图的Fragment
 * @author chengsy
 *
 */
public class ViewPagerFragment extends BaseFragment implements OnPageChangeListener {

	private AlbumInfo mAlbumInfo;
	private ViewPagerAdapter mAdapter;
	private ViewPagerFixed mViewPager;
	
	private Button mSendBtn;
	private ActionBar mActionBar;
	private ImageView mActionBarSelectIv;
	private View mMenuItemView;
	
	@Deprecated
	private CheckBox mOriginalCheckBox;
	
	private CustomImageView mCountView;
	private FrameLayout mSendFrameLayout;
	private SelectPhotoActivity mActivity;
	private Context mContext;
	
	private int mCurPosition = 0;
	private String mToastFormat;
	private String mCheckBoxFormat;
	private int mMaxCount = Constants.MAX_SELECT_COUNT;
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(getActivity().getApplicationContext(), String.format(mToastFormat, mMaxCount), Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};
	
	public void setInfo(AlbumInfo info, int position) {
		this.mAlbumInfo = info;
		this.mCurPosition = position;
	}
	
	public int getSelectedCount() {
		int selectedCount = 0;
		for (int i = 0; i < mAlbumInfo.getPhotoList().size(); i++) {
			if (mAlbumInfo.getPhotoList().get(i).isSelected) {
				selectedCount ++;
			}
		}
		return selectedCount;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragment = inflater.inflate(R.layout.fragment_viewpager, container, false);
		mActivity = (SelectPhotoActivity) getActivity();
		mContext = getActivity().getApplicationContext();
		initView();
		return mFragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mActivity != null) {
			mToastFormat = mActivity.getApplicationContext().getString(R.string.toast_max_count);
			mCheckBoxFormat = mActivity.getApplicationContext().getString(R.string.checkbox_original_size);
			mAdapter = new ViewPagerAdapter(getActivity(), mAlbumInfo);
			mViewPager.setAdapter(mAdapter);
		}
		initEvent();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		onPageSelected(mCurPosition);
	}
	
	@Override
	public void initView() {
		mViewPager = (ViewPagerFixed) mFragment.findViewById(R.id.view_pager);
		mSendFrameLayout = (FrameLayout) mFragment.findViewById(R.id.send_image_framelayout);
		mSendBtn = (Button) mFragment.findViewById(R.id.send_image_btn2);
		mOriginalCheckBox = (CheckBox) mFragment.findViewById(R.id.original_image_checkbox);
		
		mActionBar = mActivity.getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mMenuItemView = getIconMenuItem(Constants.TAG_MENU_SELECT, R.drawable.gou_normal, mOnClickListener);
		
		int count = getSelectedCount();
		mCountView = new CustomImageView(getActivity().getApplicationContext());
		mCountView.setCount(count);
		LinearLayout.LayoutParams params = new LayoutParams(60, LayoutParams.MATCH_PARENT);
		mSendFrameLayout.addView(mCountView, params);
		
		if (count == 0) {
			mSendBtn.setEnabled(false);
			mSendBtn.setTextColor(Color.GRAY);
		} else {
			mSendBtn.setEnabled(true);
			mSendBtn.setTextColor(Color.WHITE);
		}
	}
	
	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int tag = (Integer) v.getTag();
			switch (tag) {
			case Constants.TAG_MENU_SELECT:
				List<PhotoInfo> photoList = mAlbumInfo.getPhotoList();
				PhotoInfo pInfo = photoList.get(mCurPosition);
				int selectedCount = getSelectedCount();
				if (selectedCount < mMaxCount) {
					pInfo.isSelected = !pInfo.isSelected;

					if (pInfo.isSelected) {
						mActionBarSelectIv.setImageResource(R.drawable.gou_selected);
						selectedCount ++;
					} else {
						mActionBarSelectIv.setImageResource(R.drawable.gou_normal);
						selectedCount --;
					}
				} else if (selectedCount >= mMaxCount) {
					if (pInfo.isSelected == true) {
						pInfo.isSelected = !pInfo.isSelected;
						mActionBarSelectIv.setImageResource(R.drawable.gou_normal);
						selectedCount --;
					} else {
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
						
//						for (int i = 0; i < photoList.size(); i++) {
//							if (photoList.get(i).isSelected) {
//								photoList.get(i).isSelected = false;
//								break;
//							}
//						}
//						
//						pInfo.isSelected = !pInfo.isSelected;
//						mActionBarSelectIv.setImageResource(R.drawable.gou_selected);
					}
				}
				invalidate();
			
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem recordItem = menu.add(getString(R.string.action_menu_select));
		setActionViewAlways(recordItem, mMenuItemView);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	/**
	 * 设置图片MenuItem
	 * @param tag 点击事件标记
	 * @param resId 显示图片资源id
	 * @param listener 点击事件
	 * @return
	 */
	public View getIconMenuItem(int tag, int resId, OnClickListener listener) {
		View view = View.inflate(mContext, R.layout.actionbar_menu_item_view, null);
		mActionBarSelectIv = (ImageView) view.findViewById(R.id.icon);
		mActionBarSelectIv.setImageResource(resId);
		setViewBackground(view, tag, listener);
		return view;
	}

	/**
	 * 设置MenuItem一直显示
	 * @param item
	 * @param view
	 */
	public void setActionViewAlways(MenuItem item, View view){
		MenuItemCompat.setActionView(item, view);
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
	}
	
	public void setViewBackground(View view, int tag, OnClickListener listener) {
		view.setBackgroundResource(R.drawable.actionbar_menu_selector);
		view.setMinimumWidth(SelectPhotoActivity.iAcionWidth);
		view.setMinimumHeight(SelectPhotoActivity.iActionHeight);
		view.setTag(tag);
		view.setOnClickListener(listener);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			FragmentManager fm = getActivity().getSupportFragmentManager();
			ViewPagerFragment fragment = (ViewPagerFragment) fm.findFragmentByTag(getTag());
			if (fragment == null) return false;
			((SelectPhotoActivity) getActivity()).removeFragment(fragment);
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void initEvent() {
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(mCurPosition);
		mActionBar.setTitle(mCurPosition + 1 + "/" + mAdapter.getCount());
		
		mOriginalCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PhotoInfo info = mAlbumInfo.getPhotoList().get(mCurPosition);
				if (isChecked) {
					info.isOriginal = true;
					String fileSize = getFileSize(info.getImagePath());
					mOriginalCheckBox.setText(String.format(mCheckBoxFormat, fileSize));
					mOriginalCheckBox.setTextColor(Color.WHITE);
				} else {
					info.isOriginal = false;
					mOriginalCheckBox.setText(R.string.checkbox_original);
					mOriginalCheckBox.setTextColor(Color.GRAY);
				}
			}
		});
		
		mOriginalCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int count = getSelectedCount();
				PhotoInfo info = mAlbumInfo.getPhotoList().get(mCurPosition);
				if (count < mMaxCount && mOriginalCheckBox.isChecked()) {
					info.isSelected = true;
					mActionBarSelectIv.setImageResource(R.drawable.gou_selected);
				} else if (count >= mMaxCount && !info.isSelected && mOriginalCheckBox.isChecked()) {
					Message message = Message.obtain(mHandler, 0);
					message.sendToTarget();
				}
				invalidate();
			}
		});
		
		mSendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putStringArrayListExtra(Constants.EXTRA_SELECTED_FILE_PATH, (ArrayList<String>) getSelectedPhotos());
				((SelectPhotoActivity) getActivity()).setResult(Activity.RESULT_OK, intent);
				((SelectPhotoActivity) getActivity()).finish();
			}
		});
	}
	
	@Override
	public void invalidate() {
		mAdapter.notifyDataSetChanged();
		int count = getSelectedCount();
		mCountView.setCount(count);
		if (count == 0) {
			mSendBtn.setEnabled(false);
			mSendBtn.setTextColor(Color.GRAY);
		} else {
			mSendBtn.setEnabled(true);
			mSendBtn.setTextColor(Color.WHITE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		mCurPosition = position;
		mActionBar.setTitle(mCurPosition + 1 + "/" + mAdapter.getCount());
		PhotoInfo pInfo = mAlbumInfo.getPhotoList().get(position);
		
		if (pInfo.isSelected) {
			mActionBarSelectIv.setImageResource(R.drawable.gou_selected);
		} else {
			mActionBarSelectIv.setImageResource(R.drawable.gou_normal);
		}
		
		if (mOriginalCheckBox.isChecked() == pInfo.isOriginal) {
			if (mOriginalCheckBox.isChecked()) {
				String fileSize = getFileSize(pInfo.getImagePath());
				mOriginalCheckBox.setText(String.format(mCheckBoxFormat, fileSize));
				mOriginalCheckBox.setTextColor(Color.WHITE);
			} else {
				mOriginalCheckBox.setText(R.string.checkbox_original);
				mOriginalCheckBox.setTextColor(Color.GRAY);
			}
		} else {
			mOriginalCheckBox.setChecked(pInfo.isOriginal);
		}
	}
	
	/**
	 * 返回选取的单个图片
	 */
	public String getSelectedPhoto() {
		List<PhotoInfo> pInfos = mAlbumInfo.getPhotoList();
		for (int i = 0; i < pInfos.size(); i++) {
			PhotoInfo pInfo = pInfos.get(i);
			if (pInfo.isSelected) {
				return pInfo.getImageURI();
			}
		}
		return pInfos.get(mCurPosition).getImageURI();
	}
	
	/**
	 * 返回选取的多个图片
	 */
	public List<String> getSelectedPhotos() {
		ArrayList<String> selectedList = new ArrayList<String>();
		List<PhotoInfo> pInfos = mAlbumInfo.getPhotoList();
		for (int i = 0; i < pInfos.size(); i++) {
			PhotoInfo pInfo = pInfos.get(i);
			if (pInfo.isSelected) {
				selectedList.add(pInfo.getImageURI());
			}
		}
		
		return selectedList;
	}
	
	public String getFileSize(String filePath) {
		File file = new File(filePath);
		return FormatFileSize(file.length());
	}
	
	//get file size formated, for example: xxxB,xxxKB,xxxMB,xxxGB
    public String FormatFileSize(long filesize) {
		String sizeStr = null;
		float sizeFloat = 0;
		if (filesize < 1024) {
			sizeStr = Long.toString(filesize);
			sizeStr += "B";
		} else if (filesize < (1 << 20)) {
			sizeFloat = (float) filesize / (1 << 10);
			sizeFloat = (float) (Math.round(sizeFloat * 100)) / 100;
			sizeStr = Float.toString(sizeFloat) + "KB";
		} else if (filesize < (1 << 30)) {
			sizeFloat = (float) filesize / (1 << 20);
			sizeFloat = (float) (Math.round(sizeFloat * 100)) / 100;
			sizeStr = Float.toString(sizeFloat) + "MB";
		} else {
			sizeFloat = (float) filesize / (1 << 30);
			sizeFloat = (float) (Math.round(sizeFloat * 100)) / 100;
			sizeStr = Float.toString(sizeFloat) + "GB";
		}
		return sizeStr;
	}
}
