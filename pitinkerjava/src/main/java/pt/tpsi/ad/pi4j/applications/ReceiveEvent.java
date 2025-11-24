package pt.tpsi.ad.pi4j.applications;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ReceiveEvent {

    public static void main(String[] args) throws IotHubClientException {

        //String connectionString = "HostName=2044720hub.azure-devices.net;DeviceId=Device-2044720;ModuleId=TelemetryReader;SharedAccessKeyName=iothubowner;SharedAccessKey=5E3CZOTS/q1bZut+KdQLxYJSEmCLsfYKHAIoTOptx2I=";
        String connectionString = "HostName=iothub2069822.azure-devices.net;DeviceId=iotdevice2069822;ModuleId=TelemetryReader;SharedAccessKey=dBHmCgQvFrYvhc3O8vD664FeD0bZkhA+FAIoTDNGgX4=";

        ModuleClient moduleClient = new ModuleClient(connectionString, IotHubClientProtocol.AMQPS_WS);
        moduleClient.setConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallbackLogger(), new Object());
        moduleClient.open(true);

        moduleClient.setMessageCallback(new MessageCallback() {
            @Override
            public IotHubMessageResult onCloudToDeviceMessageReceived(Message message, Object o) {
                System.out.println("Received message: " + new String(message.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
                System.out.println(("Received message: " + message.getMessageId()));
                return IotHubMessageResult.COMPLETE;
            }
        }, null);


        System.out.println("Successfully created an IoT Hub client.");

        System.out.println("Successfully set message callback.");

        System.out.println("Opened connection to IoT Hub. Messages sent to this device will now be received.");

        System.out.println("Press any key to exit...");

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
        scanner.nextLine();
        System.out.println("Shutting down...");
        moduleClient.close();
    }
}
