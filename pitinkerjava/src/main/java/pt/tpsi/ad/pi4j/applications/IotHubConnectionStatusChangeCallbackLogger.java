package pt.tpsi.ad.pi4j.applications;

import com.microsoft.azure.sdk.iot.device.ConnectionStatusChangeContext;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;

public class IotHubConnectionStatusChangeCallbackLogger implements IotHubConnectionStatusChangeCallback
{
    @Override
    public void onStatusChanged(ConnectionStatusChangeContext connectionStatusChangeContext)
    {
        IotHubConnectionStatus status = connectionStatusChangeContext.getNewStatus();
        IotHubConnectionStatusChangeReason statusChangeReason = connectionStatusChangeContext.getNewStatusReason();
        Throwable throwable = connectionStatusChangeContext.getCause();

        System.out.println();
        System.out.println("CONNECTION STATUS UPDATE: " + status);
        System.out.println("CONNECTION STATUS REASON: " + statusChangeReason);
        System.out.println("CONNECTION STATUS THROWABLE: " + (throwable == null ? "null" : throwable.getMessage()));
        System.out.println();

        if (throwable != null)
        {
            throwable.printStackTrace();
        }

        if (status == IotHubConnectionStatus.DISCONNECTED)
        {
            System.out.println("The connection was lost, and is not being re-established." +
                    " Look at provided exception for how to resolve this issue." +
                    " Cannot send messages until this issue is resolved, and you manually re-open the device client");
        }
        else if (status == IotHubConnectionStatus.DISCONNECTED_RETRYING)
        {
            System.out.println("The connection was lost, but is being re-established." +
                    " Can still send messages, but they won't be sent until the connection is re-established");
        }
        else if (status == IotHubConnectionStatus.CONNECTED)
        {
            System.out.println("The connection was successfully established. Can send messages.");
        }
    }
}