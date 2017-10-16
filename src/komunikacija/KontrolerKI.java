/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikacija;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import transfer.ServerTransferObjekat;
import transfer.KlijentTransferObjekat;

/**
 *
 * @author student
 */
public class KontrolerKI {

    Socket vezaSaServerom;
    ObjectOutputStream outKaServeru;
    ObjectInputStream inOdServera;

    private static KontrolerKI instanca;

    private Properties configProperty;

    private KontrolerKI() throws IOException {
        ucitajPodatke();
        vezaSaServerom = new Socket(configProperty.getProperty("idAdresa"), Integer.parseInt(configProperty.getProperty("port")));
        outKaServeru = new ObjectOutputStream(vezaSaServerom.getOutputStream());
        inOdServera = new ObjectInputStream(vezaSaServerom.getInputStream());
    }

    public static KontrolerKI vratiInstancu() throws IOException {
        if (instanca == null) {
            instanca = new KontrolerKI();
        }
        return instanca;
    }

    public void posaljiZahtev(KlijentTransferObjekat zahtev) throws IOException {

        outKaServeru.writeObject(zahtev);
    }

    public ServerTransferObjekat procitajOdgovor() throws IOException, ClassNotFoundException {

        return (ServerTransferObjekat) inOdServera.readObject();

    }

    public static void setInstanca(KontrolerKI instanca) {
        KontrolerKI.instanca = instanca;
    }

    private void ucitajPodatke() throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream("config.properties");
        configProperty = new Properties();
        configProperty.load(fis);
    }

}
