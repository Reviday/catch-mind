package kh.mini.project.waiting_room.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import kh.mini.project.main.view.Main;
import kh.mini.project.main.view.MainView;
import kh.mini.project.model.vo.RoomInfo;
import kh.mini.project.model.vo.UserInfo;

public class WaitingRoom extends JFrame{
// Frame, Panel
	private JScrollPane chattingView = new JScrollPane(); // 채팅을 보이게하는 스크롤 페인
	private JPanel userInfoView = new JPanel(); // 유저 정보 패널
	private JPanel userListView = new JPanel(); // 유저 리스트 패널
	private JPanel gameRoomView = new JPanel(); // 게임방 패널
	private JPanel[] gameRoom = new JPanel[6]; // 24개의 방을 개설 => 버튼으로 해볼까??
	private JPanel[] userList = new JPanel[10]; // 50명의 유저 리스트를 띄우는 패널
	
// Label
	private JLabel mainMenuBar = new JLabel();
//	private JLabel[] userID_lb = new JLabel[userList.length]; 		// 유저 ID 라벨 배열
	
// Textfield	
	private JTextField chatting_tf; // 채팅 내용을 입력받기 위한 텍스트필드	
	private JTextArea chattingArea = new JTextArea(); // 채팅 스크롤 페인에 올려놓을 채팅 TextArea
	
// Network 자원 변수
	private String id =""; 
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
// 각종 변수
	private Image viewImage; // 이미지 저장용 변수
	private Graphics viewGraphics; // 그래픽 저장용 변수	
	private int mouseX, mouseY; // 마우스 좌표용 변수
	private StringTokenizer st; // 프로토콜 구현을 위해 필요함. 소켓으로 입력받은 메시지를 분리하는데 쓰임.
	private UserInfo userInfo; // 접속자의 정보를 저장하는 객체(이 이름으로 접근하면 사용자 본인의 정보에 접근할 수 있다.)
	private Vector<RoomInfo> room_list = new Vector<RoomInfo>(); // 방 리스트 Vector
	private Vector<UserInfo> user_list = new Vector<UserInfo>();
	private Toolkit tk = Toolkit.getDefaultToolkit();
	// 커서 테스트
	Image img = tk.getImage(Main.class.getResource("/images/cursorBasic.png"));
	Cursor myCursor = tk.createCustomCursor(img, new Point(10,10), "dynamite stick");
	// 방 만들기에 필요한 변수
	private String room_name; // 방제목
	private String roomPW; // 방 비밀번호
	private int fixed_User; // 방 인원수
	private int room_No; // 방 번호
	private boolean scrollpanemove = false;  // 스크롤 패인에 사용되는 변수(스크롤 허용 관련)
	private RoomInfo roomInfo; // 사용자가 생성한 방이나 입장하려는 방의 객체
	private Font wrFont = new Font("휴먼편지체", Font.PLAIN,18 ); //폰트설정
	
	
//Image	
	// #MainView 배경
	private Image backgroundImage = 
			new ImageIcon(Main.class.getResource("/images/WaitingRoom_Background.png")).getImage();
			//Main 클래스의 위치를 기준으로 이미지 파일의 위치를 찾은 다음에 이미지 인스턴스를 해당 변수에 초기화 해줌(상대경로 같은 절대경로)	
	
	// Button Icon (basic : 버튼의 기본 상태, Entered : 버튼에 마우스를 가져간 상태) 
	// => 버튼 기본상태, 마우스를 올려놨을 때 상태, 눌렀을 때 상태 3가지 가능?
	private ImageIcon exitBasicImage = new ImageIcon(Main.class.getResource("/images/exit.png"));
	private ImageIcon exitEnteredImage = new ImageIcon(Main.class.getResource("/images/exite.png")); 
	private ImageIcon createRoomBasicImage = new ImageIcon(Main.class.getResource("/images/exit.png"));
	private ImageIcon createRoomEnteredImage = new ImageIcon(Main.class.getResource("/images/exit.png"));
	private ImageIcon rightRBasicImage = new ImageIcon(Main.class.getResource("/images/화살표1_R_basic.png"));
	private ImageIcon rightREnteredImage = new ImageIcon(Main.class.getResource("/images/화살표1_R_entered.png")); 
	private ImageIcon leftRBasicImage = new ImageIcon(Main.class.getResource("/images/화살표1_L_basic.png"));
	private ImageIcon leftREnteredImage = new ImageIcon(Main.class.getResource("/images/화살표1_L_entered.png")); 
	private ImageIcon gamgeRoomBasicImage = new ImageIcon(Main.class.getResource("/images/gameroom.png")); 
	private ImageIcon gamgeRoomEnteredImage = new ImageIcon(Main.class.getResource("/images/gameroomEntered1.png")); 
	private ImageIcon gamgeRoomPressedImage = new ImageIcon(Main.class.getResource("/images/gameroomPressed.png"));
	private ImageIcon userInfoPanelImage = new ImageIcon(Main.class.getResource("/images/userInfoPanel.png")); 
	private ImageIcon inputPwPanelImage = new ImageIcon(Main.class.getResource("/images/inputPwBackground.png")); 
	
//Button
	private JButton exitButton = new JButton(exitBasicImage); // 나가기 버튼
	private JButton createRoomButton = new JButton(createRoomBasicImage); // 방만들기 버튼
	private JButton rightRButton = new JButton(rightRBasicImage); // 방 오른쪽 넘기기 버튼
	private JButton leftRButton = new JButton(leftRBasicImage); // 방 왼쪽 넘기기 버튼
	
