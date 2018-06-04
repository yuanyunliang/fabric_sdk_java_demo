package cn.neusoft.btlabs.energychain.service.impl;

import cn.neusoft.btlabs.energychain.entity.ElectricityInfo;
import cn.neusoft.btlabs.energychain.entity.ElectricityResult;
import cn.neusoft.btlabs.energychain.model.ElectricityJson;
import cn.neusoft.btlabs.energychain.model.QueryJson;
import cn.neusoft.btlabs.energychain.model.Result;
import cn.neusoft.btlabs.energychain.service.ProducerService;
import cn.neusoft.btlabs.energychain.utils.CheckUtil;
import cn.neusoft.btlabs.energychain.utils.FabricIndexUtil;
import cn.neusoft.btlabs.energychain.utils.FabricManagerUtil;
import cn.neusoft.btlabs.sdk.ChaincodeManager;
import com.alibaba.fastjson.JSON;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static cn.neusoft.btlabs.energychain.utils.FilterUtil.filterElectricitys;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 18:11
 */
@Service(value = "producerService")
public class ProducerServiceImpl implements ProducerService {
    @Override
    public String invoke(String data) {
        // 结果对象
        Result result;

        // 判断数据有效性
        String checkResult = CheckUtil.checkElectricity(data);
        if (!"success".equalsIgnoreCase(checkResult)) {
            result = new Result("1", checkResult);
        } else {
            try {
                ElectricityJson electricityJson = JSON.parseObject(data, ElectricityJson.class);
                ChaincodeManager manager = FabricManagerUtil.getProducerManager();
                if (null == manager) {
                    result = new Result("1", "Producer智能合约管理器初始化错误！");
                } else {
                    Map<String, String> producerResult = manager.invoke("invoke", new String[]{FabricIndexUtil.getProducerNumber(), electricityJson.getMeterId(), electricityJson.getElectricity(), electricityJson.getStartTime(), electricityJson.getEndTime()});
                    String reCode = producerResult.get("code");
                    if ("success".equals(reCode)) {
                        result = new Result("0", "发电信息上链成功！");
                    } else {
                        result = new Result("1", "发电信息上链失败！错误信息：" + producerResult.get("data"));
                    }
                }
            } catch (InvalidArgumentException | ProposalException e) {
                result = new Result("1", "发电合约调用异常！");
            } catch (Exception e) {
                result = new Result("1", "发生未知错误！");
            }
        }
        return JSON.toJSONString(result);
    }

    @Override
    public String query(String data) {
        // 结果对象
        Result result;

        // 判断数据有效性
        String checkResult = CheckUtil.checkQuery(data);
        if (!"success".equalsIgnoreCase(checkResult)) {
            result = new Result("1", checkResult);
        } else {
            try {
                QueryJson queryJson = JSON.parseObject(data, QueryJson.class);
                ChaincodeManager manager = FabricManagerUtil.getProducerManager();
                if (null == manager) {
                    result = new Result("1", "Producer智能合约管理器初始化错误！");
                } else {
                    Map<String, String> producerResult = manager.invoke("queryAll", null);
                    if ("success".equals(producerResult.get("code"))) {
                        // 所有查询结果
                        List<ElectricityResult> electricityResults = JSON.parseArray(producerResult.get("data"), ElectricityResult.class);
                        // 返回过滤结果
                        List<ElectricityInfo> electricityInfos = filterElectricitys(electricityResults, queryJson);
                        result = new Result<>("0", "发电信息查询成功！", electricityInfos);
                    } else {
                        result = new Result("1", "发电信息查询失败！错误信息：" + producerResult.get("data"));
                    }
                }
            } catch (InvalidArgumentException | ProposalException e) {
                result = new Result("1", "发电合约调用异常！");
            } catch (Exception e) {
                result = new Result("1", "发生未知错误！");
            }
        }
        return JSON.toJSONString(result);
    }
}
