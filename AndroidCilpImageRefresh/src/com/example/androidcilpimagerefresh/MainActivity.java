package com.example.androidcilpimagerefresh;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * ����ͼƬ
 * @author miaowei
 *
 */
public class MainActivity extends Activity {

	/**
	 * ���д�ͼ
	 */
	private Button mBtn_cilp;
	/**
	 * �鿴���ͼƬ
	 */
	private Button mBtn_find;
	/**
	 * ��������ͼ
	 */
	private Button mBtncilp_thumb;
	
	private ImageView mImageView;
	
	private Uri mImageCaptureUri;
	
	/**
	 * �����������ͼ
	 */
	private static final int CHOOSE_BIG_PICTURE_SAVE = 5;
	/**
	 * ������д�ͼ
	 */
	private static final int CHOOSE_BIG_PICTURE = 4;
	/**
	 * �������ͼƬ
	 */
	private static final int PICK_FROM_FILE = 3;
	/**
	 * ����ͼƬҪ��ŵ�Ŀ¼
	 */
	private String cilpFile =Environment.getExternalStorageDirectory().getAbsolutePath()+"/cilp";
	private String imageFilePath = cilpFile+"/temp.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBtn_cilp = (Button) findViewById(R.id.my_btn_cilp);
		mBtn_find = (Button)findViewById(R.id.my_btn_find);
		
		mImageView = (ImageView) findViewById(R.id.image_view);
		mBtncilp_thumb = (Button)findViewById(R.id.my_btncilp_thumb);
		mBtn_find.setOnClickListener(onClickListener);
		mBtn_cilp.setOnClickListener(onClickListener);
		mBtncilp_thumb.setOnClickListener(onClickListener);
		//��������Ŀ¼
		File file = new File(cilpFile);
		if (!file.exists()) {
			
			file.mkdirs();
		}
		File fileImage = new File(imageFilePath);
		if (!fileImage.exists()) {
			
			try {
				fileImage.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

	}
	
	

	private android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.my_btn_cilp: //���д�ͼ
				//(file://)����Ҫ��
				mImageCaptureUri = Uri.parse("file://"+imageFilePath);
				//��ͼʹ��URL
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
				intent.setType("image/*");
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 600);
				intent.putExtra("outputY", 600);
				intent.putExtra("scale", true);
				intent.putExtra("return-data", false);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
				intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
				intent.putExtra("noFaceDetection", true); // no face detection
				//ע��Intent.createChooser��ʹ��,ѡ��������Ӧ��
				startActivityForResult(Intent.createChooser(intent, "ѡ��"), CHOOSE_BIG_PICTURE);
				
				break;
			case R.id.my_btn_find://ѡ��ͼ��
				Intent intentimage = new Intent(Intent.ACTION_PICK, null);
				intentimage.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
				startActivityForResult(intentimage, PICK_FROM_FILE);
				break;
			case R.id.my_btncilp_thumb: //��������ͼ
				//ͨ��intent�����õ�type�������жϾ�������ĸ������
				Intent intentThumb = new Intent(Intent.ACTION_GET_CONTENT, null);
				intentThumb.setType("image/*");
				intentThumb.putExtra("crop", "true");
				intentThumb.putExtra("aspectX", 2);
				intentThumb.putExtra("aspectY", 1);
				intentThumb.putExtra("outputX", 200);
				intentThumb.putExtra("outputY", 100);
				intentThumb.putExtra("scale", true);
				intentThumb.putExtra("return-data", true);
				intentThumb.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
				intentThumb.putExtra("noFaceDetection", true); // no face detection
				startActivityForResult(intentThumb, CHOOSE_BIG_PICTURE_SAVE);
				break;
			default:
				break;
			}
			
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(
				MainActivity.this,
				"�@����������" + requestCode + "resultCode===" + resultCode
						+ "RESULT_OK======" + RESULT_OK, Toast.LENGTH_LONG)
				.show();
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case PICK_FROM_FILE:
			//����ͼʹ��bitmap
			mImageCaptureUri = data.getData();
			Log.i("cilp", "CHOOSE_BIG_PICTURE: data = " + data);//it seems to be null
			 if(mImageView != null){
			  Bitmap bitmap = decodeUriAsBitmap(mImageCaptureUri);//decode bitmap
			  mImageView.setImageBitmap(bitmap);
			 }
			break;
		case CHOOSE_BIG_PICTURE:
			/*File file = new File(cilpFile);
			if (file.exists()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].length() > 0) {
						
						files[i].delete();
					}
				}
			}*/
			saveBigmap();
			break;
		case CHOOSE_BIG_PICTURE_SAVE:
			 if(data != null){
				 saveCutPic(data);
			 	}
			break;
		default:
			break;
		}
	}

	private Bitmap decodeUriAsBitmap(Uri uri){
		 Bitmap bitmap = null;
		 try {
		  bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		 } catch (FileNotFoundException e) {
		  e.printStackTrace();
		  return null;
		 }
		 return bitmap;
		}
	
	
	/**
	 * �����ͼ
	 */
	private void saveBigmap(){
		
		if(mImageCaptureUri != null){
			 Bitmap bitmap = decodeUriAsBitmap(mImageCaptureUri);//decode bitmap
			 mImageView.setImageBitmap(bitmap);
		}
	}
	/**
	 * ������к������ͼƬ
	 * @param picdata
	 */
	private void saveCutPic(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (null != bundle) {
			Bitmap mBitmap = bundle.getParcelable("data");
			mImageView.setImageBitmap(mBitmap);
		
			try {
				saveMyBitmap(mBitmap, String.valueOf(System.currentTimeMillis()));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	}
	
	public void saveMyBitmap(Bitmap mBitmap,String bitName) throws IOException {
		File f = new File(imageFilePath);
		if (!f.exists()) {
			
			   f.createNewFile();
		}
     
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
	}

}
