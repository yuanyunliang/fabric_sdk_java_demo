package cn.neusoft.btlabs.sdk.bean;

/**
 * 排序服务器对象
 *
 * @Author XieYongJie
 * @Date 2018/5/24 17:06
 */
public class Orderer {
    /**
     * orderer 排序服务器的域名
     */
    private String ordererName;
    /**
     * orderer 排序服务器的访问地址
     */
    private String ordererLocation;

    public Orderer(String ordererName, String ordererLocation) {
        super();
        this.ordererName = ordererName;
        this.ordererLocation = ordererLocation;
    }

    public String getOrdererName() {
        return ordererName;
    }

    public String getOrdererLocation() {
        return ordererLocation;
    }
}
