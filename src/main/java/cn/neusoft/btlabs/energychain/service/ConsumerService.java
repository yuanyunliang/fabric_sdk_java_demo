package cn.neusoft.btlabs.energychain.service;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 18:07
 */
public interface ConsumerService {
    // 耗电信息上链服务
    String invoke(String data);

    // 耗电信息查询服务
    String query(String data);
}
