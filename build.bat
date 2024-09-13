::@echo off
javac PhotonSystem.java View.java Controller.java Model.java SplashScreen.java TextBox.java Database.java NetController.java NetListener.java

if %errorlevel% neq 0 (
	echo There was an error; exiting now.	
) else (
	echo Compiled correctly!  Running...
	java -cp "./lib/postgresql-42.7.4.jar;" PhotonSystem	
)

