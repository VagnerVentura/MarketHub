package com.markethub.order.application.query;

public interface QueryHandler <Q, R>{
    R handle(Q query);
}
