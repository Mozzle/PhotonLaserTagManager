/// Developed by Ben Kensington
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class NetListener
{
    private byte[] dataBuffer;
    private DatagramSocket dataSocket;
    private NetController master;
    private boolean socketOpen;

    /*-----------------------------------------------------------
     * 
     *  NetListener(NetController c)
     * 
     *  DESCRIPTION: NetListener Constructor. 
     *  Initializes a buffer for any packets received and 
     *  attempts to open both sockets for receiving and sending data
     * 
     *  Keeps a reference to the master controller for sending data
     * 
    ---------------------------------------------------------- */
    public NetListener(NetController c) {
        // Data buffer used to hold packet data, output data is sent to controller
        dataBuffer = new byte[1024];
        master = c;

        // Try/catch attempt to open a socket for receiving data
        try {
            dataSocket = new DatagramSocket(NetController.RECEIVE_PORT);
            socketOpen = true;
        } 
        // Exception is typically thrown when the port is in use, or already open
        catch (SocketException e) {
            e.printStackTrace();
            socketOpen = false;
            master.rflag(false);
        }
        // We have no idea what threw the exception
        catch (Exception e) {
            e.printStackTrace();
            socketOpen = false;
            master.rflag(false);
        }
    }

    /*-----------------------------------------------------------
     * 
     *  listen()
     * 
     *  DESCRIPTION: Listener method for receiving data. Runs
     *  in a loop, designed to be run in a separate thread. Only
     *  runs while the socket is open.
     * 
    ---------------------------------------------------------- */
    public void listen() {
        while(socketOpen) {
            // Create a packet to store the data
            DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);

            // Try to receive any data
            try {
                // Listen and receive any packets that come in on 7501
                dataSocket.receive(packet);
                // Send the data to the master controller
                master.ping(packet.getData());
            }
            // Exception is typically thrown when the port is in use, or already open
            catch (SocketException e) {
                System.out.println("Socket was closed or is inaccessible. Exiting thread now.");
                break;
            }
            // We have no idea what threw the exception
            catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /*-----------------------------------------------------------
     * 
     *  stop()
     * 
     *  DESCRIPTION: Stop method, used to free the socket and end
     *  the listener thread
     * 
    ---------------------------------------------------------- */
    public void stop() {
        dataSocket.close();
    }
}