/*
 *     Copyright (c) 2015 Maoruibin
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

package com.gudong.appkit.event;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by GuDong on 1/20/16 17:54.
 * Contact with 1252768410@qq.com.
 */
public class RxBus<T, R>  {
    private final Subject<T,R> rxBus;

    private RxBus(){
        rxBus = new SerializedSubject(PublishSubject.<T>create());
    }

    private static class SingletonHolder{
        private static RxBus instance = new RxBus();
    }

    public static RxBus getInstance(){
        return SingletonHolder.instance;
    }

    public void send(T msg){
        rxBus.onNext(msg);
    }

    public Observable<R> toObservable(){
        return rxBus.asObservable().onBackpressureBuffer();
    }

    /**
     * check the observers has subscribe or not DeadEvent https://github.com/square/otto/blob/master/otto/src/main/java/com/squareup/otto/DeadEvent.java
     *
     * @return
     */
    public boolean hasObservers() {
        return rxBus.hasObservers();
    }
}
