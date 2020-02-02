/*    T E S T I N G    */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;

class MsgBox extends Dialog implements ActionListener {
    private Button ok,can;
    public boolean isOk = false;

    /*
     * @param frame   parent frame 
     * @param msg     message to be displayed
     * @param okcan   true : ok cancel buttons, false : ok button only 
     */
    MsgBox(Frame frame, String msg, boolean okcan){
        super(frame, "Message", true);
        setLayout(new BorderLayout());
        add("Center",new Label(msg));
        addOKCancelPanel(okcan);
        createFrame();
        pack();
        setVisible(true);
    }
    
    MsgBox(Frame frame, String msg){
        this(frame, msg, false);
    }
    
    void addOKCancelPanel( boolean okcan ) {
        Panel p = new Panel();
        p.setLayout(new FlowLayout());
        createOKButton( p );
        if (okcan == true)
            createCancelButton( p );
        add("South",p);
    }

    void createOKButton(Panel p) {
        p.add(ok = new Button("OK"));
        ok.addActionListener(this); 
    }

    void createCancelButton(Panel p) {
        p.add(can = new Button("Cancel"));
        can.addActionListener(this);
    }

    void createFrame() {
        Dimension d = getToolkit().getScreenSize();
        setLocation(d.width/3,d.height/3);
    }

    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == ok) {
            isOk = true;
            setVisible(false);
        }
        else if(ae.getSource() == can) {
            setVisible(false);
        }
    }
}

/* This Class is used to Display the Input and Output Images */
class MyDisplay extends Frame
{
	Image i;
	Insets t;

	/* Constructor to Pass Input or Output Image Details */
	MyDisplay(String title, String t, Dimension size)
	{
		setTitle(title);
		setSize(size);
		setLocation(0,0);
		setLayout(null);
		setVisible(true);
				
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				/* Close Window on Exit */
        		dispose();
			}
		});
		
		/* Try to Read the Image Passed as Parameter */
		try
		{
			i = ImageIO.read(new File(t));
		}
		catch(IOException e){}
	}
	
	public void paint(Graphics g)
	{
		/* Retrieve the Space Covered by the Left and Top Borders of the Frame */
		t = getInsets();
		g.drawImage(i, t.left, t.top, this);
	}
}

public class KMeans extends Frame implements ActionListener
{
	Insets t;				// Holds Frame Insets for later Adjustment

	/* Data Structures ( this section will be extensively modified ) */
	Image I;
	BufferedImage B;
	
	int W,H;				// Width and Height of the Image
	
	int K;					// Holds value of K input by user
	double P;				// Holds value of P input by user
	
	int l;					// Intensity Level corresponding to  1 Percentile
	int h;					// Intensity Level corresponding to 99 Percentile
	
	double Mo[],Mn[];		// Array to hold the Old and New K means
	
	int If[];				// Array to hold the Final K Intensities
	int Ii[];				// Array to hold the Final K Intensities' Indices
	int Fv[];				// Array to hold the Frequency Values
	int Fi[];				// Array to hold the Frequency Indices
	int F[];				// Array to hold the Pixel Intensity Frequencies
	
	int temp;
	
	int C[];				// Array to hold the Number of Pixels in Each of the K Groups
	double E;				// Epsilon
	boolean S;				// STOPping Condition
	int G[];				// Array to hold the Grouping Information
	int Id[];				// Array to hold the Final Intensities' Distances from each other
	
	long time;				// Holds the starting time of the clustering process in milliseconds
	
	int pix,red;			// Pixel value and it's Red Component
	
	/* Controls */
	Label lInput;
	TextField tInput;
	Button bInput;
	Label lOutput;
	TextField tOutput;
	Button bOutput;
	Label lKvalue;
	TextField tKvalue;
	Label lEvalue;
	TextField tEvalue;
	Label lPvalue;
	TextField tPvalue;
	Button bRun;
	
