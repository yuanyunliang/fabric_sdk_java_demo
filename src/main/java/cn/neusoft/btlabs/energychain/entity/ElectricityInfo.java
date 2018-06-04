package cn.neusoft.btlabs.energychain.entity;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 1:21
 */
public class ElectricityInfo {
    private String meterId;
    private String electricity;
    private String startTime;
    private String endTime;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
