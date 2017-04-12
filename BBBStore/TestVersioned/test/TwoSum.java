import java.util.HashMap;
import java.util.Map;

public class TwoSum {
	public static int[] findTwoSum(int[] list, int sum) {
		int arr[] = {1,2};
		Map<Integer, Integer> pairs = new HashMap<Integer, Integer>();
		for(int i=0; i<list.length; i++){
			if(pairs.containsKey(sum-list[i])){
			    arr[0] = i;
			    arr[1] = pairs.get(sum-list[i]);
			} else {
				pairs.put(list[i], i);
			}
		}
		return arr;
	}

	public static void main(String[] args) {
		int[] indices = findTwoSum(new int[] { 1, 3, 5, 7, 9 }, 12);
		System.out.println(indices[0] + " " + indices[1]);
	}
}