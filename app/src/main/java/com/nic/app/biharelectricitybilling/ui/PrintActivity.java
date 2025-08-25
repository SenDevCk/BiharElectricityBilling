package com.nic.app.biharelectricitybilling.ui;

import harmony.java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Set;
import java.util.UUID;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.nic.app.biharelectricitybilling.R;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast; 
public class PrintActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 99;
	private ActionBar actionBar;
	Button btnPrint;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	
	// needed for communication to bluetooth device / network
	OutputStream mmOutputStream;
	InputStream mmInputStream;
	Thread workerThread;
	 
	byte[] readBuffer;
	int readBufferPosition;
	volatile boolean stopWorker;
	
	static final int BUFF_SIZE = 2048;
	static final String DEFAULT_ENCODING = "utf-8";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print);
		
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
		
		// For displaying title and subtitle and change text color
		actionBar.setTitle(Html
				.fromHtml("<font color='#FFFFFF'>Print</font>"));
		
	
	try {
		bluetooth();
		openBT();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		btnPrint = (Button) findViewById(R.id.btn_print);
		btnPrint.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				/*Printer printer = new Printer(Printer.TM_T88, Printer.MODEL_ANK, getApplicationContext());
				try {
				printer.connect("TCP:192.168.192.168", Printer.PARAM_DEFAULT);
				//...processing...
				printer.disconnect();
				} catch (Epos2Exception e) {
				int errStatus = e.getErrorStatus();
				}*/
				
				try {
					sendData();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		createPDF();
	}
	
	public void createPDF()
    {
        Document doc = new Document( new Rectangle(0, 0, 220, 1440));
       
         try {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BEB/Pdf/";
                  
                File dir = new File(path);
                    if(!dir.exists())
                        dir.mkdirs();
 
                Log.d("PDFCreator", "PDF Path: " + path);
                 
                     
                File file = new File(dir, "kk.txt");
                FileOutputStream fOut = new FileOutputStream(file);
      
                PdfWriter.getInstance(doc, fOut);
                  
                //open the document
                doc.open();
                 
                 
                Paragraph p1 = new Paragraph(1f, "ENERGY BILL", new Font(Font.BOLD, 18));
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                
                 //add paragraph to document    
                 doc.add(p1);
                 
                /* PdfPTable table = new PdfPTable(10);
                 
                 table.setWidthPercentage(100);
                 table.setSpacingBefore(0f);
                 table.setSpacingAfter(0f);
          
                 // first row
                 PdfPCell cell = new PdfPCell(new Phrase("DateRange"));
                 cell.setColspan(10);
                 cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                 cell.setPadding(5.0f);
                 table.addCell(cell);
          
                 table.addCell("Calldate");
                 table.addCell("Calltime");
                 table.addCell("Source");
                 table.addCell("DialedNo");
                 table.addCell("Extension");
                 table.addCell("Trunk");
                 table.addCell("Duration");
                 table.addCell("Calltype");
                 table.addCell("Callcost");
                 table.addCell("Site");
          
                 for (int i = 0; i < 10; i++) {
                     table.addCell("date" + i);
                     table.addCell("time" + i);
                     table.addCell("source" + i);
                     table.addCell("destination" + i);
                     table.addCell("extension" + i);
                     table.addCell("trunk" + i);
                     table.addCell("dur" + i);
                     table.addCell("toc" + i);
                     table.addCell("callcost" + i);
                     table.addCell("Site" + i);
                 }
                 doc.add(table);
                */
                 Paragraph p2 = new Paragraph(1f, "SBPDCL", new Font(Font.BOLD, 18));
                 p2.setAlignment(Paragraph.ALIGN_CENTER);
                 p2.setSpacingBefore(15f);
                  
                 doc.add(p2);
                 
                 Paragraph p3 = new Paragraph("******************************");
                 doc.add(p3);
                 
                 Paragraph p4 = new Paragraph(1f,"ELECTRICITY BILL : FEB-2016",new Font(Font.NORMAL, 8));
                 Font paraFont4= new Font(Font.NORMAL);
                 p4.setAlignment(Paragraph.ALIGN_LEFT);
                 p4.setFont(paraFont4);
                 p4.setIndentationLeft(5f);
                 p4.setIndentationRight(5f);
                 p4.setSpacingBefore(5f);
                  
                 doc.add(p4);
                 
                 Paragraph p5 = new Paragraph("******************************");
                 doc.add(p5);
                 
                 Paragraph p6 = new Paragraph(1f,"DATE: 27-01-16   TIME: 01:55 \n \n \n \n \n \n \n CONSUMER DETAILS",new Font(Font.NORMAL, 8));
                 Font paraFont6= new Font(Font.NORMAL);
                 p6.setAlignment(Paragraph.ALIGN_LEFT);
                 p6.setFont(paraFont6);
                 p6.setIndentationLeft(5f);
                 p6.setIndentationRight(5f);
                 p6.setSpacingBefore(5f);
                 doc.add(p6);
                 
                 Paragraph p7 = new Paragraph("******************************");
                 doc.add(p7);
                 
                 float[] columnWidths = {4, 1,6};
                 PdfPTable table = new PdfPTable(columnWidths);
                 
                 table.setWidthPercentage(100);
                 table.setSpacingBefore(0f);
                 table.setSpacingAfter(0f);
                 
                 Font fontH1 = new Font( Font.NORMAL,8);
                 
                 
                 table.getDefaultCell().setBorderColor(Color.WHITE);
          String[] colum =  {"Bill No" ,"DIVISION","SUBDIVN","CA NUMBER","CON ID", "MRU"};
          String[] colum1 =  {"0123456789" ,"GARDANIBAG","GARDANIBAG","100684579","789654123", "ABAMAR123"};
          
            for (int i = 0; i < 6; i++) {
            		PdfPCell cell1 = new PdfPCell(new Phrase(colum[i],fontH1));
            		cell1.setBorder(Rectangle.NO_BORDER);
                     table.addCell(cell1);
                     
                     PdfPCell cell2 = new PdfPCell(new Phrase(":",fontH1));
             		cell2.setBorder(Rectangle.NO_BORDER);
                      table.addCell(cell2);
                     
                      PdfPCell cell3 = new PdfPCell(new Phrase(colum1[i],fontH1));
               		cell3.setBorder(Rectangle.NO_BORDER);
               		cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(cell3);
                     
                 }
                 
                 doc.add(table);
                  
                /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.biling);
                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
                 Image myImg = Image.getInstance(stream.toByteArray());
                 myImg.setAlignment(Image.MIDDLE);
                 
                 //add image to document
                 doc.add(myImg);*/
                 
                 //set footer
                 Phrase footerText = new Phrase("This is an example of a footer");
                 HeaderFooter pdfFooter = new HeaderFooter(footerText, false);
                 doc.setFooter(pdfFooter);
                 
 
                 
         } catch (DocumentException de) {
                 Log.e("PDFCreator", "DocumentException:" + de);
         } catch (IOException e) {
                 Log.e("PDFCreator", "ioException:" + e);
         } 
         finally
         {
                 doc.close();
         }
        
    }      

	private void bluetooth() {
		// TODO Auto-generated method stub
		
		
		 try {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			 if (mBluetoothAdapter == null) {
				    // Device does not support Bluetooth
				 
				 Toast.makeText(PrintActivity.this, "Device does not support Bluetooth",
							Toast.LENGTH_SHORT).show();
				}
			  if (!mBluetoothAdapter.isEnabled()) {
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}
			  
			  Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			  
		        if(pairedDevices.size() > 0) {
		            for (BluetoothDevice device : pairedDevices) {
		                 
		                // RPP300 is the name of the bluetooth printer device
		                // we got this name from the list of paired devices
		                if (device.getName().equals("TM-P20_000033")) {
		                    mmDevice = device;
		                    break;
		                }
		            }
		        }
				 Toast.makeText(PrintActivity.this, "Bluetooth device found.",
							Toast.LENGTH_SHORT).show();
		      
		 
			  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		

		 if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
			// get the returned data
			 Toast.makeText(PrintActivity.this, " Bluetooth On",
						Toast.LENGTH_SHORT).show();
		}else{
			 Toast.makeText(PrintActivity.this, " Bluetooth Off",
						Toast.LENGTH_SHORT).show();
		}
	}
	// tries to open a connection to the bluetooth printer device
	void openBT() throws IOException {
	    try {
	 
	        // Standard SerialPortService ID
	        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
	        mmSocket.connect();
	        mmOutputStream = mmSocket.getOutputStream();
	        mmInputStream = mmSocket.getInputStream();
	 
	        beginListenForData();
	 
	        Toast.makeText(PrintActivity.this, "Bluetooth Opened",
					Toast.LENGTH_SHORT).show();
	 
	    } catch (Exception e) {
	        e.printStackTrace();


	    }
	}
	
	
	 /* after opening a connection to bluetooth printer device,
	 * we have to listen and check if a data were sent to be printed.
	 */
	void beginListenForData() {
	    try {
	        final Handler handler = new Handler();
	         
	        // this is the ASCII code for a newline character
	        final byte delimiter = 10;
	 
	        stopWorker = false;
	        readBufferPosition = 0;
	        readBuffer = new byte[1024];
	         
	        workerThread = new Thread(new Runnable() {
	            public void run() {
	 
	                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
	                     
	                    try {
	                         
	                        int bytesAvailable = mmInputStream.available();
	 
	                        if (bytesAvailable > 0) {
	 
	                            byte[] packetBytes = new byte[bytesAvailable];
	                            mmInputStream.read(packetBytes);
	 
	                            for (int i = 0; i < bytesAvailable; i++) {
	 
	                                byte b = packetBytes[i];
	                                if (b == delimiter) {
	 
	                                    byte[] encodedBytes = new byte[readBufferPosition];
	                                    System.arraycopy(
	                                        readBuffer, 0,
	                                        encodedBytes, 0,
	                                        encodedBytes.length
	                                    );
	 
	                                    // specify US-ASCII encoding
	                                    final String data = new String(encodedBytes, "US-ASCII");
	                                    readBufferPosition = 0;
	 
	                                    // tell the user data were sent to bluetooth printer device
	                                    handler.post(new Runnable() {
	                                        public void run() {
	                                        	 Toast.makeText(PrintActivity.this, data,
	                             				Toast.LENGTH_SHORT).show();
	                                            //myLabel.setText(data);
	                                        }
	                                    });
	 
	                                } else {
	                                    readBuffer[readBufferPosition++] = b;
	                                }
	                            }
	                        }
	                         
	                    } catch (IOException ex) {
	                        stopWorker = true;
	                    }
	                     
	                }
	            }
	        });
	 
	        workerThread.start();
	 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	// this will send text data to be printed by the bluetooth printer
	void sendData() throws IOException {
	    try {
	         
	    	
	       /* // the text typed by the user
	        String msg = "Kaushal Kishore OmPrakash Ojha Manish Kumar Nic Patna Live in Dhanbad Jharkhand";
	        msg += "\n \n \n \n \n";*/
	        
	    	 String msg = readFileToString(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BEB/Pdf/kk.pdf", null);
		        
	         
	        mmOutputStream.write(msg.getBytes());
	         
	        // tell the user data were sent
	       // myLabel.setText("Data sent.");
	        Toast.makeText(PrintActivity.this, "Data sent.",
						Toast.LENGTH_SHORT).show();
	         
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static String readFileToString(String filePath, String encoding) throws IOException {

	    if (encoding == null || encoding.length() == 0)
	        encoding = DEFAULT_ENCODING;

	    StringBuffer content = new StringBuffer();

	    FileInputStream fis = new FileInputStream(new File(filePath));
	    byte[] buffer = new byte[BUFF_SIZE];

	    int bytesRead = 0;
	    while ((bytesRead = fis.read(buffer)) != -1)
	        content.append(new String(buffer, 0, bytesRead, encoding));

	    fis.close();        
	    return content.toString();
	}
}
