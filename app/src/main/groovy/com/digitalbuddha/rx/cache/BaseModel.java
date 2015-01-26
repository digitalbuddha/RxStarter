package com.digitalbuddha.rx.cache;


import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by swapnilp on 10/14/14.
 */
public class BaseModel<R, T> implements Serializable {
    private static final long serialVersionUID = -906708533142794132L;
    public T payload;
    public HashMap<Integer, BaseModel<R, T>> partialPayload = new HashMap<>();
    public Queue<T> aggregateHelperQ = new LinkedList<>();
    public HashMap<Object, Object> dupeHelperMap = new HashMap<>();
    public long lastUpdated;
    public ResultCode resultCode;

    public BaseModel(T payload, long lastUpdated, ResultCode resultCode) {
        this.payload = payload;
        this.lastUpdated = lastUpdated;
        this.resultCode = resultCode;
    }

    public BaseModel() {}

    public boolean isValid() {

        return (payload != null && lastUpdated > 0);
    }

}
