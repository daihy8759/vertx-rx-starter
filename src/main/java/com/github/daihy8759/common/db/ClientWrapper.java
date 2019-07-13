package com.github.daihy8759.common.db;

import io.vertx.reactivex.sqlclient.Pool;

public class ClientWrapper extends Pool {

    private final ClientHolder holder;

    public ClientWrapper(ClientHolder holder) {
        super(holder.client().getDelegate());
        this.holder = holder;
    }

    @Override
    public void close() {
        holder.close();
    }
}
