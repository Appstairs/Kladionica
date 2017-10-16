/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forme;

import domen.Kvota;
import domen.Radnik;
import domen.StavkaTiketa;
import domen.Tiket;
import domen.Tip;
import domen.UplatnoMesto;
import domen.Utakmica;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.TableView;

import komponente.tabela.model.ModelTabeleStavkaTiketa;
import komponente.tabela.model.ModelTabeleUtakmice;
import komunikacija.KontrolerKI;

import repozitorijum.Repozitorijum;
import transfer.ServerTransferObjekat;
import transfer.KlijentTransferObjekat;

/**
 *
 * @author KORISNIK
 */
public class JPanelKreirajTiket extends javax.swing.JPanel {

    public static Date vratiDatumSaNulom(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public static Date vratiDatumKraj(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);

        res = calendar.getTime();

        return res;
    }

    /**
     * @return the tiket
     */
    public Tiket getTiket() {
        return tiket;
    }

    /**
     * @param tiket the tiket to set
     */
    public void setTiket(Tiket tiket) {
        this.tiket = tiket;
    }

    private Tiket tiket;

    /**
     * Creates new form JPanelKreirajTiket
     */
    public JPanelKreirajTiket() throws ClassNotFoundException, IOException {
        initComponents();

        popuniRadnika();
        popuniUplatnoMesto();

        try {
            popuniCBTipova();
        } catch (IOException ex) {
            prikaziFormuPrijava();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        pripremiTabelu();
        pripremiTabeluUtakmice();

        ubaciListenere();

    }

    private void prikaziFormuPrijava() {
        JOptionPane.showMessageDialog(this, "Izgubili ste konekciju sa serverom! Probajte ponovo da se ulogujete!");
        FPrijava fp = new FPrijava();
        fp.setLocationRelativeTo(null);
        fp.setResizable(false);
        fp.setVisible(true);

    }

    private void ubaciListenere() {
        listenerOnEnterUbaci();
        listenerOnEnterKreirajNoviTiket();
        listenerOnEnterZapamti();
        listenerOnEnterObradi();
        listenerOnEnterPretrazi();

    }

    private void listenerOnEnterPretrazi() {

        jButtonPretrazi.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pretrazi();
                }

            }
        });

    }

    private void listenerOnEnterUbaci() {

        jBtnUbaci.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ubaciUStavku();
                }

            }
        });

    }

    private void ubaciUStavku() {

        ModelTabeleStavkaTiketa model = (ModelTabeleStavkaTiketa) jTableStavkeTiketa.getModel();
        String sifraUtakmice = jTxtFieldSifraUtakmice.getText();
        int redniBroj = model.getRowCount() + 1;
        Tip tip = (Tip) jComboBoxTip.getSelectedItem();
        Utakmica utakmica = new Utakmica();

        StavkaTiketa stavka;

        try {
            stavka = kreirajStavkuIValidiraj(redniBroj, tip, utakmica, sifraUtakmice);

            model.ubaciStavkuTiketa(stavka);

            Repozitorijum.tiket.getStavkeTiketa().add(stavka);
            jBtnZapamti.setEnabled(true);
            jTxtFieldUkupnaKvota.setText(Double.toString(Math.round((model.vratiUkupnuKvotu()) * 100.0) / 100.0));

            if (!jTxtFieldUplata.getText().isEmpty() && jTxtFieldUplata.getText() != null) {
                jTxtFieldPotencijalniDobitak.setText(Double.toString(Math.round((Double.parseDouble(jTxtFieldUplata.getText()) * Double.parseDouble(jTxtFieldUkupnaKvota.getText())) * 100.0) / 100.0));
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        } catch (IOException eio) {
            prikaziFormuPrijava();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void popuniRadnika() {
        jTxtFieldRadnik.setText(Repozitorijum.ulogovaniRadnik.getIme() + " " + Repozitorijum.ulogovaniRadnik.getPrezime());
    }

    private void popuniUplatnoMesto() {
        jTxtFieldUplatnoMesto.setText(Repozitorijum.ulogovaniRadnik.getUplatnoMesto().getGrad() + ", " + Repozitorijum.ulogovaniRadnik.getUplatnoMesto().getUlica() + " " + Repozitorijum.ulogovaniRadnik.getUplatnoMesto().getBroj());
    }

    private void popuniCBTipova() throws ClassNotFoundException, IOException, Exception {
        jComboBoxTip.removeAllItems();
        KlijentTransferObjekat zahtev = new KlijentTransferObjekat();
        zahtev.setOperacija(KlijentTransferObjekat.VRATI_SVE_TIPOVE);
        KontrolerKI.vratiInstancu().posaljiZahtev(zahtev);
        ServerTransferObjekat odgovor = KontrolerKI.vratiInstancu().procitajOdgovor();
        if (odgovor.getSignal() == -1) {
            throw new Exception(odgovor.getPoruka());
        }
        List<Tip> tipovi = (List<Tip>) odgovor.getObjekatIzvrsenjaOperacije();

        for (Tip tip : tipovi) {
            jComboBoxTip.addItem(tip);
        }
    }

    private void pripremiTabelu() throws ClassNotFoundException, IOException {
        List<StavkaTiketa> stavkeTiketa = new ArrayList<>();
        ModelTabeleStavkaTiketa model = new ModelTabeleStavkaTiketa(stavkeTiketa);
        jTableStavkeTiketa.setModel(model);

    }

    private void pripremiTabeluUtakmice() {
        List<Utakmica> utakmice = new ArrayList<>();
        ModelTabeleUtakmice model = new ModelTabeleUtakmice(utakmice);
        jTableUtakmice.setModel(model);

        jTableUtakmice.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    jTxtFieldSifraUtakmice.setText(((Integer) model.getValueAt(row, 0)).toString());
                }
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTableStavkeTiketa = new javax.swing.JTable();
        jBtnKreirajNoviTiket = new javax.swing.JButton();
        jLabelUplatnoMesto = new javax.swing.JLabel();
        jTxtFieldRadnik = new javax.swing.JTextField();
        jLabelRadnik = new javax.swing.JLabel();
        jLabelSifraUtakmice = new javax.swing.JLabel();
        jTxtFieldSifraUtakmice = new javax.swing.JTextField();
        jLabelTip = new javax.swing.JLabel();
        jComboBoxTip = new javax.swing.JComboBox<>();
        jBtnUbaci = new javax.swing.JButton();
        jBtnObrisi = new javax.swing.JButton();
        jLabelUplata = new javax.swing.JLabel();
        jTxtFieldUplata = new javax.swing.JTextField();
        jLabelUkupnaKvota = new javax.swing.JLabel();
        jTxtFieldUkupnaKvota = new javax.swing.JTextField();
        jLabelPotencijalniDobitak = new javax.swing.JLabel();
        jTxtFieldPotencijalniDobitak = new javax.swing.JTextField();
        jBtnZapamti = new javax.swing.JButton();
        jBtnObradi = new javax.swing.JButton();
        jLabelUplataProvera = new javax.swing.JLabel();
        jTxtFieldUplatnoMesto = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableUtakmice = new javax.swing.JTable();
        jDateChooserPrviDatum = new com.toedter.calendar.JDateChooser();
        jDateChooserDrugiDatum = new com.toedter.calendar.JDateChooser();
        jButtonPretrazi = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setNextFocusableComponent(jTxtFieldSifraUtakmice);

        jTableStavkeTiketa.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTableStavkeTiketa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableStavkeTiketa);

        jBtnKreirajNoviTiket.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jBtnKreirajNoviTiket.setText("Kreiraj novi tiket");
        jBtnKreirajNoviTiket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnKreirajNoviTiketActionPerformed(evt);
            }
        });

        jLabelUplatnoMesto.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelUplatnoMesto.setText("Uplatno mesto:");

        jTxtFieldRadnik.setEditable(false);
        jTxtFieldRadnik.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jTxtFieldRadnik.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTxtFieldRadnik.setEnabled(false);

        jLabelRadnik.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelRadnik.setText("Radnik:");

        jLabelSifraUtakmice.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelSifraUtakmice.setText("Sifra utakmice:");

        jTxtFieldSifraUtakmice.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jTxtFieldSifraUtakmice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTxtFieldSifraUtakmiceFocusGained(evt);
            }
        });

        jLabelTip.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelTip.setText("Tip:");

        jComboBoxTip.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jComboBoxTip.setMaximumRowCount(5);
        jComboBoxTip.setAutoscrolls(true);

        jBtnUbaci.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jBtnUbaci.setText("Ubaci");
        jBtnUbaci.setNextFocusableComponent(jTxtFieldSifraUtakmice);
        jBtnUbaci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnUbaciActionPerformed(evt);
            }
        });

        jBtnObrisi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jBtnObrisi.setText("Obrisi");
        jBtnObrisi.setNextFocusableComponent(jTxtFieldSifraUtakmice);
        jBtnObrisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnObrisiActionPerformed(evt);
            }
        });

        jLabelUplata.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelUplata.setText("Uplata:");

        jTxtFieldUplata.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jTxtFieldUplata.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTxtFieldUplataCaretUpdate(evt);
            }
        });

        jLabelUkupnaKvota.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelUkupnaKvota.setText("Ukupna kvota:");

        jTxtFieldUkupnaKvota.setEditable(false);
        jTxtFieldUkupnaKvota.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabelPotencijalniDobitak.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabelPotencijalniDobitak.setText("Potencijalni dobitak:");

        jTxtFieldPotencijalniDobitak.setEditable(false);
        jTxtFieldPotencijalniDobitak.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jBtnZapamti.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jBtnZapamti.setText("Zapamti");
        jBtnZapamti.setEnabled(false);
        jBtnZapamti.setNextFocusableComponent(jBtnObradi);
        jBtnZapamti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnZapamtiActionPerformed(evt);
            }
        });

        jBtnObradi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jBtnObradi.setText("Obradi");
        jBtnObradi.setEnabled(false);
        jBtnObradi.setNextFocusableComponent(jBtnKreirajNoviTiket);
        jBtnObradi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnObradiActionPerformed(evt);
            }
        });

        jLabelUplataProvera.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabelUplataProvera.setForeground(new java.awt.Color(255, 0, 0));

        jTxtFieldUplatnoMesto.setEditable(false);
        jTxtFieldUplatnoMesto.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jTxtFieldUplatnoMesto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTxtFieldUplatnoMesto.setEnabled(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Utakmice"));

        jTableUtakmice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTableUtakmice);

        jDateChooserPrviDatum.setDateFormatString("dd.MM.yyyy.");
        jDateChooserPrviDatum.setNextFocusableComponent(jDateChooserDrugiDatum);

        jDateChooserDrugiDatum.setDateFormatString("dd.MM.yyyy.");
        jDateChooserDrugiDatum.setNextFocusableComponent(jButtonPretrazi);

        jButtonPretrazi.setText("Pretrazi");
        jButtonPretrazi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPretraziActionPerformed(evt);
            }
        });

        jLabel1.setText("od:");

        jLabel2.setText("do:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(14, 14, 14)
                .addComponent(jDateChooserPrviDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDateChooserDrugiDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonPretrazi)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jDateChooserPrviDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooserDrugiDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPretrazi))
                .addContainerGap(177, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(38, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGap(338, 338, 338)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTxtFieldUkupnaKvota, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTxtFieldUplata, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabelUplata, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelUkupnaKvota, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelPotencijalniDobitak, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(jTxtFieldPotencijalniDobitak, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(530, 530, 530)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jBtnZapamti, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jBtnObradi, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabelUplataProvera, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtnObrisi, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(jBtnKreirajNoviTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(140, 140, 140)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabelUplatnoMesto, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                            .addComponent(jLabelRadnik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTxtFieldUplatnoMesto, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                                            .addComponent(jTxtFieldRadnik)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabelSifraUtakmice, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTxtFieldSifraUtakmice, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabelTip, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBoxTip, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jBtnUbaci, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelUplatnoMesto, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxtFieldUplatnoMesto, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelRadnik, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxtFieldRadnik, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jBtnKreirajNoviTiket, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(106, 106, 106)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelSifraUtakmice, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxtFieldSifraUtakmice, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelTip, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxTip, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBtnUbaci, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnObrisi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 10, Short.MAX_VALUE)
                        .addComponent(jLabelUplataProvera, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtnZapamti, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTxtFieldUplata, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelUplata, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jBtnObradi, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelUkupnaKvota, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxtFieldUkupnaKvota, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelPotencijalniDobitak, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxtFieldPotencijalniDobitak, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnUbaciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnUbaciActionPerformed
        // TODO add your handling code here:

        ubaciUStavku();

    }//GEN-LAST:event_jBtnUbaciActionPerformed

    private StavkaTiketa kreirajStavkuIValidiraj(int redniBroj, Tip tip, Utakmica utakmica, String sifraUtakmice) throws Exception {
        int sifra;
        Date date = new Date();

        try {
            sifra = Integer.parseInt(sifraUtakmice);
            utakmica.setIdUtakmice(sifra);
        } catch (Exception e) {
            throw new Exception("Sifra utakmice mora biti broj");
        }

        utakmica = proveriUtakmicu(utakmica, date);
        proveriDaLiImaKvotaZaUtakmicu(utakmica, tip);

        return new StavkaTiketa(redniBroj, tip, utakmica);
    }


    private void jBtnKreirajNoviTiketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnKreirajNoviTiketActionPerformed
        kreirajNoviTiket();
    }//GEN-LAST:event_jBtnKreirajNoviTiketActionPerformed

    private void kreirajNoviTiket() {
        try {

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
            pripremiTabelu();
            srediFormuZaKreirajNoviTiket();
        } catch (IOException e) {
            prikaziFormuPrijava();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            ex.printStackTrace();
        }

    }

    private void srediFormuZaKreirajNoviTiket() {
        jTxtFieldSifraUtakmice.setText("");
        jTxtFieldUplata.setText("");
        jTxtFieldPotencijalniDobitak.setText("");
        jTxtFieldUkupnaKvota.setText("");

        jBtnUbaci.setEnabled(true);
        jBtnObrisi.setEnabled(true);
        jTxtFieldSifraUtakmice.setEditable(true);
        jTxtFieldUplata.setEditable(true);

        jLabelUplataProvera.setText("");
        jLabelPotencijalniDobitak.setText("");
        jBtnZapamti.setEnabled(false);
        jBtnObradi.setEnabled(false);

    }

    private void jBtnObrisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnObrisiActionPerformed
        // TODO add your handling code here:
        ModelTabeleStavkaTiketa model = (ModelTabeleStavkaTiketa) jTableStavkeTiketa.getModel();
        int row = jTableStavkeTiketa.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Morate izabrati utakmicu");
        } else {
            model.obrisiStavkuTiketa(jTableStavkeTiketa.getSelectedRow());

            Repozitorijum.tiket.getStavkeTiketa().remove(row);
        }

        for (int i = 0; i < Repozitorijum.tiket.getStavkeTiketa().size(); i++) {

            Repozitorijum.tiket.getStavkeTiketa().get(i).setRedniBroj(i + 1);
        }
        try {
            jTxtFieldUkupnaKvota.setText(Double.toString((Math.round((model.vratiUkupnuKvotu()) * 100.0) / 100.0)));
            jTxtFieldPotencijalniDobitak.setText(Double.toString((Math.round((Double.parseDouble(jTxtFieldUplata.getText()) * Double.parseDouble(jTxtFieldUkupnaKvota.getText())) * 100.0) / 100.0)));
        } catch (NumberFormatException nfe) {
            jTxtFieldUkupnaKvota.setText("");
            jTxtFieldPotencijalniDobitak.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            jTxtFieldUkupnaKvota.setText("");
            jTxtFieldPotencijalniDobitak.setText("");
        }
    }//GEN-LAST:event_jBtnObrisiActionPerformed

    private void jTxtFieldUplataCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTxtFieldUplataCaretUpdate
        // TODO add your handling code here:
        String uplataString = jTxtFieldUplata.getText();

        double uplataDouble = 0;

        try {
            uplataDouble = Double.parseDouble(uplataString);
            if (uplataDouble < 20) {
                jLabelUplataProvera.setText("Uplata je broj >=20");
                jBtnZapamti.setEnabled(false);
            } else {
                jLabelUplataProvera.setText("");
                jTxtFieldPotencijalniDobitak.setText(Double.toString(Math.round((uplataDouble * Double.parseDouble(jTxtFieldUkupnaKvota.getText())) * 100.0) / 100.0));
                jBtnZapamti.setEnabled(true);
            }
        } catch (Exception e) {
            jLabelUplataProvera.setText("Uplata je broj >=20");
            jBtnZapamti.setEnabled(false);
        }


    }//GEN-LAST:event_jTxtFieldUplataCaretUpdate

    private void jBtnZapamtiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnZapamtiActionPerformed
        zapamtiTiket();

    }//GEN-LAST:event_jBtnZapamtiActionPerformed
    private void zapamtiTiket() {
        // TODO add your handling code here:

        try {
            Repozitorijum.tiket.getStavkeTiketa().get(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Tiket je prazan, morate dodati utakmicu");
            return;
        }
        if (jTxtFieldUplata.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Uplata mora biti broj koji nije manji od 20");
            return;
        }

        Date date = new Date();

        for (StavkaTiketa sk : Repozitorijum.tiket.getStavkeTiketa()) {
            if ((sk.getUtakmica().getPocetak().getTime() - 1800000) <= date.getTime()) {
                JOptionPane.showMessageDialog(null, "Utakmica" + sk.getUtakmica().getDomacin() + "-" + sk.getUtakmica().getGost() + " je vec pocela");
                return;
            }
            sk.setTiket(Repozitorijum.tiket);
        }

        UplatnoMesto um = Repozitorijum.ulogovaniRadnik.getUplatnoMesto();
        popuniTiket(um, date);

        Tiket tiketIzRepozitorijuma = Repozitorijum.tiket;

        try {
            zapamtiTiketUBazu();
            generisiPdfTiket();
            pripremiFormuPosleZapamtiTiket();
            postaviTimerZaObradi(tiketIzRepozitorijuma);

        } catch (IOException eio) {
            prikaziFormuPrijava();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }
    private void jBtnObradiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnObradiActionPerformed
        obradiTiket();
    }//GEN-LAST:event_jBtnObradiActionPerformed
    private void obradiTiket() {
        try {
            // TODO add your handling code here:
            KlijentTransferObjekat zahtev6 = new KlijentTransferObjekat();
            zahtev6.setOperacija(KlijentTransferObjekat.OBRADI_TIKET);
            zahtev6.setObjekatOperacije(Repozitorijum.tiket);
            KontrolerKI.vratiInstancu().posaljiZahtev(zahtev6);
            ServerTransferObjekat odgovor6 = KontrolerKI.vratiInstancu().procitajOdgovor();
            if (odgovor6.getSignal() == -1) {
                throw new Exception(odgovor6.getPoruka());
            }

            JOptionPane.showMessageDialog(null, odgovor6.getPoruka());
            jBtnObradi.setEnabled(false);
        } catch (IOException eio) {
            prikaziFormuPrijava();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }
    private void jTxtFieldSifraUtakmiceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTxtFieldSifraUtakmiceFocusGained
        // kad ga prebacim sa btnUbaci da mi selektuje sve

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jTxtFieldSifraUtakmice.selectAll();
            }
        });

    }//GEN-LAST:event_jTxtFieldSifraUtakmiceFocusGained

    private void jButtonPretraziActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPretraziActionPerformed

        pretrazi();
    }//GEN-LAST:event_jButtonPretraziActionPerformed
    private void pretrazi() {

        try {

            if (jDateChooserPrviDatum.getDate() == null || jDateChooserDrugiDatum.getDate() == null) {
                throw new Exception("Morate uneti oba datuma!");

            }

            HashMap<String, Date> hmap = new HashMap<>();
            hmap.put("prviDatum", (vratiDatumSaNulom(jDateChooserPrviDatum.getDate())));
            hmap.put("drugiDatum", (vratiDatumKraj(jDateChooserDrugiDatum.getDate())));

            KlijentTransferObjekat zahtev12 = new KlijentTransferObjekat();
            zahtev12.setOperacija(KlijentTransferObjekat.VRATI_UTAKMICE);
            zahtev12.setObjekatOperacije(hmap);
            KontrolerKI.vratiInstancu().posaljiZahtev(zahtev12);
            ServerTransferObjekat odgovor12 = KontrolerKI.vratiInstancu().procitajOdgovor();
            if (odgovor12.getSignal() == -1) {
                throw new Exception(odgovor12.getPoruka());
            }
            List<Utakmica> utakmicee = (List<Utakmica>) odgovor12.getObjekatIzvrsenjaOperacije();

            ModelTabeleUtakmice model = (ModelTabeleUtakmice) jTableUtakmice.getModel();
            model.postaviListu(new ArrayList<>());
            System.out.println("nakon vracanja size utakmice je: " + utakmicee.size());
            if (utakmicee.size() == 0) {
                model.obrisiSveUtakmice();
                return;
            }

            for (Utakmica ut : utakmicee) {
                model.ubaciUtakmicu(ut);
            }

        } catch (IOException eio) {
            prikaziFormuPrijava();
        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnKreirajNoviTiket;
    private javax.swing.JButton jBtnObradi;
    private javax.swing.JButton jBtnObrisi;
    private javax.swing.JButton jBtnUbaci;
    private javax.swing.JButton jBtnZapamti;
    private javax.swing.JButton jButtonPretrazi;
    private javax.swing.JComboBox<Object> jComboBoxTip;
    private com.toedter.calendar.JDateChooser jDateChooserDrugiDatum;
    private com.toedter.calendar.JDateChooser jDateChooserPrviDatum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelPotencijalniDobitak;
    private javax.swing.JLabel jLabelRadnik;
    private javax.swing.JLabel jLabelSifraUtakmice;
    private javax.swing.JLabel jLabelTip;
    private javax.swing.JLabel jLabelUkupnaKvota;
    private javax.swing.JLabel jLabelUplata;
    private javax.swing.JLabel jLabelUplataProvera;
    private javax.swing.JLabel jLabelUplatnoMesto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableStavkeTiketa;
    private javax.swing.JTable jTableUtakmice;
    private javax.swing.JTextField jTxtFieldPotencijalniDobitak;
    private javax.swing.JTextField jTxtFieldRadnik;
    private javax.swing.JTextField jTxtFieldSifraUtakmice;
    private javax.swing.JTextField jTxtFieldUkupnaKvota;
    private javax.swing.JTextField jTxtFieldUplata;
    private javax.swing.JTextField jTxtFieldUplatnoMesto;
    // End of variables declaration//GEN-END:variables

    private void listenerOnEnterKreirajNoviTiket() {
        jBtnKreirajNoviTiket.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    kreirajNoviTiket();
                }

            }
        });
    }

    private void listenerOnEnterZapamti() {
        jBtnZapamti.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    zapamtiTiket();
                }

            }
        });
    }

    private void listenerOnEnterObradi() {
        jBtnObradi.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    obradiTiket();
                }

            }
        });
    }

    private void popuniTiket(UplatnoMesto um, Date date) {
        Repozitorijum.tiket.setVremeUplate(date);
        Repozitorijum.tiket.setUplata(Double.parseDouble(jTxtFieldUplata.getText()));
        Repozitorijum.tiket.setUkupnaKvota(Double.parseDouble(jTxtFieldUkupnaKvota.getText()));
        Repozitorijum.tiket.setDobitak(Double.parseDouble(jTxtFieldPotencijalniDobitak.getText()));
        Repozitorijum.tiket.setObradjen(false);
        Repozitorijum.tiket.setStorniran(false);
        Repozitorijum.tiket.setStatus("aktivan");
        Repozitorijum.tiket.setUplatnoMesto(um);
        Repozitorijum.tiket.setRadnik(Repozitorijum.ulogovaniRadnik);
    }

    private void pripremiFormuPosleZapamtiTiket() {
        jBtnZapamti.setEnabled(false);
        jBtnUbaci.setEnabled(false);
        jBtnObrisi.setEnabled(false);
        jTxtFieldSifraUtakmice.setEditable(false);
        jTxtFieldUplata.setEditable(false);

        jBtnObradi.setEnabled(true);
    }

    private void postaviTimerZaObradi(Tiket tiketIzRepozitorijuma) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {

                    KlijentTransferObjekat zahtev5 = new KlijentTransferObjekat();
                    zahtev5.setOperacija(KlijentTransferObjekat.OBRADI_TIKET);
                    zahtev5.setObjekatOperacije(tiketIzRepozitorijuma);
                    KontrolerKI.vratiInstancu().posaljiZahtev(zahtev5);
                    ServerTransferObjekat odgovor5 = KontrolerKI.vratiInstancu().procitajOdgovor();
                    if (odgovor5.getSignal() == -1) {
                        throw new Exception(odgovor5.getPoruka());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        timer.schedule(task, 1800000);
    }

    private void proveriDaLiImaKvotaZaUtakmicu(Utakmica utakmica, Tip tip) throws IOException, ClassNotFoundException, Exception {

        List<Kvota> listaKvotaZaUtakmicu = utakmica.getListaKvota();
        for (Kvota kvota : listaKvotaZaUtakmicu) {
            if (kvota.getTip().getIdTipa() == tip.getIdTipa()) {
                if (kvota.getBrojKvote() == 0) {
                    throw new Exception("Ne postoji kvota za utakmicu: " + utakmica.getDomacin() + " - " + utakmica.getGost() + " i tip: " + tip.getNazivTipa());
                }
            }
        }
    }

    private Utakmica proveriUtakmicu(Utakmica utakmica, Date date) throws IOException, ClassNotFoundException, Exception {
        KlijentTransferObjekat zahtev2 = new KlijentTransferObjekat();
        zahtev2.setOperacija(KlijentTransferObjekat.VRATI_UTAKMICE);
        zahtev2.setObjekatOperacije(utakmica);
        KontrolerKI.vratiInstancu().posaljiZahtev(zahtev2);
        ServerTransferObjekat odgovor2 = KontrolerKI.vratiInstancu().procitajOdgovor();
        if (odgovor2.getSignal() == -1) {
            throw new Exception(odgovor2.getPoruka());
        }
        List<Utakmica> utakmice = (List<Utakmica>) odgovor2.getObjekatIzvrsenjaOperacije();

        if (utakmice.size() == 0 || utakmice.get(0).getPocetak() == null) {
            throw new Exception("Ne postoji utakmica sa sifrom:" + utakmica.getIdUtakmice());
        } else {
            utakmica = utakmice.get(0);
        }

        for (StavkaTiketa s : Repozitorijum.tiket.getStavkeTiketa()) {
            System.out.println("u repoz tiketi size: " + Repozitorijum.tiket.getStavkeTiketa().size());
            if (s.getUtakmica().getIdUtakmice() == utakmica.getIdUtakmice()) {
                throw new Exception("Utakmica " + utakmica.getDomacin() + "-" + utakmica.getGost() + " je vec dodata na tiketu");
            }
        }

        if ((utakmica.getPocetak().getTime() - 1800000) <= date.getTime()) {
            throw new Exception("Utakmica " + utakmica.getDomacin() + "-" + utakmica.getGost() + " je pocela");
        }
        return utakmica;
    }

    private void zapamtiTiketUBazu() throws IOException, ClassNotFoundException, Exception {
        KlijentTransferObjekat zahtev4 = new KlijentTransferObjekat();
        zahtev4.setOperacija(KlijentTransferObjekat.ZAPAMTI_TIKET_I_STAVKE_TIKETA);
        zahtev4.setObjekatOperacije(Repozitorijum.tiket);
        KontrolerKI.vratiInstancu().posaljiZahtev(zahtev4);
        ServerTransferObjekat odgovor4 = KontrolerKI.vratiInstancu().procitajOdgovor();
        if (odgovor4.getSignal() == -1) {
            throw new Exception(odgovor4.getPoruka());
        }

        JOptionPane.showMessageDialog(null, odgovor4.getPoruka());
    }

    private void generisiPdfTiket() {
        pdf.Pdf.generisiPdfTiket(Repozitorijum.tiket);
    }

}
