package cn.neusoft.btlabs.energychain.utils;

import cn.neusoft.btlabs.energychain.model.AccountJson;
import cn.neusoft.btlabs.energychain.model.ElectricityJson;
import cn.neusoft.btlabs.energychain.model.QueryJson;
import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 2:20
 */
public class CheckUtil {
    public static String checkQuery(String jsonStr) {
        if (CheckUtil.isNullOrEmpty(jsonStr)) {
            return "请求参数不正确！";
        } else {
            QueryJson queryJson = JSON.parseObject(jsonStr, QueryJson.class);
            if (null == queryJson) {
                return "请求参数转化为Bean失败！";
            } else if (CheckUtil.isNullOrEmpty(queryJson.getMeterId()) || !queryJson.getMeterId().matches("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$")) {
                return "MeterId参数格式错误！";
            } else if (CheckUtil.isNullOrEmpty(queryJson.getStartTime()) || !queryJson.getStartTime().matches("^\\d{10}$")) {
                return "StartTime参数格式错误！";
            } else if (CheckUtil.isNullOrEmpty(queryJson.getEndTime()) || !queryJson.getEndTime().matches("^\\d{10}$")) {
                return "EndTime参数格式错误！";
            } else if (!isALessThanB(queryJson.getStartTime(), queryJson.getEndTime())) {
                return "起始时间区间错误！";
            }
        }
        return "success";
    }

    public static String checkAccount(String jsonStr) {
        if (CheckUtil.isNullOrEmpty(jsonStr)) {
            return "请求参数不正确！";
        } else {
            AccountJson accountJson = JSON.parseObject(jsonStr, AccountJson.class);
            if (null == accountJson) {
                return "请求参数转化为Bean失败！";
            } else if (CheckUtil.isNullOrEmpty(accountJson.getCompanyId()) || !accountJson.getCompanyId().matches("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$")) {
                return "CompanyId参数格式错误！";
            } else if (CheckUtil.isNullOrEmpty(accountJson.getMeterId()) || !accountJson.getMeterId().matches("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$")) {
                return "MeterId参数格式错误！";
            } else if (CheckUtil.isNullOrEmpty(accountJson.getElectricity()) || !accountJson.getElectricity().matches("^\\d+(\\.\\d{1,2})?$")) {
                return "Electricity参数格式错误！";
            } else if (CheckUtil.isNullOrEmpty(accountJson.getAccount()) || !accountJson.getAccount().matches("^\\d+(\\.\\d{1,2})?$")) {
                return "Account参数格式错误！";
            } else if (CheckUtil.isNullOrEmpty(accountJson.getTradingTime()) || !accountJson.getTradingTime().matches("^\\d{10}$")) {
                return "TradingTime参数格式错误！";
            } else if (CheckUtil.isNullOrEmpty(accountJson.getCheckSum()) || !accountJson.getCheckSum().matches("[0-9a-z]{64}$")) {
                return "CheckSum参数格式错误！";
            } else if (!CheckUtil.checkSHA256(accountJson)) {
                return "校验和不匹配！";
            }
        }
        return "success";
    }

    public static String checkElectricity(String jsonStr) {
        if (CheckUtil.isNullOrEmpty(jsonStr)) {
            return "请求参数不正确！";
        } else {
            ElectricityJson electricityJson = JSON.parseObject(jsonStr, ElectricityJson.class);
            if (null == electricityJson) {
                return "请求参数转化为Bean失败！";
            } else if (isNullOrEmpty(electricityJson.getMeterId()) || !electricityJson.getMeterId().matches("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$")) {
                return "MeterId参数格式错误！";
            } else if (isNullOrEmpty(electricityJson.getElectricity()) || !electricityJson.getElectricity().matches("^\\d+(\\.\\d{1,2})?$")) {
                return "Electricity参数格式错误！";
            } else if (isNullOrEmpty(electricityJson.getStartTime()) || !electricityJson.getStartTime().matches("^\\d{10}$")) {
                return "StartTime参数格式错误！";
            } else if (isNullOrEmpty(electricityJson.getEndTime()) || !electricityJson.getEndTime().matches("^\\d{10}$")) {
                return "EndTime参数格式错误！";
            } else if (!isALessThanB(electricityJson.getStartTime(), electricityJson.getEndTime())) {
                return "起始时间区间错误！";
            } else if (isNullOrEmpty(electricityJson.getCheckSum()) || !electricityJson.getCheckSum().matches("[0-9a-z]{64}$")) {
                return "CheckSum参数格式错误！";
            } else if (!checkSHA256(electricityJson)) {
                return "校验和不匹配！";
            }
        }
        return "success";
    }

    private static boolean isNullOrEmpty(String str) {
        return null == str || "".equals(str);
    }

    private static boolean isALessThanB(String A, String B) {
        double a = Double.parseDouble(A);
        double b = Double.parseDouble(B);
        return a < b;
    }

    private static boolean checkSHA256(AccountJson accountJson) {
        if (null == accountJson) {
            return false;
        }
        String checkSum = SHA256(accountJson.getCompanyId() + accountJson.getMeterId() + "!@#$" + accountJson.getElectricity() + accountJson.getAccount() + accountJson.getTradingTime());
        return checkSum.equalsIgnoreCase(accountJson.getCheckSum());
    }

    private static boolean checkSHA256(ElectricityJson electricityJson) {
        if (null == electricityJson) {
            return false;
        }
        String checkSum = SHA256(electricityJson.getMeterId() + "!@#$" + electricityJson.getElectricity() + electricityJson.getStartTime() + electricityJson.getEndTime());
        return checkSum.equalsIgnoreCase(electricityJson.getCheckSum());
    }

    private static String SHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
