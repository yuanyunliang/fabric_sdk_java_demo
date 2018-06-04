package cn.neusoft.btlabs.energychain.utils;


import cn.neusoft.btlabs.energychain.entity.AccountInfo;
import cn.neusoft.btlabs.energychain.entity.AccountResult;
import cn.neusoft.btlabs.energychain.entity.ElectricityInfo;
import cn.neusoft.btlabs.energychain.entity.ElectricityResult;
import cn.neusoft.btlabs.energychain.model.QueryJson;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 6:37
 */
public class FilterUtil {
    public static List<ElectricityInfo> filterElectricitys(List<ElectricityResult> results, QueryJson queryJson) {
        List<ElectricityInfo> filterResults = new ArrayList<>();
        long startTime = Long.parseLong(queryJson.getStartTime());
        long endTime = Long.parseLong(queryJson.getEndTime());
        for (ElectricityResult result : results) {
            ElectricityInfo electricityInfo = result.getValue();
            if (electricityInfo.getMeterId().equalsIgnoreCase(queryJson.getMeterId())) {
                long sTime = Long.parseLong(electricityInfo.getStartTime());
                long eTime = Long.parseLong(electricityInfo.getEndTime());
                if (startTime < sTime && eTime < endTime) {
                    filterResults.add(electricityInfo);
                }
            }
        }
        return filterResults;
    }

    public static List<AccountInfo> filterAccounts(List<AccountResult> results, QueryJson queryJson) {
        List<AccountInfo> filterResults = new ArrayList<>();
        long startTime = Long.parseLong(queryJson.getStartTime());
        long endTime = Long.parseLong(queryJson.getEndTime());
        for (AccountResult result : results) {
            AccountInfo accountInfo = result.getValue();
            if (accountInfo.getMeterId().equalsIgnoreCase(queryJson.getMeterId())) {
                long tTime = Long.parseLong(accountInfo.getTradingTime());
                if (startTime < tTime && tTime < endTime) {
                    filterResults.add(accountInfo);
                }
            }
        }
        return filterResults;
    }
}
