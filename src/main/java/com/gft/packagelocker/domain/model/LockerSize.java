package com.gft.packagelocker.domain.model;

public enum LockerSize {
    S, M, L;

    /**
     * Returns true if this locker size can physically fit a package of the given size.
     * S fits S; M fits S and M; L fits S, M and L.
     */
    public boolean canFit(LockerSize packageSize) {
        return this.ordinal() >= packageSize.ordinal();
    }
}
