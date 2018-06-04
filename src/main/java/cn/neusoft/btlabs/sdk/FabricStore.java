package cn.neusoft.btlabs.sdk;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;

import java.io.*;
import java.security.PrivateKey;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 联盟存储配置对象
 *
 * @Author XieYongJie
 * @Date 2018/5/24 18:43
 */
class FabricStore {
    /**
     * 配置文件存储路径
     */
    private String file;
    /**
     * 用户信息集合
     */
    private final Map<String, FabricUser> members = new HashMap<>();

    FabricStore(File file) {
        this.file = file.getAbsolutePath();
    }

    /**
     * 设置与名称相关的值
     *
     * @param name  名称
     * @param value 相关值
     */
    void setValue(String name, String value) {
        Properties properties = loadProperties();
        try (OutputStream output = new FileOutputStream(file)) {
            properties.setProperty(name, value);
            properties.store(output, ""); // 如果comments不为空，保存后的属性文件第一行会是#comments,表示注释信息；如果为空则没有注释信息
        } catch (IOException e) {
            System.out.println(String.format("Could not save the keyvalue store, reason:%s", e.getMessage()));
        }
    }

    /**
     * 获取与名称相关的值
     *
     * @param name 名称
     * @return 相关值
     */
    String getValue(String name) {
        Properties properties = loadProperties();
        return properties.getProperty(name);
    }

    /**
     * 加载配置文件
     *
     * @return 配置文件对象
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Could not find the file \"%s\"", file));
        } catch (IOException e) {
            System.out.println(String.format("Could not load keyvalue store from file \"%s\", reason:%s", file, e.getMessage()));
        }
        return properties;
    }

    /**
     * 用给定的名称获取用户
     *
     * @param name 名称
     * @param org  组织
     * @return 用户
     */
    FabricUser getMember(String name, String org) {
        // 尝试从缓存中获取User状态
        FabricUser fabricUser = members.get(FabricUser.toKeyValStoreName(name, org));
        if (null != fabricUser) {
            return fabricUser;
        }
        // 创建User，并尝试从键值存储中恢复它的状态
        fabricUser = new FabricUser(name, org, this);
        return fabricUser;
    }

    /**
     * 用给定的名称获取用户
     *
     * @param name            名称
     * @param org             组织
     * @param mspId           会员id
     * @param privateKeyFile  私钥文件
     * @param certificateFile 证书文件
     * @return user 用户
     */
    FabricUser getMember(String name, String org, String mspId, File privateKeyFile, File certificateFile) throws IOException {
        try {
            // 尝试从缓存中获取User状态
            FabricUser fabricUser = members.get(FabricUser.toKeyValStoreName(name, org));
            if (null != fabricUser) {
                System.out.println("尝试从缓存中获取User状态 User = " + fabricUser);
                return fabricUser;
            }
            // 创建User，并尝试从键值存储中恢复它的状态
            fabricUser = new FabricUser(name, org, this);
            fabricUser.setMspId(mspId);

            PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
            String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
            fabricUser.setEnrollment(new StoreEnrollment(privateKey, certificate));
            return fabricUser;
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 通过字节数组信息获取私钥
     *
     * @param data 字节数组
     * @return 私钥
     */
    private PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException {
        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        return new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
    }

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义注册登记操作类
     */
    static final class StoreEnrollment implements Enrollment, Serializable {

        private static final long serialVersionUID = 6965341351799577442L;

        /**
         * 私钥
         */
        private final PrivateKey privateKey;
        /**
         * 授权证书
         */
        private final String certificate;

        StoreEnrollment(PrivateKey privateKey, String certificate) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }

        @Override
        public PrivateKey getKey() {
            return privateKey;
        }

        @Override
        public String getCert() {
            return certificate;
        }
    }
}
