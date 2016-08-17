package feicuiedu.com.videonews.commons;


import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;

/**
 * 需要用到{@link Context}类的一些工具方法，当没有调用{@link #init(Context)}方法时，
 * 则认为是在JUnit测试环境下运行，返回各自的默认值。
 */
public class ContextUtils {

    private ContextUtils() {
    }

    private static Context context;

    public static void init(Context context) {
        ContextUtils.context = context.getApplicationContext();
    }

    /**
     * @param filename 文件名
     * @return 缓存文件
     */
    @SuppressWarnings("SameParameterValue")
    public static File getCacheFile(String filename) {
        return context == null ? new File(filename) : new File(context.getCacheDir(), filename);
    }

    /**
     * @return 当前是否有网络
     */
    public static boolean hasNetwork() {
        if (context == null) return true;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //noinspection SimplifiableIfStatement
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }
}
