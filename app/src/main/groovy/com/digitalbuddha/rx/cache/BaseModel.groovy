package com.digitalbuddha.rx.cache
/**
 * Created by swapnilp on 10/14/14.
 */
public class BaseModel<R, T> implements Serializable {
    public T payload;
    public HashMap<Integer, BaseModel<R, T>> partialPayload = new HashMap<>();
    public long lastUpdated;
    public ResultCode resultCode;

}
