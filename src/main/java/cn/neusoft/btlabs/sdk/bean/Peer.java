package cn.neusoft.btlabs.sdk.bean;

/**
 * 节点服务器对象
 *
 * @Author XieYongJie
 * @Date 2018/5/24 17:11
 */
public class Peer {
    /**
     * 当前指定的组织节点域名
     */
    private String peerName; // peer0.org1.example.com
    /**
     * 当前指定的组织节点事件域名
     */
    private String peerEventHubName; // peer0.org1.example.com
    /**
     * 当前指定的组织节点访问地址
     */
    private String peerLocation; // grpc://172.13.5.55:7051
    /**
     * 当前指定的组织节点事件监听访问地址
     */
    private String peerEventHubLocation; // grpc://172.13.5.55:7053
    /**
     * 当前指定的组织节点ca访问地址
     */
    private String caLocation; // http://172.13.5.55:7054
    /**
     * 当前peer是否增加Event事件处理
     */
    private boolean addEventHub = false;

    public Peer(String peerName, String peerEventHubName, String peerLocation, String peerEventHubLocation, String caLocation) {
        this.peerName = peerName;
        this.peerEventHubName = peerEventHubName;
        this.peerLocation = peerLocation;
        this.peerEventHubLocation = peerEventHubLocation;
        this.caLocation = caLocation;
    }

    public String getPeerName() {
        return peerName;
    }

    public String getPeerEventHubName() {
        return peerEventHubName;
    }

    public String getPeerLocation() {
        return peerLocation;
    }

    public String getPeerEventHubLocation() {
        return peerEventHubLocation;
    }

    public String getCaLocation() {
        return caLocation;
    }

    public void setAddEventHub(boolean addEventHub) {
        this.addEventHub = addEventHub;
    }

    public boolean isAddEventHub() {
        return addEventHub;
    }
}
