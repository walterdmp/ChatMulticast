package br.edu.ifsuldeminas.sd.multicast;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatMulticastGUI extends JFrame implements MessageContainer {
	
	private static final long serialVersionUID = 1L;

    private JTextField txtGrupoIp, txtPorta, txtNickname, txtMensagem;
    private JTextArea areaChat;
    private JButton btnEnviar, btnConectar;
    private ChatEngine engine;

    public ChatMulticastGUI() {
        setTitle("Chat Multicast - SD");
        setSize(450, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        JPanel painelConfig = new JPanel(new GridLayout(4, 2, 5, 5));
        
        painelConfig.add(new JLabel(" IP do Grupo Multicast:"));
        txtGrupoIp = new JTextField("224.0.0.1"); 
        painelConfig.add(txtGrupoIp);
        
        painelConfig.add(new JLabel(" Porta:"));
        txtPorta = new JTextField("5555"); 
        painelConfig.add(txtPorta);
        
        painelConfig.add(new JLabel(" Seu Nickname:"));
        txtNickname = new JTextField("");
        painelConfig.add(txtNickname);
        
        painelConfig.add(new JLabel(""));
        btnConectar = new JButton("Entrar no Grupo");
        painelConfig.add(btnConectar);
        
        add(painelConfig, BorderLayout.NORTH);

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        add(new JScrollPane(areaChat), BorderLayout.CENTER);

        JPanel painelEnvio = new JPanel(new BorderLayout(5, 5));
        txtMensagem = new JTextField();
        btnEnviar = new JButton("Enviar");
        btnEnviar.setEnabled(false); 
        
        painelEnvio.add(txtMensagem, BorderLayout.CENTER);
        painelEnvio.add(btnEnviar, BorderLayout.EAST);
        
        add(painelEnvio, BorderLayout.SOUTH);

        btnConectar.addActionListener(e -> conectar());
        btnEnviar.addActionListener(e -> enviar());
        txtMensagem.addActionListener(e -> enviar());
    }

    private void conectar() {
        String ip = txtGrupoIp.getText();
        String portaStr = txtPorta.getText();
        String nick = txtNickname.getText();

        if (nick.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O Nickname é obrigatório!");
            return;
        }

        try {
            int porta = Integer.parseInt(portaStr);
            
            engine = new ChatEngine(ip, porta, nick, this);
            
            areaChat.append("--- Conectado ao grupo " + ip + " ---\n");
            
            btnConectar.setEnabled(false);
            txtGrupoIp.setEditable(false);
            txtPorta.setEditable(false);
            txtNickname.setEditable(false);
            
            btnEnviar.setEnabled(true);
            txtMensagem.requestFocus();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao ligar a rede: " + ex.getMessage());
        }
    }

    private void enviar() {
        String msg = txtMensagem.getText();
        if (!msg.trim().isEmpty() && engine != null) {
            try {
                engine.send(msg);
                txtMensagem.setText("");
            } catch (Exception ex) {
                areaChat.append("Erro no envio: " + ex.getMessage() + "\n");
            }
        }
    }

    @Override
    public void newMessage(String message) {
        areaChat.append(message + "\n");
    }

    public static void main(String[] args) {
        ChatMulticastGUI tela = new ChatMulticastGUI();
        
        tela.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (tela.engine != null) {
                    tela.engine.disconnect();
                }
                System.exit(0);
            }
        });
        
        tela.setVisible(true);
    }
}