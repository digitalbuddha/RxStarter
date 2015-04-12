
package com.rx.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Cursor {

    private String resultCount;
    private List<Page> pages = new ArrayList<Page>();
    private String estimatedResultCount;
    private Integer currentPageIndex;
    private String moreResultsUrl;
    private String searchResultTime;

    /**
     * 
     * @return
     *     The resultCount
     */
    public String getResultCount() {
        return resultCount;
    }

    /**
     * 
     * @param resultCount
     *     The resultCount
     */
    public void setResultCount(String resultCount) {
        this.resultCount = resultCount;
    }

    /**
     * 
     * @return
     *     The pages
     */
    public List<Page> getPages() {
        return pages;
    }

    /**
     * 
     * @param pages
     *     The pages
     */
    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    /**
     * 
     * @return
     *     The estimatedResultCount
     */
    public String getEstimatedResultCount() {
        return estimatedResultCount;
    }

    /**
     * 
     * @param estimatedResultCount
     *     The estimatedResultCount
     */
    public void setEstimatedResultCount(String estimatedResultCount) {
        this.estimatedResultCount = estimatedResultCount;
    }

    /**
     * 
     * @return
     *     The currentPageIndex
     */
    public Integer getCurrentPageIndex() {
        return currentPageIndex;
    }

    /**
     * 
     * @param currentPageIndex
     *     The currentPageIndex
     */
    public void setCurrentPageIndex(Integer currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    /**
     * 
     * @return
     *     The moreResultsUrl
     */
    public String getMoreResultsUrl() {
        return moreResultsUrl;
    }

    /**
     * 
     * @param moreResultsUrl
     *     The moreResultsUrl
     */
    public void setMoreResultsUrl(String moreResultsUrl) {
        this.moreResultsUrl = moreResultsUrl;
    }

    /**
     * 
     * @return
     *     The searchResultTime
     */
    public String getSearchResultTime() {
        return searchResultTime;
    }

    /**
     * 
     * @param searchResultTime
     *     The searchResultTime
     */
    public void setSearchResultTime(String searchResultTime) {
        this.searchResultTime = searchResultTime;
    }
}
