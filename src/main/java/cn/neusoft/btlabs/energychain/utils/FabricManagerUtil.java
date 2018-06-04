package cn.neusoft.btlabs.energychain.utils;

import cn.neusoft.btlabs.sdk.ChaincodeManager;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 4:17
 */
public class FabricManagerUtil {
    private static Logger log = Logger.getLogger(FabricManagerUtil.class);

    private static FabricManager consumerManager; // 耗电信息
    private static FabricManager producerManager; // 发电信息
    private static FabricManager transactionManager; // 结算信息

    public static void init() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, CryptoException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, IllegalAccessException, EnrollmentException, ClassNotFoundException, TransactionException {
        consumerManager = new FabricManager("elechannel", "ele_consumer").obtain();
        log.debug("init consumerManager");
        producerManager = new FabricManager("elechannel", "ele_producer").obtain();
        log.debug("init producerManager");
        transactionManager = new FabricManager("elechannel", "ele_transaction").obtain();
        log.debug("init transactionManager");
    }

    public static ChaincodeManager getConsumerManager() {
        if (null != consumerManager) {
            return consumerManager.getManager();
        }
        return null;
    }

    public static ChaincodeManager getProducerManager() {
        if (null != producerManager) {
            return producerManager.getManager();
        }
        return null;
    }

    public static ChaincodeManager getTransactionManager() {
        if (null != transactionManager) {
            return transactionManager.getManager();
        }
        return null;
    }
}
