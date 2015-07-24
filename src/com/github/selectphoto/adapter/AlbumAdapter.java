package com.github.selectphoto.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.selectphoto.R;
import com.github.selectphoto.entity.AlbumInfo;
import com.github.selectphoto.entity.PhotoInfo;
import com.github.selectphoto.ui.RotateImageViewAware;
import com.github.selectphoto.util.UniversalImageLoadTool;

/**
 * 相册列表适配器
 * @author chengsy
 *
 */
public class AlbumAdapter extends BaseAdapter {

	private List<AlbumInfo> mList;
	private ViewHolder mHolder;
	private Context mContext;
	private String mPhotoCountFormat;
	
	public AlbumAdapter(Context context) {
		super();
		this.mContext = context;
		mPhotoCountFormat = mContext.getString(R.string.album_count);
	}

	@Override
	public int getCount() {
		return (mList==null ? 0 : mList.size());
	}

	@Override
	public Object getItem(int position) {
		return (mList==null ? null : mList.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder{
		ImageView iv_album;
		TextView tv_name;
		TextView tv_count;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image_list, null);
			mHolder = new ViewHolder();
			mHolder.iv_album = (ImageView)convertView.findViewById(R.id.album_iv);
			mHolder.tv_name = (TextView)convertView.findViewById(R.id.album_name_tv);
			mHolder.tv_count = (TextView)convertView.findViewById(R.id.album_count_tv);
			
			convertView.setTag(mHolder);
		}else
			mHolder = (ViewHolder) convertView.getTag();
		
		AlbumInfo aInfo = mList.get(position);
		PhotoInfo pInfo = aInfo.getPhotoList().get(0);
		UniversalImageLoadTool.disPlay(pInfo.getImageURI(), 
				new RotateImageViewAware(mHolder.iv_album, pInfo.getImagePath()));
		
		mHolder.tv_name.setText(aInfo.getAlbumName());
		
		String sSize = String.format(mPhotoCountFormat, aInfo.getPhotoList().size());
		mHolder.tv_count.setText(sSize);
		
		return convertView;
	}

	public List<AlbumInfo> getList() {
		return mList;
	}
	
	public void setList(List<AlbumInfo> list) {
		this.mList = list;
		notifyDataSetChanged();
	}
}
