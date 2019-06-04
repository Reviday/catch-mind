package kh.mini.project.main.SERVER;
// Github 푸쉬할때 자동으로 대문자로 바뀌어서 그냥 대문자로 놓음..
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kh.mini.project.main.view.Main;

public class MainServer extends JFrame {
	private static final long serialVersionUID = 1216070372320522836L;
// Frame, Panel
	private JFrame mainView = new JFrame("Server"); // 메인 프레임
	private JScrollPane statusView = new JScrollPane(); // 포트로 받은 상태 수신창 스크롤팬
	private JScrollPane userListView = new JScrollPane(); // 유저 리스트를 보이게 할 스크롤팬
	private JTextArea statusArea = new JTextArea(); // statusView에 넣을 텍스트 에어리어
	private JTextArea userListArea = new JTextArea(); // userListView에 넣을 텍스트 에어리어

// Label
	private JLabel mainMenuBar = new JLabel();
	
// Textfield	
	private JTextField port_tf; // 포트번호를 입력받기 위한 텍스트필드
	
// Network 자원
	private ServerSocket server_Socket; // 서버 소켓
	private Socket socket; // 사용자로부터 받을 소켓 
	private int port; // 포트번호
	
	
// 각종 변수
	private Image viewImage; // 이미지 저장용 변수
	private Graphics viewGraphics; // 그래픽 저장용 변수
	private boolean portConection = false; //포트 정상 연결 후 MainView 연결에 사용될 변수
	private int mouseX,  mouseY; // 마우스 좌표용 변수
	private Vector user_vc = new Vector(); // 사용자 Vector 연결시 개별 스레드 실행. 각각 객체를 Vector에 저장 (동기화를 보장하는 ArrayList라고 생각하면 됨)
	private Vector room_vc = new Vector(); // 채팅방 Vector
	private StringTokenizer st; // 프로토콜 구현을 위해 필요함. 소켓으로 입력받은 메시지를 분리하는데 쓰임.
		
//Image	
	// #MainView 배경
	private Image mainBackgroundImage = 
			new ImageIcon(Main.class.getResource("/images/임시2.jpg")).getImage();
	
//Button
	
	MainServer() {
		new intro();
	}
	
	

	
	
	
	private void main_View() { // 서버 메인 화면
	// JFrame mainView	
		setUndecorated(true); // 프레임 타이틀 바 제거(윈도우를 제거함) - 기능 완성 후 추가 예정
		setTitle("Server"); // 프레임 타이틀 바 이름(타이틀 바를 없앨 예정이기 때문에 없어도 되는 코드)
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT); // Main에서 고정시킨 화면 해상도를 사용
		setResizable(false); // 프레임 크기 고정
		setLocationRelativeTo(null); // 윈도우를 화면 정중앙에 띄우기 위함
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 종료시 남아있는 프로세스도 깨끗하게 종료하기 위함
		setBackground(new Color(0,0,0,0)); // 배경색을 투명하게 한다.(paint()메소드로 그리는 배경을 보이게 하기 위함)
		setVisible(true); // 윈도우를 볼 수 있음.
		setLayout(null);
	
		
		userListView.setBounds(10, 10, 1004, 250);
		userListView.setViewportView(userListArea);
		add(userListView);
	
	// Label
		// #메뉴바
		mainMenuBar.setBounds(0, 0, Main.SCREEN_WIDTH, 30);
		mainMenuBar.addMouseListener(new MouseAdapter() {
			// 마우스를 버튼에 올려놨을 때 이벤트
			@Override
			public void mouseEntered(MouseEvent e) {
				mainMenuBar.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
			}
			@Override
			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		mainMenuBar.addMouseMotionListener(new MouseMotionAdapter() {
			// #매뉴바 드래그 시, 움직일 수 있게 한다.
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				setLocation(x - mouseX, y - mouseY);
			}
		});
		add(mainMenuBar);
	}
	
