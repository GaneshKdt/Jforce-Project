package com.nmims.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.nmims.beans.PageCareerservicesBean;


public class PaginationHelper<E> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public PageCareerservicesBean<E> fetchPage(
            final JdbcTemplate jt,
            final String sqlCountRows,
            final String sqlFetchRows,
            final Object args[],
            final int pageNo,
            final int pageSize,
            final BeanPropertyRowMapper rowMapper) {

        // determine how many rows are available
        final int rowCount = (int) jt.queryForObject(sqlCountRows, args,Integer.class);

        // calculate the number of pages
        int pageCount = rowCount / pageSize;
        if (rowCount > pageSize * pageCount) {
            pageCount++;
        }
        
        final int noOfPagesPerView = 10;
        // create the page object
        final PageCareerservicesBean<E> page = new PageCareerservicesBean<E>();
        page.setPageNumber(pageNo);
        page.setCurrentIndex(pageNo);
        page.setPagesAvailable(pageCount);
        page.setTotalPages(pageCount);
        page.setRowCount(rowCount);
        page.setNoOfPagesPerView(noOfPagesPerView);
        
		int beginIndex = (pageNo / noOfPagesPerView) * noOfPagesPerView + 1;
		int endIndex = ((pageNo / noOfPagesPerView) + 1 ) * noOfPagesPerView;
		
		if(endIndex > pageCount){
			endIndex = pageCount;
		}
		page.setBeginIndex(beginIndex);
		page.setEndIndex(endIndex);
        
        // fetch a single page of results
        final int startRow = (pageNo - 1) * pageSize;
        jt.query(
                sqlFetchRows,
                args,
                new ResultSetExtractor() {
					public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                        final List pageItems = page.getPageItems();
                        int currentRow = 0;
                        while (rs.next() && currentRow < startRow + pageSize) {
                            if (currentRow >= startRow) {
                                pageItems.add(rowMapper.mapRow(rs, currentRow));
                            }
                            currentRow++;
                        }
                        return page;
                    }
                });
        return page;
    }

}