package com.myappcompany.rajan.zeta.model;

import java.util.Comparator;

public class TransactionComparator implements Comparator<TransactionItem> {
    @Override
    public int compare(TransactionItem t1, TransactionItem t2) {
        return (t2.getTimestamp().compareTo(t1.getTimestamp()));
    }
}
