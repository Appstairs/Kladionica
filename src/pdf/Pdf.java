/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf;

/**
 *
 * @author KORISNIK
 */
import java.io.FileOutputStream;
import java.util.Date;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import domen.Kvota;
import domen.StavkaTiketa;
import domen.Tiket;
import domen.Utakmica;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import sun.swing.SwingUtilities2;

public class Pdf {

    private static String FILE;
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    public static void generisiPdfListu(java.util.List<Utakmica> listaUtakmica, Date prviDatum, Date drugiDatum, Date trenutniDatum) {
        try {
            FILE = "pdf/lista/lista.pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE, true));
            writer.setPageEvent(new HeaderFooterPageEvent());
            document.open();

            addTitlePage(document, listaUtakmica, prviDatum, drugiDatum, trenutniDatum);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generisiPdfTiket(Tiket tiket) {
        try {
            FILE = "pdf/tiketi/tiket" + tiket.getIdTiketa() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();
            dodajUvodTiketa(document);
            addEmptyLine(new Paragraph(), 2);
            dodajPrazanParagraf(document);
            dodajSadrzajTiket(tiket, document);
            dodajPrazanParagraf(document);
            addEmptyLine(new Paragraph(), 2);
            dodajKrajTiketa(document);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void addTitlePage(Document document, java.util.List<Utakmica> listaUtakmica, Date prviDatum, Date drugiDatum, Date trenutniDatum)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Calendar cal3 = Calendar.getInstance();
        cal1.setTime(prviDatum);
        cal2.setTime(drugiDatum);
        preface.add(new Paragraph("Lista utakmica za period od " + cal1.get(cal1.DAY_OF_MONTH) + "." + cal1.get(cal1.MONTH) + "." + cal1.get(cal1.YEAR) + ". do " + cal2.get(cal2.DAY_OF_MONTH) + "." + cal2.get(cal2.MONTH) + "." + cal2.get(cal2.YEAR) + ". generisana je " + cal3.get(cal3.DAY_OF_MONTH) + "." + cal3.get(cal3.MONTH) + "." + cal3.get(cal3.YEAR) + ". u " + cal3.get(cal3.HOUR_OF_DAY) + ":" + cal3.get(cal3.MINUTE), catFont));

        addEmptyLine(preface, 1);
        Anchor anchor = new Anchor("", catFont);
        anchor.setName("");

        document.add(preface);
        document.add(createTable(listaUtakmica));

    }

    private static void dodajSadrzajTiket(Tiket tiket, Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        preface.add(new Paragraph("Kladionica", catFont));
        preface.setAlignment(-1);
        preface.setAlignment(Element.ALIGN_CENTER);
        Anchor anchor = new Anchor("", catFont);
        anchor.setName("");
        document.add(createTableTiketOpstiPodaci(tiket));
        addEmptyLine(preface, 1);
        dodajPrazanParagraf(document);
        document.add(createTableTiketUtakmice(tiket));
        addEmptyLine(preface, 1);
        dodajPrazanParagraf(document);
        document.add(createTableTiketUplata(tiket));
    }

    private static void addContent(Document document) throws DocumentException {
        Anchor anchor = new Anchor("", catFont);
        anchor.setName("");
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);
        Paragraph subPara = new Paragraph("", subFont);
        Section subCatPart = catPart.addSection(subPara);
        document.add(catPart);
    }

    private static PdfPTable createTable(java.util.List<Utakmica> listaUtakmica)
            throws BadElementException, DocumentException {
        PdfPTable table = new PdfPTable(10);
        BaseColor plava = WebColors.getRGBColor("#7ba9f2");
        BaseColor crvena = WebColors.getRGBColor("#f2353e");
        BaseColor zelena = WebColors.getRGBColor("#71d642");
        table = napraviHeader(table, plava, crvena, zelena);
        table = popuniTabelu(table, listaUtakmica, plava, crvena, zelena);
        table.setWidthPercentage(100);
        //koliko ce koja kolona da bude siroka
        table.setTotalWidth(new float[]{8, 8, 20, 3, 20, 5, 5, 5, 5, 5});

        return table;

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static PdfPTable napraviHeader(PdfPTable table, BaseColor plava, BaseColor crvena, BaseColor zelena) {
        PdfPCell c1 = new PdfPCell(new Phrase("pocetak"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(zelena);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("sifra"));
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase("domacin"));
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
        c3.setBackgroundColor(plava);
        table.addCell(c3);

        PdfPCell c4 = new PdfPCell(new Phrase(":"));
        c4.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c4);

        PdfPCell c5 = new PdfPCell(new Phrase("gost"));
        c5.setHorizontalAlignment(Element.ALIGN_CENTER);
        c5.setBackgroundColor(plava);
        table.addCell(c5);

        PdfPCell c6 = new PdfPCell(new Phrase("1"));
        c6.setHorizontalAlignment(Element.ALIGN_CENTER);
        c6.setBackgroundColor(crvena);
        table.addCell(c6);

        PdfPCell c7 = new PdfPCell(new Phrase("X"));
        c7.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c7);

        PdfPCell c8 = new PdfPCell(new Phrase("2"));
        c8.setHorizontalAlignment(Element.ALIGN_CENTER);
        c8.setBackgroundColor(crvena);
        table.addCell(c8);

        PdfPCell c9 = new PdfPCell(new Phrase("0-2"));
        c9.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c9);

        PdfPCell c10 = new PdfPCell(new Phrase("3+"));
        c10.setHorizontalAlignment(Element.ALIGN_CENTER);
        c10.setBackgroundColor(crvena);
        table.addCell(c10);

        table.setHeaderRows(1);
        return table;
    }

