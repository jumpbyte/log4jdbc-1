package fr.ms.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CollectionsUtil {

    private CollectionsUtil() {
    }

    public final static List convert(final Iterator iterator) {
	final List list = new ArrayList();
	while (iterator.hasNext()) {
	    try {
		final Object e = iterator.next();
		list.add(e);
	    } catch (final Throwable t) {
		t.printStackTrace();
	    }
	}

	return list;
    }
}
