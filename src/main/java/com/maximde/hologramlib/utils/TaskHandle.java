package com.maximde.hologramlib.utils;

public interface TaskHandle {
    void cancel();

    boolean isCancelled();
}