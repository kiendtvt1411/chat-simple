package dhbkhn.kien.simplechat;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import dhbkhn.kien.simplechat.config.AppConfigs;

/**
 * Created by kiend on 6/20/2017.
 */

public class ChatApp extends Application {

    /**
     * @param: Khi chay trong release mode thi config se duoc cache tai app trong
     * 12 tieng => thay doi gia tri tren console thi can 12 tieng moi co tac dung
     * tren tat ca nguoi dung
     * */
    private static final long CONFIG_EXPIRE_SECOND = 12 * 3600;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();

        // Singleton class
        AppConfigs.getInstance().setConfig(config);

        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        config.setConfigSettings(settings);

        config.setDefaults(R.xml.default_remote_config);

        long expireTime = config.getInfo().getConfigSettings().isDeveloperModeEnabled()?0: CONFIG_EXPIRE_SECOND;

        config.fetch(expireTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AppConfigs.getInstance().getConfig().activateFetched();
                        }
                    }
                });
    }
}
