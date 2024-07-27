package com.nmims.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nmims.enums.ProfileDetailEnum;
import com.nmims.stratergies.ChangeDetailsSRStrategyInterface;

/**
 * A Factory class through which the Change Details Service Request Strategy Implementations can be instantiated.
 * All the implementations of the ChangeDetailsSRStrategyInterface are stored in a Set,
 * this set is then iterated and each implementation is stored as a value in a HashMap 
 * with it's corresponding ProfileDetailEnum as the key.
 * 
 * A particular implementation can then be retrieved with the help of the ProfileDetailEnum key.
 * @author Raynal Dcunha
 */
@Component
public class ChangeDetailsSRStrategyFactory {
	private static Map<ProfileDetailEnum, ChangeDetailsSRStrategyInterface> changeDetailsSRStrategyMap;
	
	@Autowired
	public ChangeDetailsSRStrategyFactory(Set<ChangeDetailsSRStrategyInterface> changeDetailsSRStrategySet) {
		createChangeDetailsSRStrategyMap(changeDetailsSRStrategySet);
	}
	
	/**
	 * Returns the Strategy implementation based upon the ProfileDetailEnum element passed.
	 * @param detailType - ProfileDetailEnum element
	 * @return strategy interface implementation
	 */
	public ChangeDetailsSRStrategyInterface findStrategy(ProfileDetailEnum detailType) {
		return changeDetailsSRStrategyMap.get(detailType);
	}
	
	/**
	 * creates a HashMap containing the Strategy Implementations of the ChangeDetailsSRStrategyInterface 
	 * with ProfileDetailEnum element as their respective keys.
	 * @param changeDetailsSRStrategySet - Set containing the Strategy Implementations
	 */
	private void createChangeDetailsSRStrategyMap(Set<ChangeDetailsSRStrategyInterface> changeDetailsSRStrategySet) {
		changeDetailsSRStrategyMap = new HashMap<>();
		changeDetailsSRStrategySet.forEach(srStrategy -> changeDetailsSRStrategyMap.put(srStrategy.getDetailType(), srStrategy));
	}
}
