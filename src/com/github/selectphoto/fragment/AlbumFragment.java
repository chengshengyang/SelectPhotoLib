package com.github.selectphoto.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.github.selectphoto.R;
import com.github.selectphoto.SelectPhotoActivity;
import com.github.selectphoto.adapter.AlbumAdapter;
import com.github.selectphoto.entity.AlbumInfo;
import com.github.selectphoto.entity.PhotoInfo;
import com.github.selectphoto.fragment.PhotoFragment.OnGridClickListener;

/**
 * 展示相册列表的Fragment
 * @author chengsy
 *
 */
public class AlbumFragment extends BaseFragment {

	private ListView mListView;
	private List<AlbumInfo> mAlbumList;
	private AlbumAdapter mAdapter;
	private ActionBar mActionBar;
	private Map<String, String> mThumbnailList = new HashMap<String, String>();
	private SelectPhotoActivity mActivity;
	private OnAlbumClickListener mOnAlbumClickListener;

	public interface OnAlbumClickListener {
		public void onListClick(AlbumInfo albumInfo);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (SelectPhotoActivity) activity;
		
		if (getActivity() instanceof OnGridClickListener) {
			mOnAlbumClickListener = (OnAlbumClickListener) getActivity();
		} else if (getParentFragment() instanceof OnGridClickListener) {
			mOnAlbumClickListener = (OnAlbumClickListener) getParentFragment();
		} else {

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragment = inflater.inflate(R.layout.fragment_listview, container, false);
		initView();
		return mFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initEvent();
		new AlbumAsync().execute();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			mActionBar.setTitle(R.string.album_title);
		}
	}
	
	/**
	 * 读取媒体资源中缩略图资源，以HashMap的方式保存在mThumbnailList中。
	 */
	public void getThummnail() {
		if (isAdded()) {
			ContentResolver cr = getActivity().getContentResolver();
			String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA };
			Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, 
					projection, null, null, 
					Thumbnails.DATA + " desc ");
			
			if (cursor != null && cursor.getCount() > 0) {
				mAlbumList = new ArrayList<AlbumInfo>();
				while (cursor.moveToNext()) {
					@SuppressWarnings("unused")
					int _id;
					int image_id;
					String image_path;
					
					int _idColumn = cursor.getColumnIndex(Thumbnails._ID);
					int image_idColumn = cursor.getColumnIndex(Thumbnails.IMAGE_ID);
					int dataColumn = cursor.getColumnIndex(Thumbnails.DATA);
					
					// Get the field values
					_id = cursor.getInt(_idColumn);
					image_id = cursor.getInt(image_idColumn);
					image_path = cursor.getString(dataColumn);
					
					mThumbnailList.put("" + image_id, image_path);
				}
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.finish();
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 异步构建相册数据
	 * @author chengsy
	 *
	 */
	class AlbumAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			
			getThummnail();
			
			ContentResolver cr = getActivity().getContentResolver();
			String[] projection = { Media._ID, Media.BUCKET_ID, Media.BUCKET_DISPLAY_NAME, Media.DATA };
			Cursor cursor = cr.query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
					null, null, Media.DATE_MODIFIED + " desc ");
			
			if (cursor != null && cursor.getCount() > 0) {
				mAlbumList = new ArrayList<AlbumInfo>();
				Map<String, AlbumInfo> idMap = new HashMap<String, AlbumInfo>();
				while (cursor.moveToNext()) {
					PhotoInfo pInfo = new PhotoInfo();
					String s_ID = cursor.getString(0);
					String s_Buck_ID = cursor.getString(1);
					String sName = cursor.getString(2);
					String sPath = cursor.getString(3);

					pInfo.setImageID(s_ID);
					pInfo.setThumbnailPath(mThumbnailList.get(s_ID));
					pInfo.setImagePath(sPath);
					pInfo.setImageURI("file://" + sPath);

					File file = new File(sPath);
					if (file.length() == 0) {
						continue;
					}
					if (idMap.containsKey(s_Buck_ID)) {
						idMap.get(s_Buck_ID).getPhotoList().add(pInfo);
					} else {
						List<PhotoInfo> mPhotoList = new ArrayList<PhotoInfo>();
						mPhotoList.add(pInfo);

						AlbumInfo aInfo = new AlbumInfo();
						aInfo.setAlbumName(sName);
						aInfo.setPhotoList(mPhotoList);
						mAlbumList.add(aInfo);

						idMap.put(s_Buck_ID, aInfo);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (getActivity() != null && mAlbumList != null) {
				mAdapter = new AlbumAdapter(getActivity());
				mAdapter.setList(mAlbumList);
				mListView.setAdapter(mAdapter);
			}
		}
	}

	@Override
	public void initView() {
		mListView = (ListView) mFragment.findViewById(R.id.album_lv);
		mActionBar = mActivity.getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(R.string.album_title);
	}

	@Override
	public void initEvent() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mAdapter != null) {
					AlbumInfo aInfo = (AlbumInfo) mAdapter.getItem(position);
					//((MainActivity) getActivity()).onListClick(aInfo); also work well
					if (mOnAlbumClickListener != null)
						mOnAlbumClickListener.onListClick(aInfo);
				}
			}
		});

		mListView.setOnScrollListener(new OnScrollListener() {

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
		
	}

	@Override
	public void invalidate() {
		mAdapter.notifyDataSetChanged();
	}

}
