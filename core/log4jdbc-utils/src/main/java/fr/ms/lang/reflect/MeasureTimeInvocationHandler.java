package fr.ms.lang.reflect;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class MeasureTimeInvocationHandler implements InvocationHandler {

    private final static MeasureTime mbean = new MeasureTime();

    private final InvocationHandler invocationHandler;

    static {
	final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

	ObjectName name = null;
	try {
	    name = new ObjectName("log4jdbc.jmx:type=Time");

	    mbs.registerMBean(mbean, name);
	} catch (final Exception e) {
	    // TODO
	}
    }

    public MeasureTimeInvocationHandler(final InvocationHandler invocationHandler) {
	this.invocationHandler = invocationHandler;
    }

    public final Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
	final long start = System.currentTimeMillis();

	final TimeInvocation invokeTime = (TimeInvocation) invocationHandler.invoke(proxy, method, args);

	final long end = System.currentTimeMillis();

	final long time = (end - start) - invokeTime.getExecTime();

	mbean.setTime(time);

	return invokeTime.getInvoke();
    }
}
