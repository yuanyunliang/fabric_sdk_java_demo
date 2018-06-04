package cn.neusoft.btlabs.sdk.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric创建的peer信息，包含有cli、org、ca、couchdb等节点服务器关联启动服务信息集合
 *
 * @Author XieYongJie
 * @Date 2018/5/24 17:10
 */
public class Peers {
    /**
     * 当前指定的组织名称
     */
    private String orgName; // Org1
    /**
     * 当前指定的组织名称
     */
    private String orgMSPID; // Org1MSP
    /**
     * 当前指定的组织所在根域名
     */
    private String orgDomainName; //org1.example.com
    /**
     * orderer 排序服务器集合
     */
    private List<Peer> peers;

    public Peers() {
        this.peers = new ArrayList<>();
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgMSPID() {
        return orgMSPID;
    }

    public void setOrgMSPID(String orgMSPID) {
        this.orgMSPID = orgMSPID;
    }

    public String getOrgDomainName() {
        return orgDomainName;
    }

    public void setOrgDomainName(String orgDomainName) {
        this.orgDomainName = orgDomainName;
    }

    /**
     * 获取排序服务器集合
     */
    public List<Peer> getPeers() {
        return peers;
    }

    /**
     * 新增排序服务器
     */
    public void addPeer(Peer peer) {
        this.peers.add(peer);
    }
}