    private static PdfPTable popuniTabelu(PdfPTable table, java.util.List<Utakmica> listaUtakmica, BaseColor plava, BaseColor crvena, BaseColor zelena) {

        for (int i = 0; i < listaUtakmica.size(); i++) {
            Calendar cal = Calendar.getInstance();
            String dayString = "";

            cal.setTime(listaUtakmica.get(i).getPocetak());

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

            PdfPCell c11 = new PdfPCell(new Phrase(dayString + " " + hour + ":" + min));
            c11.setHorizontalAlignment(Element.ALIGN_CENTER);
            c11.setBackgroundColor(zelena);
            table.addCell(c11);

            PdfPCell c22 = new PdfPCell(new Phrase(Integer.toString(listaUtakmica.get(i).getIdUtakmice())));
            c22.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c22);

            PdfPCell c33 = new PdfPCell(new Phrase(listaUtakmica.get(i).getDomacin()));
            c33.setHorizontalAlignment(Element.ALIGN_CENTER);
            c33.setBackgroundColor(plava);
            table.addCell(c33);

            PdfPCell c44 = new PdfPCell(new Phrase(":"));
            c44.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c44);

            PdfPCell c55 = new PdfPCell(new Phrase(listaUtakmica.get(i).getGost()));
            c55.setHorizontalAlignment(Element.ALIGN_CENTER);
            c55.setBackgroundColor(plava);
            table.addCell(c55);

            PdfPCell c66 = null;
            PdfPCell c77 = null;
            PdfPCell c88 = null;
            PdfPCell c99 = null;
            PdfPCell c1010 = null;
            for (Kvota kvota : listaUtakmica.get(i).getListaKvota()) {

                if (kvota.getTip().getNazivTipa().equals("1")) {
                    c66 = new PdfPCell(new Phrase(Double.toString(kvota.getBrojKvote())));
                    c66.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c66.setBackgroundColor(crvena);

                } else if (kvota.getTip().getNazivTipa().equals("x")) {

                    c77 = new PdfPCell(new Phrase(Double.toString(kvota.getBrojKvote())));
                    c77.setHorizontalAlignment(Element.ALIGN_CENTER);

                } else if (kvota.getTip().getNazivTipa().equals("2")) {
                    c88 = new PdfPCell(new Phrase(Double.toString(kvota.getBrojKvote())));
                    c88.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c88.setBackgroundColor(crvena);

                } else if (kvota.getTip().getNazivTipa().equals("0-2")) {
                    c99 = new PdfPCell(new Phrase(Double.toString(kvota.getBrojKvote())));
                    c99.setHorizontalAlignment(Element.ALIGN_CENTER);

                } else if (kvota.getTip().getNazivTipa().equals("3+")) {

                    c1010 = new PdfPCell(new Phrase(Double.toString(kvota.getBrojKvote())));
                    c1010.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c1010.setBackgroundColor(crvena);

                }

            }

