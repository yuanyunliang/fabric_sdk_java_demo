package cn.neusoft.btlabs.sdk;


import cn.neusoft.btlabs.sdk.bean.Chaincode;
import cn.neusoft.btlabs.sdk.bean.Orderer;
import cn.neusoft.btlabs.sdk.bean.Orderers;
import cn.neusoft.btlabs.sdk.bean.Peers;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 智能合约管理器
 *
 * @Author XieYongJie
 * @Date 2018/5/24 19:16
 */
public class ChaincodeManager {

    private static Logger log = Logger.getLogger(ChaincodeManager.class);

    private FabricConfig config;
    private Peers peers;
    private Orderers orderers;
    private Chaincode chaincode;

    private HFClient client;
    private FabricOrg fabricOrg;
    private Channel channel;
    private ChaincodeID chaincodeID;

    public ChaincodeManager(FabricConfig fabricConfig) throws CryptoException, InvalidArgumentException, IOException, TransactionException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
        this.config = fabricConfig;

        peers = this.config.getPeers();
        orderers = this.config.getOrderers();
        chaincode = this.config.getChaincode();

        client = HFClient.createNewInstance();
        log.debug("Create instance of HFClient");
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        log.debug("Set Crypto Suite of HFClient");

        fabricOrg = getFabricOrg();
        channel = getChannel();
        chaincodeID = getChaincodeID();

