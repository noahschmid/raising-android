package com.raising.app.models;

public interface Model {
    long getId();
    String getName();

    void setId(long id);
    void setName(String name);

    @Override
    public boolean equals(Object o);
}