            table.addCell(c66);
            table.addCell(c77);
            table.addCell(c88);
            table.addCell(c99);
            table.addCell(c1010);

        }
        return table;
    }

    private static Element createTableTiketOpstiPodaci(Tiket tiket) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table = napraviHeaderTiket(tiket, table);
        table.setWidthPercentage(100);
        table.setTotalWidth(new float[]{40, 40, 40});
        return table;
    }

    private static PdfPTable napraviHeaderTiket(Tiket tiket, PdfPTable table) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(tiket.getVremeUplate());

        PdfPCell c1 = new PdfPCell(new Phrase("ID tiketa: " + tiket.getIdTiketa()));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("Vreme uplate: " + cal1.get(cal1.DAY_OF_MONTH) + "/" + cal1.get(cal1.MONTH) + "/" + cal1.get(cal1.YEAR) + " " + cal1.get(cal1.HOUR_OF_DAY) + ":" + cal1.get(cal1.MINUTE) + ":" + cal1.get(cal1.SECOND)));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase("Radnik: " + tiket.getRadnik().getKorisnickoIme()));
        c3.setBorder(Rectangle.NO_BORDER);
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c3);

        return table;
    }

    private static PdfPTable dodajUtakmice(Tiket tiket, PdfPTable table) {
        for (int i = 0; i < tiket.getStavkeTiketa().size(); i++) {
            Calendar cal = Calendar.getInstance();
            String dayString = "";

            cal.setTime(tiket.getStavkeTiketa().get(i).getUtakmica().getPocetak());

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

            PdfPCell c11 = new PdfPCell(new Phrase(dayString + " " + hour + ":" + min));
            c11.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c11);

            PdfPCell c22 = new PdfPCell(new Phrase(Integer.toString(tiket.getStavkeTiketa().get(i).getUtakmica().getIdUtakmice())));
            c22.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c22);

            PdfPCell c33 = new PdfPCell(new Phrase(tiket.getStavkeTiketa().get(i).getUtakmica().getDomacin()));
            c33.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c33);

            PdfPCell c44 = new PdfPCell(new Phrase(":"));
            c44.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c44);

            PdfPCell c55 = new PdfPCell(new Phrase(tiket.getStavkeTiketa().get(i).getUtakmica().getGost()));
            c55.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c55);

            PdfPCell c66 = new PdfPCell(new Phrase(tiket.getStavkeTiketa().get(i).getTip().getNazivTipa()));
            c55.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c66);

            PdfPCell c77 = new PdfPCell(new Phrase(Double.toString(vratiKvotu(tiket.getStavkeTiketa().get(i)))));
            c55.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c77);

        }
        return table;
    }

    private static double vratiKvotu(StavkaTiketa sk) {
        try {

            java.util.List<Kvota> listaKvotaZaUtakmicu = (java.util.List<Kvota>) sk.getUtakmica().getListaKvota();
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

    private static Element createTableTiketUtakmice(Tiket tiket) throws DocumentException {
        PdfPTable table = new PdfPTable(7);

        table = napraviHeaderTiketUtakmice(table);
        table = dodajUtakmice(tiket, table);
        table.setWidthPercentage(80);
        table.setTotalWidth(new float[]{15, 7, 20, 5, 20, 9, 9});
        return table;
    }

    private static Element createTableTiketUplata(Tiket tiket) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table = dodajUplatu(tiket, table);
        table.setWidthPercentage(100);
        table.setTotalWidth(new float[]{40, 40, 40});
        return table;
    }

    private static PdfPTable dodajUplatu(Tiket tiket, PdfPTable table) {
        PdfPCell c11 = new PdfPCell(new Phrase("Uplata: " + tiket.getUplata()));
        c11.setBorder(Rectangle.NO_BORDER);
        c11.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c11);

        PdfPCell c22 = new PdfPCell(new Phrase("Ukupna kvota: " + tiket.getUkupnaKvota()));
        c22.setBorder(Rectangle.NO_BORDER);
        c22.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c22);

        PdfPCell c33 = new PdfPCell(new Phrase("Potencijalni dobitak: " + tiket.getDobitak()));
        c33.setBorder(Rectangle.NO_BORDER);
        c22.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c33);

        return table;
    }

    private static PdfPTable napraviHeaderTiketUtakmice(PdfPTable table) {
        PdfPCell c1 = new PdfPCell(new Phrase("pocetak"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("sifra"));
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase("domacin"));
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c3);

        PdfPCell c4 = new PdfPCell(new Phrase(":"));
        c4.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c4);

        PdfPCell c5 = new PdfPCell(new Phrase("gost"));
        c5.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c5);

        PdfPCell c6 = new PdfPCell(new Phrase("tip"));
        c6.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c6);

        PdfPCell c7 = new PdfPCell(new Phrase("kvota"));
        c7.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c7);

        table.setHeaderRows(1);
        return table;
    }

    private static void dodajUvodTiketa(Document document) throws DocumentException {
        Paragraph preface = new Paragraph("Kladionica", catFont);
        preface.setAlignment(-1);
        preface.setAlignment(Element.ALIGN_CENTER);
        document.add(preface);
    }

    private static void dodajKrajTiketa(Document document) throws DocumentException {
        Paragraph p = new Paragraph("Osvoji BMV, pogodi najvecu kvotu", catFont);
        p.setAlignment(-1);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
    }

    private static void dodajPrazanParagraf(Document document) throws DocumentException {
        document.add(new Paragraph("\n"));
    }

}
