package fr.ms.log4jdbc.context.xa;

import javax.transaction.xa.Xid;

@Deprecated
public class XAResourceContextXA {

    public void start(final Xid xid, final int flags) {

    }

    public void end(final Xid xid, final int flags) {

    }

    public void prepare(final Xid xid, final int response) {

    }

    public void rollback(final Xid xid) {
    }

    public boolean commit(final Xid xid, final boolean onePhase) {
	return true;
    }

    public boolean isTransactionEnabled() {
	return true;
    }
}
