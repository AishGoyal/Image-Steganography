import java.awt.EventQueue;

public class MainPage{
	private JFrame Frame;

//	LAunch the application
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainPage window = new MainPage();
					window.frame.setVisible(true);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
//	Create the application
	public MainPage() {
		initialize();
	}
//	Initialize the contents of the frame
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100,100,450,300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}