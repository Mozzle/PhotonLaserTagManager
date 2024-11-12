/// Developed by Ben Kensington
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.nio.ByteBuffer;

    /*-----------------------------------------------------------
     * 
     *  README
     * 
     *  The main methods you will use in this class are:
     * 
     *  startListener() - Starts the listener thread. Use this to
     *  begin receiving data from the network -- should be always on. Returns boolean.
     * 
     *  stopListener() - Stops the listener thread. Call this probably
     *  on program exit. Returns boolean.
     * 
     *  transmit(String msg) - Transmits a message to all receivers. Returns boolean.
     * 
     *  pop() - Pops the most recent received data off the local data. Returns string.
     * 
     *  setSendPort(int newPort) - Sets the port to send on. Returns boolean.
     * 
     *  setReceivePort(int newPort, boolean reset) - Sets the port to listen on. 
     *  Use reset=true if you want the method to handle restarting the listener,
     *  otherwise use reset=false. Returns boolean.
     * 
     *  setSendAddress(String a) - Sets the address to send to. Returns boolean.
     * 
     *  getSendPort() - Returns the port to send on. Returns int.
     *  
     *  getReceivePort() - Returns the port to receive on. Returns int.
     * 
     *  getSendAddress() - Returns the address to send to. Returns string.
     * 
     *  The listener thread will automatically store the last 10
     *  messages received in the receivedData array. You can access
     *  this array directly or with pop().
     * 
    ---------------------------------------------------------- */

public class NetController
{
    /// Define default ports and addresses
    private int RECEIVE_PORT = 7501;
    private int SEND_PORT = 7500;
    private String SEND_ADDRESS = "127.0.0.1";
    private final int MAX_MESSAGES = 10;

    /// Listener object and thread
    private NetListener socketListener;
    private Thread listenerThread;
    private boolean isListening;

    /// Return flag and message notification flag
    private boolean returnFlag;
    public boolean pingFlag;

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

        // Set ping flag to false
        pingFlag = false;
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
    public boolean transmit(String msg)
    {
        returnFlag = true;

        // Send data buffer used to hold packet contents
        sendBuffer = new byte[1024];

        // Try/catch attempt to open a socket for sending data
        try {
            sendSocket = new DatagramSocket();
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

            sendSocket.connect(address, SEND_PORT);
            // Send the packet
            sendSocket.send(sendPacket);

            // Log message
            System.out.println("[NetController - " + timestamp() + "] Sending packet to " 
            + SEND_ADDRESS + ":" + SEND_PORT + ", data: " + new String(sendPacket.getData()));

            // Close the socket since we are done with it
            sendSocket.disconnect();
            sendSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            returnFlag = false;
        }

        return returnFlag; 
    }

