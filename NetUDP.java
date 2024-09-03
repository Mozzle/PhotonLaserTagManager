
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class NetUDP
{
    /// Define all ports to receive/send on
    public static final int RECEIVE_PORT = 7501;
    public static final int SEND_PORT = 7500;
    public boolean receiveFlag;
    private final byte[] dataBuffer;
    private DatagramSocket receiveSocket;

    /// Temporary main method for testing
    public static void main(String[] args) {
        NetUDP net = new NetUDP();
        net.update();
    }

    /*-----------------------------------------------------------
     * 
     *  NetUDP()
     * 
     *  DESCRIPTION: NetUDP Constructor. 
     *  Initializes a buffer for any packets received and 
     *  attempts to open a socket for receiving and sending data
     * 
    ---------------------------------------------------------- */
    public NetUDP() {
        // Data buffer used to hold packet data, is directly linked to the model
        dataBuffer = new byte[1024];
        receiveFlag = false;

        // Try/catch attempt to open a socket for receiving data
        try {
            // Create a socket to receive data
            receiveSocket = new DatagramSocket(RECEIVE_PORT);
        } 
        catch (SocketException e) {
            e.printStackTrace();
        }
        // We have no idea what threw the exception
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*-----------------------------------------------------------
     * 
     *  transmitCode(int code)
     * 
     *  DESCRIPTION: Transmits a code to all receivers
     * 
     *  Returns true if the code was successfully transmitted
     *  Returns false if the code was not transmitted, or failed
     * 
    ---------------------------------------------------------- */
    public boolean transmitCode(int code) {
        return true;
    }

    /*-----------------------------------------------------------
     * 
     *  update()
     * 
     *  DESCRIPTION: Update method that transmits all changes
     *  to the model. This method also acts as a listener, handling
     *  any incoming packets and updating the model accordingly.
     * 
     *  Returns true if a packet was received
     *  Returns false if nothing was received
     * 
    ---------------------------------------------------------- */
    public boolean update() {
        try {
            // Create a packet to store the data
            DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);

            System.out.println("Waiting for data on port " + RECEIVE_PORT + "...");

            // Listen and receive any packets that come in on 7501
            receiveSocket.receive(packet);

            // Convert the packet data to a string
            String stringPacket = new String(packet.getData(), 0, packet.getLength());
            System.out.println("\nReceived: " + stringPacket);

            receiveFlag = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return receiveFlag;
    }

    /*-----------------------------------------------------------
     * 
     *  outputBuffer()
     * 
     *  DESCRIPTION: Getter method for packet buffer
     *  
     *  Returns the data buffer as an integer array
     * 
    ---------------------------------------------------------- */
    public byte[] outputBuffer()
    {
        return dataBuffer;
    }

}