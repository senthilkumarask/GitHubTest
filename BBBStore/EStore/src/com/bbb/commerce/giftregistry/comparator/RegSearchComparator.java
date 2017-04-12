package com.bbb.commerce.giftregistry.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;

/**This class is used to sort results fetched from registry search 
 * based on various inputs.
 * @author 
 *
 */
public class RegSearchComparator implements Comparator<RegistrySummaryVO> {

	private List<Comparator<RegistrySummaryVO>> listComparators;
	
    @SafeVarargs
	public RegSearchComparator(Comparator<RegistrySummaryVO>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }
	
	public int compare(RegistrySummaryVO emp1, RegistrySummaryVO emp2) {
        for (Comparator<RegistrySummaryVO> comparator : listComparators) {
            int result = comparator.compare(emp1, emp2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
	
}
