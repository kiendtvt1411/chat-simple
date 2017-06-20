package dhbkhn.kien.simplechat.config;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

/**
 * Created by kiend on 6/20/2017.
 */

public class AppConfigs {

    private static AppConfigs _instance;
    private FirebaseRemoteConfig config;

    private AppConfigs(){

    }

    public FirebaseRemoteConfig getConfig() {
        return config;
    }

    public void setConfig(FirebaseRemoteConfig config) {
        this.config = config;
    }

    public static AppConfigs getInstance(){
        if (_instance == null) {
            _instance = new AppConfigs();
        }
        return _instance;
    }
}
