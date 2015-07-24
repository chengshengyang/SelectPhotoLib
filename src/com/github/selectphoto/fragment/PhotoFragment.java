package com.github.selectphoto.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.github.selectphoto.R;
import com.github.selectphoto.SelectPhotoActivity;
import com.github.selectphoto.adapter.PhotoAdapter;
import com.github.selectphoto.adapter.PhotoAdapter.onSelectedPhotoChangeListener;
import com.github.selectphoto.constants.Constants;
import com.github.selectphoto.entity.AlbumInfo;
import com.github.selectphoto.entity.PhotoInfo;
import com.github.selectphoto.ui.CustomImageView;

/**
 * 展示某一个相册中图片表格的Fragment
 * @author chengsy
 *
 */
public class PhotoFragment extends BaseFragment {

	private GridView mGridView;
	private AlbumInfo mAlbumInfo;
	private PhotoAdapter mAdapter;

	private Button mPreviewBtn;
	private Button mSendBtn;
	private ActionBar mActionBar;
	private FrameLayout mFrameLayout;
	private CustomImageView mCountView;
	private OnGridClickListener mOnGridClickListener;
	private SelectPhotoActivity mActivity;
	
	public interface OnGridClickListener {
		public void onGridItemClick(AlbumInfo albumInfo, int position);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (SelectPhotoActivity) activity;
		if (getActivity() instanceof OnGridClickListener) {
			mOnGridClickListener = (OnGridClickListener) getActivity();
		} else if (getParentFragment() instanceof OnGridClickListener) {
			mOnGridClickListener = (OnGridClickListener) getParentFragment();
		} else {

		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragment = inflater.inflate(R.layout.fragment_gridview, container, false);
		initView();
		return mFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity() != null) {
			mAdapter = new PhotoAdapter(getActivity().getApplicationContext(), mAlbumInfo);
			mGridView.setAdapter(mAdapter);
		}
		initEvent();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			mActionBar.setTitle(mAlbumInfo.getAlbumName());
		}
	}
	 
	public int getSelectedCount() {
		int selectedCount = 0;
		for (int i = 0; i < mAlbumInfo.getPhotoList().size(); i++) {
			if (mAlbumInfo.getPhotoList().get(i).isSelected) {
				selectedCount++;
			}
		}
		return selectedCount;
	}

	public void setInfo(AlbumInfo info) {
		this.mAlbumInfo = info;
	}
	
	@Override
	public void initView() {
		mGridView = (GridView) mFragment.findViewById(R.id.photo_gridview);
		mPreviewBtn = (Button) mFragment.findViewById(R.id.preview_btn);
		mSendBtn = (Button) mFragment.findViewById(R.id.send_image_btn1);
		mFrameLayout = (FrameLayout) mFragment.findViewById(R.id.send_grid_image_framelayout);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		mActionBar = mActivity.getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(mAlbumInfo.getAlbumName());
		
		mCountView = new CustomImageView(getActivity().getApplicationContext());
		mCountView.setCount(0);
		LinearLayout.LayoutParams params = new LayoutParams(60, LayoutParams.MATCH_PARENT);
		mFrameLayout.addView(mCountView, params);

		if (getSelectedCount() == 0) {
			mPreviewBtn.setEnabled(false);
			mPreviewBtn.setTextColor(Color.GRAY);
			mSendBtn.setEnabled(false);
			mSendBtn.setTextColor(Color.GRAY);
		} else {
			mPreviewBtn.setEnabled(true);
			mPreviewBtn.setTextColor(Color.WHITE);
			mSendBtn.setEnabled(true);
			mSendBtn.setTextColor(Color.WHITE);
		}
	}

	@Override
	public void initEvent() {
		mGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				if (scrollState == SCROLL_STATE_IDLE)
//					UniversalImageLoadTool.resume();
//				else
//					UniversalImageLoadTool.pause();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mAdapter != null) {
					if (mOnGridClickListener != null) {
						mOnGridClickListener.onGridItemClick(mAlbumInfo, position);
					}
				}
			}
		});

		mPreviewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mAdapter != null) {
					if (mOnGridClickListener != null) {
						List<PhotoInfo> selectedList = new ArrayList<PhotoInfo>();
						for (int i = 0; i < mAlbumInfo.getPhotoList().size(); i++) {
							PhotoInfo pInfo = mAlbumInfo.getPhotoList().get(i);
							if (pInfo.isSelected) {
								selectedList.add(pInfo);
							}
						}
						AlbumInfo tmp = new AlbumInfo();
						tmp.setPhotoList(selectedList);
						mOnGridClickListener.onGridItemClick(tmp, 0);
					}
				}
			}
		});

		mAdapter.setOnSelectedPhotoChangeListener(new onSelectedPhotoChangeListener() {

			@Override
			public void onChangedListener(List<PhotoInfo> photoList) {
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			FragmentManager fm = getActivity().getSupportFragmentManager();
			PhotoFragment fragment = (PhotoFragment) fm.findFragmentByTag(getTag());
			if (fragment == null) return false;
			((SelectPhotoActivity) getActivity()).removeFragment(fragment);
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void invalidate() {
		mAdapter.notifyDataSetChanged();
		int selected = getSelectedCount();
		mCountView.setCount(selected);
		if (selected == 0) {
			mPreviewBtn.setEnabled(false);
			mPreviewBtn.setTextColor(Color.GRAY);
			mSendBtn.setEnabled(false);
			mSendBtn.setTextColor(Color.GRAY);
		} else {
			mPreviewBtn.setEnabled(true);
			mPreviewBtn.setTextColor(Color.WHITE);
			mSendBtn.setEnabled(true);
			mSendBtn.setTextColor(Color.WHITE);
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
		return null;
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
}
