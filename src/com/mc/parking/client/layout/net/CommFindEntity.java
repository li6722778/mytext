
package com.mc.parking.client.layout.net;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 *
 * @author woderchen
 */
public class CommFindEntity<T> {

	@Expose
    private int pageCount;
	@Expose
    private int rowCount;
	@Expose
    private List<T> result;

   

    public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}



    public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
     * @return the result
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(List<T> result) {
        this.result = result;
    }
    
    
}
