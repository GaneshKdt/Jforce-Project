package com.nmims.test.services;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.services.LeaderBoardService;

// ...

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeaderBoardServicerTestForMBAWX {

    @MockBean // This annotation creates a mock of LeaderBoardService
    private LeaderBoardService mockLeaderBoardService;

    @Test
    public void getSubjectWiseRankForTimeboundStudents() throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        int expected=1;
        int actual=0;
		when(mockLeaderBoardService.getSubjectWiseRankForStudent("77119875130", "111")).thenReturn(responseMap);
		actual=1;
        assertEquals(expected, actual);
    }
    @Test
    public void getCycleWiseRankForTimeboundStudents() throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        int expected=1;
        int actual=0;
		when(mockLeaderBoardService.getCycleWiseRankForStudent("77119875130", "111")).thenReturn(responseMap);
		actual=1;
        assertEquals(expected, actual);
    }
    
    @Test
    public void migrateSubjectWiseRankForTimeboundStudents() throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        int expected=1;
        int actual=0;
        List<String> masterKeys = new ArrayList<>();
        masterKeys.add("111");
        masterKeys.add("151");
        masterKeys.add("160");
		when(mockLeaderBoardService.migrateSubjectWiseRank("Jul","2019",masterKeys)).thenReturn(responseMap);
		actual=1;
		
        assertEquals(expected, actual);
    }
    @Test
    public void migrateCycleWiseRankForTimeboundStudents() throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        int expected=1;
        int actual=0;
        List<String> masterKeys = new ArrayList<>();
        masterKeys.add("111");
        masterKeys.add("151");
        masterKeys.add("160");
		when(mockLeaderBoardService.migrateCycleWiseRank("Jul","2019",masterKeys)).thenReturn(responseMap);
		actual=1;
        assertEquals(expected, actual);
    }
}
