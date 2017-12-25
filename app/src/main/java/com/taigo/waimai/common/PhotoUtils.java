package com.taigo.waimai.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.util.List;


/**
 * [从本地选择图片以及拍照工具类，完美适配2.0-7.0版本]
 *
 * @author huxinwu
 * @version 1.0
 * @date 2015-1-7
 **/
public class PhotoUtils {

    private final String TAG = PhotoUtils.class.getSimpleName();

    /**
     * 裁剪图片成功后返回
     **/
    public static final int INTENT_CROP = 2;
    /**
     * 拍照成功后返回
     **/
    public static final int INTENT_TAKE = 3;
    /**
     * 拍照成功后返回
     **/
    public static final int INTENT_SELECT = 4;

    public static final String CROP_FILE_NAME = "crop_file.jpg";
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/CameraCache");
    private final Uri outputUri;


    /**
     * PhotoUtils对象
     **/
    private OnPhotoResultListener onPhotoResultListener;
    private File mCurrentPhotoFile;


    public PhotoUtils(OnPhotoResultListener onPhotoResultListener) {
        this.onPhotoResultListener = onPhotoResultListener;

        //String fileName = System.currentTimeMillis() + ".jpg";
        if (!PHOTO_DIR.exists()) {
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
        }
        mCurrentPhotoFile = new File(PHOTO_DIR, CROP_FILE_NAME);
        outputUri = Uri.fromFile(new File(mCurrentPhotoFile.getPath()));
    }


    /**
     * 拍照(7.0之后)
     *
     * @param
     * @return
     */
    public void takePicture(Activity activity) {

        try {
            Uri uri=buildUri(activity);

            //每次选择图片吧之前的图片删除
            //clearCropFile(outputUri);

            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri imageUri = FileProvider.getUriForFile(activity, "com.xtdar.app.provider", mCurrentPhotoFile);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            }



            if (!isIntentAvailable(activity, intent)) {
                return;
            }

            activity.startActivityForResult(intent, INTENT_TAKE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /***
     * 选择一张图片
     * 图片类型，这里是image/*，当然也可以设置限制
     * 如：image/jpeg等
     *
     * @param activity Activity
     */
    @SuppressLint("InlinedApi")
    public void selectPicture(Activity activity) {
        try {
            //每次选择图片吧之前的图片删除
            //clearCropFile(buildUri(activity));

            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            if (!isIntentAvailable(activity, intent)) {
                return;
            }
//            android.support.v4.app.Fragment fragment = f.getParentFragment();
//            while (fragment.getParentFragment() != null) {
//                fragment = fragment.getParentFragment();
//            }
            activity.startActivityForResult(intent, INTENT_SELECT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建uri
     *
     * @param activity
     * @return
     */
    private Uri buildUri(Activity activity) {
        if (CommonTools.checkSDCard()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return FileProvider.getUriForFile(activity, "com.xtdar.app.provider", mCurrentPhotoFile);
            } else {
                return Uri.fromFile(Environment.getExternalStorageDirectory()).buildUpon().appendPath(CROP_FILE_NAME).build();
            }

        } else {
            return Uri.fromFile(activity.getCacheDir()).buildUpon().appendPath(CROP_FILE_NAME).build();
        }
    }

    /**
     * @param intent
     * @return
     */
    protected boolean isIntentAvailable(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private boolean corp(Activity activity, Uri uri) {
        String url = FileUtils.getPath(activity, uri);
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        //sdk>=24
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Uri imageUri = FileProvider.getUriForFile(activity, "com.xtdar.app.provider", new File(url));//通过FileProvider创建一个content类型的Uri
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.putExtra("noFaceDetection", true);//去除默认的人脸识别，否则和剪裁匡重叠
            cropIntent.setDataAndType(imageUri, "image/*");

            //19=<sdk<24
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cropIntent.setDataAndType(Uri.fromFile(new File(url)), "image/*");

            //sdk<19
        } else {
            cropIntent.setDataAndType(uri, "image/*");
        }

        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 400);
        cropIntent.putExtra("outputY", 400);
        cropIntent.putExtra("return-data", false);
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        if (!isIntentAvailable(activity, cropIntent)) {
            return false;
        } else {
            try {
                activity.startActivityForResult(cropIntent, INTENT_CROP);
                return true;
            } catch (Exception e) {
                Log.w("sssssssssss",e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * 返回结果处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (onPhotoResultListener == null) {
            Log.e(TAG, "onPhotoResultListener is not null");
            return;
        }

        switch (requestCode) {
            //拍照
            case INTENT_TAKE:
                if (mCurrentPhotoFile.exists()) {
                    if (corp(activity, Uri.fromFile(mCurrentPhotoFile))) {
                        return;
                    }
                    onPhotoResultListener.onPhotoCancel();
                }
                break;

            //选择图片
            case INTENT_SELECT:
                if (data != null && data.getData() != null) {
                    Uri imageUri = data.getData();
                    if (corp(activity, imageUri)) {
                        return;
                    }
                }
                onPhotoResultListener.onPhotoCancel();
                break;

            //截图
            case INTENT_CROP:
                if (resultCode == Activity.RESULT_OK && mCurrentPhotoFile.exists()) {

                    onPhotoResultListener.onPhotoResult(buildUri(activity),mCurrentPhotoFile);
                }
                break;
        }
    }

    /**
     * 删除文件
     *
     * @param uri
     * @return
     */
    public boolean clearCropFile(Uri uri) {
        if (uri == null) {
            return false;
        }

        File file = new File(uri.getPath());
        if (file.exists()) {
            boolean result = file.delete();
            if (result) {
                Log.i(TAG, "Cached crop file cleared.");
            } else {
                Log.e(TAG, "Failed to clear cached crop file.");
            }
            return result;
        } else {
            Log.w(TAG, "Trying to clear cached crop file but it does not exist.");
        }

        return false;
    }

    /**
     * [回调监听类]
     *
     * @author huxinwu
     * @version 1.0
     * @date 2015-1-7
     **/
    public interface OnPhotoResultListener {
        void onPhotoResult(Uri uri, File file);

        void onPhotoCancel();
    }

    public OnPhotoResultListener getOnPhotoResultListener() {
        return onPhotoResultListener;
    }

    public void setOnPhotoResultListener(OnPhotoResultListener onPhotoResultListener) {
        this.onPhotoResultListener = onPhotoResultListener;
    }

}