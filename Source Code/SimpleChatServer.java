import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class SimpleChatServer
{
	JFrame frame; 
	JPanel panel1; 
	JPanel panel2; 
	JPanel panel3; 
	JTextArea messages=new JTextArea(30, 50); 
	
	
	private ArrayList<PrintWriter> clientOutputStreams;
	public static void main(String[] srgs)
	{
		SimpleChatServer server=new SimpleChatServer();
		server.go(); //putting things into action
	}
	
	public void go()
	{	
		//creating GUI
		frame=new JFrame("Many-many chat Server");
		frame.setSize(600, 600);
		panel1=new JPanel();
		panel2=new JPanel();
		try
		{
			panel1.add(new JLabel("My IP address is: "+InetAddress.getLocalHost().getHostAddress())); //displaying IP address of the server's machine
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		JScrollPane scroller=new JScrollPane(messages); 
		messages.setEditable(false);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel2.add(scroller);
		panel3=new JPanel();
		JButton saveButton=new JButton("Save Logs"); 
		saveButton.addActionListener(new saveButtonListener()); 
		panel3.add(saveButton);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(BorderLayout.NORTH, panel1);
		frame.getContentPane().add(BorderLayout.CENTER, panel2);
		frame.getContentPane().add(BorderLayout.SOUTH, panel3);
		frame.setVisible(true);
		clientOutputStreams=new ArrayList<PrintWriter>(); 
		
		try
		{
			ServerSocket serverSocket=new ServerSocket(5000); 
			
			
			while(true)
			{
				try
				{
					Socket clientSocket=serverSocket.accept();
					PrintWriter writer=new PrintWriter(clientSocket.getOutputStream());
					String clientAddress=clientSocket.getInetAddress().getHostAddress();

					
					clientOutputStreams.add(writer);
					
					Thread t=new Thread(new ClientHandler(clientSocket));
					t.start();
					messages.append("\n\t                             Got a new connection from "+ clientAddress+"\n");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private class saveButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			try
			{
				JFileChooser filesave=new JFileChooser();
				filesave.showSaveDialog(frame);
				BufferedWriter writer=new BufferedWriter(new FileWriter(filesave.getSelectedFile()+".txt"));
				writer.write(messages.getText());
				writer.flush();
				writer.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	private class ClientHandler implements Runnable
	{
		BufferedReader reader;
		Socket clientsocket;
		
		ClientHandler(Socket socket)
		{
			try
			{
				clientsocket=socket;
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		public void run()
		{
			String message;
			try
			{
				while((message=reader.readLine())!=null)
				{
					messages.append(message+"\n");
					sendEveryone(message);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		private void sendEveryone(String message)
		{
				Iterator it=clientOutputStreams.iterator();
				
				
				while(it.hasNext())
				{
					try
					{
						PrintWriter writer=(PrintWriter)it.next();
						writer.println(message);
						writer.flush();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
		}
	}
}