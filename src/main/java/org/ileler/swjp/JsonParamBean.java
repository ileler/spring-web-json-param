package org.ileler.swjp;

/**
 * Author: kerwin612@qq.com
 */
public class JsonParamBean {

    private String paramName;

    private Object paramDefaultValue;

    public JsonParamBean() {
    }

    public JsonParamBean(String paramName, Object paramDefaultValue) {
        this.paramName = paramName;
        this.paramDefaultValue = paramDefaultValue;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Object getParamDefaultValue() {
        return paramDefaultValue;
    }

    public void setParamDefaultValue(Object paramDefaultValue) {
        this.paramDefaultValue = paramDefaultValue;
    }

}
