package pt.tpsi.ad.pi4j.applications;


import com.microsoft.azure.sdk.iot.service.messaging.IotHubServiceClientProtocol;
import com.microsoft.azure.sdk.iot.service.messaging.Message;
import com.microsoft.azure.sdk.iot.service.messaging.MessagingClient;
import pt.tpsi.ad.pi4j.components.HumiTempComponent;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import java.io.IOException;
import java.net.URISyntaxException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;


public class HumiTempApp  {

    private static final int PIN_BUTTON = 24; // PIN 18 = BCM 24
    private static final int PIN_LED = 22; // PIN 15 = BCM 22

    private static int pressCount = 0;
    public double temp,humi;
    static HumiTempApp app = new HumiTempApp();
    SendMsg sendMsg = new SendMsg();
    static Led led =new Led();
    //static MessagingClient client = new MessagingClient("HostName=2044720hub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=5E3CZOTS/q1bZut+KdQLxYJSEmCLsfYKHAIoTOptx2I=", IotHubServiceClientProtocol.AMQPS);

    static MessagingClient client = new MessagingClient("HostName=iothub2069822.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=g+4PWe0Cds3vDh9QwUfBYmI1bFr+TnW82AIoTOWWlr0=", IotHubServiceClientProtocol.AMQPS);



    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException, IotHubException, TimeoutException {
        app.botao();
        client.open();
        if (client.isOpen()){
            app.enviodemensagem();
        }
    }

    public void enviodemensagem() throws IOException, InterruptedException, IotHubException, TimeoutException {

        System.out.println("Successfully created an IoT Hub client.");
        System.out.println("Opened connection to IoT Hub.");
        System.out.println("ENVIANDO ...");


        for (int i = 0; i < 15; ++i) {

            String msgStr = "{\"temperatura\":" + temp+ ",\"humidade\":" + humi + "}";

            Message msg = new Message(msgStr);
            msg.setMessageId(java.util.UUID.randomUUID().toString());
            System.out.println(msgStr);

            try {
                System.out.println(msg);
                //client.send( "Device-2044720", "TelemetryReader", msg);
                client.send( "iotdevice2069822", "TelemetryReader", msg);
                System.out.println("Successfully sent the message");
            } catch (IotHubException e) {
                System.out.println("Failed to send the message. Status code: " + e.getErrorCode());
            }

        }
    }



    public void lerSensor(){
        var pi4j = Pi4J.newAutoContext();
        final var dht11 = new HumiTempComponent();
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                temp = dht11.getTemperature();
                humi= dht11.getHumidity();
                System.out.println("It is currently " + temp + "°C and the Humidity is " + humi + "%.");


            }
        }, 0, 1000);

    }

    public void botao(){
        var pi4j = Pi4J.newAutoContext();
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led")
                .name("LED Flasher")
                .address(PIN_LED)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        var led = pi4j.create(ledConfig);

        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .id("button")
                .name("Press button")
                .address(PIN_BUTTON)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L)
                .provider("pigpio-digital-input");
        var button = pi4j.create(buttonConfig);

        button.addListener(e -> {
            if (e.state() == DigitalState.LOW) {
                if (led.equals(DigitalState.HIGH)) {

                    led.low();
                    System.out.println("Sistema Desligado");

                } else {
                    led.high();
                    System.out.println("Sistema Ligado");
                }

            }
        });
    }

}