	void Server_start() // // MainServer에서 사용할 수 있도록 default로 설정
	{ 		
		try {
			server_Socket = new ServerSocket(port);
			if(portConection) { // 정상포트로 연결요청 될 시에 main_View를 띄움
				try {
					Thread.sleep(500); // 0.5초뒤 실행
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				main_View();
				portConection = false; // 중복실행 방지를 위한 초기화
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			statusArea.append("다시 입력하세요.\n");
		} 
		
		if(server_Socket != null) //정상적으로 포트가 열렸을 경우
		{
			Connection();
		}
	}
	
	void Connection() // MainServer에서 사용할 수 있도록 default로 설정
	{	
		// 1가지의 스레드에서는 1가지의 일만 처리할 수 있다.
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() { // 스레드에서 처리할 일을 기재한다.
				
				while(true) {
					try { // 마찬가지로 try-catch
						statusArea.append("사용자 접속 대기중(설정 포트번호 : "+port+")\n");
						socket = server_Socket.accept(); // 사용자 접속 무한 대기 => 때문에 다른 기능들이 동작하지 않고 죽어버린다. => 멀티 스레드로 해결!
						statusArea.append("사용자 접속!!\n");
						
						//사용자가 accept로 접속을 하게되면 UserInfo 객체를 생성하게 된다.
						//UserInfo 생성자는  매개변수로 넘어온 socket을 받아주고 UserNetwork()를 실행하여 연결설정을 하게된다.
//						UserInfo user = new UserInfo(socket);
						
//						user.start(); // 객체의 스레드를 각각 실행
						
					} catch (IOException e) {
					} 
					
				} // while문 끝
				
			}
		});
		
		th.start();
	}
	
	
	
	public static void main(String[] args) {
		new MainServer();
	}

	
	@Override
	public void paint(Graphics g) {
		viewImage = createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		viewGraphics = viewImage.getGraphics();
		screenDraw(viewGraphics);
		g.drawImage(viewImage,0,0, null);
	}
	
	public void screenDraw(Graphics g) {
		g.drawImage(mainBackgroundImage, 0, 0, null);
		this.paintComponents(g);
		this.repaint();
	}
	
	
	
	//인트로 화면용 내부 클래스
	class intro extends JFrame{
	// Frame, Panel
		private JFrame introView = new JFrame("Server"); // 인트로 프레임 (포트번호를 입력하여 서버를 실행하면 메인 프레임 창을 띄워 넘어가는 구조)
		private JLabel port_lb = new JLabel("포트 번호 : ");
		private JLabel introMenuBar = new JLabel();
		
	// 각종 변수	
		private Image viewImage; // 이미지 저장용 변수
		private Graphics viewGraphics; // 그래픽 저장용 변수
		private int mouseX, mouseY; // 마우스 좌표용 변수
		private boolean state = true; // 서버 중복 실행 방지용
		
