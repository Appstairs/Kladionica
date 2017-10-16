/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forme;

import domen.Radnik;
import domen.Tiket;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import komunikacija.KontrolerKI;
//import logic.Kontroler;
import repozitorijum.Repozitorijum;
import transfer.ServerTransferObjekat;
import transfer.KlijentTransferObjekat;

/**
 *
 * @author KORISNIK
 */
public class FPrijava extends javax.swing.JFrame {

    /**
     * Creates new form FPrijava
     */
    public FPrijava() {
        initComponents();
        listenerOnEnter();

    }

    private void listenerOnEnter() {
        jBtnPrijava.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    obradiPrijavu();
                }
            }
        });

    }

    private void obradiPrijavu() {
        try {
            String korIme = jTxtKorisnickoIme.getText().trim();
            String sifra = jTxtSifra.getText().trim();
            Radnik r = new Radnik(korIme, sifra);
            boolean nasao = false;

            KlijentTransferObjekat zahtev = new KlijentTransferObjekat();
            zahtev.setOperacija(KlijentTransferObjekat.PRIJAVA);
            zahtev.setObjekatOperacije(r);
            KontrolerKI.vratiInstancu().setInstanca(null);
            KontrolerKI.vratiInstancu().posaljiZahtev(zahtev);
            ServerTransferObjekat odgovor = KontrolerKI.vratiInstancu().procitajOdgovor();
            if (odgovor.getSignal() == -1) {
                throw odgovor.getIzuzetak();
            }

            try {
                Radnik radnik = (Radnik) odgovor.getObjekatIzvrsenjaOperacije();
                Repozitorijum.ulogovaniRadnik = radnik;
                JFrame fPrijava = this;

                if (repozitorijum.Repozitorijum.daLiSePrviPutLoguje == true) {
                    Tiket tiket = new Tiket(new ArrayList<>());
                    tiket.setRadnik(repozitorijum.Repozitorijum.ulogovaniRadnik);
                    KlijentTransferObjekat zahtev10 = new KlijentTransferObjekat();
                    zahtev10.setOperacija(KlijentTransferObjekat.KREIRAJ_NOVI_TIKET);
                    zahtev10.setObjekatOperacije(tiket);
                    KontrolerKI.vratiInstancu().posaljiZahtev(zahtev10);
                    ServerTransferObjekat odgovor10 = KontrolerKI.vratiInstancu().procitajOdgovor();

                    if (odgovor10.getSignal() == -1) {
                        throw new Exception(odgovor10.getPoruka());
                    }
                    Repozitorijum.tiket = (Tiket) odgovor10.getObjekatIzvrsenjaOperacije();
                    prikaziGlavnuFormu();
                }
                fPrijava.setVisible(false);
                JOptionPane.showMessageDialog(this, odgovor.getPoruka());
                repozitorijum.Repozitorijum.daLiSePrviPutLoguje = false;

            } catch (IndexOutOfBoundsException ex) {
                JOptionPane.showMessageDialog(this, "Sistem ne moze da nadje radnika na osnovu unetih vrednosti");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Doslo je do problema sa serverom!");
            e.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTxtKorisnickoIme = new javax.swing.JTextField();
        jLabelProveraSifre = new javax.swing.JLabel();
        jBtnPrijava = new javax.swing.JButton();
        jLabelKorisnickoIme = new javax.swing.JLabel();
        jLabelSifra = new javax.swing.JLabel();
        jTxtSifra = new javax.swing.JPasswordField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuServer = new javax.swing.JMenu();
        jMenuItemKofiguracijaServera = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Prijava");

        jTxtKorisnickoIme.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTxtKorisnickoIme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtKorisnickoImeActionPerformed(evt);
            }
        });

        jLabelProveraSifre.setForeground(new java.awt.Color(255, 51, 51));

        jBtnPrijava.setText("Prijavi se");
        jBtnPrijava.setEnabled(false);
        jBtnPrijava.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnPrijavaActionPerformed(evt);
            }
        });
        jBtnPrijava.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBtnPrijavaKeyPressed(evt);
            }
        });

        jLabelKorisnickoIme.setText("Korisničko ime:");

        jLabelSifra.setText("Šifra:");

        jTxtSifra.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTxtSifraCaretUpdate(evt);
            }
        });

        jMenuServer.setText("Server");

        jMenuItemKofiguracijaServera.setText("Konfiguracija servera");
        jMenuItemKofiguracijaServera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemKofiguracijaServeraActionPerformed(evt);
            }
        });
        jMenuServer.add(jMenuItemKofiguracijaServera);

        jMenuBar1.add(jMenuServer);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabelSifra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelKorisnickoIme, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabelProveraSifre, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTxtKorisnickoIme, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                    .addComponent(jTxtSifra))
                                .addGap(5, 5, 5))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(jBtnPrijava)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxtKorisnickoIme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelKorisnickoIme))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSifra)
                    .addComponent(jTxtSifra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelProveraSifre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jBtnPrijava)
                .addGap(36, 36, 36))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTxtKorisnickoImeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtKorisnickoImeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtKorisnickoImeActionPerformed

    private void jBtnPrijavaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnPrijavaActionPerformed

        obradiPrijavu();

    }//GEN-LAST:event_jBtnPrijavaActionPerformed

    private void jTxtSifraCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTxtSifraCaretUpdate
        // TODO add your handling code here:
        String sifra = jTxtSifra.getText();
        if (sifra.length() < 8) {

            jLabelProveraSifre.setText("sifra je kratka!");
        } else {
            jBtnPrijava.setEnabled(true);
            jLabelProveraSifre.setText("");
        }
    }//GEN-LAST:event_jTxtSifraCaretUpdate

    private void jBtnPrijavaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBtnPrijavaKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_jBtnPrijavaKeyPressed

    private void jMenuItemKofiguracijaServeraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemKofiguracijaServeraActionPerformed
        // TODO add your handling code here:
        JDialogKonfiguracijaServera jdks = null;
        try {
            jdks = new JDialogKonfiguracijaServera(this, rootPaneCheckingEnabled);
        } catch (IOException ex) {
            Logger.getLogger(FPrijava.class.getName()).log(Level.SEVERE, null, ex);
        }
        jdks.setLocationRelativeTo(null);
        jdks.setResizable(false);
        jdks.setVisible(true);

    }//GEN-LAST:event_jMenuItemKofiguracijaServeraActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnPrijava;
    private javax.swing.JLabel jLabelKorisnickoIme;
    private javax.swing.JLabel jLabelProveraSifre;
    private javax.swing.JLabel jLabelSifra;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemKofiguracijaServera;
    private javax.swing.JMenu jMenuServer;
    private javax.swing.JTextField jTxtKorisnickoIme;
    private javax.swing.JPasswordField jTxtSifra;
    // End of variables declaration//GEN-END:variables

    private void prikaziGlavnuFormu() throws ClassNotFoundException, IOException {

        JFrame fGlavna = new FGlavna();
        JPanel jPanel = new JPanelKreirajTiket();
        fGlavna.setLayout(new BorderLayout());
        JScrollPane jScroll = new JScrollPane(jPanel);
        fGlavna.add(jScroll, BorderLayout.CENTER);
        fGlavna.pack();
        fGlavna.setExtendedState(fGlavna.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        fGlavna.setVisible(true);
    }
}
