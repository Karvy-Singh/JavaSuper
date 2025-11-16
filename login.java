import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class login extends JFrame implements ActionListener{

 JButton butt = new JButton("Submit");
 JTextField username = new JTextField();
 JPasswordField pass = new JPasswordField();
    JLabel errLabel = new JLabel();

login(){
    ImageIcon icon = new ImageIcon("L1.png");
    ImageIcon logo = new ImageIcon("L.png");
    JLabel label = new JLabel();
    JPanel panel = new JPanel(new BorderLayout());

Image image = logo.getImage();
        Image scaledImage = image.getScaledInstance(400, 200, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

    label.setIcon(scaledIcon);
    label.setText("Enter your credentials");
    label.setFont(new Font("Poppins", Font.BOLD, 24));
    label.setHorizontalTextPosition(SwingConstants.CENTER);
    label.setVerticalTextPosition(SwingConstants.BOTTOM);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setBounds(100,0,500,300);
    username.setBounds(250, 280, 200, 40);
    username.setFont(new Font("Poppins", Font.PLAIN, 12));
    username.setBorder(BorderFactory.createTitledBorder("Username"));
    pass.setBorder(BorderFactory.createTitledBorder("Password"));
    pass.setBounds(250, 330, 200, 40);
    butt.setFocusable(false);
    butt.addActionListener(this);
    errLabel.setHorizontalAlignment(SwingConstants.CENTER);
    errLabel.setVerticalAlignment(SwingConstants.BOTTOM);
    errLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    errLabel.setVerticalTextPosition(SwingConstants.BOTTOM);    
butt.setBounds(300, 380, 100, 30);
    butt.setForeground(Color.white);
    butt.setBackground(Color.BLACK);
    panel.add(label);
    panel.add(username);
    panel.add(pass);
    panel.add(butt);
    panel.add(errLabel);
  this.setTitle("JCart Login");
     this.setVisible(true);
     this.setSize(700, 500);
     this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     this.setResizable(false);
     this.setIconImage(icon.getImage());
     this.add(panel);
     
}
@Override
public void actionPerformed(ActionEvent e){

if(e.getSource() == butt){
    String u = username.getText();
        String p = new String(pass.getPassword());

        if (!u.equals("pandu") || !p.equals("1234")) {
            errLabel.setText("❌ Invalid username or password!");
            errLabel.setForeground(Color.RED);
        }
        else{
        errLabel.setText("Login Successfull");
            errLabel.setForeground(Color.GREEN);
            
        }
}

}

}