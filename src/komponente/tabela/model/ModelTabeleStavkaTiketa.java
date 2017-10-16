/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komponente.tabela.model;

import domen.Kvota;
import domen.StavkaTiketa;
import domen.Tiket;
import domen.Tip;
import domen.Utakmica;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import komunikacija.KontrolerKI;

import repozitorijum.Repozitorijum;
import transfer.ServerTransferObjekat;
import transfer.KlijentTransferObjekat;

/**
 *
 * @author KORISNIK
 */
public class ModelTabeleStavkaTiketa extends AbstractTableModel {

    List<StavkaTiketa> stavkeTiketa = Repozitorijum.tiket.getStavkeTiketa();

    String[] kolone = {"r.b.", "pocetak", "domacin", "gost", "tip", "kvota"};

    public ModelTabeleStavkaTiketa(List<StavkaTiketa> stavkeTiketa) {
        this.stavkeTiketa = stavkeTiketa;
    }

    @Override
    public int getRowCount() {
        if (stavkeTiketa == null) {
            return 0;
        }
        return stavkeTiketa.size();
    }

    @Override
    public int getColumnCount() {
        return kolone.length;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        StavkaTiketa sk = stavkeTiketa.get(rowIndex);

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        StavkaTiketa sk = stavkeTiketa.get(rowIndex);
        Calendar cal = Calendar.getInstance();
        String dayString = "";

        cal.setTime(sk.getUtakmica().getPocetak());

        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        switch (day) {
            case 2:
                dayString = "pon";
                break;
            case 3:
                dayString = "uto";
                break;
            case 4:
                dayString = "sre";
                break;
            case 5:
                dayString = "cet";
                break;
            case 6:
                dayString = "pet";
                break;
            case 7:
                dayString = "sub";
                break;
            case 1:
                dayString = "ned";
                break;
            default:
                dayString = "n/a";
        }

        switch (columnIndex) {
            case 0:
                return sk.getRedniBroj();

            case 1:
                return dayString + " " + hour + ":" + min;
            case 2:
                return sk.getUtakmica().getDomacin();
            case 3:
                return sk.getUtakmica().getGost();
            case 4:
                return sk.getTip().getNazivTipa();
            case 5:
                return vratiKvotu(sk);

            default:
                return "n/a";

        }

    }

    public void obrisiStavkuTiketa(int rowIndex) {
        stavkeTiketa.remove(rowIndex);
        fireTableDataChanged();

    }

    public void ubaciStavkuTiketa(StavkaTiketa sk) {
        stavkeTiketa.add(sk);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return kolone[column];
    }

    public double vratiUkupnuKvotu() throws ClassNotFoundException, SQLException, IOException, Exception {
        // double ukupnaKvota = 1;
        double pomocna = 1;
        for (StavkaTiketa sk : stavkeTiketa) {

            double kvota = 1;
            List<Kvota> listaKvotaZaUtakmicu = sk.getUtakmica().getListaKvota();
            for (Kvota kvotaa : listaKvotaZaUtakmicu) {
                if (kvotaa.getTip().getIdTipa() == sk.getTip().getIdTipa()) {
                    kvota = kvotaa.getBrojKvote();
                }
            }

            pomocna = pomocna * kvota;
        }
        return pomocna;
    }

    private Object vratiKvotu(StavkaTiketa sk) {
        try {
            List<Kvota> listaKvotaZaUtakmicu = (List<Kvota>) sk.getUtakmica().getListaKvota();
            for (Kvota kvota : listaKvotaZaUtakmicu) {
                if (kvota.getTip().getIdTipa() == sk.getTip().getIdTipa()) {
                    return kvota.getBrojKvote();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;
    }

}
