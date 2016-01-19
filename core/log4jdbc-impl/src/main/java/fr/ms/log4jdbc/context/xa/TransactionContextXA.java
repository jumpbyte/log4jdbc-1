package fr.ms.log4jdbc.context.xa;

import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;

public class TransactionContextXA extends TransactionContextJDBC {

    private int flags;

    public String getTransactionType() {
	return "XA";
    }

    public int getFlags() {
	return flags;
    }

    public void setFlags(final int flags) {
	this.flags = flags;
    }

    @Override
    public String getTransactionState() {
	return super.getTransactionState() + " - Flags : " + flags;
    }
}
