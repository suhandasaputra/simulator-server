package util;

//import client.ClientGUI;
import java.io.IOException;
import java.net.Socket;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

public class ISO8583MessageHandler {

    private final ISOPackager packager;
    private final ISO8583MessageUtil messageUtil;

    public ISO8583MessageHandler(ISOPackager packager) {
        this.packager = packager;
        this.messageUtil = new ISO8583MessageUtil(packager);
    }

    public void processISO8583Message(ISOMsg isoMessage, byte[] messageBytes, Socket socket) throws ISOException, IOException {
        if (isoMessage != null) {
            String isoMessageString = new String(messageBytes);
//            System.out.println();
//            System.out.println("Received ISO 8583 message: " + isoMessageString);

            // Print the received message fields
            System.out.println("Received Message Fields:");
            System.out.println("MTI: " + isoMessage.getMTI());
            for (int i = 1; i <= isoMessage.getMaxField(); i++) {
                if (isoMessage.hasField(i)) {
                    System.out.println("Field " + i + ": " + isoMessage.getString(i));
                }
            }

            switch (isoMessage.getMTI()) {
                case "0800":
                    isoMessage.setMTI("0810");
                    if (null != isoMessage.getString(70)) {
                        switch (isoMessage.getString(70)) {
                            case "001"://signon
                                isoMessage.set(39, "00");
                                break;
                            case "002"://signoff
                                isoMessage.set(39, "00");
                                break;
                            case "161"://changekey
                                isoMessage.set(39, "00");
                                break;
                            case "162"://newkey
                                isoMessage.set(39, "00");
                                break;
                            case "201"://cutoff
                                isoMessage.set(39, "00");
                                break;
                            case "301"://echo
                                isoMessage.set(39, "00");
                                break;
                            default:
                                break;
                        }
                    }
                    break;

                case "0200":
                    isoMessage.setMTI("0210");
                    String transaction_type_3_1 = isoMessage.getString(3).substring(0, 2);
//                    String account_3_2 = isoMessage.getString(3).substring(2, 2);
                    if (null != isoMessage.getString(3)) {
                        switch (transaction_type_3_1) {
                            case "01"://cashwithdrawal/reversal cashwithdrawal
                                isoMessage.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                                isoMessage.set(15, StringFunction.getCurrentDateMMDD());
                                isoMessage.unset(22);
                                isoMessage.set(38, "123456");
                                isoMessage.set(39, "00");
                                isoMessage.unset(43);
                                isoMessage.set(44, "2000000000000000480000000");
                                isoMessage.unset(48);
                                isoMessage.unset(52);
                                isoMessage.set(55, "910AF44FF04A78A3422B3030");
                                isoMessage.set(100, "10000000002");
                                isoMessage.set(102, "3010206012212");
                                break;
                            case "31"://balance inquiry
                                isoMessage.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                                isoMessage.set(15, StringFunction.getCurrentDateMMDD());
                                isoMessage.set(38, "009142");
                                isoMessage.set(39, "00");
                                isoMessage.unset(43);
                                isoMessage.set(44, "2000000000000000490000000");
                                isoMessage.unset(48);
                                isoMessage.unset(52);
                                isoMessage.set(55, "910A762408B33AE17BEF3030");
                                isoMessage.set(100, "10000000008");
                                isoMessage.set(102, "140023545512");
                                break;
                            case "39"://transfer inquiry                                                                                               
                                isoMessage.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                                isoMessage.set(15, StringFunction.getCurrentDateMMDD());
                                isoMessage.set(38, "123456");
                                isoMessage.set(39, "00");
                                isoMessage.unset(43);
                                isoMessage.set(48, "NAMA REKENING TUJUAN          REFERENCE NUMBERNAMA PEMILIK KARTU            ");
                                isoMessage.unset(52);
                                break;
                            case "40"://transfer/reversal transfer
                                isoMessage.set(7, StringFunction.getGMTCurrentDateMMDDHHMMSS());
                                isoMessage.set(15, StringFunction.getCurrentDateMMDD());
                                isoMessage.unset(22);
                                isoMessage.set(38, "123456");
                                isoMessage.set(39, "00");
                                isoMessage.unset(43);
                                isoMessage.set(48, "SYAHRIR K BANGSAWAN                           NAMA NASABAH BANK TUJUAN      ");
                                isoMessage.unset(52);
                                isoMessage.set(55, "910A36A9A68A87438E9D3030");
                                isoMessage.set(102, "140023545512");
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case "0420":
                    isoMessage.setMTI("0430");
                    isoMessage.set(39, "00");
                    break;
                default:
                    break;
            }
        }
    }
}
