package fr.ms.lang.reflect;

import java.util.concurrent.atomic.AtomicLong;

public class MeasureTime implements MeasureTimeMBean {

    private final AtomicLong total = new AtomicLong();
    private long time;

    public long getTime() {
	return time;
    }

    public long getTotal() {
	return total.get();
    }

    public void setTime(final long time) {
	this.time = time;
	total.addAndGet(time);
    }
}
