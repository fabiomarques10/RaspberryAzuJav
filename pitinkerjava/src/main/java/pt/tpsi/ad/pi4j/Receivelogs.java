package pt.tpsi.ad.pi4j;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import pt.tpsi.ad.pi4j.applications.IotHubConnectionStatusChangeCallbackLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class Receivelogs extends JFrame {
    private JTextArea logs;
    private JToggleButton Limpar;
    private JPanel panel1;
    private JToggleButton Baixar_logs;
    private JButton simulator;
    private ModuleClient client;
    private Timer simulationTimer;
    private boolean simulationRunning = false;

    public Receivelogs(String title) throws HeadlessException, IotHubClientException {
        super(title);

        // Configurações básicas da janela
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(750, 300));

        // Inicialização do painel e componentes
        panel1 = new JPanel(new BorderLayout());
        logs = new JTextArea();
        logs.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logs);
        panel1.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        panel1.add(southPanel, BorderLayout.SOUTH);

        Limpar = new JToggleButton("Limpar Logs");
        southPanel.add(Limpar);

        Baixar_logs = new JToggleButton("Baixar Logs");
        southPanel.add(Baixar_logs);

        simulator = new JButton("Start Simulator");
        panel1.add(simulator, BorderLayout.NORTH);

        simulator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulationRunning) {
                    stopSimulation();
                } else {
                    startSimulation();
                }
            }
        });

        Limpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logs.setText("");
            }
        });

        Baixar_logs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadLogs();
            }
        });

        setContentPane(panel1);
        pack();

        initializeIoTHub();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (client != null) {
                    client.close();
                }
                System.exit(0);
            }
        });
    }

    private void startSimulation() {
        simulationTimer = new Timer();

        simulationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Criar valores aleatórios para temperatura, humidade e o valor boolean para o botão
                double temperature = 20 + Math.random() * 10;
                double humidity = 30 + Math.random() * 20;
                boolean randomBoolean = new Random().nextBoolean();

                // Meter a data dentro da string formattedDate
                Date currentDate = new Date();
                String formattedDate = currentDate.toString();

                String msgStr = "temperature -> " + (int) temperature + " - humidity -> " + (int) humidity + "%  - Botao ativo? -> " + randomBoolean + " - date ->" + formattedDate;

                Message msg = new Message(msgStr);
                msg.setMessageId(UUID.randomUUID().toString());

                System.out.println(msgStr);
                logs.append("\n" + msgStr);
            }
        }, 0, 5000);

        simulationRunning = true;
        simulator.setText("Stop Simulator");
    }

    private void stopSimulation() {
        if (simulationTimer != null) {
            simulationTimer.cancel();
        }

        simulationRunning = false;
        simulator.setText("Start Simulator");
    }

    private void downloadLogs() {
        // Obtém o conteúdo atual dos logs
        String logContent = logs.getText();

        // Verifica se há algo para baixar
        if (!logContent.isEmpty()) {
            // Cria um JFileChooser para permitir que o utilizador escolha onde salvar o arquivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Logs");

            // Exibe o diálogo de salvar arquivo
            int userSelection = fileChooser.showSaveDialog(this);

            // Se o utilizador selecionar um local válido, salva os logs no arquivo
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                try {
                    // Obtém o arquivo selecionado pelo usuário
                    java.io.File fileToSave = fileChooser.getSelectedFile();

                    // Adiciona a extensão ".txt" se não estiver presente
                    String filePath = fileToSave.getPath();
                    if (!filePath.toLowerCase().endsWith(".txt")) {
                        fileToSave = new java.io.File(filePath + ".txt");
                    }

                    // Cria um FileWriter para escrever o conteúdo dos logs no arquivo
                    java.io.FileWriter writer = new java.io.FileWriter(fileToSave);
                    writer.write(logContent);
                    writer.close();

                    JOptionPane.showMessageDialog(this, "Logs salvos com sucesso!");
                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao salvar os logs", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum log para baixar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initializeIoTHub() {
        // Inicialização do cliente IoT Hub em uma nova thread
        new Thread(() -> {
            try {
                client = new ModuleClient("HostName=Raspberypi.azure-devices.net;DeviceId=device_2074022;ModuleId=TelemetryReader;SharedAccessKey=", IotHubClientProtocol.AMQPS);
                System.out.println("Successfully created an IoT Hub client.");

                client.setMessageCallback((message, o) -> {
                    System.out.println("Received message with content: " +
                            new String(message.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
                    logs.append("\n" + new String(message.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
                    return IotHubMessageResult.COMPLETE;
                }, null);
                System.out.println("Successfully set message callback.");

                client.setConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallbackLogger(), new Object());

                client.open(true);

                System.out.println("Opened connection to IoT Hub. Messages sent to this device will now be received.");

            } catch (IotHubClientException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws IotHubClientException {
        SwingUtilities.invokeLater(() -> {
            try {
                new Receivelogs("Java Logs").setVisible(true);
            } catch (IotHubClientException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
