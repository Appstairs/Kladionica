/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komponente.tabela.model;

import domen.Radnik;
import domen.Utakmica;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author KORISNIK
 */
public class ModelTabeleRadnici extends AbstractTableModel {

    List<Radnik> radnici;

    String[] kolone = {"id", "ime", "prezime", "korisnicko ime", "status", "uplatno mesto"};

    public ModelTabeleRadnici(List<Radnik> radnici) {
        this.radnici = radnici;
    }

    @Override
    public int getRowCount() {
        if (radnici == null) {
            return 0;
        }
        return radnici.size();
    }

    @Override
    public int getColumnCount() {
        return kolone.length;
    }

    @Override
    public String getColumnName(int column) {
        return kolone[column];
    }

    public void ubaciRadnika(Radnik r) {

        radnici.add(r);
        fireTableDataChanged();
    }

    public void izbaciRadnika(int rowIndex) {
        radnici.remove(rowIndex);

        fireTableDataChanged();

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Radnik r = radnici.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return r.getIdRadnika();
            case 1:
                return r.getIme();
            case 2:
                return r.getPrezime();
            case 3:
                return r.getKorisnickoIme();
            case 4:
                return r.getStatus();
            case 5:
                return r.getUplatnoMesto().toString();
            default:
                return "n/a";
        }
    }

    public void obrisiSveRadnike() {

        radnici = new ArrayList<>();
        fireTableDataChanged();

    }

    public Radnik vratiRadnika(int getRowIndex) {
        return radnici.get(getRowIndex);
    }

    public void obrisiRadnika(int rowIndex) {
        radnici.remove(rowIndex);

        fireTableDataChanged();

    }

}