    public boolean transmit(int msg)
    {
        returnFlag = true;

        // Send data buffer used to hold packet contents
        sendBuffer = new byte[1024];

        // Try/catch attempt to open a socket for sending data
        try {
            sendSocket = new DatagramSocket();
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
        sendBuffer = ByteBuffer.allocate(4).putInt(msg).array();

        // Try to send our data
        try {
            // Create an InetAddress object to store our address, and use it to create a packet to send
            InetAddress address = InetAddress.getByName(SEND_ADDRESS);
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, SEND_PORT);

            sendSocket.connect(address, SEND_PORT);
            // Send the packet
            sendSocket.send(sendPacket);

            // Log message
            System.out.println("[NetController - " + timestamp() + "] Sending packet to " 
            + SEND_ADDRESS + ":" + SEND_PORT + ", data: " + new String(sendPacket.getData()));

            // Close the socket since we are done with it
            sendSocket.disconnect();
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
    public boolean startListener() 
    {
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

        // Log message
        System.out.println("[NetController - " + timestamp() + "] Starting listening on " 
        + RECEIVE_PORT);

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
    public boolean stopListener() 
    {
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

        // Log message
        System.out.println("[NetController - " + timestamp() + "] Stopped listening on " 
        + RECEIVE_PORT);

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

        // Log message
        System.out.println("[NetController - " + timestamp() + "] Received message, data: " + receivedData[MAX_MESSAGES - 1]);

        // Update ping flag
        pingFlag = true;
    }

    /*-----------------------------------------------------------
     * 
     *  pop()
     * 
     *  DESCRIPTION: Pops the most recent received data off the
     *  local data array. Returns as a string.
     * 
    ---------------------------------------------------------- */
    public String pop() 
    {
        // Return the selected data
        String s = receivedData[MAX_MESSAGES - 1];
        
        // Log message
        System.out.println("[NetController - " + timestamp() + "] Popped message, data: " + s);

        // Shift all elements in the array down by one
        for (int i = 0; i < MAX_MESSAGES - 1; i++)
            receivedData[i] = receivedData[i + 1];

        return s;
    }

    /*-----------------------------------------------------------
     * 
     *  rflag(bool flag)
     * 
     *  DESCRIPTION: Sets the return flag
     * 
    ---------------------------------------------------------- */
    public void rflag(boolean flag) {
        returnFlag = flag;
    }

    /*-----------------------------------------------------------
     * 
     *  timestamp()
     * 
     *  DESCRIPTION: Helper function that just grabs the time and
     *  returns it as a string for logging purposes
     * 
    ---------------------------------------------------------- */
    public String timestamp() {
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(timestamp);
    }

    /*-----------------------------------------------------------
     * 
     *  getSendPort()
     * 
     *  DESCRIPTION: Getter method for send port. Returns as int.
     * 
    ---------------------------------------------------------- */
    public int getSendPort() {
        return SEND_PORT;
    }

    /*-----------------------------------------------------------
     * 
     *  setSendPort(int newPorts)
     * 
     *  DESCRIPTION: Setter method for send port.
     * 
     *  Returns true if the port was successfully set
     *  Returns false if no change was made
     * 
    ---------------------------------------------------------- */
    public boolean setSendPort(int newPorts) {
        if (newPorts > 0 && newPorts < 65535) {
            SEND_PORT = newPorts;
            System.out.println("[NetController - " + timestamp() + "] Set send port to " + SEND_PORT);
        }

        return SEND_PORT == newPorts;
    }

    /*-----------------------------------------------------------
     * 
     *  getReceivePort()
     * 
     *  DESCRIPTION: Getter method for send port. Returns as int.
     * 
    ---------------------------------------------------------- */
    public int getReceivePort() {
        return RECEIVE_PORT;
    }

    /*-----------------------------------------------------------
     * 
     *  setReceivePort(int newPorts, boolean reset)
     * 
     *  DESCRIPTION: Setter method for receive port.
     * 
     *  If reset is true, the listener will be 
     *  stopped and restarted
     * 
     *  If reset is false, no attempt will be
     *  made to stop/start the listener
     * 
     *  Returns true if the port was successfully set
     *  Returns false if no change was made
     * 
    ---------------------------------------------------------- */
    public boolean setReceivePort(int newPorts, boolean reset) {
        if ((newPorts > 0 && newPorts < 65535) && !isListening && !reset) {
            RECEIVE_PORT = newPorts;
            System.out.println("[NetController - " + timestamp() + "] Set receive port to " + RECEIVE_PORT);
        }

        if ((newPorts > 0 && newPorts < 65535) && reset) {
            stopListener();
            RECEIVE_PORT = newPorts;
            System.out.println("[NetController - " + timestamp() + "] Set receive port to " + RECEIVE_PORT);
            startListener();
        }

        return RECEIVE_PORT == newPorts;
    }

    /*-----------------------------------------------------------
     * 
     *  setSendAddress(String a)
     * 
     *  DESCRIPTION: Setter method for send address.
     * 
     *  Returns true if the address was successfully set
     *  Returns false if no change was made
     * 
    ---------------------------------------------------------- */
    public boolean setSendAddress(String a) {
        SEND_ADDRESS = a;
        System.out.println("[NetController - " + timestamp() + "] Set send address to " + SEND_ADDRESS);
        return SEND_ADDRESS.equals(a);
    }

     /*-----------------------------------------------------------
     * 
     *  getSendAddress()
     * 
     *  DESCRIPTION: Getter method for send address. 
     *  Returns as string.
     * 
    ---------------------------------------------------------- */
    public String getSendAddress() {
        return SEND_ADDRESS;
    }
}