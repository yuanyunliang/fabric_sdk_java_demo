package cn.neusoft.btlabs.sdk.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric创建的orderer信息，涵盖单机和集群两种方案
 *
 * @author XieYongJie
 * @Date 2018/5/24 16:59
 */
public class Orderers {
    /**
     * orderer 排序服务器所在根域名
     */
    private String ordererDomainName; // orderer.example.com
    /**
     * orderer 排序服务器集合
     */
    private List<Orderer> orderers;

    public Orderers() {
        orderers = new ArrayList<>();
    }

    public String getOrdererDomainName() {
        return ordererDomainName;
    }

    public void setOrdererDomainName(String ordererDomainName) {
        this.ordererDomainName = ordererDomainName;
    }

    /**
     * 获取排序服务器集合
     */
    public List<Orderer> getOrderers() {
        return orderers;
    }

    /**
     * 新增排序服务器
     */
    public void addOrderer(Orderer orderer) {
        this.orderers.add(orderer);
    }
}
