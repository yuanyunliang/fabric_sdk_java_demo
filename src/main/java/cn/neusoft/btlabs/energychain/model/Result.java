package cn.neusoft.btlabs.energychain.model;

import java.util.List;

/**
 * @Author XieYongJie
 * @Date 2018/5/27 3:46
 */
public class Result<T> {
    private String code;
    private String message;
    private List<T> data;

    public Result(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(String code, String message, List<T> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
