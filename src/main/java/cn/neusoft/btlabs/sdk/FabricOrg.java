package cn.neusoft.btlabs.sdk;

import cn.neusoft.btlabs.sdk.bean.Orderer;
import cn.neusoft.btlabs.sdk.bean.Orderers;
import cn.neusoft.btlabs.sdk.bean.Peers;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * 联盟组织对象
 *
 * @Author XieYongJie
 * @Date 2018/5/24 18:57
 */
public class FabricOrg {

    private static Logger log = Logger.getLogger(FabricOrg.class);

    /**
     * 名称
     */
    private String name;
    /**
     * 会员id
     */
    private String mspid;
    /**
     * 域名名称
     */
    private String domainName;

    /**
     * 本地CA
     */
    private String caLocation;
    /**
     * CA客户端
     */
    private HFCAClient caClient;
    /**
     * CA配置
     */
    private Properties caProperties = null;

    /**
     * 联盟管理员用户
     */
    private FabricUser admin;
    /**
     * 联盟单节点管理员用户
     */
    private FabricUser peerAdmin;

    /**
     * 节点集合
     */
    private final Set<Peer> peers;
    /**
     * 用户集合
     */
    private Map<String, User> userMap = new HashMap<>();
    /**
     * 本地节点集合
     */
    private Map<String, String> peerLocations = new HashMap<>();
    /**
     * 本地排序服务集合
     */
    private Map<String, String> ordererLocations = new HashMap<>();
    /**
     * 本地事件集合
     */
    private Map<String, String> eventHubLocations = new HashMap<>();

