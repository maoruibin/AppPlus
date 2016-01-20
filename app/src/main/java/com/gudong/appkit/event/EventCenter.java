/*
 *     Copyright (c) 2015 GuDong
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

import com.gudong.appkit.utils.logger.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by GuDong on 12/8/15 10:55.
 * Update now use RxBus replace it
 * Contact with 1252768410@qq.com.
 */
@Deprecated
public class EventCenter {
    private static EventCenter ourInstance = new EventCenter();
//    private static HashSet mEvents;
    private Map<EEvent,Set<Subscribe>> mEvents;
    public static EventCenter getInstance() {
        return ourInstance;
    }

    private EventCenter() {
        mEvents = new HashMap<EEvent,Set<Subscribe>>();
    }

    /**
     * trigger event and all object which register this event will observe this event
     * @param event
     */
    public void triggerEvent(EEvent event, Bundle data) {
        Set<Subscribe>registerList = mEvents.get(event);
        if(registerList == null){
            return;
        }
        if(!registerList.isEmpty()){
            for (Iterator<Subscribe>it = registerList.iterator();it.hasNext();) {
                Subscribe subscribe = it.next();
                subscribe.update(event,data);
            }
        }
    }

    public void registerEvent(EEvent event,Subscribe subscribe) {
        Set<Subscribe> registerList = mEvents.get(event);
        if(registerList == null){
            registerList = new HashSet<Subscribe>();
        }
        if(registerList.isEmpty()){
            mEvents.put(event,registerList);
        }
        registerList.add(subscribe);
    }

    public void unregisterEvent(EEvent event,Subscribe subscribe){
        Set<Subscribe> registerList = mEvents.get(event);
        if(registerList!=null && !registerList.isEmpty()){
            registerList.remove(subscribe);
            Logger.i("unregister "+event.name());
        }
    }
}
