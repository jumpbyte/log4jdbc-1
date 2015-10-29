/*
 * This file is part of Log4Jdbc.
 *
 * Log4Jdbc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Log4Jdbc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Log4Jdbc.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package fr.ms.lang;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ClassUtilsTest {

    @Test
    public void classPresentTest() {
	boolean classPresent = ClassUtils.classPresent(String.class.getName());

	Assert.assertTrue(classPresent);

	classPresent = ClassUtils.classPresent("java.util.FakeClass");

	Assert.assertFalse(classPresent);
    }

    @Test
    public void findInterfacesTest() {
	final ArrayList<Class> checkInterfaces = new ArrayList<Class>();
	checkInterfaces.add(java.io.Serializable.class);
	checkInterfaces.add(java.util.Collection.class);
	checkInterfaces.add(java.lang.Iterable.class);
	checkInterfaces.add(java.util.List.class);
	checkInterfaces.add(java.lang.Cloneable.class);
	checkInterfaces.add(java.util.RandomAccess.class);

	Class[] interfaces = ClassUtils.findInterfaces(ArrayList.class);

	for (final Class clazz : interfaces) {
	    Assert.assertTrue(checkInterfaces.contains(clazz));
	}

	interfaces = ClassUtils.findInterfaces(Object.class);

	Assert.assertEquals(0, interfaces.length);
    }
}
