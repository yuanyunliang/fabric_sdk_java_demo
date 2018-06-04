package cn.neusoft.btlabs.energychain.utils;

import cn.neusoft.btlabs.sdk.ChaincodeManager;
import cn.neusoft.btlabs.sdk.FabricConfig;
import cn.neusoft.btlabs.sdk.bean.*;

import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @Author XieYongJie
 * @Date 2018/5/24 20:20
 */
class FabricManager {
    private static Logger log = Logger.getLogger(FabricManager.class);

    private ChaincodeManager manager;

    private FabricManager instance = null;

    // 由于是单机部署，所以使用统一地址
    private final static String LOCALHOST = "172.13.5.55";

    // 排序服务器
    private final static String ORDERER_DOMAIN_NAME = "example.com";
    private final static String ORDERER_NAME = "orderer.example.com";
    private final static String ORDERER_LOCATION = "grpc://" + LOCALHOST + ":7050";

    // 组织服务器
    private final static String ORG_NAME = "Org1";
    private final static String ORG_MSPID = "Org1MSP";
    private final static String ORG_DOMAIN_NAME = "org1.example.com";
    private final static String ORG_CA_LOCATION = "http://" + LOCALHOST + ":7054";
    private final static String ORG_PEER_1_NAME = "peer0.org1.example.com";
    private final static String ORG_PEER_1_LOCATION = "grpc://" + LOCALHOST + ":7051";
    private final static String ORG_PEER_1_EVENT_HUB_NAME = "peer0.org1.example.com";
    private final static String ORG_PEER_1_EVENT_HUB_LOCATION = "grpc://" + LOCALHOST + ":7053";

    // 通道名称
    private static String channel_name;
    // 合约名称
    private static String chaincodeName;
    // 合约版本
    private static String chaincodeVersion;

    FabricManager obtain() throws CryptoException, InvalidArgumentException, TransactionException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
        if (null == channel_name || null == chaincodeName) {
            return null;
        }
        // 获取对应的通道上对应的合约管理器
        if (null == instance) {
            synchronized (FabricManager.class) {
                if (null == instance) {
                    instance = new FabricManager(channel_name, chaincodeName);
                }
            }
        }
        return instance;
    }

    public FabricManager(String chName, String ccName) throws CryptoException, InvalidArgumentException, TransactionException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
        // 判断通道有效性
        if ("elechannel".equalsIgnoreCase(chName)) {
            channel_name = chName;
        }
        // 判断合约有效性并指定合约版本
        if ("ele_consumer".equalsIgnoreCase(ccName)) {
            chaincodeName = "ele_consumer";
            chaincodeVersion = "2.1";
        } else if ("ele_producer".equalsIgnoreCase(ccName)) {
            chaincodeName = "ele_producer";
            chaincodeVersion = "2.1";
        } else if ("ele_transaction".equalsIgnoreCase(ccName)) {
            chaincodeName = "ele_transaction";
            chaincodeVersion = "2.1";
        }
        manager = new ChaincodeManager(getConfig());
    }

    /**
     * 获取节点服务器管理器
     *
     * @return 节点服务器管理器
     */
    ChaincodeManager getManager() {
        return manager;
    }

    /**
     * 根据节点作用类型获取节点服务器配置
     *
     * @return 节点服务器配置
     */
    private FabricConfig getConfig() {
        FabricConfig config = new FabricConfig();
        config.setOrderers(getOrderers());
        config.setPeers(getPeers());
        config.setChaincode(getChaincode(chaincodeName, "github.com/hyperledger/fabric/examples/chaincode/go/" + chaincodeName, chaincodeVersion));
        log.debug("init Chaincode = " + chaincodeName + " - " + chaincodeVersion);
        config.setChannelArtifactsPath(getChannleArtifactsPath());
        config.setCryptoConfigPath(getCryptoConfigPath());
        return config;
    }

    private Orderers getOrderers() {
        Orderers orderer = new Orderers();
        orderer.setOrdererDomainName(ORDERER_DOMAIN_NAME);
        orderer.addOrderer(new Orderer(ORDERER_NAME, ORDERER_LOCATION));
        return orderer;
    }

    /**
     * 获取节点服务器集
     *
     * @return 节点服务器集
     */
    private Peers getPeers() {
        Peers peers = new Peers();
        peers.setOrgName(ORG_NAME);
        peers.setOrgMSPID(ORG_MSPID);
        peers.setOrgDomainName(ORG_DOMAIN_NAME);
        peers.addPeer(new Peer(ORG_PEER_1_NAME, ORG_PEER_1_EVENT_HUB_NAME, ORG_PEER_1_LOCATION, ORG_PEER_1_EVENT_HUB_LOCATION, ORG_CA_LOCATION));
        return peers;
    }

    /**
     * 获取智能合约
     *
     * @param chaincodeName    智能合约名称
     * @param chaincodePath    智能合约路径
     * @param chaincodeVersion 智能合约版本
     * @return 智能合约
     */
    private Chaincode getChaincode(String chaincodeName, String chaincodePath, String chaincodeVersion) {
        Chaincode chaincode = new Chaincode();
        chaincode.setChannelName(channel_name);
        chaincode.setChaincodeName(chaincodeName);
        chaincode.setChaincodePath(chaincodePath);
        chaincode.setChaincodeVersion(chaincodeVersion);
        chaincode.setInvokeWatiTime(chaincode.getInvokeWatiTime());
        chaincode.setDeployWatiTime(chaincode.getDeployWatiTime());
        return chaincode;
    }

    /**
     * 获取fabric配置路径
     * 注意：工作路径不能有空格！
     *
     * @return %WORKSPACE%\target\classes\fabric\channel-artifacts\
     */
    private String getChannleArtifactsPath() {
        String directorys = Objects.requireNonNull(FabricManager.class.getClassLoader().getResource("fabric")).getFile();
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath());
        return directory.getPath() + "/channel-artifacts/";
    }

    /**
     * 获取fabric配置路径
     * 注意：工作路径不能有空格！
     *
     * @return %WORKSPACE%\target\classes\fabric\crypto-config\
     */
    private String getCryptoConfigPath() {
        String directorys = Objects.requireNonNull(FabricManager.class.getClassLoader().getResource("fabric")).getFile();
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath());
        return directory.getPath() + "/crypto-config/";
    }
}
