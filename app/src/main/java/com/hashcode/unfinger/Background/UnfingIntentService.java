package com.hashcode.unfinger.Background;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by oluwalekefakorede on 30/07/2017.
 */

public class UnfingIntentService extends IntentService {
    private final static String UNFING_ACTION = "unfing";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UnfingIntentService(String name) {
        super(name);
    }
    public void unFinger(Context context){
        Intent intent = new Intent(context, UnfingIntentService.class);
        intent.setAction(UNFING_ACTION);
        startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        switch (action){
            case UNFING_ACTION:

        }
    }
}
