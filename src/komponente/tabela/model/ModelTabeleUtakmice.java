/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komponente.tabela.model;

import domen.Utakmica;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author KORISNIK
 */
public class ModelTabeleUtakmice extends AbstractTableModel {

    List<Utakmica> utakmice;

    String[] kolone = {"id", "domacin", "gost", "pocetak"};

    public ModelTabeleUtakmice(List<Utakmica> utakmice) {
        this.utakmice = utakmice;
    }

    public List<Utakmica> vratiListuUtakmica() {

        return utakmice;
    }

    public void postaviListu(List<Utakmica> lista) {
        utakmice = lista;
    }

    @Override
    public int getRowCount() {
        if (utakmice == null) {
            return 0;
        }
        return utakmice.size();
    }

    @Override
    public int getColumnCount() {
        return kolone.length;
    }

    @Override
    public String getColumnName(int column) {
        return kolone[column];
    }

    public void ubaciUtakmicu(Utakmica u) {

        utakmice.add(u);
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Utakmica u = utakmice.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return u.getIdUtakmice();
            case 1:
                return u.getDomacin();
            case 2:
                return u.getGost();
            case 3:
                return u.getPocetak();
            default:
                return "n/a";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Utakmica u = utakmice.get(rowIndex);
        switch (columnIndex) {
            case 0:
                u.setIdUtakmice((Integer) aValue);
                break;

        }
    }

    public void obrisiSveUtakmice() {

        for (int i = 0; i < utakmice.size(); i++) {
            utakmice.remove(i);
        }
        fireTableDataChanged();

    }

}
