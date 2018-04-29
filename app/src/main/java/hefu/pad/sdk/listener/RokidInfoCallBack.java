package hefu.pad.sdk.listener;

public interface RokidInfoCallBack {
    /**
     * 获取Rokid 设备的回调
     */
    void RokidInfos(String str);

    /**
     * 获取rokid 设备数据失败
     */
    void RokidInfoFail();
}
