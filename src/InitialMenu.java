import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InitialMenu extends JFrame{
	
	public InitialMenu()
	{
		this.setLocation(800, 300);
		this.setSize(700,300);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);
		
		JPanel panel = new JPanel(new GridBagLayout());
		this.add(panel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel label = new JLabel("Enter an IP Address");
		panel.add(label, gbc);
		JTextField ip = new JTextField();
		ip.setSize(getPreferredSize());
		ip.setColumns(10);
		ip.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try
				{
					if (!ip.getText().trim().isEmpty())
					{
						String[] array = ip.getText().split("\\.");
						boolean check = true;
						for (int i = 0; i < array.length;i++)
						{
							if (Integer.valueOf(array[i]) > 255 || Integer.valueOf(array[i]) < 0)
							{
								check = false;
							}
						}
						if(check && array.length == 4)
						{
							InetAddress inetAddress = InetAddress.getByName(ip.getText());
							if (inetAddress.isReachable(5000))
							{
								setVisible(false);
								ChatAppClient client = new ChatAppClient(ip.getText(), 5000);
							}
							else
							{
								label.setText("Connection Denied. Is the machine on?");
							}
						}
						else
						{
							ip.setText("");
							label.setText("Incorrect IP Address");
						}
					}
				}
				catch (Exception e1)
				{
					System.out.println(e1);
				}
			}
			
		});
		panel.add(ip);
		
		this.setVisible(true);
	}
}