    FabricOrg(Peers peers, Orderers orderers, FabricStore fabricStore, String cryptoConfigPath) throws IOException {
        this.name = peers.getOrgName();
        this.mspid = peers.getOrgMSPID();
        this.domainName = peers.getOrgDomainName();
        this.peers = new HashSet<>();
        for (int i = 0; i < peers.getPeers().size(); i++) {
            cn.neusoft.btlabs.sdk.bean.Peer peer = peers.getPeers().get(i);
            addPeerLocation(peer.getPeerName(), peer.getPeerLocation());
            addEventHubLocation(peer.getPeerEventHubName(), peer.getPeerEventHubLocation());
            setCALocation(peer.getCaLocation());
        }
        for (int i = 0; i < orderers.getOrderers().size(); i++) {
            Orderer orderer = orderers.getOrderers().get(i);
            addOrdererLocation(orderer.getOrdererName(), orderer.getOrdererLocation());
        }

        // setCAClient(HFCAClient.createNewInstance(peers.getCaLocation(), getCAProperties())); // 设置该组织的CA客户端

        String adminName = "admin";
        setAdmin(fabricStore.getMember(adminName, getDomainName())); // 设置该组织的管理员

        File skTempFile = Paths.get(cryptoConfigPath, "/peerOrganizations/", getDomainName(), String.format("/users/Admin@%s/msp/keystore", getDomainName())).toFile(); // “sk”文件夹
        File skFile = findFileSk(skTempFile); // “sk”文件
        log.debug("skFile = " + skFile.getAbsolutePath());

        File certificateFile = Paths.get(cryptoConfigPath, "/peerOrganizations/", getDomainName(), String.format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", getDomainName(), getDomainName())).toFile(); // “pem”文件
        log.debug("certificateFile = " + certificateFile.getAbsolutePath());

        setPeerAdmin(fabricStore.getMember(peers.getOrgName() + "Admin", peers.getOrgName(), peers.getOrgMSPID(), skFile, certificateFile)); // 设置单节点管理员，可以创建channel，连接peer，并安装chaincode
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取会员id
     *
     * @return 会员id
     */
    public String getMspid() {
        return mspid;
    }

    /**
     * 获取域名名称
     *
     * @return 域名名称
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * 获取本地CA
     *
     * @return 本地CA
     */
    public String getCaLocation() {
        return caLocation;
    }

    /**
     * 获取 CA客户端
     *
     * @return CA客户端
     */
    public HFCAClient getCaClient() {
        return caClient;
    }

    /**
     * 获取CA配置
     *
     * @return CA配置
     */
    public Properties getCaProperties() {
        return caProperties;
    }

    /**
     * 获取联盟管理员用户
     *
     * @return 联盟管理员用户
     */
    public FabricUser getAdmin() {
        return admin;
    }

    /**
     * 获取联盟单节点管理员用户
     *
     * @return 联盟单节点管理员用户
     */
    public FabricUser getPeerAdmin() {
        return peerAdmin;
    }

    /**
     * 获取节点集合
     *
     * @return
     */
    public Set<Peer> getPeers() {
        return peers;
    }

    /**
     * 获取一个不可修改的本地用户key集合
     *
     * @return 用户key集合
     */
    public Set<String> getUserNames() {
        return Collections.unmodifiableSet(userMap.keySet());
    }

    /**
     * 获取一个不可修改的本地用户集合
     *
     * @return 用户集合
     */
    public Map<String, User> getUsers() {
        return userMap;
    }

    /**
     * 从用户集合根据名称获取用户
     *
     * @param name 名称
     * @return 用户
     */
    public User getUserForName(String name) {
        return userMap.get(name);
    }

    /**
     * 获取一个不可修改的本地节点key集合
     *
     * @return 节点key集合
     */
    public Set<String> getPeerNames() {
        return Collections.unmodifiableSet(peerLocations.keySet());
    }

    /**
     * 获取一个不可修改的本地节点集合
     *
     * @return 节点集合
     */
    public Map<String, String> getPeerLocations() {
        return peerLocations;
    }

    /**
     * 获取本地节点
     *
     * @param name 组织key
     * @return 组织
     */
    String getPeerLocationForName(String name) {
        return peerLocations.get(name);
    }

    /**
     * 获取一个不可修改的本地组织key集合
     *
     * @return 组织key集合
     */
    public Set<String> getOrdererNames() {
        return Collections.unmodifiableSet(ordererLocations.keySet());
    }

    /**
     * 获取一个不可修改的本地组织集合
     *
     * @return 组织集合
     */
    public Map<String, String> getOrdererLocations() {
        return ordererLocations;
    }

    /**
     * 获取本地组织
     *
     * @param name 组织key
     * @return 组织
     */
    String getOrdererLocationForName(String name) {
        return ordererLocations.get(name);
    }

    /**
     * 获取一个不可修改的本地事件key集合
     *
     * @return 事件key集合
     */
    public Set<String> getEventHubNames() {
        return Collections.unmodifiableSet(eventHubLocations.keySet());
    }

    /**
     * 获取一个不可修改的本地事件集合
     *
     * @return 事件集合
     */
    public Map<String, String> getEventHubLocations() {
        return eventHubLocations;
    }

    /**
     * 获取本地事件
     *
     * @param name 事件key
     * @return 事件
     */
    String getEventHubLocationForName(String name) {
        return eventHubLocations.get(name);
    }

    /**
     * 设置本地ca
     *
     * @param caLocation 本地ca
     */
    private void setCALocation(String caLocation) {
        this.caLocation = caLocation;
    }

    /**
     * 设置 ca 客户端
     *
     * @param caClient ca 客户端
     */
    public void setCAClient(HFCAClient caClient) {
        this.caClient = caClient;
    }

    /**
     * 设置 ca 配置
     *
     * @param caProperties ca 配置
     */
    public void setCAProperties(Properties caProperties) {
        this.caProperties = caProperties;
    }

    /**
     * 设置联盟管理员用户
     *
     * @param admin 联盟管理员用户
     */
    public void setAdmin(FabricUser admin) {
        this.admin = admin;
    }

    /**
     * 设置联盟单节点管理员用户
     *
     * @param peerAdmin 联盟单节点管理员用户
     */
    private void setPeerAdmin(FabricUser peerAdmin) {
        this.peerAdmin = peerAdmin;
    }

    /**
     * 设置域名名称
     *
     * @param domainName 域名名称
     */
    private void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * 向节点集合中添加节点
     *
     * @param peer 节点
     */
    public void addPeer(Peer peer) {
        peers.add(peer);
    }

    /**
     * 向用户集合中添加用户
     *
     * @param user 用户
     */
    public void addUser(FabricUser user) {
        userMap.put(user.getName(), user);
    }

    /**
     * 添加本地节点
     *
     * @param name     节点key
     * @param location 节点
     */
    private void addPeerLocation(String name, String location) {
        peerLocations.put(name, location);
    }

    /**
     * 添加本地组织
     *
     * @param name     组织key
     * @param location 组织
     */
    private void addOrdererLocation(String name, String location) {
        ordererLocations.put(name, location);
    }

    /**
     * 添加本地事件
     *
     * @param name     事件key
     * @param location 事件
     */
    private void addEventHubLocation(String name, String location) {
        eventHubLocations.put(name, location);
    }

    /**
     * 从指定路径中获取后缀为 _sk 的文件，且该路径下有且仅有该文件
     *
     * @param directory 指定路径
     * @return File
     */
    private File findFileSk(File directory) {
        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));
        if (null == matches) {
            throw new RuntimeException(String.format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }
        if (matches.length != 1) {
            throw new RuntimeException(String.format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }
}

