import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.LineBorder;


public class ChatAppClient extends JFrame{
	
	private static ArrayList<String> history = new ArrayList<>();
	private static String trueHistory = "";
	
	private Socket socket = null;
	private DataOutputStream out = null;
	private ObjectInputStream in = null;
	
	public ChatAppClient(String address, int port)
	{

		
		this.setLocation(800,300);
		this.setSize(new Dimension(500,500));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane historyPane = new JScrollPane();
		historyPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		historyPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		historyPane.setPreferredSize(new Dimension(100, 300));
		JTextArea chatHistory = new JTextArea();
		chatHistory.setEditable(false);
		chatHistory.setBorder(new LineBorder(Color.black));
		historyPane.setViewportView(chatHistory);
		this.add(historyPane);
		
		//Socket declaration and waiting for server connection
		try {
			socket = new Socket(address, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		}
		catch (ConnectException e)
		{
			history.add("Connection failed. Is the server started?");
			updateTrueHistory(chatHistory);
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
		//////////////////////////////////////////////////////////
		JScrollPane scroller = new JScrollPane();
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		JTextField inputChat = new JTextField();
		inputChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!inputChat.getText().trim().isEmpty())
				{
					try {
						out.writeUTF(inputChat.getText());
						inputChat.setText("");
					} catch (SocketException e1) {
						// TODO Auto-generated catch block
						history.add("Message did not send: Connection Closed");
						updateTrueHistory(chatHistory);
					}
					catch (IOException e1)
					{
						System.out.println(e);
					}
				}

			}
		
		});
		
		new Thread(() -> {
			try
			{
				while (!Thread.interrupted())
				{
					if (in != null)
					{
						updateChatHistory((List<String>) in.readObject(), chatHistory);
					}
				}

			}
			catch (IOException | ClassNotFoundException e) {
				try {
					in.close();
					System.out.println("Input Closed");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println(e);
			}
		}).start();
		
		scroller.setViewportView(inputChat);
		this.add(scroller);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e)
			{
				try
				{
					if (out != null)
					{
						out.writeUTF("end");
						out.close();
						System.out.println("Output Closed");
					}
					if (socket != null)
					{
						socket.close();
						System.out.println("Socket closed");
					}

				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		
		
		this.setVisible(true);
		

	}
	
	private void updateChatHistory(List<String> received, JTextArea chatHistory)
	{
		SwingUtilities.invokeLater(() -> {
			history = (ArrayList<String>) received;
			updateTrueHistory(chatHistory);
		});
	}
	public static void updateTrueHistory(JTextArea chatHistory)
	{
		trueHistory = "";
		for (String a : history)
		{
			trueHistory += a + " \n";
		}
		chatHistory.setText(trueHistory);;
	}
	
}
