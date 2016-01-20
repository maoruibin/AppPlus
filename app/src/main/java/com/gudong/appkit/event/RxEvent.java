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

import android.os.Bundle;

/**
 * Created by GuDong on 1/20/16 18:22.
 * Contact with 1252768410@qq.com.
 */
public class RxEvent {
    private EEvent mType;
    private Bundle mData;

    public RxEvent() {
    }

    public RxEvent(EEvent type) {
        mType = type;
    }

    public RxEvent(EEvent type,Bundle data) {
        mData = data;
        mType = type;
    }

    public static RxEvent get(EEvent mType){
        return new RxEvent(mType);
    }

    public Bundle getData() {
        return mData;
    }

    public void setData(Bundle data) {
        mData = data;
    }

    public EEvent getType() {
        return mType;
    }

    public void setType(EEvent type) {
        mType = type;
    }
}
