package cn.neusoft.btlabs.energychain.entity;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 7:13
 */
public class ElectricityResult {

    private String key;
    private ElectricityInfo value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ElectricityInfo getValue() {
        return value;
    }

    public void setValue(ElectricityInfo value) {
        this.value = value;
    }
}