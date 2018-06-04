package cn.neusoft.btlabs.sdk;

import io.netty.util.internal.StringUtil;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.*;
import java.util.Set;

/**
 * 联盟用户对象
 *
 * @Author XieYongJie
 * @Date 2018/5/24 17:18
 */
public class FabricUser implements User, Serializable {

    private static final long serialVersionUID = 5695080465408336815L;

    /**
     * 名称
     */
    private String name;
    /**
     * 规则
     */
    private Set<String> roles;
    /**
     * 账户
     */
    private String account;
    /**
     * 从属联盟
     */
    private String affiliation;
    /**
     * 组织
     */
    private String organization;
    /**
     * 注册操作的密码
     */
    private String enrollmentSecret;
    /**
     * 会员id
     */
    private String mspId;
    /**
     * 注册登记操作，需要在测试env中访问
     */
    private Enrollment enrollment = null;

    /**
     * 存储配置对象
     */
    private String keyValStoreName;
    private transient FabricStore keyValStore;

    FabricUser(String name, String organization, FabricStore keyValStore) {
        this.name = name;
        this.keyValStore = keyValStore;
        this.organization = organization;
        this.keyValStoreName = toKeyValStoreName(name, organization);

        String memberStr = this.keyValStore.getValue(keyValStoreName);
        if (null == memberStr) {
            restoreState();
        } else {
            saveState();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    public String getEnrollmentSecret() {
        return enrollmentSecret;
    }

    /**
     * 设置规则信息并将用户状态更新至存储配置对象
     *
     * @param roles 规则
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
        saveState();
    }

    /**
     * 设置账户信息并将用户状态更新至存储配置对象
     *
     * @param account 账户
     */
    public void setAccount(String account) {
        this.account = account;
        saveState();
    }

    /**
     * 设置从属联盟信息并将用户状态更新至存储配置对象
     *
     * @param affiliation 从属联盟
     */
    void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
        saveState();
    }

    /**
     * 设置会员id信息并将用户状态更新至存储配置对象
     *
     * @param mspID 会员id
     */
    void setMspId(String mspID) {
        this.mspId = mspID;
        saveState();
    }

    /**
     * 设置注册登记操作信息并将用户状态更新至存储配置对象
     *
     * @param enrollment 注册登记操作
     */
    void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
        saveState();
    }

    /**
     * 设置注册操作的密钥信息并将用户状态更新至存储配置对象
     *
     * @param enrollmentSecret 注册操作的密钥
     */
    void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
        saveState();
    }

    /**
     * 确定这个名称是否已注册
     *
     * @return 与否
     */
    boolean isRegistered() {
        return !StringUtil.isNullOrEmpty(enrollmentSecret);
    }

    /**
     * 保存用户状态至存储配置对象
     */
    private void saveState() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            keyValStore.setValue(keyValStoreName, Hex.toHexString(byteArrayOutputStream.toByteArray()));
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 尝试从键值存储中恢复该用户的状态
     */
    private void restoreState() {
        String memberStr = keyValStore.getValue(keyValStoreName);
        if (null != memberStr) {
            // 用户在键值存储中被找到，因此恢复状态
            byte[] serialized = Hex.decode(memberStr);
            ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
            try {
                ObjectInputStream ois = new ObjectInputStream(bis);
                FabricUser state = (FabricUser) ois.readObject();
                if (state != null) {
                    this.name = state.name;
                    this.roles = state.roles;
                    this.account = state.account;
                    this.affiliation = state.affiliation;
                    this.organization = state.organization;
                    this.enrollmentSecret = state.enrollmentSecret;
                    this.enrollment = state.enrollment;
                    this.mspId = state.mspId;
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("Could not restore state of member %s", this.name), e);
            }
        }
    }

    static String toKeyValStoreName(String name, String organization) {
        // System.out.println("toKeyValStoreName = " + "user." + name + organization);
        return "user." + name + organization;
    }
}
