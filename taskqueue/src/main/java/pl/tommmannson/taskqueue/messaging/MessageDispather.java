package pl.tommmannson.taskqueue.messaging;

import android.os.Handler;
import android.os.Looper;

import pl.tommmannson.taskqueue.messaging.impl.AddTaskMessage;
import pl.tommmannson.taskqueue.messaging.impl.AddTaskMessageHandler;
import pl.tommmannson.taskqueue.messaging.impl.BootServiceMessage;
import pl.tommmannson.taskqueue.messaging.impl.BootServiceMessageHandler;
import pl.tommmannson.taskqueue.messaging.impl.CancelTaskMessage;
import pl.tommmannson.taskqueue.messaging.impl.CancelTaskMessageHandler;
import pl.tommmannson.taskqueue.messaging.impl.RegisterCallbackMessage;
import pl.tommmannson.taskqueue.messaging.impl.RegisterCallbackMessageHandler;
import pl.tommmannson.taskqueue.messaging.impl.UnregisterCallbackMessage;
import pl.tommmannson.taskqueue.messaging.impl.UnregisterCallbackMessageHandler;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class MessageDispather {

    private final Thread dispatherThread;
    Handler dispatherHandler = null;
    static MessageHandlerMap map = new MessageHandlerMap();

    static{
        map.put(BootServiceMessage.class, new BootServiceMessageHandler());
        map.put(AddTaskMessage.class, new AddTaskMessageHandler());
        map.put(RegisterCallbackMessage.class, new RegisterCallbackMessageHandler());
        map.put(UnregisterCallbackMessage.class, new UnregisterCallbackMessageHandler());
        map.put(CancelTaskMessage.class, new CancelTaskMessageHandler());
    }

    public MessageDispather(){
        dispatherThread = new Thread("Dispather thread"){
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                dispatherHandler = new Handler(Looper.myLooper()){
                    @Override
                    public void handleMessage(android.os.Message msg) {
                        Message message = (Message) msg.obj;
                        MessageHandler<Message> handler = (MessageHandler<Message>) map.get(message.getClass());
                        handler.handleMessage(message);
                    }
                };

                Looper.loop();
            }
        };
        dispatherThread.start();
    }

    public void dispatch(Message message){
        while (dispatherHandler == null){
            Thread.yield();
        }

        android.os.Message androidMessage = new android.os.Message();
        androidMessage.obj = message;

        dispatherHandler.sendMessage(androidMessage);
    }

    private void handleMessage(Message message){

    }
}
