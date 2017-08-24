package com.ftdi.j2xx.hyperterm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import java.util.Timer;
import java.util.TimerTask;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class J2xxHyperTerm extends Activity implements SensorEventListener
{

	// Defining all variables

	Button reset;                                                                                   // Defining start and reset button


	private String N1="0", N2="0", N3="0", N4="0", N5="0", N6="0", N7="0", N8="0", N9="0",N10="0" ,N11, N12;                                // Defining strings containing each RSS reading

	// Defining text boxes containing RSS Reading
	private TextView N1_textView, N2_textView, N3_textView, N4_textView, N5_textView, N6_textView,N7_textView, N8_textView, N9_textView,N10_textView, N11_textView, N12_textView;
	private TextView test1, test2, test3, test4,test5, test6,test7, test8,test9, test10;


	private int R1, R2, R3, sensor_countX,sensor_countY,sensor_countZ,count_step,count_step_old, delay=0, approx_azimuth,sens_countX,sens_countY,sens_countZ;   // Defining Integers containing each RSS reading

	ImageButton Button_zigbee,Button_pdr, Button_pdr_corrected;                                                           // Defining the position point withg Zigbee and the position point with PDR

	private TextView zigbeeX,zigbeeY;                                                               // Defining text box for X-axies and Y-axies (Zigbee)
	private double X_corrected, Y_corrected;




	// Defining Sensors variables

	private SensorManager mSensorManager;

	private Sensor mAccelerometer,mMagnetometer,mGyroscope;

	private float[] mLastAccelerometer = new float[3];
	private float[] mLastMagnetometer = new float[3];
	private boolean mLastAccelerometerSet = false;
	private boolean mLastMagnetometerSet = false;

	private float[] mR = new float[9];
	private float[] mOrientation = new float[3];
	private float Accel_X = 0,Accel_Y = 0,Accel_Z = 0;

	// Defining PDR variables

	private TextView pdrX,pdrY;                                                                     // Defining text box for X-axies and Y-axies (PDR)\

	private TextView pdr_correctedX, pdr_correctedY;

	private TextView approx_azimuth_text,count_step_text,delay_text,RMS_text;

	private int count_gyro_left, count_gyro_right;

	private double X_pdr, Y_pdr;

	private double X_pdr_total=0,Y_pdr_total=0, X_pdr_total_corrected=0, Y_pdr_total_corrected=0 ;

	private float azimuth = 0f,azimuthInRadians, initial_azimuth, delta_azimuth =0,delta_azimuth_temp=0,Previous_azimuth=0,gyro, error;


	private float sumX=0, averageX=0,sumY=0, averageY=0,sumZ=0, averageZ=0,step_detect;
	private double RMS_store=0, delta_RMS, RMS;

	private TextView averageX_text, averageY_text, averageZ_text,compass, gyro_text;
	private EditText step_detect_edittext;

	private float[] arrayX = new float[30],arrayY = new float[30],arrayZ = new float[30];

	private int detect, ret, correct;
	private double x3;

	private double N1x = 0, N1y = 0;
	private double N2x = 33, N2y = 4.8;
	private double N3x = 50, N3y = 4.8;
	private double N4x = 67.8, N4y = 11.4;
	private double N5x = 62.4, N5y = 31.8;
	private double N6x = 34.8, N6y = 27;
	private double N7x = 3, N7y = 27;
	private double N8x = 12, N8y = 7.8;
	private double N9x = 33, N9y = 16.2;
	private double N10x = 12.5, N10y = 21;







    //*****************************************************************************************************************************************
	//*****************************************************************************************************************************************
	//*****************************************************************************************************************************************

	//Serial Communication Declarations:


	// j2xx
	public static D2xxManager ftD2xx = null;
	FT_Device ftDev;
	int DevCount = -1;
	int currentPortIndex = -1;
	int portIndex = -1;

	// log tag
	final String TT = "Trace";
	final String TXS = "XM-Send";
	final String TZR = "ZM-Rec";
	
	// handler event
	final int UPDATE_TEXT_VIEW_CONTENT = 0;
	final int UPDATE_SEND_FILE_STATUS = 1;

	final int MSG_SELECT_FOLDER_NOT_FILE = 7;
	final int UPDATE_MODEM_RECEIVE_DATA = 9;
	final int UPDATE_MODEM_RECEIVE_DATA_BYTES = 10;
	final int MSG_FORCE_STOP_SEND_FILE = 16;
	final int UPDATE_ASCII_RECEIVE_DATA_BYTES = 17;
	final int MSG_FORCE_STOP_SAVE_TO_FILE = 19;
	final int UPDATE_ZMODEM_STATE_INFO = 20;
	final int ACT_ZMODEM_AUTO_START_RECEIVE = 21;
	
	final int MSG_SPECIAL_INFO = 98;
	final int MSG_UNHANDLED_CASE = 99;

    final byte XON = 0x11;    /* Resume transmission */
    final byte XOFF = 0x13;    /* Pause transmission */
    
	// strings of file transfer protocols
    String currentProtocol;
    
	final int MODE_GENERAL_UART = 0;
	final int MODE_X_MODEM_CHECKSUM_RECEIVE = 1;
	final int MODE_X_MODEM_CHECKSUM_SEND = 2;
	final int MODE_X_MODEM_CRC_RECEIVE = 3;
	final int MODE_X_MODEM_CRC_SEND = 4;
	final int MODE_X_MODEM_1K_CRC_RECEIVE = 5;
	final int MODE_X_MODEM_1K_CRC_SEND = 6;
	final int MODE_Y_MODEM_1K_CRC_RECEIVE = 7;
	final int MODE_Z_MODEM_RECEIVE = 9;
	final int MODE_Z_MODEM_SEND = 10;
		
	int transferMode = MODE_GENERAL_UART;


	// X, Y, Z modem - UART MODE: Asynchronous�B8 data��bits�Bno parity�Bone stop��bit
	// X modem + //

    final byte ACK = 6;    /* ACKnowlege */
    final byte NAK = 0x15; /* Negative AcKnowlege */
    final byte CHAR_C = 0x43; /* Character 'C' */
    final byte CHAR_G = 0x47; /* Character 'G' */

    
    final int MODEM_BUFFER_SIZE = 2048;
    int[] modemReceiveDataBytes;    
    byte[] modemDataBuffer;
    byte[] zmDataBuffer;
    byte receivedPacketNumber = 1;
    
    boolean bModemGetNak = false;
    boolean bModemGetAck = false;
    boolean bModemGetCharC = false;
    boolean bModemGetCharG = false;
    
    int totalModemReceiveDataBytes = 0;
    boolean bDataReceived = false;
    boolean bReceiveFirstPacket = false;

    boolean bUartModeTaskSet = true;

    
    // Z modem +//
    final byte ZPAD = 0x2A; // '*' 052 Padding character begins frames 
    final byte ZDLE = 0x18;     

    final byte ZHEX = 0x42;		// 'B' HEX frame indicator

    final int ZRINIT = 1;   /* Receive init */

    final int ZOO = 20;


    int zmodemState = 0;

    // fixed pattern, used to check ZRQINIT
    final int ZMS_0 = 0; 
    final int ZMS_1 = 1; // r 
    final int ZMS_2 = 2; // z
    final int ZMS_3 = 3; // \r
    final int ZMS_4 = 4; // ZPAD (ZRQINIT)
    final int ZMS_5 = 5; // ZPAD
    final int ZMS_6 = 6; // ZDLE
    final int ZMS_7 = 7; // ZHEX
    final int ZMS_8 = 8; // 0x30
    final int ZMS_9 = 9; // 0x30
    final int ZMS_10 = 10; // 0x30
    final int ZMS_11 = 11; // 0x30
    final int ZMS_12 = 12; // 0x30
    final int ZMS_13 = 13; // 0x30
    final int ZMS_14 = 14; // 0x30
    final int ZMS_15 = 15; // 0x30
    final int ZMS_16 = 16; // 0x30
    final int ZMS_17 = 17; // 0x30
    final int ZMS_18 = 18; // 0x30
    final int ZMS_19 = 19; // 0x30
    final int ZMS_20 = 20; // 0x30
    final int ZMS_21 = 21; // 0x30 (14th 0x30)
    final int ZMS_22 = 22; // 0x0D
    final int ZMS_23 = 23; // 0x0A
    final int ZMS_24 = 24; // 0x11
    int zmStartState = 0;
    // Z modem -//
    
    // general data count
	int totalReceiveDataBytes = 0;
	int totalUpdateDataBytes = 0;
	
	SelectFileDialog fileDialog;
	File mPath = new File(android.os.Environment.getExternalStorageDirectory() + "//DIR//");
	File fGetFile = null;

	long back_button_click_time;
	boolean bBackButtonClick = false;

	
	// thread to read the data
	HandlerThread handlerThread; // update data to UI
	ReadThread readThread; // read data from USB

	// graphical objects    

	//TextView readText,test;

	ArrayAdapter<CharSequence> baudAdapter;
	ArrayAdapter<CharSequence> portAdapter;


	boolean bSendHexData = false;
		

	boolean bContentFormatHex = false;

	// show information message while send data by tapping "Write" button in hex content format
	int timesMessageHexFormatWriteData = 0;
	
	// note: when this values changed, need to check main.xml - android:id="@+id/ReadValues - android:maxLines="5000"
	final int TEXT_MAX_LINE = 1000; 

	// variables
	final int UI_READ_BUFFER_SIZE = 10240; // Notes: 115K:1440B/100ms, 230k:2880B/100ms
	byte[] writeBuffer;
	byte[] readBuffer;
	char[] readBufferToChar;
	int actualNumBytes;
	
	int baudRate; /* baud rate */
	byte stopBit; /* 1:1stop bits, 2:2 stop bits */
	byte dataBit; /* 8:8bit, 7: 7bit */
	byte parity; /* 0: none, 1: odd, 2: even, 3: mark, 4: space */
	byte flowControl; /* 0:none, 1: CTS/RTS, 2:DTR/DSR, 3:XOFF/XON */	
	public Context global_context;
	boolean uart_configured = false;

	String uartSettings  = "";


	BufferedOutputStream buf_save;	
	boolean WriteFileThread_start = false;
	
	String fileNameInfo;
	int iFileSize = 0;
	int sendByteCount = 0;
	long start_time;

	
	// data buffer
	byte[] readDataBuffer; /* circular buffer */
	
	int iTotalBytes;
	int iReadIndex;

	final int MAX_NUM_BYTES = 65536;

	boolean bReadTheadEnable = false;

	//*****************************************************************************************************************************************
	//*****************************************************************************************************************************************
	//*****************************************************************************************************************************************

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        //test = (TextView) findViewById(R.id.test);

		serial_initialize();

        initialize();                                                                               // initialize all sensors, buttons and text boxes


        reset.setOnClickListener(new View.OnClickListener()                                         // Starting reset button
		{
			@Override
			public void onClick(View arg0) {
				sumX = 0;
				averageX = 0;
				sumY = 0;
				averageY = 0;
				sumZ = 0;
				averageZ = 0;
				RMS_store = 0;// Resetting all values
				count_step = 0;
				count_step_old = 0;
				X_pdr = 0;
				X_pdr = 0;
				approx_azimuth = 0;
				X_pdr_total = 0;
				Y_pdr_total = 0;

				X_pdr_total_corrected=0;
				Y_pdr_total_corrected=0;

				error = 0;
				count_gyro_left = 0;
				count_gyro_right = 0;
				X_corrected = 0;
				Y_corrected = 0;

				Button_zigbee.setX(160);
				Button_zigbee.setY(12);
				Button_pdr.setX(160);
				Button_pdr.setY(12);
				Button_pdr_corrected.setX(160);
				Button_pdr_corrected.setY(12);

				initial_azimuth = azimuth;
				azimuth = 0;                                              // Saving the initial orientation then resetting it.

				step_detect = Float.parseFloat(step_detect_edittext.getText().toString());

			}
		});

	}



	@Override
	protected void onStart()
    {
		super.onStart();
		createDeviceList();
		if(DevCount > 0)
		{
			connectFunction();

			setConfig(baudRate, dataBit, stopBit, parity, flowControl);
		}
	}

	protected void onResume()
	{
		super.onResume();
		if(null == ftDev || false == ftDev.isOpen())
		{
			DLog.e(TT, "onResume - reconnect");
			createDeviceList();
			if(DevCount > 0)
			{
				connectFunction();

				setConfig(baudRate, dataBit, stopBit, parity, flowControl);
			}
		}

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);  // Register the accelerometer and determining its speed
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);   // Register the magnetometer and determining its speed
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);      // Register the Gyroscope and determining its speed
	}


    protected void onPause()                                                                        // Unregister the accelerometer for stop listening the events
    {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);                                    // Unregister the accelerometer
        mSensorManager.unregisterListener(this, mMagnetometer);                                     // Unregister the magnetometer
        mSensorManager.unregisterListener(this, mGyroscope);                                        // Unregister the Gyroscope
    }



    //*****************************************************************************************************************************************


	protected void onStop()
	{
		super.onStop();
	}

	protected void onDestroy()
	{
		disconnectFunction();
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

    //*****************************************************************************************************************************************




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_STATUS_ACCURACY_HIGH); // Determining the accuracy of the Magnetometer
    }



    //*****************************************************************************************************************************************



	public void initialize()                                                                        // initialize all sensors, buttons and text boxes
	{
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);                          // Setting up Sensor Manager

		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);                // Setting up ACCELEROMETER
		mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);                // Setting up MAGNETOMETER
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);                        // Setting up Gyroscope


		reset = (Button)findViewById(R.id.reset);                                                   // Assigning reset button

		Button_zigbee = (ImageButton) findViewById(R.id.Button_zigbee);                             // Assigning the position point with Zigbee
		Button_pdr =  (ImageButton) findViewById(R.id.Button_pdr);                                  // Assigning the position point with PDR
		Button_pdr_corrected=  (ImageButton) findViewById(R.id.Button_pdr_corrected);



		zigbeeX = (TextView) findViewById(R.id.zigbeeX);                                            // Assigning text box for X-axis (Zigbee)
		zigbeeY = (TextView) findViewById(R.id.zigbeeY);                                            // Assigning text box for Y-axis (Zigbee)

		pdrX = (TextView) findViewById(R.id.pdrX);                                                  // Assigning text box for X-axis (PDR)
		pdrY = (TextView) findViewById(R.id.pdrY);                                                  // Assigning text box for X-axis (PDR)

		pdr_correctedX = (TextView) findViewById(R.id.pdr_correctedX);
		pdr_correctedY= (TextView) findViewById(R.id.pdr_correctedY);

		averageX_text = (TextView) findViewById(R.id.averageX_text);                                // Assigning text box for Accelerometer readings of X-axis
		averageY_text = (TextView) findViewById(R.id.averageY_text);                                // Assigning text box for Accelerometer readings of Y-axis
		averageZ_text = (TextView) findViewById(R.id.averageZ_text);                                // Assigning text box for Accelerometer readings of Z-axis

		compass = (TextView) findViewById(R.id.compass);                                            // Assigning text box for Compass readings

		gyro_text= (TextView) findViewById(R.id.gyro_text);                                         // Assigning text box for Gyroscope readings of X-axis

		N1_textView = (TextView) findViewById(R.id.N1);                                             // Assigning text boxes containing RSS Reading
		N2_textView = (TextView) findViewById(R.id.N2);
		N3_textView = (TextView) findViewById(R.id.N3);
		N4_textView = (TextView) findViewById(R.id.N4);
		N5_textView = (TextView) findViewById(R.id.N5);
		N6_textView = (TextView) findViewById(R.id.N6);
		N7_textView = (TextView) findViewById(R.id.N7);
		N8_textView = (TextView) findViewById(R.id.N8);
		N9_textView = (TextView) findViewById(R.id.N9);
		N10_textView = (TextView) findViewById(R.id.N10);
		N11_textView = (TextView) findViewById(R.id.N11);
		N12_textView = (TextView) findViewById(R.id.N12);




		test1 = (TextView) findViewById(R.id.test1);
		test2 = (TextView) findViewById(R.id.test2);
		test3 = (TextView) findViewById(R.id.test3);
		test4 = (TextView) findViewById(R.id.test4);
		test5 = (TextView) findViewById(R.id.test5);
		test6 = (TextView) findViewById(R.id.test6);
		test7 = (TextView) findViewById(R.id.test7);
		test8 = (TextView) findViewById(R.id.test8);
		test9 = (TextView) findViewById(R.id.test9);
		test10 = (TextView) findViewById(R.id.test10);


		RMS_text = (TextView) findViewById(R.id.RMS_text);                 						    // Display the value of initial azimuth
		approx_azimuth_text = (TextView) findViewById(R.id.approx_azimuth_text);                    // Display the value of the approximated azimuth which is the approximated orientation of the tablet with respect to initial orientation
		count_step_text = (TextView) findViewById(R.id.count_step_text);                            // Display the number of steps done by the pedestrian
		delay_text = (TextView) findViewById(R.id.delay_text);                                      // Display the value of delay time (which resembles the time in which the system doesn't consider any additional step, to prevent calculating a step two times)
		step_detect_edittext= (EditText) findViewById(R.id.step_detect);
	}



	//*****************************************************************************************************************************************


    @Override
	public void onSensorChanged(SensorEvent event)
	{
		displayCleanValues();                                                                       // display clean value of x,y,z accelerometer

		if (event.sensor == mGyroscope)                                                             // Reading Gyroscope
		{
			gyro = event.values[1];                                                       			// Reading Gyroscope in Y -axies
		}
		else if (event.sensor == mAccelerometer)                                                    // Reading Accelerometer
		{
			System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
			mLastAccelerometerSet = true;
			Accel_X = Math.abs(event.values[0]);                                                    // Reading Acceleration in X-axis
			Accel_Y = Math.abs(event.values[1]);                                                    // Reading Acceleration in Y-axis
			Accel_Z = Math.abs(event.values[2]);                                                    // Reading Acceleration in Z-axis
		}
		else if (event.sensor == mMagnetometer)                                                     // Reading Magnetometer
		{
			System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
			mLastMagnetometerSet = true;
		}

		if (mLastAccelerometerSet && mLastMagnetometerSet)
		{
			SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
			SensorManager.getOrientation(mR, mOrientation);

			azimuthInRadians = mOrientation[0];                                                     // Reading Azimuth in radians
			float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;             // Converting Azimuth to degrees
			azimuth = azimuthInDegress;
		}

		adjust_values();                                                                            // Adjusting the reading of accelerometer and produce average values
		calculate_pdr();               			                                                    // Calculating position by Pedestrian Dead Reckoning (PDR)
		displayCurrentValues();                                                                     // Display the current x,y,z accelerometer values

		zigbee_correction_total();

	}



	//*****************************************************************************************************************************************



	public void displayCleanValues()                                                                // display clean value of x,y,z accelerometer
	{
		averageX_text.setText("0.0");
		averageY_text.setText("0.0");
		averageZ_text.setText("0.0");
	}



	//*****************************************************************************************************************************************




	public void adjust_values()                                                                     // Adjusting the reading of accelerometer and produce average values
	{
		if (Accel_X<11&&Accel_X>0)                                                                  // Neglecting Acceleration values less than 1 and greater than 11 (Omitting Error)
		{
			sensor_countX++;                                                                        // Creating the index of the upcoming array
			arrayX[sensor_countX] = Accel_X;                                                        // Creating an array of 30 values of Acceleration in X-axis

			// Taking average of 25 reading of Acceleration in X-axis

			if (sensor_countX == 29)                                                                // Detection for the end of the array
			{
				for (int ix = 5; ix < 30; ix++)                                                     // looping on all array values neglecting first 5 readings (Omitting Error)
					sumX = sumX + arrayX[ix];                                                       // Calculating sum of all values of the array

				averageX = sumX / 25;                                                               // Calculating average of all values of the array

				sensor_countX = 0;                                                                  // Resting all variables for the next array
				sumX=0;
				sens_countX++;
			}
		}

		if (Accel_Y<11&&Accel_Y>0)                                                                  // Neglecting Acceleration values less than 1 and greater than 11 (Omitting Error)
		{
			sensor_countY++;                                                                        // Creating the index of the upcoming array
			arrayY[sensor_countY] = Accel_Y;                                                        // Creating an array of 30 values of Acceleration in Y-axis

			if (sensor_countY == 29)                                                                // Detection for the end of the array
			{
				for (int iy = 5; iy < 30; iy++)                                                     // looping on all array values neglecting first 5 readings (Omitting Error)
					sumY = sumY + arrayY[iy];                                                       // Calculating sum of all values of the array

				averageY = sumY / 25;                                                               // Calculating average of all values of the array

				sensor_countY = 0;                                                                  // Resting all variables for the next array
				sumY=0;
				sens_countY++;
			}
		}

		if (Accel_Z<11&&Accel_Z>0)                                                                  // Neglecting Acceleration values less than 1 and greater than 11 (Omitting Error)
		{
			sensor_countZ++;                                                                        // Creating the index of the upcoming array
			arrayZ[sensor_countZ] = Accel_Z;                                                        // Creating an array of 30 values of Acceleration in Z-axis

			if (sensor_countZ == 29)                                                                // Detection for the end of the array
			{
				for (int iz = 5; iz < 30; iz++)                                                     // looping on all array values neglecting first 5 readings (Omitting Error)
					sumZ = sumZ + arrayZ[iz];                                                       // Calculating sum of all values of the array

				averageZ = sumZ / 25;                                                               // Calculating average of all values of the array

				sensor_countZ = 0;                                                                  // Resting all variables for the next array
				sumZ=0;
				sens_countZ++;
			}
		}
	}



	//*****************************************************************************************************************************************



	public void calculate_pdr()                                                                     // Calculating position by Pedestrian Dead Reckoning (PDR)
	{
		// Counting number of steps

		RMS = Math.pow((averageZ*averageZ)+(averageX*averageX),.5);                           // Calculating the rms value (which will be the mathematical value used to detect step)


		delta_RMS=RMS-RMS_store;                                                                    // Determining the delta rms (difference between rms value of this step and the previous one)
		RMS_store=RMS;                                                                              // Storing the Value of rms value of this step
		delay--;                                                                                    // Detecting number of steps

		if(delta_RMS>step_detect&&delay<0)                                                                   // if the rms value is high (which is an indication for a new step) and the delay is negative (which means the delay time initiated after the step, has finished)
		{
			count_step++;                                                                            // Increase step counter
			delay=200;                                                                               // Initiate the delay
			RMS_text.setText(Double.toString(delta_RMS));                                         // Display the value of initial azimuth
		}


		//Determining Approximated Azimuth

		if (gyro>1)
		{
			count_gyro_left++;
		}

		if (gyro<-1)
		{
			count_gyro_right++;
		}


		if (gyro<1&&(gyro>-1))
		{
			if (count_gyro_right>150&&count_gyro_right<280)
			{
				approx_azimuth += 90;
			}
			if (count_gyro_right>280)
			{
				approx_azimuth += 180;
			}


			if (count_gyro_left>150&&count_gyro_left<280)
			{
				approx_azimuth += -90;
			}
			if (count_gyro_left>280)
			{
				approx_azimuth += -180;
			}

			if ( approx_azimuth==360||approx_azimuth==-360)
			{
				approx_azimuth= 0;
			}



			count_gyro_left=0;
			count_gyro_right=0;
		}



		if (count_step>count_step_old)                                                              // if a step is taken
		{
			// Calculating position

			// Calculating PDR Position

			X_pdr=(.6)*(Math.round((Math.cos(Math.toRadians(approx_azimuth))) * 10000.0) / 10000.0);// Final Value of X-axes (PDR)
			Y_pdr=(.6)*(Math.round((Math.sin(Math.toRadians(approx_azimuth))) * 10000.0) / 10000.0);// Final Value of Y-axes (PDR)

			X_pdr_total=X_pdr_total+X_pdr;                                                          // Total Value of X-axes (PDR)
			Y_pdr_total=Y_pdr_total+Y_pdr;                                                          // Total Value of Y-axes (PDR)

			long CoordX_pdr= Math.round(X_pdr_total * 160/12);                                      // Converting the value of X-axes (PDR) from meter to pixels
			long CoordY_pdr= Math.round(Y_pdr_total * 160/12);                                      // Converting the value of Y-axes (PDR) from meter to pixels

			Button_pdr.setX(CoordX_pdr + 160);                                                      // Plotting the value of X-axes (PDR) on map
			Button_pdr.setY(CoordY_pdr + 12);                                                       // Plotting the value of Y-axes (PDR) on map




			X_pdr_total_corrected=X_pdr_total_corrected+X_pdr;
			Y_pdr_total_corrected=Y_pdr_total_corrected+Y_pdr;


			long CoordX_pdr_corrected= Math.round(X_pdr_total_corrected * 160/12);
			long CoordY_pdr_corrected= Math.round(Y_pdr_total_corrected * 160/12);

			Button_pdr_corrected.setX(CoordX_pdr_corrected + 160);
			Button_pdr_corrected.setY(CoordY_pdr_corrected + 12);
			Button_pdr_corrected.setY(CoordY_pdr_corrected + 12);


			pdr_correctedX.setText(Double.toString(X_pdr_total_corrected));
			pdr_correctedY.setText(Double.toString(Y_pdr_total_corrected));




			count_step_old=count_step;                                                              // Store the number of steps till now

			pdrX.setText(Double.toString(X_pdr_total));                                             // Writing the value of X-axes (PDR) in a text box
			pdrY.setText(Double.toString(Y_pdr_total));                                             // Writing the value of Y-axes (PDR) in a text box


			// Calculating Zigbee Corrected Position

			long CoordX_corrected= Math.round(X_corrected * 160/12);                                // Converting the value of X-axes (Zigbee Corrected) from meter to pixels
			long CoordY_corrected= Math.round(Y_corrected * 160/12);                                // Converting the value of Y-axes (Zigbee Corrected) from meter to pixels

		    Button_zigbee.setX(CoordX_corrected + 160);                                             // Plotting the value of X-axes (Zigbee Corrected) on map
			Button_zigbee.setY(CoordY_corrected + 12);                                              // Plotting the value of Y-axes (Zigbee Corrected) on map

			zigbeeX.setText(Double.toString(X_corrected));                                          // Writing the value of X-axes (Zigbee Corrected) in a text box
			zigbeeY.setText(Double.toString(Y_corrected));                                          // Writing the value of Y-axes (Zigbee Corrected) in a text box

		}
	}



	//*****************************************************************************************************************************************



	public void displayCurrentValues()                                                              // Display the current x,y,z accelerometer values
	{
		averageX_text.setText(Float.toString(averageX));                                            // Display Acceleration in X-axis
		averageY_text.setText(Float.toString(averageY));                                            // Display Acceleration in Y-axis
		averageZ_text.setText(Float.toString(averageZ));                                            // Display Acceleration in Z-axis

		compass.setText(Float.toString(azimuth));                                                   // Display azimuth value of the compass

		gyro_text.setText(Float.toString(gyro));                                                    // Display Gyroscope in X-axis

		approx_azimuth_text.setText(Float.toString(approx_azimuth));                                // Display the value of the approximated azimuth which is the approximated orientation of the tablet with respect to initial orientation
		count_step_text.setText(Float.toString(count_step));                                        // Display the number of steps done by the pedestrian
		delay_text.setText(Float.toString(delay));                                                  // Display the value of delay time (which resembles the time in which the system doesn't consider any additional step, to prevent calculating a step two times)  )

	}



	//*****************************************************************************************************************************************



	public void read_zigbee(String data)                                                            // Read data from Zigbee
	{

		//readText.append(data + "("+String.valueOf(data.length())+")"+"???");

		if(data.length()==20)
		{

			//readText.append(data.charAt(1) + "/"+ data.charAt(2)+ "/"+ data.charAt(3)+ "/"+ data.charAt(4) + "/"+ data.charAt(5)+ "/"+ data.charAt(6)+ "/"+data.charAt(7) + "/"+ data.charAt(8)+ "/"+ data.charAt(9)+ "/"+"*****************"+"\n");


			if (Character.getNumericValue(data.charAt(1)) == 2)
			{
				if (Character.getNumericValue(data.charAt(3)) == 0)
				{
					N2_textView.setText("0");
					N2="0";
				}
				else
				{
					N2 = data.substring(3, 6);
					N2_textView.setText(N2);
				}

			}

			if (Character.getNumericValue(data.charAt(1))==3)
			{
				if (Character.getNumericValue(data.charAt(3)) == 0)
				{
					N3_textView.setText("0");
					N3="0";
				}
				else
				{
					N3 = data.substring(3, 6);
					N3_textView.setText(N3);
				}

			}


			if (Character.getNumericValue(data.charAt(1))==4)
			{
				if (Character.getNumericValue(data.charAt(3)) == 0)
				{
					N4_textView.setText("0");
					N4="0";
				}
				else
				{
					N4 = data.substring(3, 6);
					N4_textView.setText(N4);
				}

			}

			if (Character.getNumericValue(data.charAt(1))==5)
			{
				if (Character.getNumericValue(data.charAt(3)) == 0)
				{
					N5_textView.setText("0");
					N5="0";
				}
				else
				{
					N5 = data.substring(3, 6);
					N5_textView.setText(N5);
				}

			}

			if (Character.getNumericValue(data.charAt(1))==6)
			{
				if (Character.getNumericValue(data.charAt(3)) == 0)
				{
					N6_textView.setText("0");
					N6="0";
				}
				else
				{
					N6 = data.substring(3, 6);
					N6_textView.setText(N6);
				}

			}


			if (Character.getNumericValue(data.charAt(1))==7)
			{
				if (Character.getNumericValue(data.charAt(3)) == 0)
				{
					//N7_textView.setText("0");
					//N7="0";
				}
				else
				{
					//N7 = data.substring(3, 6);
					//N7_textView.setText(N7);
				}

			}


			if (Character.getNumericValue(data.charAt(1))==8)
			{
				if (Character.getNumericValue(data.charAt(3)) == 0)
				{
					N8_textView.setText("0");
					N8="0";
				}
				else
				{
					N8 = data.substring(3, 6);
					N8_textView.setText(N8);
				}
			}

			if (Character.getNumericValue(data.charAt(1))==9)
			{
				if (Character.getNumericValue(data.charAt(3)) == 0)
				{
					N9_textView.setText("0");
					N9="0";
				}
				else
				{
					N9 = data.substring(3, 6);
					N9_textView.setText(N9);
				}
			}


			if (Character.getNumericValue(data.charAt(1))==1)
			{
				if (Character.getNumericValue(data.charAt(2))==0)
				{
					if (Character.getNumericValue(data.charAt(4)) == 0)
					{
						N10_textView.setText("0");
						N10="0";

					}
					else
					{
						N10 = data.substring(4, 7);
						N10_textView.setText(N10);
					}
				}
				else if (Character.getNumericValue(data.charAt(2))==1)
				{
					if (Character.getNumericValue(data.charAt(4)) == 0)
					{
					    N11_textView.setText("0");
					}
					else
					{
						N11 = data.substring(4, 7);
						N11_textView.setText(N11);
					}

				}
				else if (Character.getNumericValue(data.charAt(2))==2)
				{
					if (Character.getNumericValue(data.charAt(4)) == 0)
					{
						//N12_textView.setText("0");
					}
					else
					{
						//N12 = data.substring(4, 7);
						//N12_textView.setText(N12);
					}
				}


				else
				{
					if (Character.getNumericValue(data.charAt(3)) == 0)
					{
						N1_textView.setText("0");
						N1="0";

					}
					else
					{
						N1 = data.substring(3, 6);
						N1_textView.setText(N1);
					}
				}
			}
		}

	}



	//*****************************************************************************************************************************************



	public void zigbee_correction_total() {
		detect = 0;
		detect = zigbee_correction(N1, N1x, N1y);
		test1.setText(Integer.toString(detect));

		if (detect == 0)
		{
			detect = zigbee_correction(N2, N2x, N2y);
			test2.setText(Integer.toString(detect));
		}

		if (detect == 0)
		{
			detect = zigbee_correction(N3, N3x, N3y);
			test3.setText(Integer.toString(detect));
		}

		if (detect == 0)
		{
			detect = zigbee_correction(N4, N4x, N4y);
			test4.setText(Integer.toString(detect));
		}

		if 	(detect == 0)
		{
			detect = zigbee_correction(N5, N5x, N5y);
			test5.setText(Integer.toString(detect));
		}

		if 	(detect == 0)
		{
			detect = zigbee_correction(N6, N6x, N6y);
			test6.setText(Integer.toString(detect));
		}

		if 	(detect == 0)
		{
			detect = zigbee_correction(N7, N7x, N7y);
			test7.setText(Integer.toString(detect));
		}

		if 	(detect == 0)
		{
			detect = zigbee_correction(N8, N8x, N8y);
			test8.setText(Integer.toString(detect));
		}

		if 	(detect == 0)
		{
			detect = zigbee_correction(N9, N9x, N9y);
			test9.setText(Integer.toString(detect));
		}

		if 	(detect == 0)
		{
			detect = zigbee_correction(N10, N10x, N10y);
			test10.setText(Integer.toString(detect));
		}


			//if (detect==1)
			//	N12_textView.setText("N7");

		if 	(detect == 1)
		{
			N12_textView.setText("Yes");
		}
		if 	(detect == 0)
		{
			X_corrected = X_pdr_total;
			Y_corrected = Y_pdr_total;
		    N12_textView.setText("NO");
		}

	}



	//*****************************************************************************************************************************************



	public int zigbee_correction(String N_string, double Nx, double Ny)
	{
		    if (N_string.length()>1)
			{
				int N = Integer.parseInt(N_string.substring(1, 3));;

				double dist = Math.pow(((Math.pow((Nx - X_pdr_total_corrected), 2)) + (Math.pow((Ny - Y_pdr_total_corrected), 2))), 0.5);
				N7_textView.setText(String.valueOf(dist));

				if ((N >= 0) && (N <= 40)) {
					if (dist < 6)                                                                       // PDR is right
					{
						X_corrected = X_pdr_total;
						Y_corrected = Y_pdr_total;
						ret = 1;
						correct=0;

					} else                                                                                // PDR is wrong
					{
						double m = ((Y_pdr_total - Ny) / (X_pdr_total - Nx));
						double x3_estimated = Nx + (Math.pow((9 / (1 + (m * m))), 0.5));
						if ((x3_estimated>Nx&&(x3_estimated<X_pdr_total)))
						{
							x3=x3_estimated;
						}
						else
						{
							x3= Nx - (Math.pow((9 / (1 + (m * m))), 0.5));
						}

						double y3 = Ny + (m * (x3 - Nx));
						X_corrected = x3;
						Y_corrected = y3;
						if (correct==0)
						{
							X_pdr_total_corrected = x3;
							Y_pdr_total_corrected = y3;
							correct=1;
						}

						ret = 2;
					}

				} else if ((N > 40) && (N <= 50)) {
					if (dist > 6 && dist < 12)                                                          // PDR is right
					{
						X_corrected = X_pdr_total;
						Y_corrected = Y_pdr_total;
						ret = 3;
					} else                                                                                // PDR is wrong
					{
						double m = ((Y_pdr_total - Ny) / (X_pdr_total - Nx));
						double x3_estimated = Nx + (Math.pow((81 / (1 + (m * m))), 0.5));
						if ((x3_estimated>Nx&&(x3_estimated<X_pdr_total)))
						{
							x3=x3_estimated;
						}
						else
						{
							x3= Nx - (Math.pow((81 / (1 + (m * m))), 0.5));
						}
						double y3 = Ny + (m * (x3 - Nx));
						X_corrected = x3;
						Y_corrected = y3;


						correct=0;

						ret = 4;
					}
				} else
				{
					X_corrected = X_pdr_total;
					Y_corrected = Y_pdr_total;
					ret = 0;
					correct=0;
				}
			}
			else
			{
				X_corrected = X_pdr_total;
				Y_corrected = Y_pdr_total;
				ret = 0;
				correct=0;
			}

		return (ret);
	}






	//*****************************************************************************************************************************************
	//*****************************************************************************************************************************************
	//*****************************************************************************************************************************************

	//Serial Communication Functions

	public void serial_initialize()                                                                 // Initialize all serial communication items
	{

		try
		{
			ftD2xx = D2xxManager.getInstance(this);
		} catch (D2xxManager.D2xxException e)
		{
			Log.e("FTDI_HT", "getInstance fail!!");
		}


		global_context = this;

		// init modem variables
		modemReceiveDataBytes = new int[1];
		modemReceiveDataBytes[0] = 0;
		modemDataBuffer = new byte[MODEM_BUFFER_SIZE];
		zmDataBuffer = new byte[MODEM_BUFFER_SIZE];

		// file explore settings:
		fileDialog = new SelectFileDialog(this, handler, mPath);
		fileDialog.setCanceledOnTouchOutside(false);
		fileDialog.addFileListener(new SelectFileDialog.FileSelectedListener() {
			public void fileSelected(File file) {
				Log.d(getClass().getName(), "selected file " + file.toString());
				fGetFile = file;
			}
		});


		//readText = (TextView) findViewById(R.id.ReadValues);


		/* allocate buffer */
		writeBuffer = new byte[512];
		readBuffer = new byte[UI_READ_BUFFER_SIZE];
		readBufferToChar = new char[UI_READ_BUFFER_SIZE];
		readDataBuffer = new byte[MAX_NUM_BYTES];
		actualNumBytes = 0;

		// start main text area read thread
		handlerThread = new HandlerThread(handler);
		handlerThread.start();

		/* setup the baud rate list*/
		baudAdapter = ArrayAdapter.createFromResource(this,
				R.array.baud_rate_1, R.layout.my_spinner_textview);
		baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		baudRate = 115200;

		/* stop bits */
		ArrayAdapter<CharSequence> stopAdapter = ArrayAdapter.createFromResource(this,
				R.array.stop_bits, R.layout.my_spinner_textview);
		stopAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		stopBit = 1;

		/* data bits */
		ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
				R.array.data_bits, R.layout.my_spinner_textview);
		dataAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		dataBit = 8;

		/* parity */
		ArrayAdapter<CharSequence> parityAdapter = ArrayAdapter.createFromResource(this,
				R.array.parity, R.layout.my_spinner_textview);
		parityAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		parity = 0;

		/* flow control */
		ArrayAdapter<CharSequence> flowAdapter = ArrayAdapter.createFromResource(this,
				R.array.flow_control, R.layout.my_spinner_textview);
		flowAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		flowControl = 1;

		/* port */
		portAdapter = ArrayAdapter.createFromResource(this, R.array.port_list_1,
				R.layout.my_spinner_textview);
		flowAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		portIndex = 0;
	}


	// call this API to show message
    void midToast(String str, int showTime)
    {
		Toast toast = Toast.makeText(global_context, str, showTime);			
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL , 0, 0);
		
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextColor(Color.YELLOW);
		toast.show();	
    }


	// add data to UI(@+id/ReadValues)
	void appendData(String data) 
	{
		if(true == bContentFormatHex)
		{
			if(timesMessageHexFormatWriteData < 3)
			{
				timesMessageHexFormatWriteData++;
				midToast("The writing data won��t be showed on data area while content format is hexadecimal format.",Toast.LENGTH_LONG);
			}
			return;
		}

		if(true == bSendHexData)
		{
			SpannableString text = new SpannableString(data);
			text.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, data.length(), 0);
			//readText.append(text);
			bSendHexData = false;
		}
		else
		{
			read_zigbee(data);
		}
	}

	
	public void onAttachedToWindow() 
	{
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}	

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(KeyEvent.KEYCODE_HOME == keyCode)
		{
			DLog.e(TT, "Home key pressed");
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() 
	{
		if(false == bBackButtonClick)
		{
			midToast("Are you sure you will exit the program? Press again to exit.", Toast.LENGTH_LONG);
			
			back_button_click_time = System.currentTimeMillis();			
			bBackButtonClick = true;
			
			ResetBackButtonThread backButtonThread = new ResetBackButtonThread();
			backButtonThread.start();
		}
		else
		{	
			super.onBackPressed();
		}
	}

	class ResetBackButtonThread extends Thread
	{
		public void run() 
		{
			try 
			{
				Thread.sleep(3500);
			} catch (InterruptedException e) {e.printStackTrace();}
			
			bBackButtonClick = false;
		}
	}
	

// j2xx functions +
	public void createDeviceList()
	{
		int tempDevCount = ftD2xx.createDeviceInfoList(global_context);
		
		if (tempDevCount > 0)
		{
			if( DevCount != tempDevCount )
			{
				DevCount = tempDevCount;
			}
		}
		else
		{
			DevCount = -1;
			currentPortIndex = -1;
		}
	}

	public void disconnectFunction()
	{
		DevCount = -1;
		currentPortIndex = -1;
		bReadTheadEnable = false;
		try 
		{
			Thread.sleep(50);
		}
		catch (InterruptedException e) {e.printStackTrace();}
		
		if(ftDev != null)
		{
			if( true == ftDev.isOpen())
			{
				ftDev.close();
			}
		}
	}
	
	public void connectFunction()
	{
		if( portIndex + 1 > DevCount) 
		{
			portIndex = 0;
		}
		
		if( currentPortIndex == portIndex
				&& ftDev != null 
				&& true == ftDev.isOpen() )
		{
			//Toast.makeText(global_context,"Port("+portIndex+") is already opened.", Toast.LENGTH_SHORT).show();
			return;
		}
        
		if(true == bReadTheadEnable)
		{
			bReadTheadEnable = false;
			try 
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		if(null == ftDev)
		{
			ftDev = ftD2xx.openByIndex(global_context, portIndex);
		}
		else
		{
			ftDev = ftD2xx.openByIndex(global_context, portIndex);
		}
		uart_configured = false;

		if(ftDev == null)
		{
			midToast("Open port("+portIndex+") NG!", Toast.LENGTH_LONG);
			return;
		}
			
		if (true == ftDev.isOpen())
		{
			currentPortIndex = portIndex;
			//Toast.makeText(global_context, "open device port(" + portIndex + ") OK", Toast.LENGTH_SHORT).show();
				
			if(false == bReadTheadEnable)
			{	
				readThread = new ReadThread(handler);
				readThread.start();
			}
		}
		else 
		{			
			midToast("Open port("+portIndex+") NG!", Toast.LENGTH_LONG);			
		}
	}

	


	void setConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl)
	{
		// configure port
		// reset to UART mode for 232 devices
		ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

		ftDev.setBaudRate(baud);

		switch (dataBits)
		{
		case 7:
			dataBits = D2xxManager.FT_DATA_BITS_7;
			break;
		case 8:
			dataBits = D2xxManager.FT_DATA_BITS_8;
			break;
		default:
			dataBits = D2xxManager.FT_DATA_BITS_8;
			break;
		}

		switch (stopBits)
		{
		case 1:
			stopBits = D2xxManager.FT_STOP_BITS_1;
			break;
		case 2:
			stopBits = D2xxManager.FT_STOP_BITS_2;
			break;
		default:
			stopBits = D2xxManager.FT_STOP_BITS_1;
			break;
		}

		switch (parity)
		{
		case 0:
			parity = D2xxManager.FT_PARITY_NONE;
			break;
		case 1:
			parity = D2xxManager.FT_PARITY_ODD;
			break;
		case 2:
			parity = D2xxManager.FT_PARITY_EVEN;
			break;
		case 3:
			parity = D2xxManager.FT_PARITY_MARK;
			break;
		case 4:
			parity = D2xxManager.FT_PARITY_SPACE;
			break;
		default:
			parity = D2xxManager.FT_PARITY_NONE;
			break;
		}

		ftDev.setDataCharacteristics(dataBits, stopBits, parity);

		short flowCtrlSetting;
		switch (flowControl)
		{
		case 0:
			flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
			break;
		case 1:
			flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
			break;
		case 2:
			flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
			break;
		case 3:
			flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
			break;
		default:
			flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
			break;
		}

		ftDev.setFlowControl(flowCtrlSetting, XON, XOFF);

		midToast(uartSettings,Toast.LENGTH_SHORT);
		
		uart_configured = true;
	}


// j2xx functions -
	byte readData(int numBytes, byte[] buffer)
	{
		byte intstatus = 0x00; /* success by default */

		/* should be at least one byte to read */
		if ((numBytes < 1) || (0 == iTotalBytes))
		{
			actualNumBytes = 0;
			intstatus = 0x01;
			return intstatus;
		}

		if (numBytes > iTotalBytes)
		{
			numBytes = iTotalBytes;
		}

		/* update the number of bytes available */
		iTotalBytes -= numBytes;
		actualNumBytes = numBytes;

		/* copy to the user buffer */
		for (int count = 0; count < numBytes; count++) 
		{
			buffer[count] = readDataBuffer[iReadIndex];
			iReadIndex++;
			iReadIndex %= MAX_NUM_BYTES;
		}
		
		return intstatus;
	}



	final Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case UPDATE_TEXT_VIEW_CONTENT:
				if (actualNumBytes > 0)
				{
					totalUpdateDataBytes += actualNumBytes;
					for(int i=0; i<actualNumBytes; i++)
					{
						readBufferToChar[i] = (char)readBuffer[i];
					}
					appendData(String.copyValueOf(readBufferToChar, 0, actualNumBytes));
				}
				break;

			case UPDATE_SEND_FILE_STATUS:
	    	{
    			String temp = currentProtocol;
    			if(sendByteCount <= 10240)
    				temp += " Send:" + sendByteCount + "B("
    						+ new java.text.DecimalFormat("#.00").format(sendByteCount/(iFileSize/(double)100))+"%)";
    			else
    				temp += " Send:" +  new java.text.DecimalFormat("#.00").format(sendByteCount/(double)1024) + "KB("
    						+ new java.text.DecimalFormat("#.00").format(sendByteCount/(iFileSize/(double)100))+"%)";


    		}
				break;



			case MSG_SELECT_FOLDER_NOT_FILE:
				midToast("Do not pick a file.\n" +
        				"Plesae press \"Select Directory\" button to select current directory.", Toast.LENGTH_LONG);
				break;



			case UPDATE_MODEM_RECEIVE_DATA:
				midToast(currentProtocol + " - Receiving data...",Toast.LENGTH_LONG);

			case UPDATE_MODEM_RECEIVE_DATA_BYTES:
			{
    			String temp = currentProtocol;
    			if(totalModemReceiveDataBytes <= 10240)
    				temp += " Receive " + totalModemReceiveDataBytes + "Bytes";
    			else
    				temp += " Receive " +  new java.text.DecimalFormat("#.00").format(totalModemReceiveDataBytes/(double)1024) + "KBytes";


			}
				break;


			case MSG_FORCE_STOP_SEND_FILE:
				midToast("Stop sending file.", Toast.LENGTH_LONG);
				break;

			case UPDATE_ASCII_RECEIVE_DATA_BYTES:
			{
    			String temp = currentProtocol;
    			if(totalReceiveDataBytes <= 10240)
    				temp += " Receive " + totalReceiveDataBytes + "Bytes";
    			else
    				temp += " Receive " +  new java.text.DecimalFormat("#.00").format(totalReceiveDataBytes/(double)1024) + "KBytes";

        		long tempTime = System.currentTimeMillis();
        		Double diffime = (double)(tempTime-start_time)/1000;
				temp += " in " + diffime.toString() + " seconds";


			}
				break;

			case MSG_FORCE_STOP_SAVE_TO_FILE:
				midToast("Stop saving to file.", Toast.LENGTH_LONG);
				break;

			case UPDATE_ZMODEM_STATE_INFO:


				if(ZOO == zmodemState)
				{
					midToast("ZModem revice file done.", Toast.LENGTH_SHORT);
				}
				break;

			case ACT_ZMODEM_AUTO_START_RECEIVE:
				bUartModeTaskSet = false;
				transferMode = MODE_Z_MODEM_RECEIVE;
				currentProtocol = "ZModem";


				receivedPacketNumber = 1;
				modemReceiveDataBytes[0] = 0;
				totalModemReceiveDataBytes = 0;
				bDataReceived = false;
				bReceiveFirstPacket = false;
				fileNameInfo = null;



				zmodemState = ZRINIT;
				start_time = System.currentTimeMillis();

				break;


			case MSG_SPECIAL_INFO:

				midToast("INFO:" + (String)(msg.obj), Toast.LENGTH_LONG);
				break;

			case MSG_UNHANDLED_CASE:
				if(msg.obj != null)
					midToast("UNHANDLED CASE:"+ (String)(msg.obj), Toast.LENGTH_LONG);
				else
					midToast("UNHANDLED CASE ?", Toast.LENGTH_LONG);
				break;
			default:
				midToast("NG CASE", Toast.LENGTH_LONG);
				//Toast.makeText(global_context, ".", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};


	// Update UI content
	class HandlerThread extends Thread 
	{
		Handler mHandler;

		HandlerThread(Handler h) 
		{
			mHandler = h;
		}

		public void run() 
		{
			byte status;			
			Message msg;
			
			while (true) 
			{				
				try 
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e) {e.printStackTrace();}

				if(true == bContentFormatHex) // consume input data at hex content format
				{
					status = readData(UI_READ_BUFFER_SIZE, readBuffer);
				}
				else if(MODE_GENERAL_UART == transferMode)
				{
					status = readData(UI_READ_BUFFER_SIZE, readBuffer);
					
					if (0x00 == status) 
					{
						if(false == WriteFileThread_start)
						{
							checkZMStartingZRQINIT();
						}						
						
						// save data to file
						if(true == WriteFileThread_start && buf_save != null)
						{
							try
							{
								buf_save.write(readBuffer, 0, actualNumBytes);
							}  
							catch (IOException e){e.printStackTrace();}
						}
						
						msg = mHandler.obtainMessage(UPDATE_TEXT_VIEW_CONTENT);
						mHandler.sendMessage(msg);
					}
				}
			}
		}
	}

	class ReadThread extends Thread
	{
		final int USB_DATA_BUFFER = 8192;
		
		Handler mHandler;
		ReadThread(Handler h) 
		{
			mHandler = h;
			this.setPriority(MAX_PRIORITY);			
		}

		public void run() 
		{
			byte[] usbdata = new byte[USB_DATA_BUFFER];
			int readcount = 0;
			int iWriteIndex = 0;
			bReadTheadEnable = true;

			while (true == bReadTheadEnable) 
			{
				try 
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e) {e.printStackTrace();}		
				
				DLog.e(TT,"iTotalBytes:"+iTotalBytes);
				while(iTotalBytes > (MAX_NUM_BYTES - (USB_DATA_BUFFER+1)))
				{
					try 
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e) {e.printStackTrace();}						
				}

				readcount = ftDev.getQueueStatus();
				//Log.e(">>@@","iavailable:" + iavailable);
				if (readcount > 0) 
				{
					if(readcount > USB_DATA_BUFFER)
					{
						readcount = USB_DATA_BUFFER;
					}
					ftDev.read(usbdata, readcount);

					if( (MODE_X_MODEM_CHECKSUM_SEND == transferMode)
							||(MODE_X_MODEM_CRC_SEND == transferMode)
							||(MODE_X_MODEM_1K_CRC_SEND == transferMode) )
					{
						for (int i = 0; i < readcount; i++) 
						{	
							modemDataBuffer[i] = usbdata[i];
							DLog.e(TXS,"RT usbdata["+i+"]:("+usbdata[i]+")");
						}

						if(NAK == modemDataBuffer[0])
						{
							DLog.e(TXS,"get response - NAK"); 
							bModemGetNak = true;
						}
						else if(ACK == modemDataBuffer[0])
						{
							DLog.e(TXS,"get response - ACK");
							bModemGetAck = true;
						}
						else if(CHAR_C == modemDataBuffer[0])
						{
							DLog.e(TXS,"get response - CHAR_C");
							bModemGetCharC = true;									
						}
						if(CHAR_G == modemDataBuffer[0])
						{
							DLog.e(TXS,"get response - CHAR_G");
							bModemGetCharG = true;									
						}
					}
					else
					{						
						totalReceiveDataBytes += readcount;
						//DLog.e(TT,"totalReceiveDataBytes:"+totalReceiveDataBytes);

						//DLog.e(TT,"readcount:"+readcount);
						for (int count = 0; count < readcount; count++) 
						{	
							readDataBuffer[iWriteIndex] = usbdata[count];
							iWriteIndex++;
							iWriteIndex %= MAX_NUM_BYTES;
						}

						if (iWriteIndex >= iReadIndex)
						{
							iTotalBytes = iWriteIndex - iReadIndex;
						}
						else
						{
							iTotalBytes = (MAX_NUM_BYTES - iReadIndex) + iWriteIndex;
						}

						//DLog.e(TT,"iTotalBytes:"+iTotalBytes);
						if( (MODE_X_MODEM_CHECKSUM_RECEIVE == transferMode)
								|| (MODE_X_MODEM_CRC_RECEIVE == transferMode) 
								|| (MODE_X_MODEM_1K_CRC_RECEIVE == transferMode) 
								|| (MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode)
								|| (MODE_Z_MODEM_RECEIVE == transferMode)
								|| (MODE_Z_MODEM_SEND == transferMode) )
						{	
							modemReceiveDataBytes[0] += readcount;
							DLog.e(TT,"modemReceiveDataBytes:"+modemReceiveDataBytes[0]);
						}
					}
				}
			}

			DLog.e(TT, "read thread terminate...");;
		}		
	}


	
	void checkZMStartingZRQINIT()
	{		
		Message msg;
		for(int i = 0; i < actualNumBytes; i++)
		{
			switch(zmStartState)
			{
			case ZMS_0:
				if(0x72 == readBuffer[i]) zmStartState = ZMS_1;
				break;
			case ZMS_1:
				if(0x7A == readBuffer[i]) zmStartState = ZMS_2;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_2:
				if(0x0D == readBuffer[i]) zmStartState = ZMS_3;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_3:
				if(ZPAD == readBuffer[i]) zmStartState = ZMS_4;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_4:
				if(ZPAD == readBuffer[i]) zmStartState = ZMS_5;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_5:
				if(ZDLE == readBuffer[i]) zmStartState = ZMS_6;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_6:
				if(ZHEX == readBuffer[i]) zmStartState = ZMS_7;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_7:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_8;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_8:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_9;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_9:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_10;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_10:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_11;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_11:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_12;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_12:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_13;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_13:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_14;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_14:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_15;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_15:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_16;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_16:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_17;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_17:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_18;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_18:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_19;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_19:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_20;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_20:
				if(0x30 == readBuffer[i]) zmStartState = ZMS_21;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_21:
				if(0x0D == readBuffer[i]) zmStartState = ZMS_22;
				else  zmStartState = ZMS_0;
				break;
			case ZMS_22:
				if(0x0A == readBuffer[i] || (byte)0x8A == readBuffer[i]) zmStartState = ZMS_23;
				else{
					DLog.e(TT,"ZMS_22 stop, readBuffer[i]:"+Integer.toHexString(readBuffer[i]));
					zmStartState = ZMS_0;
				}
				break;
			case ZMS_23:
				if(0x11 == readBuffer[i]) zmStartState = ZMS_24;
				else  zmStartState = ZMS_0;
				break;
			default:
				break;
			}
			
			if(zmStartState >= ZMS_1)
			{
				DLog.e(TZR,"zmStartState:"+zmStartState);
			}
			
			if(ZMS_24 == zmStartState)
			{
				DLog.e(TZR,"ZModem auto-start receiving file");
				zmStartState = ZMS_0;
				msg= handler.obtainMessage(ACT_ZMODEM_AUTO_START_RECEIVE);
				handler.sendMessage(msg);
			}
		}
	}
}