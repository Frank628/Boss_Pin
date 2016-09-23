package com.offer9191.boss.activity.my;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.LoginJson;
import com.offer9191.boss.jsonbean.UploadPicJson;
import com.offer9191.boss.utils.Base64Coder;
import com.offer9191.boss.utils.FileUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.NavigationLayout;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnOperItemClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.ActionSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by OfferJiShu01 on 2016/9/6.
 */
@ContentView(R.layout.activity_personinfo)
public class ActivityPersonInfo extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.avatar)SimpleDraweeView avatar;
    @ViewInject(R.id.tv_name)TextView tv_name;
    @ViewInject(R.id.tv_companyname)TextView tv_companyname;
    @ViewInject(R.id.tv_phone)TextView tv_phone;
    @ViewInject(R.id.tv_logaccount)TextView tv_logaccount;
    @ViewInject(R.id.rg)RadioGroup rg;
    @ViewInject(R.id.rb_male)RadioButton rb_male;
    @ViewInject(R.id.rb_female)RadioButton rb_female;
    public static final int NAME_RESULT=1;
    public static final int PHONE_RESULT=2;
    public static final int COMPANY_RESULT=3;
    public static final int CAPTRUE_REQUEST_CODE=4;
    private static final int IMAGE_REQUEST_CODE = 5;
    private static final int CAMERA_REQUEST_CODE = 6;
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setColor(this, ContextCompat.getColor(this,R.color.nav_bg),true);
        navigationLayout.setCenterText(getResources().getString(R.string.person_info));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getPersonInfo();
        if (!TextUtils.isEmpty(MyInfoManager.getUserPreview(this))){
            avatar.setImageURI(Uri.parse(MyInfoManager.getUserPreview(this)));
        }
        tv_name.setText(MyInfoManager.getDisplayName(this));
        tv_companyname.setText(MyInfoManager.getCompanyName(this));
        tv_phone.setText(MyInfoManager.getPhone(this));
        tv_logaccount.setText(MyInfoManager.getUserName(this));
        if (MyInfoManager.getSEX(this).equals("男"))
            rb_male.setChecked(true);
        else
            rb_female.setChecked(true);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkID) {
                switch (checkID){
                    case R.id.rb_male:
                        saveSex("男");
                        break;
                    case R.id.rb_female:
                        saveSex("女");
                        break;
                }
            }
        });
    }

    @Event(value = R.id.rl_avatar)
    private void avatar(View view){
        final String[] stringItems = {"从手机相册选择", "拍照"};
        final ActionSheetDialog actionSheetDialog=new ActionSheetDialog(ActivityPersonInfo.this,stringItems,null);
        actionSheetDialog.isTitleShow(false).setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==1){
                    Intent intentFromCapture = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                    if (FileUtils.hasSdcard()) {
                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(Environment .getExternalStorageDirectory(),IMAGE_FILE_NAME)));
                    }
                    startActivityForResult(intentFromCapture,CAMERA_REQUEST_CODE);
                }else if(position==0){
                    Intent intentFromGallery = new Intent();
                    intentFromGallery.setType("image/*"); // 设置文件类型
                    intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intentFromGallery,IMAGE_REQUEST_CODE);
                }
            }
        });
        actionSheetDialog.show();
    }
    @Event(value = R.id.rl_name)
    private void editname(View view){
        Intent intent =new Intent(this, EditPersonInfoActivity.class);
        intent.putExtra("name",getResources().getString(R.string.name));
        intent.putExtra("param","myName");
        intent.putExtra("inputtype", InputType.TYPE_CLASS_TEXT);
        intent.putExtra("content",tv_name.getText().toString().trim());
        startActivityForResult(intent,NAME_RESULT);
    }
    @Event(value = R.id.rl_phone)
    private void editphone(View view){
        Intent intent =new Intent(this, EditPersonInfoActivity.class);
        intent.putExtra("name",getResources().getString(R.string.phonenumber));
        intent.putExtra("param","mobilePhoneNumber");
        intent.putExtra("inputtype", InputType.TYPE_CLASS_PHONE);
        intent.putExtra("content",tv_phone.getText().toString().trim());
        startActivityForResult(intent,PHONE_RESULT);
    }
    @Event(value = R.id.rl_company)
    private void editcompany(View view){
        Intent intent =new Intent(this, EditPersonInfoActivity.class);
        intent.putExtra("name",getResources().getString(R.string.company_name));
        intent.putExtra("param","partnerName");
        intent.putExtra("inputtype", InputType.TYPE_CLASS_TEXT);
        intent.putExtra("content",tv_companyname.getText().toString().trim());
        startActivityForResult(intent,COMPANY_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case NAME_RESULT:
                    tv_name.setText(data.getStringExtra("result"));
                    break;
                case COMPANY_RESULT:
                    tv_companyname.setText(data.getStringExtra("result"));
                    break;
                case PHONE_RESULT:
                    tv_phone.setText(data.getStringExtra("result"));
                    break;
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (FileUtils.hasSdcard()) {
                        File tempFile = new File(Environment.getExternalStorageDirectory()+"/"+ IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    }else{
                        Toast.makeText(ActivityPersonInfo.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                    break;
                case CAPTRUE_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url=FileUtils.getPath(ActivityPersonInfo.this,uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }else{
            intent.setDataAndType(uri, "image/*");
        }
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CAPTRUE_REQUEST_CODE);
    }
    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] b = stream.toByteArray();
            // 将图片流以字符串形式存储下来
            String tp= new String(Base64Coder.encodeLines(b));
            //		如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
            //		Bitmap dBitmap = BitmapFactory.decodeFile(tp);
            //		Drawable drawable = new BitmapDrawable(dBitmap);
            uploadhead(tp);
            avatar.setImageDrawable(drawable);
            avatar.setAdjustViewBounds(true);
        }
    }

    private void uploadhead(String pic){
        JSONObject jsonObject=null;
        try {
            jsonObject =new JSONObject();
            jsonObject.put("SessionId", MyInfoManager.getSessionID(getApplicationContext()));
            jsonObject.put("FileName","headpic.jpg");
            jsonObject.put("UserPreview",pic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params=new RequestParams(Constants.URL+"api/User/UpdateUserPhoto");
        params.addBodyParameter("", jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    UploadPicJson uploadPicJson = GsonTools.changeGsonToBean(result,UploadPicJson.class);
                    if (uploadPicJson.code==0){
                        MyInfoManager.setUserPreview(ActivityPersonInfo.this,uploadPicJson.data.UserPreview);
                        avatar.setImageURI(Uri.parse(uploadPicJson.data.UserPreview));
                    }else{
                        Toast.makeText(ActivityPersonInfo.this,getString(R.string.uploadhead_fail),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {}
        });
    }
    private void getPersonInfo(){
        RequestParams params=new RequestParams(Constants.URL+"api/User/GetLoginedUserInfo");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(this));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("GetLoginedUserInfo",result);
                try {
                    LoginJson loginJson= GsonTools.changeGsonToBean(result,LoginJson.class);
                    if (loginJson.code==0){
                        MyInfoManager.setUserName(ActivityPersonInfo.this,loginJson.data.UserName);
                        MyInfoManager.setSEX(ActivityPersonInfo.this,loginJson.data.Sex);
                        MyInfoManager.setPhone(ActivityPersonInfo.this,loginJson.data.MobilePhoneNumber);
                        MyInfoManager.setCompanyName(ActivityPersonInfo.this,loginJson.data.PartnersName);
                        MyInfoManager.setDisplayName(ActivityPersonInfo.this,loginJson.data.DisplayName);
                        MyInfoManager.setUserPreview(ActivityPersonInfo.this,loginJson.data.HDpic);
                    }else{
                        Toast.makeText(ActivityPersonInfo.this,loginJson.msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {}
        });
    }
    private void saveSex(String sex){
        RequestParams params=new RequestParams(Constants.URL+"api/User/UpdateUserInfo");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(this));
        params.addBodyParameter("myName","");
        params.addBodyParameter("sex", sex);
        params.addBodyParameter("mobilePhoneNumber", "");
        params.addBodyParameter("partnerName", "");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("api/User/UpdateUserInfo",result);
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {}
        });
    }
}
