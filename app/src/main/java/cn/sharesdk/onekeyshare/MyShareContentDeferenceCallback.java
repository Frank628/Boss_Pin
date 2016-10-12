package cn.sharesdk.onekeyshare;

import android.content.Context;

import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;

/**
 * Created by OfferJiShu01 on 2016/9/28.
 */
public class MyShareContentDeferenceCallback implements ShareContentCustomizeCallback {
    private Context context;
    private String title="", text="",url="";




    public MyShareContentDeferenceCallback(Context context, String title, String text, String url) {
        super();
        this.context = context;
        this.title = title;
        this.text = text;
        this.url=url;
    }
    @Override
    public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
        if(platform instanceof CustomPlatform){
            return;
        }
        if ("ShortMessage".equals(platform.getName())||"SinaWeibo".equals(platform.getName())) {
            paramsToShare.setText(text+"\n"+url);

        }else if ("WechatMoments".equals(platform.getName())||"Wechat".equals(platform.getName())||"WechatFavorite".equals(platform.getName())) {
            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
            paramsToShare.setTitle(title);
            paramsToShare.setText(text);
            paramsToShare.setUrl(url);
        }else if("QZone".equals(platform.getName())||"QQ".equals(platform.getName())){
            paramsToShare.setTitle(title);
            paramsToShare.setTitleUrl(url);
            paramsToShare.setText(text);

        }else{
            paramsToShare.setText(text+"\n"+url);
//			paramsToShare.setImageData(getImageFromAssetsFile("qrcode.png"));
        }
    }
}
