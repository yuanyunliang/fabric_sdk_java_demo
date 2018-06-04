package cn.neusoft.btlabs.energychain.common;

import cn.neusoft.btlabs.energychain.utils.FabricIndexUtil;
import cn.neusoft.btlabs.energychain.utils.FabricManagerUtil;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.jboss.logging.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 20:25
 */
@Component
public class InitConfig implements ApplicationListener<ApplicationReadyEvent> {
    private static Logger log = Logger.getLogger(InitConfig.class);
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            FabricManagerUtil.init();
            log.debug("Init FabricManagerUtil - Success");
        } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | InvalidArgumentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException | EnrollmentException | IllegalAccessException | ClassNotFoundException | TransactionException | CryptoException e) {
            log.debug("Init FabricManagerUtil - Error");
        }
        try {
            FabricIndexUtil.init();
            log.debug("Init FabricIndexUtil - Success");
        } catch (IOException e) {
            log.debug("Init FabricIndexUtil - Error");
        }
    }
}
