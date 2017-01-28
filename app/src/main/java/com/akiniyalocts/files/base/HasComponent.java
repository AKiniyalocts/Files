package com.akiniyalocts.files.base;

public interface HasComponent<T> {
    T getComponent();

    void injectComponent();
}