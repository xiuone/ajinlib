package xy.xy.base.utils.notify.version;

import java.io.Serializable;

public class VersionMode implements Serializable {
    public String appKey;
    public int code;
    public String downloadUrl;
    public int id;
    public int iosTradeType;
    public int mandatoryUpgrade;
    public int rollback;
    public String upgradeDesc;
    public String versionNo;

    public boolean isMust(){
        return mandatoryUpgrade == 1;
    }
}
