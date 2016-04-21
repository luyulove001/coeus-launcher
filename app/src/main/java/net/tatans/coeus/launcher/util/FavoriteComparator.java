package net.tatans.coeus.launcher.util;

import java.util.Comparator;

/**
 * 收藏夹比较器
 */
public class FavoriteComparator implements Comparator<Person> {
    @Override
    public int compare(Person lhs, Person rhs) {
        if (lhs.getPinYinName().equals("★") && rhs.getPinYinName().equals("★")) {
            return StringHelper.getPinYinHeadChar(lhs.getName()).compareTo(StringHelper.getPinYinHeadChar(rhs.getName()));
        }
        return 0;
    }
}