	public WaitingRoom() {
		//실행과 동시에 네트워크 자원과 id를 MainView로부터 이어받아온다.
		id = MainView.getId();
		dos = MainView.getDos();
		
		Font font = new Font("Inconsolata",Font.BOLD,15); // 폰트 설정
		
		setUndecorated(true); // 프레임 타이틀 바 제거(윈도우를 제거함)
		setTitle("Catch Mind"); // 프레임 타이틀 바 이름(타이틀 바를 없앨 예정이기 때문에 없어도 되는 코드)
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT); // Main에서 고정시킨 화면 해상도를 사용
		setResizable(false); // 프레임 크기 고정
		setLocationRelativeTo(null); // 윈도우를 화면 정중앙에 띄우기 위함
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 종료시 남아있는 프로세스도 깨끗하게 종료하기 위함
		setBackground(new Color(0,0,0,0)); // 배경색을 투명하게 한다.(paint()메소드로 그리는 배경을 보이게 하기 위함)
		setVisible(true); // 윈도우를 볼 수 있음.
		setLayout(null);
		setCursor(myCursor);
		
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
		
	// JScrollPane
		// #채팅뷰
		chattingView.setBounds(240, 490, 768, 200);
		chattingView.setBackground(new Color(40,40,40,40));
		chattingView.setViewportView(chattingArea);
		chattingArea.setBackground(new Color(0,0,0,0)); 
		chattingArea.setFont(font);
		chattingArea.setForeground(Color.BLACK);
		chattingArea.setEditable(false); // 해당 필드를 수정할 수 없음
		chattingArea.setLineWrap(true); // 자동 줄바꿈
		chattingView.setViewportView(chattingArea);
		/* 이하 코드는 쓰레드 환경에서도 자동 스크롤이 되게하려는 메소드이다. */
		chattingView.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrollpanemove = true;
			}
		});
		chattingView.getVerticalScrollBar().addAdjustmentListener(new	AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) { // 수정리스너에서 변수(휠의 길이,위치)가 변경될시 메소드 작성
				if (scrollpanemove) { // 만약 스크롤 무브가 허용되있을시
					scrollpanemove = false; // 밑으로 내리는 것을 하지않고, 비허용으로 바꾼다.
				} else {
					JScrollBar src = (JScrollBar) e.getSource();
					src.setValue(src.getMaximum());
				}
			}
		});
		add(chattingView);
		
		// #유저 정보(자신) 뷰
		userInfoView.setBounds(30, 250, 190, 68);
		userInfoView.setBackground(new Color(40,40,40,40));
		add(userInfoView); 
		
		
		// #유저 리스트 뷰
		userListView.setBounds(30, 330, 190, 374);
		userListView.setLayout(new FlowLayout(FlowLayout.CENTER));
		userListView.setBackground(new Color(40,40,40,40));
		allocationUserInfo();
		add(userListView); 
		
		// #게임방 뷰
		gameRoomView.setBounds(240, 110, 768, 370);
		gameRoomView.setLayout(new FlowLayout(FlowLayout.CENTER));
		gameRoomView.setBackground(new Color(40,40,40,40));
		allocationRoom(); // 대기실에 게임방이 보이도록 하는 메소드
		add(gameRoomView); 
		
		
	// TextField
		chatting_tf = new JTextField(); 
		chatting_tf.setBounds(260, 690, 728, 30);
		chatting_tf.setBackground(new Color(40,40,40,40));
		add(chatting_tf);
		chatting_tf.setDocument(new JTextFieldLimit(45)); // 채팅 45자 제한 	 
		chatting_tf.setFont(font);
		chatting_tf.setForeground(Color.BLACK);
		chatting_tf.addKeyListener(new keyAdapter());
			
	// Button
		// #나가기 버튼
		exitButton.setBounds(440, 60, 180, 50);
		add(exitButton);
		exitButton.addMouseListener(new MouseAdapter() {
			// 마우스를 버튼에 올려놨을 때 이벤트
			@Override
			public void mouseEntered(MouseEvent e) {
				exitButton.setIcon(exitEnteredImage); // 마우스를 올려놨을때 이미지 변경(Entered Image)
//				exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
				exitButton.setCursor(myCursor);
			}
			
			// 마우스를 버튼에서 떼었을때 이벤트
			@Override  
			public void mouseExited(MouseEvent e) {
				exitButton.setIcon(exitBasicImage); // 마우스를 떼었을때 이미지 변경(Basic Image)
				exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
			}
//			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1) {
					// 서버에 해당 유저가 로그아웃 하였음을 알리고
					send_message("UserLogout/"+userInfo.getUserID());
					// 프로세스 종료
					System.exit(0); 
				}
			}
		});
		
		// #방만들기 버튼
		createRoomButton.setBounds(260, 60, 180, 50);
		add(createRoomButton);
		createRoomButton.addMouseListener(new MouseAdapter() {
			// 마우스를 버튼에 올려놨을 때 이벤트
			@Override
			public void mouseEntered(MouseEvent e) {
				createRoomButton.setIcon(createRoomEnteredImage); // 마우스를 올려놨을때 이미지 변경(Entered Image)
				createRoomButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
			}

			// 마우스를 버튼에서 떼었을때 이벤트
			@Override
			public void mouseExited(MouseEvent e) {
				createRoomButton.setIcon(createRoomBasicImage); // 마우스를 떼었을때 이미지 변경(Basic Image)
				createRoomButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
			}
			// 버튼을 떼었을때 이벤트
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1) {
					new CreateRoom();
				}
			}
		});
		
		// #방 오른쪽 넘기기 버튼
		rightRButton.setBounds(640, 450, 60, 40);
		add(rightRButton);
		rightRButton.addMouseListener(new MouseAdapter() {
			// 마우스를 버튼에 올려놨을 때 이벤트
			@Override
			public void mouseEntered(MouseEvent e) {
				rightRButton.setIcon(rightREnteredImage); // 마우스를 올려놨을때 이미지 변경(Entered Image)
				rightRButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
			}

			// 마우스를 버튼에서 떼었을때 이벤트
			@Override
			public void mouseExited(MouseEvent e) {
				rightRButton.setIcon(rightRBasicImage); // 마우스를 떼었을때 이미지 변경(Basic Image)
				rightRButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
			}
			// 버튼을 떼었을때 이벤트
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1) {
					
				}
			}
		});

		// #방 왼쪽 넘기기 버튼
		leftRButton.setBounds(540, 450, 60, 40);
		add(leftRButton);
		leftRButton.addMouseListener(new MouseAdapter() {
			// 마우스를 버튼에 올려놨을 때 이벤트
			@Override
			public void mouseEntered(MouseEvent e) {
				leftRButton.setIcon(leftREnteredImage); // 마우스를 올려놨을때 이미지 변경(Entered Image)
				leftRButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
			}

			// 마우스를 버튼에서 떼었을때 이벤트
			@Override
			public void mouseExited(MouseEvent e) {
				leftRButton.setIcon(leftRBasicImage); // 마우스를 떼었을때 이미지 변경(Basic Image)
				leftRButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
			}
			// 버튼을 떼었을때 이벤트
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==1) {
					
				}
			}
		});
		
		//대기실 입장시, 환영하는 문구 출력
		chattingArea.append("["+id+"]님 환영합니다! \n");
	} // WaitingRoom() 생성자 끝

	private void Inmessage(String str) // 서버로부터 들어오는 모든 메세지
	{
		st = new StringTokenizer(str, "@");  // 어떤 문자열을 사용할 것인지, 어떤 문자열로 자를 것인지 =>  [ NewUser/사용자ID ] 형태로 들어옴
		
		String protocol = st.nextToken(); // 프로토콜을 저장한다.
		String mUserId = st.nextToken(); // 메시지를 저장한다.
		
		System.out.println("프로토콜 : " + protocol);
		System.out.println("내용 : " + mUserId);
		
		// protocol 수신 처리
		switch(protocol) {
		
		// #새로운 접속자 알림
		case "NewUser": 
			// 새로운 접속자의 정보를 가져와 저장한다.
			int level = Integer.parseInt(st.nextToken()); //레벨
			int exp = Integer.parseInt(st.nextToken()); //경험치
			int corAnswer = Integer.parseInt(st.nextToken()); //누적 정답수
			
			// 가져온 정보로 객체를 생성
			UserInfo u = new UserInfo(mUserId, level, exp, corAnswer);
			
			// 해당 객체를 Vector에 추가
			user_list.add(u);
			updateUserInfo();  	// 유저리스트 갱신
			break;
		
		// #접속자 정보 받아오기(사용자 본인의 정보)
		case "UserInfo":
			// 사용자의 정보를 가져와 저장한다.
			level = Integer.parseInt(st.nextToken()); //레벨
			exp = Integer.parseInt(st.nextToken()); //경험치
			corAnswer = Integer.parseInt(st.nextToken()); //누적 정답수
			
			// 가져온 정보로 객체를 생성
			userInfo = new UserInfo(mUserId, level, exp, corAnswer);
			
			// 해당 객체를 Vector에 추가
			user_list.add(userInfo);
			updateUserInfo(); 	// 유저리스트 갱신
			break;
		
		// #기존 접속자의 정보를 받아온다.(초기 세팅 작업)
		case "OldUser":
			// 해당 프로토콜로 받는 사용자 정보는 첫 로그인 한 번만 적용된다.
			// 이전에 접속중이던 모든 유저의 정보를 가져온다.
			level = Integer.parseInt(st.nextToken()); //레벨
			exp = Integer.parseInt(st.nextToken()); //경험치
			corAnswer = Integer.parseInt(st.nextToken()); //누적 정답수
			
			// 가져온 정보로 객체를 생성
			u = new UserInfo(mUserId, level, exp, corAnswer);
			
			// 해당 객체를 Vector에 추가
			user_list.add(u);

			// 마지막 토큰이 last일 경우 리스트를 갱신한다.
			String lastCheck = st.nextToken();
			if (lastCheck.equals("last"))
				updateUserInfo();
			break;
			
		// #게임방 입장, 혹은 로그아웃으로인한 해당 유저를 유저 리스트에서 제거한다.
		case "RemoveUser":
			for(int i=0; i<user_list.size(); i++) {
				UserInfo rUser = (UserInfo)user_list.get(i);
				// 해당 유저 아이디를 찾는다.
				if(rUser.getUserID().equals(mUserId)) {
					// 해당 아이디를 대기실 유저에서 지운다.
					user_list.remove(i); 
					// 유저 리스트를 업데이트하여 패널에 적용시킨다.
					updateUserInfo();
					break;
				}
			}
			
			break;
			
		// #쪽지 보내기
		case "Note":
			String note = st.nextToken(); // 받은 내용
			System.out.println(mUserId+" 사용자로부터 온 쪽지 "+note);
			JOptionPane.showMessageDialog(null, note, mUserId+"님으로 부터 쪽지", JOptionPane.CLOSED_OPTION);
			break;
			
		// #방 생성
		case "CreateRoom":
			int room_No = Integer.parseInt(st.nextToken()); // 방 번호
			
			send_message("EntryRoom/"+userInfo.getUserID());
			// WaitingRoom 창을 종료하고 게임창을 연다. 이때, 방 제목과 방 번호를 같이 보낸다.
			dispose();
//			paint = new PaintEx(room_No);
			
			break;
		
		// #기존에 생성된 방의 정보를 넘겨받음(초기 세팅 작업)
		case "OldRoom":
			// 해당 프로토콜로 받는 방 정보는 첫 로그인 한 번만 적용된다.
			// 이전에 개설된 모든 방의 정보를 가져온다.
			room_No = Integer.parseInt(st.nextToken()); // 방 번호
			String room_name = st.nextToken(); // 방 이름
			String room_PW = st.nextToken(); // 방 비밀번호
			int fixed_User = Integer.parseInt(st.nextToken()); // 유저 정원
			// 게임방에 들어가있는 유저 객체의 정보까진 필요없으므로 인원 수만 받는다.
			int room_UCount = Integer.parseInt(st.nextToken()); // 현재 유저수
			
			//넘겨 받은 정보로 객체를 생성
			RoomInfo oldRoom = new RoomInfo(room_No, room_name, room_PW, room_UCount, fixed_User);
			//해당 객체를 room_list에 추가
			room_list.add(oldRoom);
			
			// 마지막 토큰이 last일 경우 방 패널을 갱신한다.
			String lastroomCheck = st.nextToken();
			if (lastroomCheck.equals("last")) 
				relocationRoom(); //방 업데이트
			break;
			
		// #새로운 방 생성 알림
		case "NewRoom":
			room_No = Integer.parseInt(st.nextToken()); // 방 번호
			room_name = st.nextToken(); // 방 이름
			room_PW = st.nextToken(); // 방 비밀번호
			fixed_User = Integer.parseInt(st.nextToken()); // 유저 정원
			// 게임방에 들어가있는 유저 객체의 정보까진 필요없으므로 인원 수만 받는다.
			room_UCount = Integer.parseInt(st.nextToken()); // 현재 유저수
			
			//넘겨 받은 정보로 객체를 생성
			RoomInfo NewRoom = new RoomInfo(room_No, room_name, room_PW, room_UCount, fixed_User);
			//해당 객체를 room_list에 추가
			room_list.add(NewRoom);
			
			//방 업데이트
			relocationRoom();
			break;
		
		// #방 입장 허가를 받음
		case "EntryRoom":
			// 해당 창을 종료한다. 방에 대한 정보는 이어서 MainView로 받는다.
			dispose();
			break;
			
		// #비밀번호 입력 요청
		case "InputPW":
			/*
			 *  패스워드 입력 후 입장하는 구조!
			 */
			// 방번호와 패스워드를 받는다.
			room_No = Integer.parseInt(st.nextToken());
			room_PW = st.nextToken();
//			send_message("EntryRoom/"+Message+"/"+room_No);
			new inputPw(room_No,room_PW);
//			JFrame inputPW_fr = new JFrame();
//			inputPW_fr.setUndecorated(true); // 프레임 타이틀 바 제거(윈도우를 제거함) - 기능 완성 후 추가 예정
//			inputPW_fr.setSize(1024,768); // null은 최댓값
//			inputPW_fr.setBackground(new Color(40,40,40,40));
//			inputPW_fr.setAlwaysOnTop(true); // 항상 모든 윈도우 위에 위치하도록 함
//			inputPW_fr.setPreferredSize(new Dimension(1024,768));
//			inputPW_fr.setResizable(false); // 프레임 크기 고정
//			inputPW_fr.setLocationRelativeTo(null); // 윈도우를 화면 정중앙에 띄우기 위함
//			inputPW_fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 종료시 남아있는 프로세스도 깨끗하게 종료하기 위함
//			inputPW_fr.setVisible(true); // 윈도우를 볼 수 있음.
//			inputPW_fr.setLayout(null);
//			
//			
//			JLabel inputPW = new JLabel(inputPwPanelImage);
//			inputPW.setBounds(100, 100, 500, 500);
//			inputPW_fr.add(inputPW);
			
//			JPanel inputPW = new GameRoomPanel(inputPwPanelImage.getImage());
//			inputPW.setLocation(0, 0);
//			inputPW_fr.add(inputPW);
			
			
			
			break;
			
		// #채팅
		case "ChattingWR":
			String chattingMsg = st.nextToken(); 
			System.out.println("WaingRoom 채팅 내용 : " + chattingMsg);
			chattingArea.append("["+mUserId+"] : "+chattingMsg+"\n");
			break;
		}
	}
	
	private void send_message(String str) // 서버에게 메세지를 보내는 부분
	{
		try {
			dos.writeUTF(str);
		} catch (IOException e) // 에러 처리 부분
		{
			e.printStackTrace();
		}
	}
	
	// MainView 클래스에서 WaitingRoom 클래스로 메시지를 전달하기 위해 사용하는 메소드
	public void wr_Inmessage(String str) {
		Inmessage(str);
	}
	
	// 대기실 24개의 방을 생성하기 위한 메소드 (방 개수의 변동이 생길때마다 동작)
	private void allocationRoom() {
		// 추후 생성된 방에게만 이벤트를 할당하도록 변경
		for(int i=0; i<gameRoom.length; i++) {
			// 내부 클래스 GameRoomPanel 클래스를 이용해서 gameRoom Panel을 생성
			GameRoomPanel grp = new GameRoomPanel(gamgeRoomBasicImage.getImage());
			gameRoom[i] = grp;
			gameRoomView.add(gameRoom[i]);
		}
	}
	
	
	// 패널에 방을 생성하는 메소드(방 번호와 해당 패널의 인덱스 값을 받아서 생성한다.)
	// 메소드 수정, 해당 메소드로 전체 방을 개설하는 쪽으로 해보자. 
	private void createRoom() {
		Font roomFont = new Font("Inconsolata",Font.BOLD,17); // 폰트 설정
		
		// 기존에 띄워둔 패널을 지운다.
		gameRoomView.removeAll();
		
		// 모든 gameRoom에 적용할 코드. 리스트에 등록된 객체 수 만큼 패널에 이벤트 적용
		for(int i=0; i<gameRoom.length; i++) {
			// 방에 적용할 패널을 만들어 리스너를 적용시킨다(기존에 띄워둔 패널들을 삭제하고 다시 그리는 형식으로 그리기때문에 다시 설정한다.)
			GameRoomPanel grp = new GameRoomPanel(gamgeRoomBasicImage.getImage());
			gameRoom[i] = grp; // 패널 상태 적용
			
			// room_list의 크기보다 작을 때, room_list의 정보를 방에 추가한다.
			if(i<room_list.size()) {
				// room_list에서 객체 하나를
				RoomInfo r = (RoomInfo)room_list.get(i);
				// 해당 grp에 RoomInfo 객체를 저장시킨다.(grp에 room_list의 객체를 저장시키는 과정)
				grp.setRoomInfo(r);
				grp.addMouseListener(new MouseAdapter() {
					// 마우스를 버튼에 올려놨을 때 이벤트
					@Override
					public void mouseEntered(MouseEvent e) {
						grp.setGRImage(gamgeRoomEnteredImage.getImage()); // 마우스를 올려놨을때 이미지 변경(Entered Image)
						grp.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
					}
					
					// 마우스를 버튼에서 떼었을때 이벤트
					@Override
					public void mouseExited(MouseEvent e) {
						grp.setGRImage(gamgeRoomBasicImage.getImage()); // 마우스를 떼었을때 이미지 변경(Basic Image)
						grp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
					}
					
					// 마우스로 버튼을 눌렀을 때 이벤트
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == 1) {
							grp.setGRImage(gamgeRoomPressedImage.getImage()); // 마우스를 눌렀을 때 이미지 변경(Pressed Image)
							
						}
					}
					
					// 마우스로 버튼을 누른 후 떼었을 때 이벤트
					@Override
					public void mouseReleased(MouseEvent e) {
						if (e.getButton() == 1) {
							grp.setGRImage(gamgeRoomEnteredImage.getImage()); // 누른 버튼이 떼어졌을 때 이미지 변경(Entered Image) - 마우스는 이미 패널에 올려놓여진 상태이기 때문에
							// 해당 패널에 저장된 객체 정보를 가져와서 roomInfo에 저장한다.
							RoomInfo roomInfo = grp.getRoomInfo();
							// 프로토콜은 EnterRoom으로 유저 id와 방 번호를 서버에 보낸다.
							send_message("EnterRoom/"+userInfo.getUserID()+"/"+roomInfo.getRoom_No());
						}
					}
				});
				gameRoom[i] = grp; // 갱신된 패널로 다시 적용
				
				// 게임방 번호를 설정할 JLabel을 할당
				JLabel gameRoomNumber_lb = new JLabel();
				// 방번호 설정. room_No값에 따라 해당 방번호를 지정. 000 의 형태로 설정. 
				String number = "";
				int temp = r.getRoom_No(), tempI;
				for (int j = 1; j <= 3; j++) {
					if ((tempI=temp % 10)==0) {
						number = "" + 0 + number;
					} else {
						number = "" + tempI + number;
					}
					temp /= 10;
				}
				// gameRoomNumber_lb를 세팅한다.
				gameRoomNumber_lb.setText(number);
				gameRoomNumber_lb.setFont(roomFont);
				gameRoomNumber_lb.setBounds(32, 23, 40, 20);
				gameRoomNumber_lb.setForeground(Color.DARK_GRAY);
				gameRoomNumber_lb.setLayout(null);
				gameRoom[i].add(gameRoomNumber_lb);
				
				// 게임방 제목을 설정할 JLabel을 할당
				JLabel gameRoomTitle_lb = new JLabel();
				gameRoomTitle_lb.setText(r.getRoom_name());
				gameRoomTitle_lb.setFont(roomFont);
				gameRoomTitle_lb.setBounds(90, 23, 200, 20);
				gameRoomTitle_lb.setForeground(Color.DARK_GRAY);
				gameRoom[i].add(gameRoomTitle_lb);
				
				// 게임방 인원수를 설정할 JLabel을 할당
				JLabel gameRoomPlayerCount_lb = new JLabel();
				String userCount = "" + r.getRoom_UCount() + " / " + r.getFixed_User();
				gameRoomPlayerCount_lb.setText(userCount);
				gameRoomPlayerCount_lb.setFont(roomFont);
				gameRoomPlayerCount_lb.setBounds(275, 68, 50, 20);
				gameRoomPlayerCount_lb.setForeground(Color.DARK_GRAY);
				gameRoom[i].add(gameRoomPlayerCount_lb);
			}
			// 이렇게 생성한 패널을 뷰에 추가한다.
			gameRoomView.add(gameRoom[i]);
		}
	}
	
	// #방이 생성/제거될 때마다 방번호에 따라 순서를 재배치하는 메소드(업데이트 메소드)
	public void relocationRoom() {
				
		// room_list를 AscendingRoomInfo 정렬 조건에 따라 재정렬한다.
		Collections.sort(room_list, new AscendingRoomInfo());
		
		// 기본적인 배경 패널만 생성된 방에 list에 등록된 방만 큼 정보를 적용시킨다.
		createRoom();
		
		System.out.println("방 개수 : " +room_list.size());
		
		// 패널의 변경사항을 적용하기위한 메소드
		gameRoomView.revalidate(); // 레이아웃 변화를 재확인 시킨다.
		gameRoomView.repaint(); // removeAll()에 의해 제거 된 오래된 자식의 이미지를 지우는 데 필요하다.
	}
	
	// #50명의 유저 리스트를 생성하기 위한 메소드
	private void allocationUserInfo() {
		for(int i=0; i<userList.length; i++) {
			// 내부 클래스 GameRoomPanel 클래스를 이용해서 User List Panel을 생성
			GameRoomPanel grp = new GameRoomPanel(userInfoPanelImage.getImage());
			// 마우스 리스너 추가 예정 => 업데이트 메소드에서 해야할듯?
//			grp.addMouseListener( new MouseAdapter() {
//			});
			userList[i] = grp;
			
			userListView.add(userList[i]);
		}
	}
	
	// #유저 리스트 업데이트 메소드 - 유저 리스트에 변동이 생기게되면 실행한다.
	private void updateUserInfo() {
		Font infoFont = new Font("Inconsolata",Font.BOLD,17); // 폰트 설정
		// 기존에 띄워둔 패널을 지운다.
		userListView.removeAll();
		
		for(int i=0; i<userList.length; i++) {
			// 내부 클래스 GameRoomPanel 클래스를 이용해서 User List Panel을 생성
			GameRoomPanel grp = new GameRoomPanel(userInfoPanelImage.getImage());
			// 마우스 리스너 추가 예정 => 업데이트 메소드에서 해야할듯?
//			grp.addMouseListener( new MouseAdapter() {
//			});
			
			userList[i] = grp;
			
			if(i<user_list.size()) {
				// JPanel,JLabel을 선언 및 할당
				// JLabel을 선언 및 할당
				JLabel userID_lb = new JLabel();
				// 현재 접속된 유저의 리스트 만큼 텍스트를 지정해준다. (저장값은 유저id)
				// userID_lb 셋팅
				userID_lb.setFont(infoFont);
				userID_lb.setBounds(40, 1, 160, 30);
				userID_lb.setForeground(Color.black);
				userID_lb.setLayout(null);
				userList[i].add(userID_lb);
				// 현재 접속된 유저의 리스트 만큼 텍스트를 지정해준다. (저장값은 유저id)
				UserInfo u = (UserInfo) user_list.get(i);
				userID_lb.setText(u.getUserID());
				// userID_lb 셋팅
				userList[i].add(userID_lb);
			}
			userListView.add(userList[i]);
		}
		
		// 패널의 변경사항을 적용하기위한 메소드
		userListView.revalidate(); // 레이아웃 변화를 재확인 시킨다.
		userListView.repaint(); // removeAll()에 의해 제거 된 오래된 자식의 이미지를 지우는 데 필요하다.
	}
	
	// 텍스트 필드 글자 수 제한을 위한 클래스 및 메소드
	public class JTextFieldLimit extends PlainDocument {
		private int limit;
		
		JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}
		
		public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
			if (str == null) return;
			
			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}
	} // JTextFieldLimit class 끝
	
	// 키 이벤트를 주기위한 클래스
	public class keyAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				// 엔터를 누르면 전송이 되게 하기위한 메소드
				String message = chatting_tf.getText();
				if(message.equals("")) { //아무것도 입력하지 않았을 시 알림창을 띄움
					JOptionPane.showMessageDialog(null, 
							"내용을 입력하시기 바랍니다.","알림",JOptionPane.NO_OPTION);
				} else {
					send_message("ChattingWR/"+id+"/"+message);
					chatting_tf.setText("");
				}
			}
		}
	} // keyAdapter class 끝
	
	
	/* 아래 paint() 메소드는 GUI Application이 실행되거나 
	 * 활성/비활성으로 인한 변동 영역을 감지했을때, 실행되는 메소드이다. */
	
	@Override
	public void paint(Graphics g) {
		viewImage = createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		viewGraphics = viewImage.getGraphics();
		screenDraw(viewGraphics);
		g.drawImage(viewImage,0,0, null);
	}
	
	public void screenDraw(Graphics g) {
		g.drawImage(backgroundImage, 0, 0, null);
		paintComponents(g);
		this.repaint();
	}	
	
	
	public static void main(String[] args) {
		
	}
	
	
	// 방만들기 프레임
	class CreateRoom extends JFrame{
	// TextField
		private JTextField roomTitel_tf = new JTextField(); // 방 제목
		private JTextField roomPw_tf = new JTextField();  // 방 비밀번호
	
	// ComboBox
		private String[] state = {"공개","비공개"};
		private Integer[] player = {2,4,6}; // 최대인원 6명으로 설정(공평한 문제수 배분을 위해 2,4,6명만 받을 예정)
		private JComboBox<String> roomState_tf = new JComboBox<String>(state); // 공개/비공개 설정을 위한 콤보박스
		private JComboBox<Integer> rPlayer_tf = new JComboBox<Integer>(player); // 인원수 설정을 위한 콤보박스
		
	// 각종 변수 변수
		private Image viewImage; // 이미지 저장용 변수
		private Graphics viewGraphics; // 그래픽 저장용 변수	
		private int mouseX; // 마우스 좌표 변수
		private int mouseY; // 마우스 좌표 변수
		
	// Image
		// # CreateRoom 배경
		private Image crbackgroundImage = 
				new ImageIcon(Main.class.getResource("/images/CreateRoom.png")).getImage();
		// #방만들기 프레임 내부 버튼용 이미지
		private ImageIcon crCancelBasicImage = new ImageIcon(Main.class.getResource("/images/cancelButtonBasic.png"));
		private ImageIcon crCancelEnteredImage = new ImageIcon(Main.class.getResource("/images/cancelButtonEntered.png")); 
		private ImageIcon createBasicImage = new ImageIcon(Main.class.getResource("/images/createRoomButtonBasic.png"));
		private ImageIcon createEnteredImage = new ImageIcon(Main.class.getResource("/images/createRoomButtonEntered.png"));
		// Button
		private JButton cancelButton = new JButton(crCancelBasicImage); // 취소 버튼
		private JButton createButton = new JButton(createBasicImage); // 방만들기 버튼
		
		public CreateRoom() {
			Font font = new Font("Inconsolata",Font.PLAIN,11); 
			setUndecorated(true); // 프레임 타이틀 바 제거(윈도우를 제거함) - 기능 완성 후 추가 예정
			setSize(360,213); // null은 최댓값
			setBackground(new Color(0,0,0,0));
			setAlwaysOnTop(true); // 항상 모든 윈도우 위에 위치하도록 함
			setResizable(false); // 프레임 크기 고정
			setLocationRelativeTo(null); // 윈도우를 화면 정중앙에 띄우기 위함
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 종료시 남아있는 프로세스도 깨끗하게 종료하기 위함
			setVisible(true); // 윈도우를 볼 수 있음.
			setLayout(null);	
			
			// 마우스로 창을 움직일 수 있다.
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					mouseX = e.getX();
					mouseY = e.getY();
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				// #해당 창을 드래그 시, 움직일 수 있게 한다.
				@Override
				public void mouseDragged(MouseEvent e) {
					int x = e.getXOnScreen();
					int y = e.getYOnScreen();
					setLocation(x - mouseX, y - mouseY);
				}
			});
		
		// TextField / ComboBox
			// # 제목 입력
			roomTitel_tf.setBounds(88,47,244,20);
			roomTitel_tf.setFont(font);
			roomTitel_tf.setDocument(new JTextFieldLimit(20)); // 제목 20자 제한 	 
			add(roomTitel_tf);
			
			// # 공개/비공개 상태 콤보박스
			roomState_tf.setBounds(88,78,78,20);
			roomState_tf.setFont(font);
			add(roomState_tf);
			roomState_tf.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String state = roomState_tf.getSelectedItem().toString();
					if(state.equals("공개")) {
						roomPw_tf.setEnabled(false);
					} else if (state.equals("비공개")) {
						roomPw_tf.setEnabled(true);
					}
				}
			});
			
			// # 방 비밀번호 입력(콤보박스 이벤트에 따라 활설/비활성)
			roomPw_tf.setBounds(88,103,78,20);
			roomPw_tf.setFont(font);
			roomPw_tf.setDocument(new JTextFieldLimit(10)); // 비밀번호 10자 제한 	
			roomPw_tf.setEnabled(false); // 초기에 "공개"설정이기때문에 비활성 상태로 둔다.
			add(roomPw_tf);
			
			// # 인원수 설정 콤보박스
			rPlayer_tf.setBounds(88,128,78,20);
			roomPw_tf.setFont(font);
			add(rPlayer_tf);
			
			
		// Button
			// #츼소 버튼
			cancelButton.setBounds(187, 180, 72, 24);
			add(cancelButton);
			cancelButton.addMouseListener(new MouseAdapter() {
				// 마우스를 버튼에 올려놨을 때 이벤트
				@Override
				public void mouseEntered(MouseEvent e) {
					cancelButton.setIcon(crCancelEnteredImage); // 마우스를 올려놨을때 이미지 변경(Entered Image)
					cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
				}
				
				// 마우스를 버튼에서 떼었을때 이벤트
				@Override  
				public void mouseExited(MouseEvent e) {
					cancelButton.setIcon(crCancelBasicImage); // 마우스를 떼었을때 이미지 변경(Basic Image)
					cancelButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton()==1) {
						dispose(); 
					}
				}
			});
			
			// #만들기 버튼
			createButton.setBounds(101, 180, 72, 24);
			add(createButton);
			createButton.addMouseListener(new MouseAdapter() {
				// 마우스를 버튼에 올려놨을 때 이벤트
				@Override
				public void mouseEntered(MouseEvent e) {
					createButton.setIcon(createEnteredImage); // 마우스를 올려놨을때 이미지 변경(Entered Image)
					createButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
				}
				
				// 마우스를 버튼에서 떼었을때 이벤트
				@Override  
				public void mouseExited(MouseEvent e) {
					createButton.setIcon(createBasicImage); // 마우스를 떼었을때 이미지 변경(Basic Image)
					createButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
				}
				// 버튼을 떼었을때 이벤트
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton()==1) {
						// 만들기 버튼을 누르면 설정사항을 그대로 서버에 전송한다.
						room_name = roomTitel_tf.getText().trim(); // 방 제목
						String state = roomState_tf.getSelectedItem().toString(); // 공개/비공개
						roomPW = null; // 방 비밀번호 (공개면 null로, 비공개면 입력받은 패스워드를 저장)
						if(state.equals("비공개")) {
							roomPW = roomPw_tf.getText().trim();
						} 
						fixed_User = Integer.parseInt(rPlayer_tf.getSelectedItem().toString()); // rPlayer_tf의 제네릭을 Integer로 해놓음
						
						// 입력받은 값을 서버에 전송한다.
						send_message("CreateRoom/"+id+"/"+room_name+"/"+state+"/"+roomPW+"/"+fixed_User);
						
						/* 해당 값을 가지고 RoomInfo 객체를 생성한다. 이때, 방번호는 0번으로 초기화하여 생성
						 * 이때, room_list에는 등록하지 않는다.(방 번호를 할당 받고 등록)	 */
						roomInfo = new RoomInfo(0,room_name, roomPW, fixed_User, userInfo); 
						dispose();
					}
				}
			});
			
		}
		
		
		@Override
		public void paint(Graphics g) {
			viewImage = createImage(360,213);
			viewGraphics = viewImage.getGraphics();
			screenDraw(viewGraphics);
			g.drawImage(viewImage,0,0, null);
		}
		
		public void screenDraw(Graphics g) {
			g.drawImage(crbackgroundImage, 0, 0, null);
			paintComponents(g);
			this.repaint();
		}	
	}
	
	
	class inputPw extends JFrame {
//		JPanel inputPW = new GameRoomPanel(inputPwPanelImage.getImage());
		private JLabel inputPW_lb;
		private JButton inputPW_bt;
		private JButton cancel_bt;
		private JPasswordField inputPW_tf;
		
		// 각종 변수 변수
//		private Image viewImage; // 이미지 저장용 변수
//		private Graphics viewGraphics; // 그래픽 저장용 변수	
		
		public inputPw(int room_No, String room_Pw) {
			setUndecorated(true); // 프레임 타이틀 바 제거(윈도우를 제거함) - 기능 완성 후 추가 예정
			setSize(1024,768); // null은 최댓값
			setBackground(new Color(40,40,40,40));
//			setAlwaysOnTop(true); // 항상 모든 윈도우 위에 위치하도록 함
			setPreferredSize(new Dimension(1024,768));
			setResizable(false); // 프레임 크기 고정
			setLocationRelativeTo(null); // 윈도우를 화면 정중앙에 띄우기 위함
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 종료시 남아있는 프로세스도 깨끗하게 종료하기 위함
			setVisible(true); // 윈도우를 볼 수 있음.
			setLayout(null);

			inputPW_lb = new JLabel();
			inputPW_lb.setBounds(430, 310,200,30);
			inputPW_lb.setText("비밀번호를 입력하세요.");
			inputPW_lb.setFont(wrFont);
			add(inputPW_lb);
			
			inputPW_tf  = new JPasswordField();
			inputPW_tf.setBounds(410, 350,200,30);
			inputPW_tf.setBorder(null); // 테두리 제거
			inputPW_tf.setBackground(new Color(40,40,40,40)); // 배경 반투명
			inputPW_tf.setEchoChar('*'); // 화면에 표기할 문자를 '*'로 지정
			inputPW_tf.setFont(wrFont);
			add(inputPW_tf);
			
			
			inputPW_bt = new JButton();
			inputPW_bt.setText("입력");
			inputPW_bt.setBounds(412, 400,80,30);
			inputPW_bt.setBorder(null); // 테두리 제거
			inputPW_bt.setBackground(new Color(0,0,0,0)); // 배경 투명
			inputPW_bt.setFont(wrFont);
			add(inputPW_bt);
			inputPW_bt.addMouseListener(new MouseAdapter() {
				// 마우스를 버튼에 올려놨을 때 이벤트
				@Override
				public void mouseEntered(MouseEvent e) {
					inputPW_bt.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
				}
				
				// 마우스를 버튼에서 떼었을때 이벤트
				@Override  
				public void mouseExited(MouseEvent e) {
					inputPW_bt.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
				}
//				
				// 해당 버튼을 클릭했을 때
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton()==1) {
						// pw와 입력한 값이 같으면
						if(room_Pw.equals(inputPW_tf.getText())) {
							// 방에 입장함을 서버에게 알린다.
							send_message("EntryRoom/"+userInfo.getUserID());
							// WaitingRoom 창을 종료하고 게임창을 연다. 이때, 방 제목과 방 번호를 같이 보낸다.
							dispose();
//							new PaintEx(room_No);
						}
					}
				}
			});
			
			
			cancel_bt = new JButton();
			cancel_bt.setText("취소");
			cancel_bt.setBounds(532, 400,80,30);
			cancel_bt.setBorder(null); // 테두리 제거
			cancel_bt.setBackground(new Color(0,0,0,0)); // 배경 투명
			cancel_bt.setFont(wrFont);
			add(cancel_bt);
			cancel_bt.addMouseListener(new MouseAdapter() {
				// 마우스를 버튼에 올려놨을 때 이벤트
				@Override
				public void mouseEntered(MouseEvent e) {
					cancel_bt.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서를 손모양 커서로 변경
				}
				
				// 마우스를 버튼에서 떼었을때 이벤트
				@Override  
				public void mouseExited(MouseEvent e) {
					cancel_bt.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서를 기본 커서로 변경
				}
				
//				// 버튼을 클릭할 시	
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton()==1) {
						dispose(); // 창 종료
					}
				}
			});
			
		}
		
		@Override
		public void paint(Graphics g) {
			g.drawImage(inputPwPanelImage.getImage(), 262, 268, null);
			paintComponents(g);
			this.repaint();
		}
	}
	
	
	// 게임방&유저info 하나하나를 JPanel을 상속받은 GameRoomPanel 클래스로 생성한다.
	class GameRoomPanel extends JPanel{
		private Image img;
		private RoomInfo roomInfo;
		
		public GameRoomPanel(Image img) {
			this.img = img;
			setSize(new Dimension(img.getWidth(null), img.getHeight(null))); // null은 최댓값
			setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null)));
			setOpaque(false);
			setLayout(null); // 패널에 추가하는 요소들의 위치를 자유롭게 설정하기 위해 Layout을 null로 해준다.
		}
		
		// 패널을 열었을 때 자동으로 이미지를 그려주는 메소드
		public void paintComponent(Graphics g)  {
			g.drawImage(img, 0, 0, null);
		}
		
		// 이미지를 바꿔주기 위한 메소드
		public void setGRImage(Image img) {
			this.img = img;
		}
		
		public void setRoomInfo(RoomInfo roomInfo) {
			this.roomInfo = roomInfo;
		}
		
		public RoomInfo getRoomInfo() {
			return roomInfo;
		}
	}
	
	// RoomInfo의 Vector를 재정렬하기 위한 클래스(방번호를 기준으로 재정렬)
	class AscendingRoomInfo implements Comparator<RoomInfo> {

		@Override
		public int compare(RoomInfo o1, RoomInfo o2) {
			//방번호 생성 로직에서는 중복을 허용하지 않기 때문에 두 가지만 생각한다.
			if(o1.getRoom_No() > o2.getRoom_No()) return 1;
			else return -1;
		} 
	}
}
