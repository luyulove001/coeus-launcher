package net.tatans.coeus.launcher.util;

import java.util.Comparator;

/**
 * Created by SiLiPing on 2015/12/23.
 * 排序方案
 */
public class PinyinComparator implements Comparator<Person> {

	public int compare(Person o1, Person o2) {
		if (o1.getPinYinName().equals("★")
				|| o2.getPinYinName().equals("#")) {
			return -1;
		} else if (o1.getPinYinName().equals("#")
				|| o2.getPinYinName().equals("★")) {
			return 1;
		} else {
			return o1.getPinYinName().compareTo(o2.getPinYinName());
		}
	}
}
