package httpserver;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrameServer extends javax.swing.JFrame implements ActionListener {

    public FrameServer() {
        initComponents();
    }
    
    boolean isOpenServer=false;
    Thread serverSocketThread;
    
    public void addTextFrame(String msg){
        jtText.setText(jtText.getText()+"\n"+msg);
    }   
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtIP = new javax.swing.JTextField();
        jtPort = new javax.swing.JTextField();
        bServer = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtText = new javax.swing.JTextPane();
        bGetIP = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel1.setText("IP:");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel2.setText("PORT:");

        jtIP.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N

        jtPort.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jtPort.setText("8080");

        bServer.setText("START SERVER");
        bServer.addActionListener(this);

        jtText.setText("SERVER PANELİ (browser için IP:PORT/index.html)");
        jtText.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jtText.setEnabled(false);
        jScrollPane1.setViewportView(jtText);

        bGetIP.setText("<-");
        bGetIP.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bServer)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(bGetIP))
                            .addComponent(jtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bGetIP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(bServer)
                .addGap(41, 41, 41)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == bGetIP) {
            FrameServer.this.bGetIPActionPerformed(evt);
        }
        else if (evt.getSource() == bServer) {
            FrameServer.this.bServerActionPerformed(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void bGetIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bGetIPActionPerformed
        try {
            String IP = "" + InetAddress.getLocalHost();
            int ind = IP.indexOf("/");
            IP = IP.substring(ind + 1);
            jtIP.setText(IP);
        } catch (UnknownHostException ex) {
            addTextFrame(ex.getMessage());
        }
    }//GEN-LAST:event_bGetIPActionPerformed

    private void bServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bServerActionPerformed
//        if(isOpenServer){
//            try {
//                String ip = jtIP.getText();
//                int port = Integer.parseInt(jtPort.getText());
//                ServerAcceptThread.continueThread=false;
//                try {
//                    Socket sock=new Socket(ip,port);
//                    sock.getOutputStream().write("close".getBytes());
//                    int gr=sock.getInputStream().read();
//                } catch (IOException ex) {
//                    addTextFrame(ex.getMessage());
//                }
//                
//                serverSocketThread.interrupt();
//                serverSocketThread.join();
//            } catch (InterruptedException ex) {
//                addTextFrame(ex.getMessage());
//                return;
//            }
//            bServer.setText("START SERVER");
//            isOpenServer=false;
//            return;
//        }
        if(isOpenServer){
             addTextFrame("server açık");
            return;
        }
        String ip = jtIP.getText();
        int port = Integer.parseInt(jtPort.getText());
        try {
            ServerSocket serverConnect = new ServerSocket();
            serverConnect.bind(new InetSocketAddress(ip, port));
            
            serverSocketThread=new Thread(new ServerAcceptThread(serverConnect, this));
            serverSocketThread.start();
        } catch (IOException ex) {
            addTextFrame(ex.getMessage());
            return;
        }
        jtIP.setEnabled(false);
        jtPort.setEnabled(false);
        bGetIP.setEnabled(false);
//        bServer.setText("STOP SERVER");
        isOpenServer=true;
        addTextFrame("Sunucu çalışıyor.\n Sunucu dinelemede... IP : " + ip + " Port : " + port + " ...");
    }//GEN-LAST:event_bServerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrameServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrameServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrameServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrameServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameServer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bGetIP;
    private javax.swing.JButton bServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jtIP;
    private javax.swing.JTextField jtPort;
    private javax.swing.JTextPane jtText;
    // End of variables declaration//GEN-END:variables
}
