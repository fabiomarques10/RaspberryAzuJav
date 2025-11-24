package pt.tpsi.ad.pi4j;

import pt.tpsi.ad.pi4j.applications.HumiTempApp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class SmartTermo extends JFrame {

    private JButton buttonAumentar;
    private JButton buttonDiminuir;
    private JPanel jPanelMain;
    private JLabel statusLabel;
    private JLabel labelTempDesejadaValor;
    private JLabel labelTempAtualValor;
    private JTextArea logstextarea;
    private JTextArea textAreaLog;
    private JSlider slider1;
    private JTextField temperatura_desejada;
    private JTextField ativado_desativado;
    private JButton ativar;
    private JButton button2;
    private JButton buttonAumentarHumi;
    private JLabel labelHumiDesejadaValor;
    private JLabel labelHumiAtualValor;
    private JLabel logs;
    private JLabel labelTempAtual;
    private JLabel labelTempDesejada;
    private JButton buttonDiminuirHumi;
    private JButton buttonSetCurrentValues;
    private JToggleButton limpar;

    private double desiredTemp;
    //private double tempatual;
    private double desiredHumi;
    private Date dataAtual;
    private String valorBotao;
    private Timer timer;
    private double initialTemperature;
    private double initialHumidity;
    HumiTempApp humiTempApp = new HumiTempApp();

    public SmartTermo(String title) {
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(jPanelMain);
        this.pack();
        this.initialTemperature = 25;
        this.initialHumidity = 50;
        this.desiredTemp = initialTemperature;
        this.desiredHumi = initialHumidity;
        this.dataAtual = new Date();
        this.ativar.setText("OFF");

        //this.tempatual = humiTempApp.temp;

        ativar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                valorBotao = ativar.getText();
                ativar.setText("ON");
                if (valorBotao.equals("OFF")) {
                    ativar.setText("ON");
                    logstextarea.append("Sistema Desligado\n");
                } else {
                    ativar.setText("OFF");
                    logstextarea.append("Sistema Ligado\n");
                }
            }
        });

        buttonAumentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desiredTemp += 1;
                labelTempDesejadaValor.setText(desiredTemp + "º");
                logstextarea.append("Aumentou a temperatura para " + desiredTemp + "º" + "\n");
            }
        });

        buttonDiminuir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desiredTemp -= 1;
                labelTempDesejadaValor.setText(desiredTemp + "º");
                logstextarea.append("Diminuiu a temperatura para " + desiredTemp + "º" + "\n");
            }
        });

        buttonAumentarHumi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desiredHumi += 1;
                labelHumiDesejadaValor.setText(desiredHumi + "º");
                logstextarea.append("Aumentou a humidade para " + desiredHumi + "º" + "\n");
            }
        });

        buttonDiminuirHumi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desiredHumi -= 1;
                labelHumiDesejadaValor.setText(desiredHumi + "º");
                logstextarea.append("Diminuiu a humidade para " + desiredHumi + "%" + "\n");
            }
        });

        limpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logstextarea.setText("");
            }
        });

        buttonSetCurrentValues.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //desiredTemp = humiTempApp.temp;
                //desiredHumi = humiTempApp.humi;
                //codigo acima so funcionaria com informacao do sensor
                initialTemperature = desiredTemp;
                initialHumidity = desiredHumi;

                labelTempAtualValor.setText(initialTemperature + "º");
                labelHumiAtualValor.setText(initialHumidity + "%");

                logstextarea.append("Set desired temperature to " + desiredTemp + "º and humidity to " + desiredHumi + "%\n");
            }
        });

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelTempDesejadaValor.setText(desiredTemp + "º");
                labelHumiDesejadaValor.setText(desiredHumi + "%");
            }
        });
        timer.start();
    }

    public static void main(String[] args) {


        SmartTermo frame = new SmartTermo("Java Termostat");
        frame.labelTempAtualValor.setText(frame.initialTemperature + "º");
        frame.labelHumiAtualValor.setText(frame.initialHumidity + "%");

        frame.setVisible(true);
    }
}