	/* Constructor */
	KMeans()
	{
		super("Fast K-Means Grayscale Image Enhancement");
		setSize(430,270);
		setLocation(40,30);
		setLayout(null);
		setResizable(false);
		setVisible(true);

		/* Add Controls to the Frame */
		t=getInsets();
		addControls();

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				/* Close window & quit application on exit */
        		System.exit(0);
			}
		});
	}
	
	/* Method to Add Controls to the Frame */
	private void addControls()
	{
		lInput=new Label("Input Image :",Label.RIGHT);
		lInput.reshape(t.left+30,t.top+40,106,14);
		add(lInput);
		
		tInput=new TextField("Input Image");
		tInput.reshape(t.left+150,t.top+40,130,20);
		add(tInput);
		
		bInput=new Button("Browse");
		bInput.reshape(t.left+300,t.top+37,67,23);
		bInput.addActionListener(this);
		add(bInput);
		
		lOutput=new Label("Output Image :",Label.RIGHT);
		lOutput.reshape(t.left+30,t.top+70,106,14);
		add(lOutput);
		
		tOutput=new TextField("Output Image");
		tOutput.reshape(t.left+150,t.top+70,130,20);
		add(tOutput);
		
		bOutput=new Button("Browse");
		bOutput.reshape(t.left+300,t.top+67,67,23);
		bOutput.addActionListener(this);
		add(bOutput);
		
		lKvalue=new Label("K Value :",Label.RIGHT);
		lKvalue.reshape(t.left+30,t.top+100,106,14);
		add(lKvalue);
		
		tKvalue=new TextField("30");
		tKvalue.reshape(t.left+150,t.top+100,130,20);
		add(tKvalue);

		lEvalue=new Label("E Value :",Label.RIGHT);
		lEvalue.reshape(t.left+30,t.top+130,106,14);
		add(lEvalue);
		
		tEvalue=new TextField("0.000001");
		tEvalue.reshape(t.left+150,t.top+130,130,20);
		add(tEvalue);
		
		lPvalue=new Label("P Value :",Label.RIGHT);
		lPvalue.reshape(t.left+30,t.top+160,106,14);
		add(lPvalue);
		
		tPvalue=new TextField("1.0");
		tPvalue.reshape(t.left+150,t.top+160,130,20);
		add(tPvalue);
		
		bRun=new Button("Run");
		bRun.reshape(t.left+300,t.top+157,67,23);
		bRun.addActionListener(this);
		add(bRun);
	}

	/* Action Handler for the Controls */
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource()==bInput)
		{
			/* Display File Dialog for Selecting Input Image */
			FileDialog fd = new FileDialog(this,"Select Input Image",FileDialog.LOAD);
			fd.setVisible(true);
			tInput.setText(fd.getDirectory()+fd.getFile());
			new MyDisplay("Input Image",tInput.getText(),new Dimension(400,400));
		}
		if(ae.getSource()==bOutput)
		{
			/* Display File Dialog for Selecting Output Image */
			FileDialog fd = new FileDialog(this,"Select Output Image",FileDialog.SAVE);
			fd.setVisible(true);
			tOutput.setText(fd.getDirectory()+fd.getFile());
		}
		if(ae.getSource()==bRun)
		{
			/* Try to Read Image */
			I = null;
			try
			{
				I = ImageIO.read(new File(tInput.getText()));
			}
			catch(IOException e){}

			/* Get the Width and Height of the Image */
			W = I.getWidth(this);
			H = I.getHeight(this);

			/* Construct B with W,H */
			B = new BufferedImage(W,H,BufferedImage.TYPE_INT_RGB);
			B.getGraphics().drawImage(I,0,0,this);
			
			/* Get the values of K, E and P input by the user in the TextBox */
			K = Integer.parseInt(tKvalue.getText());
			E = Double.parseDouble(tEvalue.getText());
			P = Double.parseDouble(tPvalue.getText());
			
			/* Initialize time */
			time = System.currentTimeMillis();
			
			/* Allocate Memory for the Arrays to be used */
			Fv = new int[256];
			Fi = new int[256];
			F = new int[256];
			Mo = new double[K];
			Mn = new double[K];
			If = new int[K];
			Ii = new int[K];
			Id = new int[K-2];
			C = new int[K];
			G = new int[256];
			
			/* Tackle the Outliers (intensities lying outside the 1 and 99 percentiles) */
			/* Populate the Array F with the Pixel Intensity Frequencies */
			for(int i=0;i<H;i++)
				for(int j=0;j<W;j++)
				{
					pix = B.getRGB(j,i);
					red = 0xff & (pix >> 16);		// Store value of Red Component by Convention
					F[red]++;
				}
			
			pix = W * H;	// Calculate the Total number of Pixels in the Image
			
			temp = 0;	// Clear the Pixel Counter
			l = 0;		// Start from the Lowest possible Intensity Level
			do
			{
				temp += F[l];
				F[l++] = 0;
			}
			while((double)temp / (double)pix < P / (double)100);
			F[l] += temp;
			
			temp = 0;	// Clear the Pixel Counter
			h = 255;	// Start from the Highest possible Intensity Level
			do
			{
				temp += F[h];
				F[h--] = 0;
			}
			while((double)temp / (double)pix < P / (double)100);
			F[h] += temp;
			
			// System.out.println(F[h]+" , "+F[l]);
			// System.exit(0);
			
			/* Assign Frequency Indices to the Array Fi and Synchronize the Arrays Fv and F */
			for(int i=0;i<256;i++)
			{
				Fi[i] = i;
				Fv[i] = F[i];
			}
			
			/* Sort the Array Fv in Ascending Order and the Array Fi accordingly */
			for(int i=0;i<256-1;i++)
				for(int j=0;j<256-1-i;j++)
					if(Fv[j]>Fv[j+1])
					{
						temp = Fv[j];
						Fv[j] = Fv[j+1];
						Fv[j+1] = temp;
						temp = Fi[j];
						Fi[j] = Fi[j+1];
						Fi[j+1] = temp;
					}
			
			/* Select Last K Values from the Array Fi as the Starting Means */
			for(int i=0;i<K;i++)
			{
				Mo[i] = Fi[255-i];
				
				/* Debugging */
				// taTest.appendText("\nMo["+i+"]="+Mo[i]);
			}
			
			/* Main Loop */
			do
			{
				/* Clear the Group Counter Array and New K Means Array */
				for(int i=0;i<K;i++)
				{
					C[i] = 0;
					Mn[i] = 0;
				}
				
				/* Set STOPping Condition to True */
				S = true;
				
				/* Update the Grouping Information */
				for(int i=0;i<256;i++)
					if(F[i]>0)
					{
						/* Determine the Index of the Minimum Difference */
						int minL = 0,l = 0;
						for(double minV=255;l<K;l++)
							if(Math.abs((double)i-Mo[l])<minV)
							{
								minV = Math.abs((double)i-Mo[l]);
								minL = l;
							}
						
						/* Accordingly, Update Grouping Information */
						G[i] = minL;
						C[minL] += F[i];
						
						/* Start ReComputing the K Means */
						Mn[minL] += (double)i*(double)F[i];
					}
				
				/* Finish ReComputing the K Means and Update Stopping Condition and Array Mo */
				for(int i=0;i<K;i++)
					if(C[i]>0)		// Eliminate "Division By Zero" Errors
					{
						Mn[i] /= (double)C[i];
						if(Math.abs(Mn[i]-Mo[i])>E)
							S = false;
						Mo[i] = Mn[i];
					}
			}
			while(!S);
			
			/* Debugging */
			// for(int i=0;i<K;i++)
			//	taTest.appendText("\nMo["+i+"]="+Mo[i]);
			
			/* Initialize the Arrays If and Ii */
			for(int i=0;i<K;i++)
			{
				If[i] = (int)Mo[i];
				Ii[i] = i;
			}
			
			// for(int i=0;i<K;i++)
			//	System.out.println("\nIf["+i+"]="+If[i]);
			
			/* Sort the Array If in Ascending Order and the Array Ii Accordingly */
			for(int i=0;i<K-1;i++)
				for(int j=0;j<K-1-i;j++)
					if(If[j]>If[j+1])
					{
						temp = If[j];
						If[j] = If[j+1];
						If[j+1] = temp;
						temp = Ii[j];
						Ii[j] = Ii[j+1];
						Ii[j+1] = temp;
					}
			
			/* Initialize the Array Id */
			for(int i=0;i<K-2;i++)
				Id[i] = If[i+1] - If[i];
			
			/* Find the Maximum Possible Total Distance Increment */
			temp = (If[0]-0) + (255-If[K-1]);
			
			/* Find the Maximum Possible Distance Increment Per Distance */
			temp /= (K-1);
			
			/* Update the Array Id with the Increased Distances */
			for(int i=0;i<K-2;i++)
				Id[i] += temp;
			
			/* Update the Array If with the Changed Final Intensities */
			If[0] = 0;
			If[K-1] = 255;
			for(int i=1;i<K-1;i++)
				If[i] = If[i-1] + Id[i-1];
			
			// System.exit(0);
			
			/* Sort the Array Ii in Ascending Order and the Array If Accordingly */
			for(int i=0;i<K-1;i++)
				for(int j=0;j<K-1-i;j++)
					if(Ii[j]>Ii[j+1])
					{
						temp = Ii[j];
						Ii[j] = Ii[j+1];
						Ii[j+1] = temp;
						temp = If[j];
						If[j] = If[j+1];
						If[j+1] = temp;
					}
			
			/* Debugging */
			// for(int i=0;i<K;i++)
			//	System.out.println("\nIf["+i+"]="+If[i]);
			
			/* Update B with Modified Image Data */
			for(int i=0;i<H;i++)
				for(int j=0;j<W;j++)
				{
					/* Read and Process the Pixel Values */
					try
					{
						pix = B.getRGB(j,i);
					}
					catch(IllegalArgumentException e){}
					
					/* Extract the Red Component */
					red = 0xff & (pix >> 16);
					
					/* Determine whether the Pixel is an Outlier and Treat it accordingly */
					if(red < l)
						red = l;
					else
						if(red > h)
							red = h;
					
					/* Pack RGB Components of Group Mean of this Pixel into pix */
					pix = If[G[red]];
					pix <<= 8;
					pix += If[G[red]];
					pix <<= 8;
					pix += If[G[red]];
					
					/* Incorporate Modified Pixel Values into Image Data */
					B.setRGB(j,i,pix);
				}
			
			/* Calculate the time taken for processing */
			time = System.currentTimeMillis() - time;
			
			/* Write B as Output Image */
			try
			{
				ImageIO.write(B,tOutput.getText().toLowerCase().endsWith("jpg")?"jpg":"bmp",new File(tOutput.getText()));
			}
			catch(Exception e){}
			new MyDisplay("Output Image",tOutput.getText(),new Dimension(400,400));
			
			/* Display the time taken for processing */
			new MsgBox(this , "No. of pixels: " + W*H + " Processing time: " + time + " milliseconds", false);
		}
	}

	/* main() Method */
	public static void main (String[] args)
	{
		new KMeans();
	}
}