package cn.neusoft.btlabs.sdk;

import cn.neusoft.btlabs.sdk.bean.Chaincode;
import cn.neusoft.btlabs.sdk.bean.Orderers;
import cn.neusoft.btlabs.sdk.bean.Peers;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Objects;

/**
 * @Author XieYongJie
 * @Date 2018/5/24 19:14
 */
public class FabricConfig {

    private static Logger log = Logger.getLogger(FabricConfig.class);

    /**
     * 节点服务器对象
     */
    private Peers peers;
    /**
     * 排序服务器对象
     */
    private Orderers orderers;
    /**
     * 智能合约对象
     */
    private Chaincode chaincode;
    /**
     * channel-artifacts所在路径
     * 默认：/resources/classes/fabric/channel-artifacts/
     */
    private String channelArtifactsPath;
    /**
     * crypto-config所在路径
     * 默认：/resources/fabric/crypto-config/
     */
    private String cryptoConfigPath;
    private boolean registerEvent = false;

    public FabricConfig() {
        channelArtifactsPath = getChannlePath() + "/channel-artifacts/";
        cryptoConfigPath = getChannlePath() + "/crypto-config/";
    }

    /**
     * 默认fabric配置路径
     *
     * @return fabric配置的绝对路径
     */
    private String getChannlePath() {
        String directorys = Objects.requireNonNull(ChaincodeManager.class.getClassLoader().getResource("fabric")).getFile();
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath()); // E:/OneDrive - s.nuit.edu.cn/workspace/IntelliJ/fabric-sdk-java-xyj/target/classes/fabric
        return directory.getPath();
    }

    Peers getPeers() {
        return peers;
    }

    Orderers getOrderers() {
        return orderers;
    }

    Chaincode getChaincode() {
        return chaincode;
    }

    String getChannelArtifactsPath() {
        return channelArtifactsPath;
    }

    boolean isRegisterEvent() {
        return registerEvent;
    }

    String getCryptoConfigPath() {
        return cryptoConfigPath;
    }

    public void setPeers(Peers peers) {
        this.peers = peers;
    }

    public void setOrderers(Orderers orderers) {
        this.orderers = orderers;
    }

    public void setChaincode(Chaincode chaincode) {
        this.chaincode = chaincode;
    }

    public void setChannelArtifactsPath(String channelArtifactsPath) {
        this.channelArtifactsPath = channelArtifactsPath;
    }

    public void setCryptoConfigPath(String cryptoConfigPath) {
        this.cryptoConfigPath = cryptoConfigPath;
    }

    public void setRegisterEvent(boolean registerEvent) {
        this.registerEvent = registerEvent;
    }
}
