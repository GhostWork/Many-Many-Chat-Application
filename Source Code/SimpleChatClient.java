import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class SimpleChatClient
{
	Socket sock;
	PrintWriter writer;
	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	String name;
	String ip;
	JTextField ip1;
	JTextField ip2;
	JTextField ip3;
	JTextField ip4;
	JFrame nameAndIpframe;
	JTextField nameTextField;
	
	public static void main(String[] args)
	{
		SimpleChatClient client=new SimpleChatClient();
		client.getNameandIpAddress();
	}
	
	public void go()
	{
				
		JFrame frame=new JFrame("Welcome "+"\""+name+"\" to many-many chat program");
		JPanel panel=new JPanel();
		incoming=new JTextArea(15,50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane scroller=new JScrollPane(incoming);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing=new JTextField(50);
		JButton sendButton=new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		frame.setSize(600,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.add(scroller);
		panel.add(outgoing);
		panel.add(sendButton);
		
		Thread readerThread=new Thread(new IncomingReader());
		readerThread.start();
		
		frame.getContentPane().add(BorderLayout.CENTER, panel);
		frame.setVisible(true);
	}
	
	private boolean setUpNetworking()
	{
		try
		{
			sock=new Socket(ip, 5000);
			reader=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer=new PrintWriter(sock.getOutputStream());
			System.out.println("Connection with the server Established");
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private class SendButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			try
			{
				String outmessage=name+": "+outgoing.getText();
				writer.println(outmessage);
				writer.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				outgoing.setText("");
				outgoing.requestFocus();
			}
		}
	}
	
	private class IncomingReader implements Runnable
	{
		String inmessage;
	
			public void run()
			{
				try
				{
					while((inmessage=reader.readLine())!=null)
					{
						
						System.out.println("Read: "+inmessage);
						incoming.append(inmessage+"\n");
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		
	}
	
	 void getNameandIpAddress()
	{
		nameAndIpframe=new JFrame("Initialization");
		nameAndIpframe.setResizable(false);
		JPanel nameAndIpPanel=new JPanel(new FlowLayout());
		
		nameAndIpPanel.add(new JLabel("Your Name: "));
		nameTextField=new JTextField(20);
		name=nameTextField.getText();
		nameAndIpPanel.add(nameTextField);
		
		JButton sendNameAndIp=new JButton("I wanna have a chat RIGHT NOW!");
		sendNameAndIp.addActionListener(new sendNameAndIpButtonListener());
		nameAndIpPanel.add(new JLabel("                                                     "));
		nameAndIpPanel.add(new JLabel("                                     "));
		nameAndIpPanel.add(new JLabel("Server's port number is 5000.\n Enter its valid Ip Address: "));
		nameAndIpPanel.add(new JLabel("                    "));
		ip1=new JTextField(2);
		ip2=new JTextField(2);
		ip3=new JTextField(2);
		ip4=new JTextField(2);
		nameAndIpPanel.add(ip1);
		nameAndIpPanel.add(new Label("."));
		nameAndIpPanel.add(ip2);
		nameAndIpPanel.add(new Label("."));
		nameAndIpPanel.add(ip3);
		nameAndIpPanel.add(new Label("."));
		nameAndIpPanel.add(ip4);
		nameAndIpPanel.add(sendNameAndIp);
		nameAndIpframe.getContentPane().add(BorderLayout.CENTER, nameAndIpPanel);
		nameAndIpframe.setSize(400, 200);
		nameAndIpframe.setVisible(true);
		nameAndIpframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		 
	}
	class sendNameAndIpButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent ae)
			{
				boolean isIpTrue=false;
				
					try
					{
						if(!isInteger(ip1.getText()))
						throw new Exception();				
						if(!isInteger(ip2.getText()))
						throw new Exception();	
						if(!isInteger(ip3.getText()))
						throw new Exception();	
						if(!isInteger(ip4.getText()))
						throw new Exception();
						if(nameTextField.getText().equals("")) 
						{
							nameTextField.setText("I've no name so call me an Idiot");
						}
						else
						{
							isIpTrue=true;
						}
						
					}
					catch (Exception e)
					{
						System.err.println("Invalid IP Address");
						JFrame error=new JFrame("Error");
						JPanel ErrorPanel=new JPanel();
						ErrorPanel.add(new Label("Invalid IP Dude!"));
						error.getContentPane().add(BorderLayout.NORTH, ErrorPanel);
						error.setResizable(false);
						error.setSize(70, 100);
						error.setVisible(true);
						error.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					}
				
				if(isIpTrue)
				{
					name=nameTextField.getText();
					ip=ip1.getText()+"."+ip2.getText()+"."+ip3.getText()+"."+ip4.getText();
					System.out.println(ip);
					if (setUpNetworking())
					{
						go();
						nameAndIpframe.setVisible(false);
					}
					else
					{
						System.err.println("Server seems to be Unavailable. Make sure you typed in the IP address correctly");
						JFrame error2=new JFrame("Error");
						JPanel ErrorPanel2=new JPanel();
						ErrorPanel2.add(new Label("Server seems to be Unavailable. Make sure you typed in the IP address correctly."));
						error2.getContentPane().add(BorderLayout.NORTH, ErrorPanel2);
						error2.setResizable(false);
						error2.setSize(600, 100);
						error2.setVisible(true);
						error2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					}
				}
				
				
			}
			public boolean isInteger(String input)
			{
				if (input == null)
				{
					return false;
				}
				int length = input.length();
				if (length == 0)
				{
					return false;
				}
				int i = 0;
				if (input.charAt(0) == '-') 
				{
					if (length == 1) 
					{
						return false;
					}
					i = 1;
				}
				for (; i < length; i++) 
				{
					char c = input.charAt(i);
					if (c <= '/' || c >= ':')
					{
						return false;
					}
				}
				try 
				{
						int n=Integer.parseInt(input);
						if (n>=0 && n<=255)
						return true;
						
						else
						return false;
				}
				catch( Exception e ) 
				{
						return false;
				}
			}
		}
}