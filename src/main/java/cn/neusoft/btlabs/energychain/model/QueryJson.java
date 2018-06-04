package cn.neusoft.btlabs.energychain.model;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 6:21
 */
public class QueryJson {
    private String meterId;
    private String startTime;
    private String endTime;

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
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
