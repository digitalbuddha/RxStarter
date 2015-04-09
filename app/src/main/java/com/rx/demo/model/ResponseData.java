
package com.rx.demo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResponseData {

    private ArrayList<Result> results = new ArrayList<Result>();
    private Cursor cursor;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The results
     */
    public ArrayList<Result> getResults() {
        return results;
    }

    /**
     *
     * @param results
     *     The results
     */
    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    /**
     * 
     * @return
     *     The cursor
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * 
     * @param cursor
     *     The cursor
     */
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
