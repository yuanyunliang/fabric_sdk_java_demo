package cn.neusoft.btlabs.energychain.entity;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 7:13
 */
public class AccountResult {

    private String key;
    private AccountInfo value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AccountInfo getValue() {
        return value;
    }

    public void setValue(AccountInfo value) {
        this.value = value;
    }
}