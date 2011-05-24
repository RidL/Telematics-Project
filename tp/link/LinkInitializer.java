package tp.link;

public class LinkInitializer {

    /**
     * This class is the temporary startup class of the link.
     *
     * It is meant to be replaced by a router program, but since this is not yet finished
     * we created this class for building and testing purposes of the Link layer.
     *
     * Once started, it creates a HLReceiver and HLSender and starts both.
     * @param args
     */
    public static void main(String[] args) {
        HLReceiver hlr = null;
        HLSender hls = null;
        hlr = new HLReceiver();
        hls = new HLSender(hlr);
        hlr.setSender(hls);

        // random data
        byte[] bytes = new byte[]{3, 30, 1, 5, 12, 7, 4, 6, 9, 4, 4, 2, 16, 23, 2, 11, 3, 30, 1, 5,
            12, 7, 4, 6, 9, 4, 4, 2, 16, 23, 2, 11, 6, 3, 16, 14, 7, 9, 26, 12, 13, 3, 30, 1, 5, 12, 7, 4, 6,
            9, 4, 4, 2, 16, 23, 2, 11, 3, 30, 1, 5, 12, 7, 4, 6, 9, 4, 4, 2, 16, 23, 2, 11, 3, 16, 14, 7, 9,
            26, 12, 13, 3, 30, 1, 5, 12, 7, 4, 6, 9, 4, 4, 2, 16, 23, 2, 11, 3, 30, 1, 5, 12, 7};   // 103 bytes
        //        byte[] bytes2 = new byte[]{3, 30, 1, 5, 12, 7, 4, 6, 9, 4, 4, 2, 16, 23, 2, 11, 3, 30, 1, 5,
//            12, 7, 4, 6, 9, 4, 4, 2, 16, 23, 2, 11, 6, 3, 16, 14, 7, 9, 26, 12, 13, 3, 30, 1, 5, 12, 7, 4, 6,
//            9, 4, 4, 2, 16, 23, 2, 11, 3, 30, 1, 5, 12, 7, 4, 6, 9, 4, 4, 2, 16, 23, 2, 11, 3, 16, 14, 7, 9,
//            26, 12, 13, 3, 30, 1, 5, 12, 7, 4, 6};   // 89 bytes


        hlr.start();
        hls.start();
        while (true) {
            if (hls.readyToPushSegment()) {
                if (hls.readyToPushSegment()) {
                    hls.pushSegment(bytes);
                }
            }
        }

    }
}
