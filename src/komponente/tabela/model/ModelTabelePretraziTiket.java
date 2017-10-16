/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komponente.tabela.model;

import domen.Kvota;
import domen.StavkaTiketa;
import domen.Tiket;
import domen.Utakmica;
import java.awt.Color;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import komunikacija.KontrolerKI;
import transfer.ServerTransferObjekat;
import transfer.KlijentTransferObjekat;

/**
 *
 * @author KORISNIK
 */
public class ModelTabelePretraziTiket extends AbstractTableModel {

    Tiket pronadjeniTiket;

    /**
     * @return the pronadjeniTiket
     */
    public Tiket getPronadjeniTiket() {
        return pronadjeniTiket;
    }

    /**
     * @param pronadjeniTiket the pronadjeniTiket to set
     */
    public void setPronadjeniTiket(Tiket pronadjeniTiket) {
        this.pronadjeniTiket = pronadjeniTiket;
    }

    String[] kolone = {"r.b.", "pocetak", "domacin", "gost", "tip", "kvota", "rezultat"};

    public ModelTabelePretraziTiket(Tiket pronadjeniTiket) {
        this.pronadjeniTiket = pronadjeniTiket;
    }

    @Override
    public int getRowCount() {
        if (getPronadjeniTiket().getStavkeTiketa() == null) {
            return 0;
        }
        return getPronadjeniTiket().getStavkeTiketa().size();
    }

    @Override
    public int getColumnCount() {
        return kolone.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        StavkaTiketa sk = getPronadjeniTiket().getStavkeTiketa().get(rowIndex);
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
                return vratiKvotuZaStavku(sk);

            case 6:
                return vratiRezultat(sk);

            default:
                return "n/a";

        }

    }

    @Override
    public String getColumnName(int column) {
        return kolone[column];
    }

    public void ubaciStavkuTiketa(StavkaTiketa sk) {

        getPronadjeniTiket().getStavkeTiketa().add(sk);
        fireTableDataChanged();
    }

    public void obrisiStavkuTiketa(int rowIndex) {
        getPronadjeniTiket().getStavkeTiketa().remove(rowIndex);

        fireTableDataChanged();

    }

    private Double vratiKvotuZaStavku(StavkaTiketa sk) {
        try {
            KlijentTransferObjekat zahtev2 = new KlijentTransferObjekat();
            zahtev2.setOperacija(KlijentTransferObjekat.VRATI_UTAKMICE);
            zahtev2.setObjekatOperacije(sk.getUtakmica());
            KontrolerKI.vratiInstancu().posaljiZahtev(zahtev2);
            ServerTransferObjekat odgovor2 = KontrolerKI.vratiInstancu().procitajOdgovor();
            if (odgovor2.getSignal() == -1) {
                throw new Exception(odgovor2.getPoruka());
            }
            List<Utakmica> utakmice = (List<Utakmica>) odgovor2.getObjekatIzvrsenjaOperacije();

            sk.setUtakmica(utakmice.get(0));

            List<Kvota> listaKvotaZaUtakmicu = sk.getUtakmica().getListaKvota();
            System.out.println("utakmica je: " + sk.getUtakmica());
            System.out.println("size liste kvota: " + sk.getUtakmica().getListaKvota().size());
            for (Kvota kvota : listaKvotaZaUtakmicu) {
                if (kvota.getTip().getIdTipa() == sk.getTip().getIdTipa()) {
                    System.out.println("kvota za taj tip je: " + kvota.getBrojKvote());
                    return kvota.getBrojKvote();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;

    }

    private Object vratiRezultat(StavkaTiketa sk) {
        if (sk.getUtakmica().getPocetak().after(new Date())) {
            return "-:-";
        } else if (sk.getUtakmica().getGoloviDomacin() == -1) {
            return "-:-";
        } else {
            return sk.getUtakmica().getGoloviDomacin() + ":" + sk.getUtakmica().getGoloviGost();
        }
    }

}
