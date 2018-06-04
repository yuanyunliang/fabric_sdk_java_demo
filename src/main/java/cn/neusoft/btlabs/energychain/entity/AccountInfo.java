package cn.neusoft.btlabs.energychain.entity;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 16:10
 */
public class AccountInfo {
    private String companyId;
    private String meterId;
    private String electricity;
    private String account;
    private String tradingTime;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTradingTime() {
        return tradingTime;
    }

    public void setTradingTime(String tradingTime) {
        this.tradingTime = tradingTime;
    }
}
