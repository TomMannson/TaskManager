package pl.tommmannson.taskqueue.messaging.impl;

import android.content.Context;
import android.content.Intent;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskQueueThread;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.messaging.MessageHandler;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class BootServiceMessageHandler implements MessageHandler<BootServiceMessage> {

    @Override
    public void handleMessage(BootServiceMessage message) {
        TaskManager manager = message.getTaskManager();
        TaskManagerConfiguration configuration = message.getConfig();

        if (configuration.isThreadPoolMode()) {
            TaskQueueThread service = new TaskQueueThread(message.getAppContext());

            service.configure(configuration);
            service.setQueueId(manager.getId());
            service.start();

            manager.setService(service);
//            messageDispatcher.dispatch();
        } else {

            if (!tryToStartService(message)) {
//            Logger.d(this.getClass(), "Service was not started as Activity died prematurely");
                return;
            }

//            if (!isBinded) {
            bindService(message);
//            }
        }
    }

    private boolean tryToStartService(BootServiceMessage message) {
        boolean success = false;

        Context context = message.getAppContext();
        TaskManagerConfiguration config = message.getConfig();

        if (context != null) {
            checkServiceIsProperlyDeclaredInAndroidManifest(context, config);
            final Intent intent = new Intent(context, config.getClassOfService());
            context.startService(intent);
            success = true;
        }

        return success;
    }

    private void checkServiceIsProperlyDeclaredInAndroidManifest(Context context, TaskManagerConfiguration configuration) {

        final Intent intentCheck = new Intent(context, configuration.getClassOfService());
        if (context.getPackageManager().queryIntentServices(intentCheck, 0).isEmpty()) {
            throw new RuntimeException("Impossible to start TaskManager as no service of class : "
                    + configuration.getClassOfService().getName() + " is registered in AndroidManifest.xml file !");
        }
    }

    private void bindService(BootServiceMessage message) {

        Context context = message.getAppContext();
        TaskManagerConfiguration config = message.getConfig();

        if (context != null) {
            TaskManager.RequestServiceConnection connection = message.getTaskManager().new RequestServiceConnection();
            context.bindService(new Intent(context, config.getClassOfService()), connection, Context.BIND_AUTO_CREATE);
        } else {

        }
    }

}
