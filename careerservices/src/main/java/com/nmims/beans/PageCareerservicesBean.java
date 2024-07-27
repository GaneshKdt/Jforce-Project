package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageCareerservicesBean<E>  implements Serializable{

    private int pageNumber;
    private int pagesAvailable;
    private int rowCount;
    private int noOfPagesPerView;
    private int currentIndex;
    private int beginIndex;
    private int endIndex;
    private int totalPages;
    
    private List<E> pageItems = new ArrayList<E>();

    
    
    
    
    public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getNoOfPagesPerView() {
		return noOfPagesPerView;
	}

	public void setNoOfPagesPerView(int noOfPagesPerView) {
		this.noOfPagesPerView = noOfPagesPerView;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPagesAvailable(int pagesAvailable) {
        this.pagesAvailable = pagesAvailable;
    }

    public void setPageItems(List<E> pageItems) {
        this.pageItems = pageItems;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPagesAvailable() {
        return pagesAvailable;
    }

    public List<E> getPageItems() {
        return pageItems;
    }
}
