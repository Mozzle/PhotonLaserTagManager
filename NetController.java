/// Developed by Ben Kensington
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

    /*-----------------------------------------------------------
     * 
     *  README
     * 
     *  The main methods you will use in this class are:
     * 
     *  startListener() - Starts the listener thread. Use this to
     *  begin receiving data from the network -- should be always on.
     * 
     *  stopListener() - Stops the listener thread. Call this probably
     *  on program exit.
     * 
     *  transmit(String msg) - Transmits a message to all receivers.
     * 
     *  The listener thread will automatically store the last 10
     *  messages received in the receivedData array. You can access
     *  this array directly.
     * 
    ---------------------------------------------------------- */

public class NetController
{
    /// Define all ports to receive/send on
    public static final int RECEIVE_PORT = 7501;
    public static final int SEND_PORT = 7500;
    private static final String SEND_ADDRESS = "192.168.1.100";
    private final int MAX_MESSAGES = 10;

    /// Listener object and thread
    private NetListener socketListener;
    private Thread listenerThread;
    private boolean isListening;

    /// Return flag
    private boolean returnFlag;

    /// Send buffer and socket
    private byte[] sendBuffer;
    private DatagramSocket sendSocket;

    // Output array of received data
    public String[] receivedData;

    /*-----------------------------------------------------------
     * 
     *  NetController()
     * 
     *  DESCRIPTION: NetController Constructor. 
     *  Sets listening flag and initializes output array
     * 
    ---------------------------------------------------------- */
    public NetController() {
        // Output data array, holds 10 messages at a time.
        receivedData = new String[MAX_MESSAGES];

        // Set listener flag to false
        isListening = false;
    }

    /*-----------------------------------------------------------
     * 
     *  transmit(String msg)
     * 
     *  DESCRIPTION: Transmits a message to all receivers. Starts by
     *  converting the message to a byte array, then the byte array
     *  is created into a packet, then sent to the specified address
     * 
     *  Returns true if the code was successfully transmitted
     *  Returns false if the code was not transmitted, or failed
     * 
    ---------------------------------------------------------- */
    public boolean transmit(String msg) {

        returnFlag = true;

        // Send data buffer used to hold packet contents
        sendBuffer = new byte[1024];

        // Try/catch attempt to open a socket for sending data
        try {
            sendSocket = new DatagramSocket(SEND_PORT);
        } 
        // Exception is typically thrown when the port is in use, or already open
        catch (SocketException e) {
            e.printStackTrace();
            returnFlag = false;
        }
        // We have no idea what threw the exception
        catch (Exception e) {
            e.printStackTrace();
            returnFlag = false;
        }

        // Convert the message to a byte array
        sendBuffer = msg.getBytes();

        // Try to send our data
        try {
            // Create an InetAddress object to store our address, and use it to create a packet to send
            InetAddress address = InetAddress.getByName(SEND_ADDRESS);
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, SEND_PORT);

            // Send the packet
            sendSocket.send(sendPacket);
            System.out.println("Sent: " + new String(sendPacket.getData()));

            // Close the socket since we are done with it
            sendSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            returnFlag = false;
        }

        return returnFlag; 
    }

    /*-----------------------------------------------------------
     * 
     *  startListener()
     * 
     *  DESCRIPTION: Starts the socket listener
     * 
     *  Returns true if the socket listener was able to start
     *  Returns false if the socket listener couldn't start
     * 
    ---------------------------------------------------------- */
    public boolean startListener() {

        returnFlag = true;
        // If we're already listening, return false
        if (isListening)
            return false;
        isListening = true;

        // Create the listener object
        socketListener = new NetListener(this);
        
        // Create a thread for our listener
        listenerThread = new Thread(new Runnable() {
            public void run() {
                while (isListening)
                    socketListener.listen();
            }
        });

        // Start the listener thread
        listenerThread.start();

        System.out.println("Started listening on port " + RECEIVE_PORT + "...");

        return returnFlag;
    }

    /*-----------------------------------------------------------
     * 
     *  stopListener()
     * 
     *  DESCRIPTION: Stops the current listener
     * 
     *  Returns true if the socket listener was able to start
     *  Returns false if the socket listener couldn't start
     * 
    ---------------------------------------------------------- */
    public boolean stopListener() {

        returnFlag = true;
        // If we're not listening, return false
        if (!isListening)
            return false;
        isListening = false;

        // Close the socket
        socketListener.stop();

        // Wait for the listener thread to die
        try {
            listenerThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Clean up references
        socketListener = null;
        listenerThread = null;

        System.out.println("Stopped listening on port " + RECEIVE_PORT + ".");

        return returnFlag;
    }

    /*-----------------------------------------------------------
     * 
     *  ping(byte[] data)
     * 
     *  DESCRIPTION: Receives a packet of data and pushes it into
     *  the receivedData array. The last element is removed.
     * 
    ---------------------------------------------------------- */
    public void ping(byte[] data) 
    {
        // Shift all elements in the array down by one
        for (int i = 0; i < MAX_MESSAGES - 1; i++)
            receivedData[i] = receivedData[i + 1];

        // Add the new data to the end of the array
        receivedData[MAX_MESSAGES - 1] = new String(data);

        // Print the received data
        System.out.println(Arrays.toString(receivedData));
    }

    /*-----------------------------------------------------------
     * 
     *  rflag(bool flag)
     * 
     *  DESCRIPTION: Sets the return flag
     * 
    ---------------------------------------------------------- */
    public void rflag(boolean flag) 
    {
        returnFlag = flag;
    }
}