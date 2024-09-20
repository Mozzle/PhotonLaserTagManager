# 'Theranos, Inc.' (Team #12)
## "Bold Promises, Zero Results!"

### Name         - Github Username

Nathaniel Bowles - NBCode45

Dylan Collier    - Mozzle

Ben Kensington   - V3NTYY

Zachary Tucker   - zty1ooo


## Program Setup Instructions

These are instructions for setting up and running the Photon Laser Tag Manager on the Provided Debian Image.
The following terminal commands must be ran to set up the Manager Program.

### Step 1. Update your package lists:

'sudo apt update'

### Step 2. Install the Java 17 JDK:

'apt install openjdk-17-jdk openjdk-17-jre'
(We will eventually make this project to be reliant only on the JRE, but for development, we want to build every time we run the program)

Verify that a 17.x version of the JDK was installed properly:
'java -version'

### Step 3. Clone the repository:

'git clone https://github.com/Mozzle/PhotonLaserTagManager'

Now move to the directory you cloned the repo to. This is usually /home/student/PhotonLaserTagManager/
'cd /home/student/PhotonLaserTagManager/'

### Step 4. Build and run the program:

'./build.bash'

If you get a 'permission denied' error, run the following command to give yourself execution permission:
'chmod u+x ./build.bash'

Then run the './build.bash' command again.