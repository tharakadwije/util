/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psa.pc.fw.ac.util;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author ngch
 */
public class TimerUtil {
    
    private long startTime;
    
    public TimerUtil(){
        startTime = System.nanoTime();
    }
    
    public long getElapsedTimeMS() {
        return TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    }
    
    public long getElapsedTimeS() {
        return TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    }
}
