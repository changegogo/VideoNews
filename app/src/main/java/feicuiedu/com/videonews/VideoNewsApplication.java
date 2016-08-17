package feicuiedu.com.videonews;


import android.app.Application;

import feicuiedu.com.videonews.commons.ContextUtils;
import feicuiedu.com.videonews.commons.ToastUtils;
import timber.log.Timber;

public class VideoNewsApplication extends Application{


    @Override public void onCreate() {
        super.onCreate();

        ContextUtils.init(this);
        ToastUtils.init(this);

        // 默认的Tree，在每个类中使用类名作为TAG
        Timber.plant(new Timber.DebugTree());
    }
}
