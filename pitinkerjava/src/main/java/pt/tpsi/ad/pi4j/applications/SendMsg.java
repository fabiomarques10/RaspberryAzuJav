package pt.tpsi.ad.pi4j.applications;

import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.messaging.*;
import com.microsoft.azure.sdk.iot.service.messaging.IotHubServiceClientProtocol;
import com.microsoft.azure.sdk.iot.service.messaging.MessagingClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class SendMsg {
    private static final int D2C_MESSAGE_TIMEOUT_MILLISECONDS = 10000;

    //private String connectionString = "HostName=;SharedAccessKeyName=;SharedAccessKey=";
    String connectionString = "HostName=i;DeviceId=;ModuleId=TelemetryReader;SharedAccessKey=";

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, IotHubException, TimeoutException {
        //MessagingClient client = new MessagingClient("HostName=;DeviceId=Device-;ModuleId=TelemetryReader;SharedAccessKeyName=iothubowner;SharedAccessKey=", IotHubServiceClientProtocol.AMQPS);
        MessagingClient client = new MessagingClient("HostName=;SharedAccessKeyName=iothubowner;SharedAccessKey=", IotHubServiceClientProtocol.AMQPS);
        SendMsg sendMsg = new SendMsg();
        client.open();
        System.out.println("Successfully created an IoT Hub client.");
        System.out.println("Opened connection to IoT Hub.");
        System.out.println("ENVIANDO AS VARIAVEIS");

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Criar valores aleatórios para temperatura, humidade e o valor boolean para o botão
                double temperature = 20 + Math.random() * 10;
                double humidity = 30 + Math.random() * 20;
                boolean randomBoolean = new Random().nextBoolean();

                //Meter a data dentro da string formattedDate
                Date currentDate = new Date();
                String formattedDate = currentDate.toString();

                String msgStr = "temperature -> " + (int) temperature + " - humidity -> " + (int) humidity + "%  - Botao ativo? -> " + randomBoolean + " - date ->" + formattedDate;

                Message msg = new Message(msgStr);
                msg.setMessageId(UUID.randomUUID().toString());
                System.out.println(msgStr);

                try {
                    // Send message to the TelemetryReader module
                    //client.send("Device-2044720", "TelemetryReader", msg);
                    client.send( "iotdevice2069822", "TelemetryReader", msg);
                    System.out.println("Successfully sent the message to TelemetryReader module");
                } catch (IotHubException e) {
                    System.out.println("Failed to send the message. Status code: " + e.getErrorCode());
                } catch (InterruptedException | TimeoutException e) {
                    System.out.println("Failed to send message, it was interrupted or timed out");
                }
            }
        }, 0, 2000);

        // Close the connection
        // System.out.println("Closing the client...");
        // client.close();
    }
}