        client.setUserContext(fabricOrg.getPeerAdmin()); // 也许是1.0.0测试版的bug，只有节点管理员可以调用链码
    }

    private FabricOrg getFabricOrg() throws IOException {
        // java.io.tmpdir（临时路径）：%temp%
        File storeFile = new File(System.getProperty("java.io.tmpdir") + "/XieYongJie_HFSample.properties");
        FabricStore fabricStore = new FabricStore(storeFile);

        // 获取组织的配置
        FabricOrg fabricOrg = new FabricOrg(peers, orderers, fabricStore, config.getCryptoConfigPath());
        log.debug("Get FabricOrg");
        return fabricOrg;
    }

    private Channel getChannel() throws InvalidArgumentException, TransactionException, IllegalAccessException, EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, IOException {
        client.setUserContext(fabricOrg.getPeerAdmin());
        return getChannel(fabricOrg, client);
    }

    private Channel getChannel(FabricOrg fabricOrg, HFClient client) throws InvalidArgumentException, TransactionException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException, EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, IOException {
        Channel channel = client.newChannel(chaincode.getChannelName());
        log.debug("Get Chain " + chaincode.getChannelName());

        for (int i = 0; i < peers.getPeers().size(); i++) {
            cn.neusoft.btlabs.sdk.bean.Peer peer = peers.getPeers().get(i);
            String peerDomainName = peers.getOrgDomainName();
            File peerCert = Paths.get(config.getCryptoConfigPath(), "/peerOrganizations", peerDomainName, "peers", peer.getPeerName(), "tls/server.crt").toFile();
            if (!peerCert.exists()) {
                throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", peer.getPeerName(), peerCert.getAbsolutePath()));
            }
            Properties peerProperties = new Properties();
            peerProperties.setProperty("pemFile", peerCert.getAbsolutePath());
            peerProperties.setProperty("hostnameOverride", peerDomainName);
            peerProperties.setProperty("sslProvider", "openSSL");
            peerProperties.setProperty("negotiationType", "TLS");
            peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
            channel.addPeer(client.newPeer(peer.getPeerName(), fabricOrg.getPeerLocationForName(peer.getPeerName()), peerProperties));
            if (peer.isAddEventHub()) {
                channel.addEventHub(client.newEventHub(peer.getPeerEventHubName(), fabricOrg.getEventHubLocationForName(peer.getPeerEventHubName()), peerProperties));
            }
        }

        for (int i = 0; i < orderers.getOrderers().size(); i++) {
            Orderer orderer = orderers.getOrderers().get(i);
            String ordererDomainName = orderers.getOrdererDomainName();
            File ordererCert = Paths.get(config.getCryptoConfigPath(), "/ordererOrganizations", ordererDomainName, "orderers", orderer.getOrdererName(), "tls/server.crt").toFile();
            if (!ordererCert.exists()) {
                throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", orderer.getOrdererName(), ordererCert.getAbsolutePath()));
            }
            Properties ordererProperties = new Properties();
            ordererProperties.setProperty("pemFile", ordererCert.getAbsolutePath());
            ordererProperties.setProperty("hostnameOverride", ordererDomainName);
            ordererProperties.setProperty("sslProvider", "openSSL");
            ordererProperties.setProperty("negotiationType", "TLS");
            ordererProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
            ordererProperties.setProperty("ordererWaitTimeMilliSecs", "300000");
            // ordererProperties.putAll(tlsProperties);
            channel.addOrderer(client.newOrderer(orderer.getOrdererName(), fabricOrg.getOrdererLocationForName(orderer.getOrdererName()), ordererProperties));
        }

        log.debug("channel.isInitialized() = " + channel.isInitialized());
        if (!channel.isInitialized()) {
            channel.initialize();
        }

        // config.setRegisterEvent(true);
        if (config.isRegisterEvent()) {
            channel.registerBlockListener(event -> {
                // TODO
                log.debug("========================Event事件监听开始========================");
                try {
                    log.debug("event.getChannelId() = " + event.getChannelId());
                    // log.debug("event.getEvent().getChaincodeEvent().getPayload().toStringUtf8() = " + event.getEvent().getChaincodeEvent().getPayload().toStringUtf8());
                    log.debug("event.getBlock().getData().getDataList().size() = " + event.getBlock().getData().getDataList().size());
                    ByteString byteString = event.getBlock().getData().getData(0);
                    String result = byteString.toStringUtf8();
                    log.debug("byteString.toStringUtf8() = " + result);

                    String r1[] = result.split("END CERTIFICATE");
                    String rr = r1[2];
                    log.debug("rr = " + rr);
                } catch (InvalidProtocolBufferException e) {
                    // TODO
                    e.printStackTrace();
                }
                log.debug("========================Event事件监听结束========================");
            });
        }
        return channel;
    }

    private ChaincodeID getChaincodeID() {
        return ChaincodeID.newBuilder().setName(chaincode.getChaincodeName()).setVersion(chaincode.getChaincodeVersion()).setPath(chaincode.getChaincodePath()).build();
    }

    /**
     * 执行智能合约
     *
     * @param fcn  方法名
     * @param args 参数数组
     * @return 插入结果
     */
    public Map<String, String> invoke(String fcn, String[] args) throws InvalidArgumentException, ProposalException, IOException {
        Map<String, String> resultMap = new HashMap<>();

        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        /// 将交易提议发送给所有节点
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        log.debug("chaincodeID = " + chaincodeID.toString());
        transactionProposalRequest.setFcn(fcn);
        if (null != args && args.length != 0) {
            transactionProposalRequest.setArgs(args);
        }

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));
        transactionProposalRequest.setTransientMap(tm2);

        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(transactionPropResp);
        if (proposalConsistencySets.size() != 1) {
            log.error("Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size());
        }

        if (failed.size() > 0) {
            ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
            log.error("Not enough endorsers for inspect:" + failed.size() + " endorser error: " + firstTransactionProposalResponse.getMessage() + ". Was verified: " + firstTransactionProposalResponse.isVerified());
            resultMap.put("code", "error");
            resultMap.put("data", firstTransactionProposalResponse.getMessage());
            return resultMap;
        } else {
            log.info("Successfully received transaction proposal responses.");
            ProposalResponse resp = transactionPropResp.iterator().next();
            byte[] x = resp.getChaincodeActionResponsePayload();
            String resultAsString = null;
            if (x != null) {
                resultAsString = new String(x, "UTF-8");
            }
            log.info("resultAsString = " + resultAsString);
            channel.sendTransaction(successful);
            resultMap.put("code", "success");
            resultMap.put("data", resultAsString);
            return resultMap;
        }
    }

    /**
     * 查询智能合约
     *
     * @param fcn  方法名
     * @param args 参数数组
     * @return 查询结果
     */
    public Map<String, String> query(String fcn, String[] args) throws InvalidArgumentException, ProposalException {
        Map<String, String> resultMap = new HashMap<>();
        String payload;
        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        if (null != args && args.length != 0) {
            queryByChaincodeRequest.setArgs(args);
        }
        queryByChaincodeRequest.setFcn(fcn);
        queryByChaincodeRequest.setChaincodeID(chaincodeID);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
        queryByChaincodeRequest.setTransientMap(tm2);

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());
        for (ProposalResponse proposalResponse : queryProposals) {
            if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                log.debug("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage() + ". Was verified : " + proposalResponse.isVerified());
                resultMap.put("code", "error");
                resultMap.put("data", "Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage() + ". Was verified : " + proposalResponse.isVerified());
            } else {
                payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                log.debug("Query payload from peer: " + proposalResponse.getPeer().getName());
                log.debug("" + payload);
                resultMap.put("code", "success");
                resultMap.put("data", payload);
            }
        }
        return resultMap;
    }
}
