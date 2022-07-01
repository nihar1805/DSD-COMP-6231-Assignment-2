import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class IDirectory_Impl implements IDirectory{

    @Override
    public void peerDiscovery() {
        try {
            DatagramSocket udpmultiSocket = new DatagramSocket();
            udpmultiSocket.setBroadcast(true);
            byte[] msg = "Waiting for port...".getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(msg, msg.length, InetAddress.getByName("255.255.255.255"), 8888);
            udpmultiSocket.send(packet);

            Enumeration interfaces = null;

            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();

                    if (broadcast == null) {
                        continue;
                    }

                    try {
                        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, broadcast, 7777);
                        udpmultiSocket.send(sendPacket);

                        DatagramPacket sendPacket2 = new DatagramPacket(msg, msg.length, broadcast, 8888);
                        udpmultiSocket.send(sendPacket2);

                        DatagramPacket sendPacket3 = new DatagramPacket(msg, msg.length, broadcast, 9999);
                        udpmultiSocket.send(sendPacket3);
//                        System.out.println("Packet sent");

                    } catch (Exception e) {
                    }
                }
            }
//            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void receivePort() {
        try{
            int i =0;
            DatagramSocket s = new DatagramSocket(6231, InetAddress.getByName("0.0.0.0"));
            while(i <= 3){
                byte[] buf = new byte[1000];
                DatagramPacket port_msg = new DatagramPacket(buf, buf.length);
//                System.out.println("waiting for server response");
                s.receive(port_msg);
//                System.out.println("Response received from server");
                String portString = new String(port_msg.getData()).trim();
                ClientApp.server_port_list.add(portString);
                i++;
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }
}