	//Image	
		// #MainView 배경	
		private Image introBackgroundImage = 
				new ImageIcon(Main.class.getResource("/images/gifTest.gif")).getImage();
		// #버튼 이미지
		private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("/images/exit_entered.png"));
		private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("/images/exit_basic.png"));
		
	//Button
		private JButton start_btn = new JButton("서버 실행"); // 서버 실행 버튼
		private JButton stop_btn = new JButton("서버 중지"); // 서버 중지 버튼
		private JButton exitButton = new JButton(exitButtonBasicImage); // 나가기 버튼
		
		
		intro() {
			Font font = new Font("Inconsolata",Font.BOLD,12);
			setUndecorated(true); // 프레임 타이틀 바 제거(윈도우를 제거함) - 기능 완성 후 추가 예정
			setTitle("Server"); // 프레임 타이틀 바 이름(타이틀 바를 없앨 예정이기 때문에 없어도 되는 코드)
			setSize(280, 450); // Main에서 고정시킨 화면 해상도를 사용
			setResizable(false); // 프레임 크기 고정
			setLocationRelativeTo(null); // 윈도우를 화면 정중앙에 띄우기 위함
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 종료시 남아있는 프로세스도 깨끗하게 종료하기 위함
			setBackground(new Color(0,0,0,0)); // 배경색을 투명하게 한다.(paint()메소드로 그리는 배경을 보이게 하기 위함)
			setVisible(true); // 윈도우를 볼 수 있음.
			setLayout(null);
			
		// ScrollPane
			statusView.setBounds(10, 20, 260,330 );
			statusView.setBackground(new Color(0,0,0,0));
//			statusArea.setBackground(new Color(255,255, 0,0)); 
			statusArea.setFont(font);
			statusArea.setForeground(Color.white);
			statusView.setViewportView(statusArea);
			add(statusView);
			
		// label
			// #포트번호
			port_lb.setBounds(10, 360, 100, 30);
			add(port_lb);
			
		// Button
			// #시작 버튼 
			start_btn.setBounds(10, 400, 100, 30);
			add(start_btn);
			start_btn.addMouseListener(new MouseAdapter() {
				// 마우스를 버튼에 올려놨을 때 이벤트
				@Override
				public void mouseEntered(MouseEvent e) {
//					start_btn.setIcon(); // 마우스를 올려놨을때 이미지 변경(Entered Image)
					start_btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
				}
					
				// 마우스를 버튼에서 떼었을때 이벤트
				@Override  
				public void mouseExited(MouseEvent e) {
//					start_btn.setIcon(); // 마우스를 떼었을때 이미지 변경(Basic Image)
					start_btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
				}
				
				// 마우스로 버튼을 눌렀을 때 이벤트
				@Override 
				public void mousePressed(MouseEvent e) {
					if(state) {
						System.out.println("스타트 버튼 클릭");
						/* portCheck() 메소드를 통해 음수를 반환 받을 경우 서버를 가동하지않음.
						 * 음수가 반환 되었다는 것은 정상적인 포트번호가 아님을 의미. */
						if(portCheck(port_tf.getText().trim())>=0) {
							portConection = true;
							System.out.println("포트번호 : " + port);
							Server_start(); // 소켓 생성 및 사용자 접속 대기
							state = false; // 중복 실행 방지를 위해 false로 변경
							
						}
					} else {
						statusArea.append("서버가 이미 실행중 입니다.\n");
					}
				}
			});
		
			
			// #중지 버튼 
			stop_btn.setBounds(170, 400, 100, 30);
			add(stop_btn);
			stop_btn.addMouseListener(new MouseAdapter() {
				// 마우스를 버튼에 올려놨을 때 이벤트
				@Override
				public void mouseEntered(MouseEvent e) {
//					stop_btn.setIcon(); // 마우스를 올려놨을때 이미지 변경(Entered Image)
					stop_btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
				}
					
				// 마우스를 버튼에서 떼었을때 이벤트
				@Override  
				public void mouseExited(MouseEvent e) {
//					stop_btn.setIcon(); // 마우스를 떼었을때 이미지 변경(Basic Image)
					stop_btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
				}
							
				// 마우스로 버튼을 눌렀을 때 이벤트
				@Override 
				public void mousePressed(MouseEvent e) {
					state = true; // 서버 실행이 가능한 상태로 변경
					System.out.println("서버 스탑 버튼 클릭");
				}
			});
			
			// #나가기 버튼
			exitButton.setBounds(125, 400, 30, 30);
			exitButton.setBorderPainted(false);
			exitButton.setContentAreaFilled(false);
			exitButton.setFocusPainted(false);
			exitButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					exitButton.setIcon(exitButtonEnteredImage);
					exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
					//마우스를 올렸을 때 손모양으로 나타나게
				
				}
				@Override
				public void mouseExited(MouseEvent e) {
					exitButton.setIcon(exitButtonBasicImage);
					exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					//마우스를 때면 원래 상태로
				}
				@Override
				public void mousePressed(MouseEvent e) {
					try {
						Thread.sleep(500); // 0.5초뒤 종료
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					System.exit(0);
				}
			});
			add(exitButton);
			
		// TextField
			//포트번호 입력
			port_tf = new JTextField("12345"); //포트번호 기본값 12345
			port_tf.setBounds(100, 360, 170, 30);
			add(port_tf);
			port_tf.setColumns(10);		
			
			// 메뉴바
			introMenuBar.setBounds(0, 0, 280, 30);
			introMenuBar.addMouseListener(new MouseAdapter() {
				// 마우스를 버튼에 올려놨을 때 이벤트
				@Override
				public void mouseEntered(MouseEvent e) {
					introMenuBar.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
				}
				@Override
				public void mousePressed(MouseEvent e) {
					mouseX = e.getX();
					mouseY = e.getY();
				}
			});
			introMenuBar.addMouseMotionListener(new MouseMotionAdapter() {
				// #매뉴바 드래그 시, 움직일 수 있게 한다.
				@Override
				public void mouseDragged(MouseEvent e) {
					int x = e.getXOnScreen();
					int y = e.getYOnScreen();
					setLocation(x - mouseX, y - mouseY);
				}
			});
			add(introMenuBar);	
			
		}		
		
		

		
		
		@Override
		public void paint(Graphics g) {
			viewImage = createImage(280, 450);
			viewGraphics = viewImage.getGraphics();
			screenDraw(viewGraphics);
			g.drawImage(viewImage,0,0, null);
		}
		
		public void screenDraw(Graphics g) {
			g.drawImage(introBackgroundImage, 0, 0, null);
			this.paintComponents(g);
			this.repaint();
		}

		// 유효 포트번호 체크 메소드
		private int portCheck(String str) {
			try {
				// int형으로 변경이 가능한 문자열이 입력되었는지 try-catch로 확인
				int tempPort = Integer.parseInt(str);
				// 가능하다면 유효 포트번호를 입력하였는지 확인.
				if(tempPort >= 0 && tempPort <= 65535)
				{
					return port=Integer.parseInt(str); //포트번호 범위는 0~65535
				} 
				else 
				{	// 유효 포트번호가 아니면 음수를 반환
					statusArea.append("잘못된 포트번호입니다. 다시 입력하세요.\n");
					return -1;
				}
			} catch(NumberFormatException e) { // 예외 처리 대상의 경우에도 음수를 반환
				statusArea.append("잘못된 포트번호입니다. 다시 입력하세요.\n");
				return -1;
			}
		}
	}
	
	class UserInfo extends Thread
	{
		private OutputStream os;
		private InputStream is;
		private DataOutputStream dos;
		private DataInputStream dis;
		
		private Socket user_socket; // 사용자의 닉네임을 받는다.
		private String Nickname ="";
		
		private boolean RoomCh = true;
		
		UserInfo(Socket soc) // 생성자 메소드
		{
			this.user_socket = soc;
			
			UserNetwork();
		}
		
		
		/* [Protocol 정리]
		 * - 사용자 추가 = NewUser/사용자ID
		 * - 기존 사용자 = OldUser/사용자ID
		 * - 쪽지 = Note/User@내용
		 * 		  Client 입장 => Note/받는사람@내용
		 * 		  Server 입장 => Note/받는사람@내용 (메세지 들어올떄)
		 * 					=> Note/보낸사람@내용 (메세지 나갈떄) => Client 입장 => Note/보낸사람@내용 (메세지 받을때)
		 */
		private void UserNetwork() // 네트워크 자원 설정
		{
			try {
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);
				
				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);
				
				//연결 설정 후에 사용자의 닉네임을 받아들인다.
				Nickname = dis.readUTF();
				statusArea.append(Nickname+" : 사용자 접속!");
				
				//기존 사용자들에게 새로운 사용자 알림(broadcast)
				System.out.println("현재 접속된 사용자 수 : " + user_vc.size());
				
				BroadCast("NewUser/" + Nickname); // 기존 사용자에게 자신을 알린다. 프로토콜 사용 [ NewUser/사용자ID ]
				
				/* 이 상태로 2번쨰 접속자가 접속을 할 경우, 리스트에는 2번째 접속자만 뜨게 된다. 
				 * 자신의 ID를 리스트에 추가하기전에 자신에게 기존 사용자의 리스트를 알린다.
				 */
				//현재 접속중인 사용자의 리스트를 자신에게 알림
				for(int i=0; i<user_vc.size(); i++) 
				{
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					// 서버가 연결된 사용자에게 보내는 부분
					send_Message("OldUser/"+u.Nickname);
				}
				
				
				user_vc.add(this); // 사용자에게 알린 후 Verctor에 자신을 추가
				//Vector는 동적으로 늘어나는 배열로 이해하면 되는데, 객체에 저장한 사용자 정보를 Vector에 저장한다.
				
				BroadCast("user_list_update/ ");
				
			} catch (IOException e) {
			}
		}
		
		
		@Override
		public void run() //Thread에서 처리할 내용
		{
			try {
				//스트림설정을 안했기 때문에 해당 스트림 설정을 생성자에서 실시
				String msg = dis.readUTF();
				statusArea.append(Nickname+ ": 사용자로부터 들어온 메세지 : " + msg + "\n");
				Inmessage(msg);
			} catch (IOException e) {
			}
		} // run 메소드 끝
		
		
		// 클라이언트로부터 들어오는 메세지 처리
		private void Inmessage(String str) 
		{
			System.out.println(str);
			st = new StringTokenizer(str,"/");
			
			String protocol = st.nextToken();
			String message = st.nextToken();
			
			System.out.println("프로토콜 : " +protocol);
			System.out.println("메세지 : " + message);
			
			if(protocol.equals("Note")) 
			{
				//protocol = Note
				//message = user
				//note = 받는 내용
				
				String note = st.nextToken();
				
				System.out.println("받는 사람 : "+message);
				System.out.println("보낼 내용 : "+note);
				
				// 벡터에서 해당 사용자를 찾아서 메세지 전송
				for(int i=0; i<user_vc.size(); i++) 
				{
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					
					if(u.Nickname.equals(message))
					{
						u.send_Message("Note/"+Nickname+"/"+note);
						// Note/User1/~~~
					}
				}
			}
			else if(protocol.equals("CreateRoom"))
			{
				//1. 현재 같은 방이 존재 하는지 확인한다.
				for(int i=0; i<room_vc.size(); i++)
				{
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					
					if(r.Room_name.equals(message)) // 만들고자 하는 방이 이미 존재 할 때
					{
						send_Message("CreateRoomFail/ok");
						RoomCh = false;
						break; // 이미 방이 있으면 for문 중지
					}
				} // for문 끝
				if(RoomCh) // 방을 만들 수 있을 때
				{
					RoomInfo new_room = new RoomInfo(message, this);
					room_vc.add(new_room); // 전체 채팅 방 Vector에 방을 추가
					
					send_Message("CreateRoom/"+message); 
					//방이 만들어졌을때 BroadCast로 알린다.
					BroadCast("New_Room/"+message);
				}
				
				RoomCh = true; // 다시 초기값으로 돌림.
			}// else if 문
			else if(protocol.equals("Chatting")) // 채팅
			{
				String msg = st.nextToken(); // 메세지 부분을 잘라서 저장
				for(int i=0; i<room_vc.size(); i++)  // 전체 방에 대해서 for문을 돌려 방 이름이 같은 곳을 찾는다.
				{
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					
					if(r.Room_name.equals(message)) // 해당 방을 찾았으면
					{
						r.BroadCast_Room("Chatting/"+Nickname+"/"+msg);
					}
				}
			}
			
		}
		
		private void BroadCast(String str) // 전체 사용자에게 메세지를 보내는 부분
		{
			for(int i=0; i<user_vc.size(); i++)  //현재 접속된 사용자에게 새로운 사용자 알림
			{	
				UserInfo u = (UserInfo)user_vc.elementAt(i); //i번쨰에 있는 사용자에게 메세지를 전송
				
				u.send_Message(str);
			}
		}
		
		
		// 서버쪽에서도 클라이언트와 대화할 수 있는 메소드를 만들어 줍니다.
		private void send_Message(String str)  // 문자열을 받아서 전송
		{
			try {
				dos.writeUTF(str);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	} // UserInfo class 끝
	
	class RoomInfo
	{
		private String Room_name; // 채팅방 이름
		private Vector Room_user_vc = new Vector(); // 채팅방 유저 Vector
		
		RoomInfo(String str, UserInfo u)
		{
			this.Room_name = str;
			this.Room_user_vc.add(u); // 채팅방에 입장한 사용자 정보를 Room_user_vc에 추가
		}
		
		public void BroadCast_Room(String str) // 현재 방의 모든 사람에게 알린다.
		{
			for(int i=0; i<Room_user_vc.size(); i++) 
			{
				UserInfo u = (UserInfo)Room_user_vc.elementAt(i);
				
				u.send_Message(str); //넘어온 문자열을 보내준다.
			}
		}
	}
}
