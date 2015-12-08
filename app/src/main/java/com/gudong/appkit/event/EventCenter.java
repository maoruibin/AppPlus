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
 * Contact with 1252768410@qq.com.
 */
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
