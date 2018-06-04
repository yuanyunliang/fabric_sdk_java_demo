package cn.neusoft.btlabs.energychain.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 4:44
 */
public class FabricIndexUtil {
    private static Properties properties;

    public static void init() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("fabric.properties")).getPath()));
    }

    public static String getConsumerNumber() throws IOException {
        String consumerNumber = properties.getProperty("CONSUMER_NUMBER");
        long temp = Long.parseLong(consumerNumber) + 1;
        setConsumerNumber(Long.toString(temp));
        return consumerNumber;
    }

    public static String getProducerNumber() throws IOException {
        String producerNumber = properties.getProperty("PRODUCER_NUMBER");
        long temp = Long.parseLong(producerNumber) + 1;
        setProducerNumber(Long.toString(temp));
        return properties.getProperty("PRODUCER_NUMBER");
    }

    public static String getTransactionNumber() throws IOException {
        String transactionNumber = properties.getProperty("TRANSACTION_NUMBER");
        long temp = Long.parseLong(transactionNumber) + 1;
        setTransactionNumber(Long.toString(temp));
        return properties.getProperty("TRANSACTION_NUMBER");
    }

    private static void setConsumerNumber(String value) throws IOException {
        properties.setProperty("CONSUMER_NUMBER", value);
        propertiesStore();
    }

    private static void setProducerNumber(String value) throws IOException {
        properties.setProperty("PRODUCER_NUMBER", value);
        propertiesStore();
    }

    private static void setTransactionNumber(String value) throws IOException {
        properties.setProperty("TRANSACTION_NUMBER", value);
        propertiesStore();
    }

    private static void propertiesStore() throws IOException {
        properties.store(new FileOutputStream(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("fabric.properties")).getPath()), "");
    }
}
