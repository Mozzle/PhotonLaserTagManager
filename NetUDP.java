
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
    private final byte[] receiveBuffer;
    private final byte[] sendBuffer;
    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;

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
     *  attempts to open both sockets for receiving and sending data
     * 
    ---------------------------------------------------------- */
    public NetUDP() {
        // Data buffer used to hold packet data, is directly linked to the model
        receiveBuffer = new byte[1024];
        sendBuffer = new byte[1024];

        // Try/catch attempt to open a socket for receiving data
        try {
            // Try to open a socket to receive data
            receiveSocket = new DatagramSocket(RECEIVE_PORT);
        } 
        // Exception is typically thrown when the port is in use, or already open
        catch (SocketException e) {
            e.printStackTrace();
        }
        // We have no idea what threw the exception
        catch (Exception e) {
            e.printStackTrace();
        }

        // Try/catch attempt to open a socket for receiving data
        try {
            // Try to open a socket to send data
            sendSocket = new DatagramSocket(SEND_PORT);
        } 
        // Exception is typically thrown when the port is in use, or already open
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
     *  DESCRIPTION: Transmits a code to all receivers. Starts by
     *  converting the code to a byte array, then the byte array
     *  updates the private sendBuffer. When sendBuffer contains 
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
     *  Returns true if a packet was received or sent
     *  Returns false if nothing was received or sent
     * 
    ---------------------------------------------------------- */
    public boolean update() {

        boolean receiveFlag = false;
        boolean sendFlag = false;

        // Create a packet to store the data
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        // Try to receive any data
        try {
            System.out.println("Waiting for data on port " + RECEIVE_PORT + "...");

            // Listen and receive any packets that come in on 7501
            receiveSocket.receive(packet);

            // Convert the packet data to a string via the string constructor
            String stringPacket = new String(packet.getData(), 0, packet.getLength());
            System.out.println("\nReceived: " + stringPacket);

            receiveFlag = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Try to send any data
        try {
            /// TODO: Implement sending method via UDP
            sendFlag = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return (receiveFlag || sendFlag);
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
    public void outputBuffer()
    {

    }

}